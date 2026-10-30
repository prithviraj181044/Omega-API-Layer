package com.eurobank.omega.api.contract;

/** Describes a persisted API version lifecycle state. */
public enum ApiLifecycleState {
  /** Onboarding value. */
  ONBOARDING,

  /** In service value. */
  IN_SERVICE,

  /** Deprecated value. */
  DEPRECATED,

  /** Retired value. */
  RETIRED;
}
