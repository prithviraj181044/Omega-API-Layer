package com.eurobank.omega.api.testsupport;

import java.util.Objects;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

/** Creates a MongoDB replica-set fixture without selecting or pulling an unapproved image. */
public final class MongoReplicaSetSupport {
  private MongoReplicaSetSupport() {}

  /**
   * Creates an unstarted MongoDB container using the caller's explicitly approved image.
   *
   * @param approvedImage approved Testcontainers-compatible image reference
   * @return an unstarted replica-set-capable container; caller owns start and cleanup
   */
  public static MongoDBContainer create(String approvedImage) {
    Objects.requireNonNull(approvedImage, "approvedImage");
    if (approvedImage.isBlank()) {
      throw new IllegalArgumentException("An explicitly approved MongoDB image is required");
    }
    return new MongoDBContainer(DockerImageName.parse(approvedImage));
  }
}
