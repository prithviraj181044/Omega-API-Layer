package com.eurobank.omega.error.contract;

import java.io.Serial;

/**
 * Base checked exception carrying validated code-space metadata and the unchanged correlation ID.
 */
public abstract class OmegaException extends Exception {
  @Serial private static final long serialVersionUID = 1L;
  private final ErrorContext context;

  /**
   * Creates an exception from already validated immutable context.
   *
   * @param context immutable validated metadata, never null
   * @param cause internal cause, never a client response
   */
  protected OmegaException(ErrorContext context, Throwable cause) {
    super(context.error().getWireCode(), cause);
    this.context = context;
  }

  /**
   * Returns the typed catalogue entry.
   *
   * @return the error metadata
   */
  public final OmegaBusinessError getError() {
    return context.error();
  }

  /**
   * Returns the original correlation identifier without generating or overwriting it.
   *
   * @return the correlation identifier
   */
  public final String getCorrelationId() {
    return context.correlationId();
  }

  /**
   * Returns the source-permitted status stored in the validated context.
   *
   * @return the HTTP status
   */
  public final int getHttpStatus() {
    return context.httpStatus();
  }

  /**
   * Identifies retry classification implemented by the exception subtree, not message text.
   *
   * @return whether this exception belongs to the technical subtree
   */
  public abstract boolean isRetryable();
}
