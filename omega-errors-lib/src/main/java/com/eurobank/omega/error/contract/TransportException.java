package com.eurobank.omega.error.contract;

import java.io.Serial;

/** Represents a translated transport failure without exposing the upstream cause. */
public final class TransportException extends OlTechnicalException {
  @Serial private static final long serialVersionUID = 1L;

  /**
   * Creates the typed technical failure.
   *
   * @param correlationId existing request correlation identifier
   * @param cause internal cause, never a client response
   */
  public TransportException(String correlationId, Throwable cause) {
    super(
        ErrorContext.forDefaultStatus(
            OmegaBusinessError.PLATFORM_DEPENDENCY_UNAVAILABLE, correlationId),
        cause);
  }
}
