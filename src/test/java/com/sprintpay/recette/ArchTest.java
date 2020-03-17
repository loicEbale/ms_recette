package com.sprintpay.recette;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {

        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.sprintpay.recette");

        noClasses()
            .that()
                .resideInAnyPackage("com.sprintpay.recette.service..")
            .or()
                .resideInAnyPackage("com.sprintpay.recette.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..com.sprintpay.recette.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses);
    }
}
