package com.eurobank.omega.error.contract;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

/** Checks the plain shared contract boundary and checked recoverable hierarchy. */
class ErrorArchitectureTest {
  private final JavaClasses production =
      new ClassFileImporter()
          .withImportOption(new ImportOption.DoNotIncludeTests())
          .importPackages("com.eurobank.omega.error");

  @Test
  void keepsTheLibraryFreeOfApplicationFrameworks() {
    noClasses()
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("org.springframework..", "jakarta..", "com.mongodb..")
        .check(production);
  }

  @Test
  void keepsRecoverableExceptionsChecked() {
    classes()
        .that()
        .areAssignableTo(OmegaException.class)
        .should()
        .notBeAssignableTo(RuntimeException.class)
        .check(production);
  }

  @Test
  void exposesOnlyTheSharedContractLayer() {
    classes().should().resideInAPackage("..contract..").check(production);
  }
}
