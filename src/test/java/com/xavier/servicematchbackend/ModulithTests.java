package com.xavier.servicematchbackend;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModulithTests {

    @Test
    void verifiesModuleStructure() {
        ApplicationModules.of(ServicematchBackendApplication.class).verify();
    }
}
