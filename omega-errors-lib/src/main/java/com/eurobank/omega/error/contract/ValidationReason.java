package com.eurobank.omega.error.contract;

/** Names safe validation reasons without carrying rejected input values. */
public enum ValidationReason {
  /** Missing required field classification. */
  MISSING_REQUIRED_FIELD,

  /** Type mismatch classification. */
  TYPE_MISMATCH,

  /** Additional property classification. */
  ADDITIONAL_PROPERTY;
}
