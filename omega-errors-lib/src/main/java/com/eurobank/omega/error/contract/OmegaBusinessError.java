package com.eurobank.omega.error.contract;

import java.util.Set;

/**
 * Owns the active catalogue metadata for the project-local error library.
 *
 * <p>Proposed gateway additions and parked Admin codes are intentionally not activated.
 */
public enum OmegaBusinessError {
  /** Gw sec 001 condition. */
  GW_SEC_001(400, Category.SECURITY_ERROR),

  /** Gw ipf 001 condition. */
  GW_IPF_001(403, Category.IP_FILTER),

  /** Gw auth 001 condition. */
  GW_AUTH_001(401, Category.AUTHENTICATION),

  /** Gw auth 002 condition. */
  GW_AUTH_002(403, Category.AUTHORIZATION),

  /** Gw sch 001 condition. */
  GW_SCH_001(422, Category.VALIDATION_ERROR, Set.of(400, 422)),

  /** Gw pri 001 condition. */
  GW_PRI_001(503, Category.TRAFFIC_MANAGEMENT, Set.of(429, 503)),

  /** Gw rtl 001 condition. */
  GW_RTL_001(429, Category.RATE_LIMIT_EXCEEDED),

  /** Gw thr 001 condition. */
  GW_THR_001(429, Category.TRAFFIC_MANAGEMENT),

  /** Gw rte 001 condition. */
  GW_RTE_001(503, Category.ROUTING_FAILURE),

  /** Gw cbk 001 condition. */
  GW_CBK_001(503, Category.CIRCUIT_BREAKER),

  /** Gw tmo 001 condition. */
  GW_TMO_001(504, Category.UPSTREAM_TIMEOUT),

  /** Gw int 001 condition. */
  GW_INT_001(500, Category.INTERNAL_GATEWAY_ERROR),

  /** Onboarding validation required condition. */
  ONBOARDING_VALIDATION_REQUIRED(422, Category.RESOURCE),

  /** Onboarding specification invalid condition. */
  ONBOARDING_SPECIFICATION_INVALID(422, Category.RESOURCE),

  /** Onboarding lifecycle transition invalid condition. */
  ONBOARDING_LIFECYCLE_TRANSITION_INVALID(409, Category.RESOURCE),

  /** Onboarding api not found condition. */
  ONBOARDING_API_NOT_FOUND(404, Category.RESOURCE),

  /** Event subscription not found condition. */
  EVENT_SUBSCRIPTION_NOT_FOUND(404, Category.RESOURCE),

  /** Event subscription forbidden condition. */
  EVENT_SUBSCRIPTION_FORBIDDEN(403, Category.COMPLIANCE),

  /** Event callback url invalid condition. */
  EVENT_CALLBACK_URL_INVALID(400, Category.RESOURCE),

  /** Event metadata missing condition. */
  EVENT_METADATA_MISSING(422, Category.RESOURCE),

  /** Event schema incompatible condition. */
  EVENT_SCHEMA_INCOMPATIBLE(422, Category.RESOURCE),

  /** Event schema violation condition. */
  EVENT_SCHEMA_VIOLATION(422, Category.RESOURCE),

  /** Event stream token expired condition. */
  EVENT_STREAM_TOKEN_EXPIRED(401, Category.COMPLIANCE),

  /** Platform dependency unavailable condition. */
  PLATFORM_DEPENDENCY_UNAVAILABLE(503, Category.SYSTEM);

  private final int httpStatus;
  private final Category category;
  private final Set<Integer> permittedHttpStatuses;

  OmegaBusinessError(int httpStatus, Category category) {
    this(httpStatus, category, Set.of(httpStatus));
  }

  OmegaBusinessError(int httpStatus, Category category, Set<Integer> permittedHttpStatuses) {
    this.httpStatus = httpStatus;
    this.category = category;
    this.permittedHttpStatuses = Set.copyOf(permittedHttpStatuses);
  }

  /**
   * Returns the canonical SCREAMING-KEBAB-CASE wire code.
   *
   * @return the wire code
   */
  public String getWireCode() {
    return name().replace('_', '-');
  }

  /**
   * Returns the default HTTP status for this condition.
   *
   * @return the catalogue status
   */
  public int getHttpStatus() {
    return httpStatus;
  }

  /**
   * Returns the category; retry classification belongs to exception types.
   *
   * @return the catalogue category
   */
  public Category getCategory() {
    return category;
  }

  /**
   * Returns the severity of the active baseline condition.
   *
   * @return ERROR for every current catalogue entry
   */
  public Severity getSeverity() {
    return Severity.ERROR;
  }

  /**
   * Returns the immutable set of explicitly documented status variants.
   *
   * @return the permitted statuses
   */
  public Set<Integer> getPermittedHttpStatuses() {
    return permittedHttpStatuses;
  }

  /**
   * Checks an edge-specific status without inventing another wire code.
   *
   * @param status requested HTTP status
   * @return whether the source catalogue permits it
   */
  public boolean permitsHttpStatus(int status) {
    return permittedHttpStatuses.contains(status);
  }

  /**
   * Identifies gateway-owned catalogue entries, not retry behaviour.
   *
   * @return whether the entry belongs to the gateway code family
   */
  public boolean isGatewayOwned() {
    return name().startsWith("GW_");
  }
}
