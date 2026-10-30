package com.eurobank.omega.error.contract;

import java.io.Serial;

/** Base for non-retryable business/data failures with catalogue-only codes. */
public abstract class OlBusinessDataException extends OmegaException {
  @Serial private static final long serialVersionUID = 1L;

  /**
   * Creates a business/data failure from context validated by the concrete subtype.
   *
   * @param context immutable validated business metadata
   */
  protected OlBusinessDataException(ErrorContext context) {
    super(context, null);
  }

  @Override
  public final boolean isRetryable() {
    return false;
  }
}
