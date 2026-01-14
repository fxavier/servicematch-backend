package com.xavier.servicematchbackend.identityaccess.infra.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.xavier.servicematchbackend.support.PostgresTestContainer;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RbacSecurityTests extends PostgresTestContainer {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void clientEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/rbac/client"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void clientEndpointAllowsClientRole() throws Exception {
        mockMvc.perform(get("/rbac/client")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("roles", List.of("CLIENT")))
                                .authorities(new SimpleGrantedAuthority("ROLE_CLIENT"))))
                .andExpect(status().isOk());
    }

    @Test
    void adminEndpointRejectsClientRole() throws Exception {
        mockMvc.perform(get("/rbac/admin")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("roles", List.of("CLIENT")))
                                .authorities(new SimpleGrantedAuthority("ROLE_CLIENT"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpointAllowsAdminRole() throws Exception {
        mockMvc.perform(get("/rbac/admin")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("roles", List.of("ADMIN")))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());
    }
}
