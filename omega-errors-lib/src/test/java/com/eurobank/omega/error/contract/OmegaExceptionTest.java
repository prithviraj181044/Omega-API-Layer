package com.eurobank.omega.error.contract;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Verifies checked exceptions, correlation preservation and type-based retry classification. */
class OmegaExceptionTest {
  private static final String CORRELATION_ID = "9b1e0b1a-5e9a-4e3e-9b1e-0b1a5e9a4e3e";

  @Test
  void keepsRecoverableErrorsChecked() {
    Assertions.assertTrue(Exception.class.isAssignableFrom(OmegaException.class));
    Assertions.assertFalse(RuntimeException.class.isAssignableFrom(OmegaException.class));
  }

  @Test
  void preservesCatalogueAndCorrelationWithoutCopyingCauseText() {
    Exception cause = new Exception("test-secret-not-for-a-client");
    TransportException error = new TransportException(CORRELATION_ID, cause);
    Assertions.assertEquals(OmegaBusinessError.PLATFORM_DEPENDENCY_UNAVAILABLE, error.getError());
    Assertions.assertEquals(CORRELATION_ID, error.getCorrelationId());
    Assertions.assertSame(cause, error.getCause());
    Assertions.assertEquals(error.getError().getWireCode(), error.getMessage());
    Assertions.assertFalse(error.getMessage().contains("test-secret"));
  }

  @Test
  void usesExceptionTypesForRetryDecisions() {
    Assertions.assertInstanceOf(
        OlTechnicalException.class, new TransportException(CORRELATION_ID, null));
    Assertions.assertTrue(new ServiceUnavailableException(CORRELATION_ID, null).isRetryable());
    Assertions.assertTrue(new TimeoutException(CORRELATION_ID, null).isRetryable());
    Assertions.assertEquals(504, new TimeoutException(CORRELATION_ID, null).getHttpStatus());
    BusinessErrorException business =
        new BusinessErrorException(
            OmegaBusinessError.ONBOARDING_VALIDATION_REQUIRED, CORRELATION_ID);
    Assertions.assertInstanceOf(OlBusinessDataException.class, business);
    Assertions.assertFalse(business.isRetryable());
    Assertions.assertEquals(422, business.getHttpStatus());
  }

  @Test
  void rejectsMissingOrUnsafeCorrelationAsProgrammingMisuse() {
    Assertions.assertThrows(NullPointerException.class, () -> new TransportException(null, null));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> new TransportException(" ", null));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> new TransportException("line\nnext", null));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> new TransportException("line\rnext", null));
  }

  @Test
  void rejectsTechnicalAndGatewayConstantsInTheBusinessSubtree() {
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () ->
            new BusinessErrorException(
                OmegaBusinessError.PLATFORM_DEPENDENCY_UNAVAILABLE, CORRELATION_ID));
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> new BusinessErrorException(OmegaBusinessError.GW_AUTH_001, CORRELATION_ID));
    Assertions.assertThrows(
        NullPointerException.class, () -> new BusinessErrorException(null, CORRELATION_ID));
  }

  @Test
  void permitsOnlyDocumentedEdgeStatusOverrides() {
    Assertions.assertEquals(
        400, new StatusVariant(OmegaBusinessError.GW_SCH_001, 400).getHttpStatus());
    Assertions.assertEquals(
        429, new StatusVariant(OmegaBusinessError.GW_PRI_001, 429).getHttpStatus());
    Assertions.assertFalse(new StatusVariant(OmegaBusinessError.GW_PRI_001, 429).isRetryable());
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> new StatusVariant(OmegaBusinessError.GW_AUTH_001, 500));
  }

  private static final class StatusVariant extends OmegaException {
    @Serial private static final long serialVersionUID = 1L;

    private StatusVariant(OmegaBusinessError error, int status) {
      super(new ErrorContext(error, CORRELATION_ID, status), null);
    }

    @Override
    public boolean isRetryable() {
      return false;
    }
  }

  @Test
  void validationFailuresAreImmutableAndContainNoRejectedValueField() {
    List<ValidationFailure> failures = new ArrayList<>();
    failures.add(new ValidationFailure("apiId", ValidationReason.MISSING_REQUIRED_FIELD));
    DataValidationException error =
        new DataValidationException(
            OmegaBusinessError.ONBOARDING_SPECIFICATION_INVALID, CORRELATION_ID, failures);
    failures.clear();
    Assertions.assertEquals(1, error.getErrors().size());
    Assertions.assertEquals("apiId", error.getErrors().getFirst().field());
    Assertions.assertEquals(
        ValidationReason.MISSING_REQUIRED_FIELD, error.getErrors().getFirst().reason());
    Assertions.assertFalse(error.isRetryable());
    Assertions.assertThrows(UnsupportedOperationException.class, () -> error.getErrors().clear());
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> new ValidationFailure(" ", ValidationReason.TYPE_MISMATCH));
    Assertions.assertThrows(NullPointerException.class, () -> new ValidationFailure("field", null));
  }
}
