package com.eurobank.omega.error.contract;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/** Verifies unique typed catalogue entries and source-defined status variants. */
class OmegaBusinessErrorTest {
  @Test
  void keepsTheActiveBaselineUniqueWithoutProposedOrParkedCodes() {
    Set<String> wireCodes = new HashSet<>();
    for (OmegaBusinessError error : OmegaBusinessError.values()) {
      Assertions.assertTrue(wireCodes.add(error.getWireCode()));
      Assertions.assertEquals(error.name().replace('_', '-'), error.getWireCode());
      Assertions.assertFalse(
          error.name().startsWith("CONFIGURATION_") || error.name().startsWith("ADMIN_"));
    }
    Assertions.assertEquals(24, wireCodes.size());
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> OmegaBusinessError.valueOf("GW_AUTH_003"));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> OmegaBusinessError.valueOf("GW_DEP_001"));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> OmegaBusinessError.valueOf("GW_RTE_404"));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> OmegaBusinessError.valueOf("GW_RTE_410"));
  }

  @ParameterizedTest
  @EnumSource(OmegaBusinessError.class)
  void carriesValidImmutableMetadata(OmegaBusinessError error) {
    Assertions.assertEquals(Severity.ERROR, error.getSeverity());
    Assertions.assertNotNull(error.getCategory());
    Assertions.assertEquals(error.name().startsWith("GW_"), error.isGatewayOwned());
    Assertions.assertTrue(error.getHttpStatus() >= 400 && error.getHttpStatus() <= 599);
    Assertions.assertTrue(error.permitsHttpStatus(error.getHttpStatus()));
    Assertions.assertFalse(error.permitsHttpStatus(200));
    Assertions.assertThrows(
        UnsupportedOperationException.class, () -> error.getPermittedHttpStatuses().add(200));
  }

  @Test
  void limitsStatusVariantsToTheSourceCases() {
    Assertions.assertEquals(
        Set.of(400, 422), OmegaBusinessError.GW_SCH_001.getPermittedHttpStatuses());
    Assertions.assertEquals(
        Set.of(429, 503), OmegaBusinessError.GW_PRI_001.getPermittedHttpStatuses());
    Assertions.assertEquals(422, OmegaBusinessError.GW_SCH_001.getHttpStatus());
    Assertions.assertEquals(503, OmegaBusinessError.GW_PRI_001.getHttpStatus());
    Assertions.assertEquals(
        503, OmegaBusinessError.PLATFORM_DEPENDENCY_UNAVAILABLE.getHttpStatus());
    Assertions.assertEquals(504, OmegaBusinessError.GW_TMO_001.getHttpStatus());
  }
}
