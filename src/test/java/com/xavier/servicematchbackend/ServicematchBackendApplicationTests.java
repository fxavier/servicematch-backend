package com.xavier.servicematchbackend;

import com.xavier.servicematchbackend.support.PostgresTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ServicematchBackendApplicationTests extends PostgresTestContainer {

    @Test
    void contextLoads() {
    }

}
