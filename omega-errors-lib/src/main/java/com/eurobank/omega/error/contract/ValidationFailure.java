package com.eurobank.omega.error.contract;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Describes a trusted schema field and reason, never a rejected input value.
 *
 * @param field trusted schema field name or path, not input content
 * @param reason catalogue-backed validation reason
 */
public record ValidationFailure(String field, ValidationReason reason) implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  /**
   * Validates the field metadata at construction.
   *
   * @param field trusted schema field name or path
   * @param reason validation reason
   */
  public ValidationFailure {
    Objects.requireNonNull(field, "field");
    Objects.requireNonNull(reason, "reason");
    if (field.isBlank()) {
      throw new IllegalArgumentException("A trusted schema field is required");
    }
  }
}
