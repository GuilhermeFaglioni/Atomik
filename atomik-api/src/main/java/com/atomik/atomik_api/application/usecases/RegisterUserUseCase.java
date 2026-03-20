package com.atomik.atomik_api.application.usecases;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.UserCreatedResponse;
import com.atomik.atomik_api.domain.exception.EmailAlreadyExistsException;
import com.atomik.atomik_api.domain.model.User;
import com.atomik.atomik_api.domain.repository.UserRepository;
import com.atomik.atomik_api.domain.service.PasswordHasherService;

@Service
public class RegisterUserUseCase {
    private final UserRepository userRepository;
    private final PasswordHasherService passwordHasherService;

    public RegisterUserUseCase(UserRepository userRepository, PasswordHasherService passwordHasherService) {
        this.userRepository = userRepository;
        this.passwordHasherService = passwordHasherService;
    }

    public UserCreatedResponse execute(String name, String email, String password, String preferredCurrency) {
        var user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new EmailAlreadyExistsException("Email already taken");
        }

        var passwordHash = passwordHasherService.hashPassword(password);
        var newUser = User.createNewUser(name, email, passwordHash, preferredCurrency);
        userRepository.save(newUser);

        return toResponse(newUser);
    }

    private UserCreatedResponse toResponse(User user) {
        return new UserCreatedResponse(user.getId().toString(), user.getPreferredCurrency());
    }

}
