package com.eurobank.omega.error.contract;

import java.io.Serial;

/** Base for technical failures whose retry policy is selected by exception type. */
public abstract class OlTechnicalException extends OmegaException {
  @Serial private static final long serialVersionUID = 1L;

  /**
   * Creates a technical failure from validated metadata.
   *
   * @param context immutable validated error metadata
   * @param cause internal cause
   */
  protected OlTechnicalException(ErrorContext context, Throwable cause) {
    super(context, cause);
  }

  @Override
  public final boolean isRetryable() {
    return true;
  }
}
