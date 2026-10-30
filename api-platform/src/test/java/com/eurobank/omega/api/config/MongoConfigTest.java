package com.eurobank.omega.api.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;

/** Verifies reactive transaction-manager wiring without connecting to MongoDB. */
class MongoConfigTest {
  @Test
  void createsTransactionManagerWithoutContactingDatabase() {
    ReactiveMongoDatabaseFactory factory = mock(ReactiveMongoDatabaseFactory.class);
    MongoConfig config = new MongoConfig(factory);
    assertNotNull(config.mongoTransactionManager());
    verifyNoInteractions(factory);
  }
}
