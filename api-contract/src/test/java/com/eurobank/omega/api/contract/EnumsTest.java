package com.eurobank.omega.api.contract;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

/** Verifies the source-defined shared enum wire values without adding policy states. */
class EnumsTest {
  @Test
  void preservesConsumerTypes() {
    assertArrayEquals(
        new String[] {
          "CHANNEL", "APPLICATION", "SERVICE", "MCP_TOOL", "AGENT", "SYSTEM_INTEGRATION"
        },
        Arrays.stream(ConsumerType.values()).map(Enum::name).toArray(String[]::new));
    for (ConsumerType value : ConsumerType.values()) {
      assertEquals(value, ConsumerType.valueOf(value.name()));
    }
  }

  @Test
  void preservesHttpEndpointTypesOnly() {
    assertArrayEquals(
        new String[] {"REST", "ALTAMIRA_REST", "AS400_REST"},
        Arrays.stream(EndpointType.values()).map(Enum::name).toArray(String[]::new));
    for (EndpointType value : EndpointType.values()) {
      assertEquals(value, EndpointType.valueOf(value.name()));
    }
  }

  @Test
  void preservesApiLifecycleStates() {
    assertArrayEquals(
        new String[] {"ONBOARDING", "IN_SERVICE", "DEPRECATED", "RETIRED"},
        Arrays.stream(ApiLifecycleState.values()).map(Enum::name).toArray(String[]::new));
    for (ApiLifecycleState value : ApiLifecycleState.values()) {
      assertEquals(value, ApiLifecycleState.valueOf(value.name()));
    }
  }
}
