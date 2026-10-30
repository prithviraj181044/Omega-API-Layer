package com.eurobank.omega.error.contract;

import java.io.Serial;

/** Represents the gateway timeout condition from the active catalogue. */
public final class TimeoutException extends OlTechnicalException {
  @Serial private static final long serialVersionUID = 1L;

  /**
   * Creates the typed technical failure.
   *
   * @param correlationId existing request correlation identifier
   * @param cause internal cause, never a client response
   */
  public TimeoutException(String correlationId, Throwable cause) {
    super(ErrorContext.forDefaultStatus(OmegaBusinessError.GW_TMO_001, correlationId), cause);
  }
}
