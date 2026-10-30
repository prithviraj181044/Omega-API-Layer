package com.eurobank.omega.api.adapter.mongo;

import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.ReactiveIndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/** Verifies that every operation carries the tenant filter, without contacting MongoDB. */
class TenantScopedRepositoryTest {
  private static final String COLLECTION = "items";
  private static final String TENANT_A = "tenant-a";

  private record Item(String tenantId, String name) {}

  private static final class ItemRepository extends TenantScopedRepository<Item> {
    ItemRepository(ReactiveMongoTemplate template) {
      super(template, Item.class, COLLECTION);
    }

    @Override
    protected String tenantIdOf(Item entity) {
      return entity.tenantId();
    }

    Mono<String> createIndex() {
      return ensureTenantUniqueIndex("uq_tenant_name", "name");
    }
  }

  private final ReactiveMongoTemplate template = Mockito.mock(ReactiveMongoTemplate.class);
  private final ItemRepository repository = new ItemRepository(template);

  private static Query anyQuery() {
    return ArgumentMatchers.any(Query.class);
  }

  private static Class<Item> itemType() {
    return ArgumentMatchers.eq(Item.class);
  }

  private static String collection() {
    return ArgumentMatchers.eq(COLLECTION);
  }

  private static String filterOf(ArgumentCaptor<Query> captor) {
    return captor.getValue().getQueryObject().toJson();
  }

  @Test
  void findByIdAlwaysFiltersOnTenant() {
    Mockito.when(template.findOne(anyQuery(), itemType(), collection())).thenReturn(Mono.empty());

    StepVerifier.create(repository.findById(TENANT_A, "id-1")).verifyComplete();

    ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
    Mockito.verify(template).findOne(query.capture(), itemType(), collection());
    Assertions.assertTrue(filterOf(query).contains(TENANT_A), filterOf(query));
    Assertions.assertTrue(filterOf(query).contains("id-1"), filterOf(query));
  }

  @Test
  void filterWithoutExtraCriteriaStillHasTheTenant() {
    Document filter = repository.filterFor(TENANT_A, null);
    Assertions.assertEquals(TENANT_A, filter.get("tenantId"));
  }

  @Test
  void filterWithExtraCriteriaKeepsTheTenantCondition() {
    Document filter = repository.filterFor(TENANT_A, Criteria.where("name").is("x"));
    Assertions.assertTrue(filter.toJson().contains(TENANT_A), filter.toJson());
    Assertions.assertTrue(filter.toJson().contains("\"name\""), filter.toJson());
  }

  @Test
  void blankOrNullTenantIsRejectedBeforeAnyDatabaseCall() {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> repository.findById(null, "id"));
    Assertions.assertThrows(IllegalArgumentException.class, () -> repository.find(" ", null));
    Assertions.assertThrows(IllegalArgumentException.class, () -> repository.count("", null));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> repository.deleteById(null, "id"));
    Mockito.verifyNoInteractions(template);
  }

  @Test
  void writeForAnotherTenantIsRejectedBeforeAnyDatabaseCall() {
    StepVerifier.create(repository.save(TENANT_A, new Item("tenant-b", "x")))
        .expectError(IllegalArgumentException.class)
        .verify();
    StepVerifier.create(repository.insert(TENANT_A, new Item("tenant-b", "x")))
        .expectError(IllegalArgumentException.class)
        .verify();
    Mockito.verifyNoInteractions(template);
  }

  @Test
  void findAndCountSendTheTenantFilter() {
    Mockito.when(template.find(anyQuery(), itemType(), collection())).thenReturn(Flux.empty());
    Mockito.when(template.count(anyQuery(), itemType(), collection()))
        .thenReturn(Mono.just(0L));

    StepVerifier.create(repository.find(TENANT_A, null)).verifyComplete();
    StepVerifier.create(repository.count(TENANT_A, Criteria.where("name").is("x")))
        .expectNext(0L)
        .verifyComplete();

    ArgumentCaptor<Query> found = ArgumentCaptor.forClass(Query.class);
    Mockito.verify(template).find(found.capture(), itemType(), collection());
    Assertions.assertTrue(filterOf(found).contains(TENANT_A));
    ArgumentCaptor<Query> counted = ArgumentCaptor.forClass(Query.class);
    Mockito.verify(template).count(counted.capture(), itemType(), collection());
    Assertions.assertTrue(filterOf(counted).contains(TENANT_A));
  }

  @Test
  void insertForOwnTenantIsPassedToTheTemplate() {
    Item entity = new Item(TENANT_A, "x");
    Mockito.when(template.insert(entity, COLLECTION)).thenReturn(Mono.just(entity));

    StepVerifier.create(repository.insert(TENANT_A, entity)).expectNext(entity).verifyComplete();
  }

  @Test
  void writeForOwnTenantIsPassedToTheTemplate() {
    Item entity = new Item(TENANT_A, "x");
    Mockito.when(template.save(entity, COLLECTION)).thenReturn(Mono.just(entity));

    StepVerifier.create(repository.save(TENANT_A, entity)).expectNext(entity).verifyComplete();
  }

  @Test
  void deleteByIdFiltersOnTenantAndReturnsDeletedCount() {
    Mockito.when(template.remove(anyQuery(), itemType(), collection()))
        .thenReturn(Mono.just(DeleteResult.acknowledged(1L)));

    StepVerifier.create(repository.deleteById(TENANT_A, "id-1")).expectNext(1L).verifyComplete();

    ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
    Mockito.verify(template).remove(query.capture(), itemType(), collection());
    Assertions.assertTrue(filterOf(query).contains(TENANT_A));
  }

  @Test
  void uniqueIndexStartsWithTheTenantField() {
    ReactiveIndexOperations indexOps = Mockito.mock(ReactiveIndexOperations.class);
    Mockito.when(template.indexOps(COLLECTION)).thenReturn(indexOps);
    Mockito.when(indexOps.createIndex(ArgumentMatchers.any(IndexDefinition.class)))
        .thenReturn(Mono.just("uq_tenant_name"));

    StepVerifier.create(repository.createIndex()).expectNext("uq_tenant_name").verifyComplete();

    ArgumentCaptor<IndexDefinition> index = ArgumentCaptor.forClass(IndexDefinition.class);
    Mockito.verify(indexOps).createIndex(index.capture());
    Document keys = index.getValue().getIndexKeys();
    Assertions.assertEquals("tenantId", keys.keySet().iterator().next());
    Assertions.assertTrue(keys.containsKey("name"));
    Assertions.assertEquals(true, index.getValue().getIndexOptions().get("unique"));
  }
}
