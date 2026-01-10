package com.xavier.servicematchbackend.identityaccess.application.usecase;

import com.xavier.servicematchbackend.identityaccess.domain.entity.User;
import com.xavier.servicematchbackend.identityaccess.domain.event.UserRegistered;
import com.xavier.servicematchbackend.identityaccess.domain.exception.DuplicateEmailException;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.Email;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.PasswordHash;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.Role;
import com.xavier.servicematchbackend.identityaccess.infra.persistence.UserRepository;
import java.util.Set;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UserRegistrationService(UserRepository userRepository,
                                   ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public User register(Email email, PasswordHash passwordHash, Set<Role> roles) {
        User user = User.register(email, passwordHash, roles);
        if (userRepository.existsByEmail(user.email())) {
            throw new DuplicateEmailException(user.email());
        }
        User saved = userRepository.save(user);
        eventPublisher.publishEvent(new UserRegistered(saved.id(), saved.email(), saved.roles()));
        return saved;
    }
}
