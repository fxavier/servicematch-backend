package com.xavier.servicematchbackend.identityaccess.infra.web;

import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rbac")
public class RbacController {

    @GetMapping("/client")
    @PreAuthorize("hasRole('CLIENT')")
    public Map<String, String> client() {
        return Map.of("role", "CLIENT");
    }

    @GetMapping("/provider")
    @PreAuthorize("hasRole('PROVIDER')")
    public Map<String, String> provider() {
        return Map.of("role", "PROVIDER");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> admin() {
        return Map.of("role", "ADMIN");
    }
}
