package com.eurobank.omega.error.contract;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Validates immutable error metadata before an exception superclass is initialised.
 *
 * @param error typed catalogue entry
 * @param correlationId unchanged request correlation identifier
 * @param httpStatus catalogue-permitted status
 */
public record ErrorContext(OmegaBusinessError error, String correlationId, int httpStatus)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  /**
   * Validates error metadata without creating a partially initialised exception.
   *
   * @param error typed catalogue entry
   * @param correlationId unchanged single-line identifier
   * @param httpStatus catalogue-permitted status
   */
  public ErrorContext {
    Objects.requireNonNull(error, "error");
    Objects.requireNonNull(correlationId, "correlationId");
    if (correlationId.isBlank() || correlationId.contains("\r") || correlationId.contains("\n")) {
      throw new IllegalArgumentException(
          "A nonblank single-line correlation identifier is required");
    }
    if (!error.permitsHttpStatus(httpStatus)) {
      throw new IllegalArgumentException("Status is not permitted by the catalogue entry");
    }
  }

  /**
   * Creates context using the catalogue default status.
   *
   * @param error typed catalogue entry
   * @param correlationId unchanged request identifier
   * @return validated immutable context
   */
  public static ErrorContext forDefaultStatus(OmegaBusinessError error, String correlationId) {
    Objects.requireNonNull(error, "error");
    return new ErrorContext(error, correlationId, error.getHttpStatus());
  }

  static ErrorContext business(OmegaBusinessError error, String correlationId) {
    Objects.requireNonNull(error, "error");
    if (error.isGatewayOwned() || error == OmegaBusinessError.PLATFORM_DEPENDENCY_UNAVAILABLE) {
      throw new IllegalArgumentException(
          "Use the gateway or technical exception subtree for this entry");
    }
    return forDefaultStatus(error, correlationId);
  }
}
