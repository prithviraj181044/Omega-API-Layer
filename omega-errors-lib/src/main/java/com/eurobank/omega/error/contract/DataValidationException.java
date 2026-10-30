package com.eurobank.omega.error.contract;

import java.io.Serial;
import java.util.List;

/** Carries immutable field failures without copying rejected input into the exception. */
public final class DataValidationException extends OlBusinessDataException {
  @Serial private static final long serialVersionUID = 1L;
  private final List<ValidationFailure> errors;

  /**
   * Creates a validation failure from typed metadata.
   *
   * @param error non-gateway catalogue entry
   * @param correlationId existing request correlation identifier
   * @param errors field metadata without rejected values
   */
  public DataValidationException(
      OmegaBusinessError error, String correlationId, List<ValidationFailure> errors) {
    super(ErrorContext.business(error, correlationId));
    this.errors = List.copyOf(errors);
  }

  /**
   * Returns immutable safe field metadata.
   *
   * @return the field failures
   */
  public List<ValidationFailure> getErrors() {
    return errors;
  }
}
