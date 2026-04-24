package com.atomik.atomik_api.presentation.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.atomik.atomik_api.domain.exception.UnauthorizedException;

@Component
public class AuthenticatedUserService {

    public String getAuthenticatedUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new UnauthorizedException("Authentication required");
        }

        return authentication.getName();
    }

    public String requireCurrentUser(Authentication authentication, String requestedUserId) {
        String authenticatedUserId = getAuthenticatedUserId(authentication);

        if (requestedUserId != null && !requestedUserId.isBlank() && !authenticatedUserId.equals(requestedUserId)) {
            throw new UnauthorizedException("You do not have permission to access this resource");
        }

        return authenticatedUserId;
    }
}
