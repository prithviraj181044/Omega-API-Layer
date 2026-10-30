package com.eurobank.omega.api.adapter.mongo;

import java.util.Objects;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Base for every tenant-owned MongoDB collection. Every read, write and delete carries the
 * {@code tenantId} filter, so a caller cannot reach another tenant's document (MT-3).
 *
 * @param <T> document type stored in the collection
 */
public abstract class TenantScopedRepository<T> {
  /** Name of the tenant field that every tenant-owned document carries. */
  public static final String TENANT_FIELD = "tenantId";

  private static final String ID_FIELD = "_id";

  private final ReactiveMongoTemplate template;
  private final Class<T> documentType;
  private final String collection;

  /**
   * Creates the repository for one collection.
   *
   * @param template reactive MongoDB template
   * @param documentType stored document type
   * @param collection collection name
   */
  protected TenantScopedRepository(
      ReactiveMongoTemplate template, Class<T> documentType, String collection) {
    this.template = template;
    this.documentType = documentType;
    this.collection = collection;
  }

  /**
   * Reads the tenant that owns an entity, so a write cannot target a different tenant.
   *
   * @param entity entity about to be written
   * @return the entity's own tenant identifier
   */
  protected abstract String tenantIdOf(T entity);

  /**
   * Finds one document by identifier inside one tenant.
   *
   * @param tenantId owning tenant
   * @param id document identifier
   * @return the document, or empty when absent or owned by another tenant
   */
  public Mono<T> findById(String tenantId, Object id) {
    return template.findOne(
        scoped(tenantId, Criteria.where(ID_FIELD).is(id)), documentType, collection);
  }

  /**
   * Finds documents inside one tenant.
   *
   * @param tenantId owning tenant
   * @param extra additional criteria combined with the tenant filter, or {@code null}
   * @return matching documents of that tenant only
   */
  public Flux<T> find(String tenantId, Criteria extra) {
    return template.find(scoped(tenantId, extra), documentType, collection);
  }

  /**
   * Counts documents inside one tenant.
   *
   * @param tenantId owning tenant
   * @param extra additional criteria combined with the tenant filter, or {@code null}
   * @return the count for that tenant only
   */
  public Mono<Long> count(String tenantId, Criteria extra) {
    return template.count(scoped(tenantId, extra), documentType, collection);
  }

  /**
   * Inserts a new document into its own tenant.
   *
   * @param tenantId tenant the caller is acting for
   * @param entity document to insert; its tenant must equal {@code tenantId}
   * @return the inserted document
   */
  public Mono<T> insert(String tenantId, T entity) {
    return Mono.defer(
        () -> {
          requireSameTenant(tenantId, entity);
          return template.insert(entity, collection);
        });
  }

  /**
   * Inserts or replaces a document inside its own tenant.
   *
   * @param tenantId tenant the caller is acting for
   * @param entity document to save; its tenant must equal {@code tenantId}
   * @return the saved document
   */
  public Mono<T> save(String tenantId, T entity) {
    return Mono.defer(
        () -> {
          requireSameTenant(tenantId, entity);
          return template.save(entity, collection);
        });
  }

  /**
   * Deletes one document by identifier inside one tenant.
   *
   * @param tenantId owning tenant
   * @param id document identifier
   * @return the number of documents deleted, 0 when absent or owned by another tenant
   */
  public Mono<Long> deleteById(String tenantId, Object id) {
    return template
        .remove(scoped(tenantId, Criteria.where(ID_FIELD).is(id)), documentType, collection)
        .map(result -> result.getDeletedCount());
  }

  /**
   * Creates a unique compound index that starts with the tenant field. Index creation is code,
   * not a manual step.
   *
   * @param name index name
   * @param fields remaining indexed fields after {@code tenantId}
   * @return the created index name
   */
  protected Mono<String> ensureTenantUniqueIndex(String name, String... fields) {
    Index index = new Index().named(name).unique().on(TENANT_FIELD, Sort.Direction.ASC);
    for (String field : fields) {
      index = index.on(field, Sort.Direction.ASC);
    }
    return template.indexOps(collection).createIndex(index);
  }

  private Query scoped(String tenantId, Criteria extra) {
    requireTenant(tenantId);
    Criteria tenant = Criteria.where(TENANT_FIELD).is(tenantId);
    return new Query(extra == null ? tenant : new Criteria().andOperator(tenant, extra));
  }

  private void requireSameTenant(String tenantId, T entity) {
    requireTenant(tenantId);
    Objects.requireNonNull(entity, "entity");
    if (!tenantId.equals(tenantIdOf(entity))) {
      throw new IllegalArgumentException("Entity tenant does not match the acting tenant");
    }
  }

  private static void requireTenant(String tenantId) {
    if (tenantId == null || tenantId.isBlank()) {
      throw new IllegalArgumentException("tenantId is required on every tenant-scoped operation");
    }
  }

  /**
   * Exposes the raw filter document for tests that assert the tenant condition is always present.
   *
   * @param tenantId owning tenant
   * @param extra additional criteria or {@code null}
   * @return the query document sent to MongoDB
   */
  final Document filterFor(String tenantId, Criteria extra) {
    return scoped(tenantId, extra).getQueryObject();
  }
}
