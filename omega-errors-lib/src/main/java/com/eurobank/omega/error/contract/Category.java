package com.eurobank.omega.error.contract;

/** Provides the source-defined categories used by this active API Layer catalogue. */
public enum Category {
  /** Security error classification. */
  SECURITY_ERROR,

  /** Ip filter classification. */
  IP_FILTER,

  /** Authentication classification. */
  AUTHENTICATION,

  /** Authorization classification. */
  AUTHORIZATION,

  /** Validation error classification. */
  VALIDATION_ERROR,

  /** Traffic management classification. */
  TRAFFIC_MANAGEMENT,

  /** Rate limit exceeded classification. */
  RATE_LIMIT_EXCEEDED,

  /** Routing failure classification. */
  ROUTING_FAILURE,

  /** Circuit breaker classification. */
  CIRCUIT_BREAKER,

  /** Upstream timeout classification. */
  UPSTREAM_TIMEOUT,

  /** Internal gateway error classification. */
  INTERNAL_GATEWAY_ERROR,

  /** Resource classification. */
  RESOURCE,

  /** Compliance classification. */
  COMPLIANCE,

  /** System classification. */
  SYSTEM;
}
