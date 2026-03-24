package com.atomik.atomik_api.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.UserResponseDTO;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.Email;
import com.atomik.atomik_api.domain.model.User;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class UpdateUserUseCase {
    private final UserRepository userRepository;

    public UpdateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO execute(String userId, String name, String email, String prefferedCurrency) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        var updatedUser = new User(user.getId(), name, new Email(email), user.getPasswordHash(), prefferedCurrency,
                user.getCreatedAt());
        updatedUser.validate();
        var savedUser = userRepository.update(updatedUser)
                .orElseThrow(() -> new RuntimeException("Error updating user"));
        return toResponseDTO(savedUser);
    }

    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(user.getId().toString(), user.getName(), user.getEmail().value(),
                user.getPreferredCurrency());
    }
}
