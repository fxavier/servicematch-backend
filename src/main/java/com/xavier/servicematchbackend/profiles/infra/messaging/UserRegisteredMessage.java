package com.xavier.servicematchbackend.profiles.infra.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserRegisteredMessage(String userId, String email, List<String> roles, String version) {
}
