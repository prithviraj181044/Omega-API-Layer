package com.eurobank.omega.api.testsupport;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Verifies that fixtures do not silently choose or start a default database image. */
class MongoReplicaSetSupportTest {
  @Test
  void rejectsMissingImage() {
    assertThrows(NullPointerException.class, () -> MongoReplicaSetSupport.create(null));
  }

  @Test
  void rejectsBlankImage() {
    assertThrows(IllegalArgumentException.class, () -> MongoReplicaSetSupport.create(" "));
  }
}
