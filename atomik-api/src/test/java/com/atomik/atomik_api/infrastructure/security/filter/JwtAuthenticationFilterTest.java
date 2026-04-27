package com.atomik.atomik_api.infrastructure.security.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import com.atomik.atomik_api.domain.service.TokenService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private TokenService tokenService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should use user id from token subject as authenticated principal")
    void shouldUseUserIdFromTokenSubjectAsAuthenticatedPrincipal() throws Exception {
        String token = "valid-token";
        String userId = UUID.randomUUID().toString();
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokenService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        when(tokenService.validateToken(token)).thenReturn(true);
        when(tokenService.extractSubject(token)).thenReturn(userId);

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(userId, authentication.getName());
    }
}
