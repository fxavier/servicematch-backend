package com.xavier.servicematchbackend;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

@AnalyzeClasses(
        packages = "com.xavier.servicematchbackend",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class ArchitectureTests {

    @ArchTest
    static final ArchRule modulesShouldBeFreeOfCycles = slices()
            .matching("com.xavier.servicematchbackend.(*)..")
            .should().beFreeOfCycles();

    @Test
    void modulithDependenciesRespectAllowedOnes() {
        ApplicationModules.of(ServicematchBackendApplication.class).verify();
    }
}
