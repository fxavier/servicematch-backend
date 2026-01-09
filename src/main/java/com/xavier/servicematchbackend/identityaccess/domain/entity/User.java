package com.xavier.servicematchbackend.identityaccess.domain.entity;

import com.xavier.servicematchbackend.identityaccess.domain.valueobject.AccountStatus;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.Email;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.PasswordHash;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.Role;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.UserId;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email")
)
public class User {

    @EmbeddedId
    private UserId id;

    @Embedded
    private Email email;

    @Embedded
    private PasswordHash passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false, length = 30)
    private AccountStatus accountStatus;

    protected User() {
    }

    private User(UserId id,
                 Email email,
                 PasswordHash passwordHash,
                 Set<Role> roles,
                 AccountStatus accountStatus) {
        this.id = requireNonNull(id, "id must not be null");
        this.email = requireNonNull(email, "email must not be null");
        this.passwordHash = requireNonNull(passwordHash, "passwordHash must not be null");
        this.roles = normalizeRoles(roles);
        this.accountStatus = requireNonNull(accountStatus, "accountStatus must not be null");
    }

    public static User register(Email email, PasswordHash passwordHash, Set<Role> roles) {
        return new User(UserId.newId(), email, passwordHash, roles, AccountStatus.ACTIVE);
    }

    public UserId id() {
        return id;
    }

    public Email email() {
        return email;
    }

    public PasswordHash passwordHash() {
        return passwordHash;
    }

    public Set<Role> roles() {
        return Collections.unmodifiableSet(roles);
    }

    public AccountStatus accountStatus() {
        return accountStatus;
    }

    private static Set<Role> normalizeRoles(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("roles must not be empty");
        }
        for (Role role : roles) {
            if (role == null) {
                throw new IllegalArgumentException("roles must not contain nulls");
            }
        }
        return EnumSet.copyOf(roles);
    }

    private static <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
