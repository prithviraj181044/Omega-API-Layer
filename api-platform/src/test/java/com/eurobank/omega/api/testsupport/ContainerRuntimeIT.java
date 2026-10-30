package com.eurobank.omega.api.testsupport;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.github.dockerjava.api.DockerClient;
import java.io.IOException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.dockerclient.DockerClientProviderStrategy;

/** Checks the compatible API only; deliberately does not initialise the resource reaper. */
@Tag("container")
class ContainerRuntimeIT {
  @Test
  void reachesConfiguredContainerApi() throws IOException {
    try (DockerClient client =
        DockerClientProviderStrategy.getClientForConfig(
            DockerClientFactory.instance().getTransportConfig())) {
      String version = client.infoCmd().exec().getServerVersion();
      assertFalse(version.isBlank(), "Container API must return its server version");
    }
  }
}
