package com.eurobank.omega.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;

/**
 * Supplies reactive MongoDB transaction wiring without embedding credentials or deployment values.
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class MongoConfig {
  private final ReactiveMongoDatabaseFactory databaseFactory;

  /**
   * Creates the transaction manager for the configured reactive database factory.
   *
   * @return the reactive MongoDB transaction manager
   */
  @Bean
  public ReactiveMongoTransactionManager mongoTransactionManager() {
    return new ReactiveMongoTransactionManager(databaseFactory);
  }
}
