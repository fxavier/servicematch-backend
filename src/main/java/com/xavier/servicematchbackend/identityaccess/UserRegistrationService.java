package com.xavier.servicematchbackend.identityaccess;

import com.xavier.servicematchbackend.identityaccess.domain.DuplicateEmailException;
import com.xavier.servicematchbackend.identityaccess.domain.Email;
import com.xavier.servicematchbackend.identityaccess.domain.PasswordHash;
import com.xavier.servicematchbackend.identityaccess.domain.Role;
import com.xavier.servicematchbackend.identityaccess.domain.User;
import com.xavier.servicematchbackend.identityaccess.domain.UserRegistered;
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
        eventPublisher.publishEvent(new UserRegistered(saved.id(), saved.email()));
        return saved;
    }
}
