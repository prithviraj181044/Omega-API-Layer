package com.eurobank.omega.error.contract;

import java.io.Serial;

/** Represents an unavailable dependency. */
public final class ServiceUnavailableException extends OlTechnicalException {
  @Serial private static final long serialVersionUID = 1L;

  /**
   * Creates the typed technical failure.
   *
   * @param correlationId existing request correlation identifier
   * @param cause internal cause, never a client response
   */
  public ServiceUnavailableException(String correlationId, Throwable cause) {
    super(
        ErrorContext.forDefaultStatus(
            OmegaBusinessError.PLATFORM_DEPENDENCY_UNAVAILABLE, correlationId),
        cause);
  }
}
