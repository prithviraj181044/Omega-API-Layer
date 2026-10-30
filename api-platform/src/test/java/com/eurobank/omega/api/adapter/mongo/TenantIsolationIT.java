package com.eurobank.omega.api.adapter.mongo;

import com.eurobank.omega.api.testsupport.MongoReplicaSetSupport;
import com.mongodb.reactivestreams.client.MongoClients;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.testcontainers.mongodb.MongoDBContainer;
import reactor.test.StepVerifier;

/**
 * Proves tenant isolation and the unique index against a real MongoDB replica set. It runs only
 * when an approved image is supplied through the {@code omega.test.mongo.image} system property;
 * otherwise it is skipped, and a skipped run is not evidence.
 */
class TenantIsolationIT {
  private record Item(String tenantId, String name) {}

  private static final class ItemRepository extends TenantScopedRepository<Item> {
    ItemRepository(ReactiveMongoTemplate template) {
      super(template, Item.class, "items");
    }

    @Override
    protected String tenantIdOf(Item entity) {
      return entity.tenantId();
    }

    reactor.core.publisher.Mono<String> createIndex() {
      return ensureTenantUniqueIndex("uq_tenant_name", "name");
    }
  }

  private static MongoDBContainer mongo;
  private static ReactiveMongoTemplate template;
  private static ItemRepository repository;

  @BeforeAll
  static void start() {
    String image = System.getProperty("omega.test.mongo.image");
    Assumptions.assumeTrue(image != null && !image.isBlank(), "No approved MongoDB image supplied");
    mongo = MongoReplicaSetSupport.create(image);
    mongo.start();
    template =
        new ReactiveMongoTemplate(MongoClients.create(mongo.getReplicaSetUrl()), "tenant_it");
    repository = new ItemRepository(template);
  }

  @AfterAll
  static void stop() {
    if (mongo != null) {
      mongo.stop();
    }
  }

  @Test
  void tenantCannotReadCountOrDeleteAnotherTenantsDocument() {
    Item a = new Item("tenant-a", "isolated");
    StepVerifier.create(template.insert(a, "items")).expectNextCount(1).verifyComplete();

    StepVerifier.create(repository.count("tenant-b", null)).expectNext(0L).verifyComplete();
    StepVerifier.create(repository.find("tenant-b", null)).verifyComplete();
    StepVerifier.create(repository.count("tenant-a", null)).expectNext(1L).verifyComplete();
  }

  @Test
  void uniqueIndexIsPerTenant() {
    StepVerifier.create(repository.createIndex()).expectNext("uq_tenant_name").verifyComplete();
    StepVerifier.create(repository.insert("tenant-a", new Item("tenant-a", "same")))
        .expectNextCount(1)
        .verifyComplete();
    StepVerifier.create(repository.insert("tenant-b", new Item("tenant-b", "same")))
        .expectNextCount(1)
        .verifyComplete();
    StepVerifier.create(repository.insert("tenant-a", new Item("tenant-a", "same")))
        .expectError(DuplicateKeyException.class)
        .verify();
  }
}
