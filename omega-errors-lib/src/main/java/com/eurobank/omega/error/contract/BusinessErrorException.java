package com.eurobank.omega.error.contract;

import java.io.Serial;

/** Represents a non-retryable business/data condition using a catalogue constant only. */
public final class BusinessErrorException extends OlBusinessDataException {
  @Serial private static final long serialVersionUID = 1L;

  /**
   * Creates the typed business failure.
   *
   * @param error non-gateway business catalogue entry
   * @param correlationId existing request correlation identifier
   */
  public BusinessErrorException(OmegaBusinessError error, String correlationId) {
    super(ErrorContext.business(error, correlationId));
  }
}
