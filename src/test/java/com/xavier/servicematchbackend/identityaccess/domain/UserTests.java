package com.xavier.servicematchbackend.identityaccess.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import org.junit.jupiter.api.Test;

class UserTests {

    @Test
    void emailIsNormalized() {
        Email email = Email.of("  USER@Example.com ");

        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @Test
    void emailRejectsInvalidFormat() {
        assertThatThrownBy(() -> Email.of("invalid-email"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void passwordHashRejectsBlankValues() {
        assertThatThrownBy(() -> PasswordHash.of(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void userRejectsEmptyRoles() {
        Email email = Email.of("user@example.com");
        PasswordHash passwordHash = PasswordHash.of("hash");

        assertThatThrownBy(() -> User.register(email, passwordHash, Set.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void userDefaultsToActiveStatus() {
        Email email = Email.of("user@example.com");
        PasswordHash passwordHash = PasswordHash.of("hash");

        User user = User.register(email, passwordHash, Set.of(Role.USER));

        assertThat(user.accountStatus()).isEqualTo(AccountStatus.ACTIVE);
    }
}
