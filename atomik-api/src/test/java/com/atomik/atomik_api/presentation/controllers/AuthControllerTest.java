package com.atomik.atomik_api.presentation.controllers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.atomik.atomik_api.application.dto.AuthResponse;
import com.atomik.atomik_api.application.dto.UserCreatedResponse;
import com.atomik.atomik_api.application.usecases.AuthenticateUserUseCase;
import com.atomik.atomik_api.application.usecases.LogoutUseCase;
import com.atomik.atomik_api.application.usecases.RefreshAuthTokenUseCase;
import com.atomik.atomik_api.application.usecases.RegisterUserUseCase;
import com.atomik.atomik_api.domain.exception.EmailAlreadyExistsException;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.service.TokenService;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AuthenticateUserUseCase authenticateUserUseCase;

        @MockBean
        private RegisterUserUseCase registerUserUseCase;

        @MockBean
        private RefreshAuthTokenUseCase refreshAuthTokenUseCase;

        @MockBean
        private LogoutUseCase logoutUseCase;

        @MockBean
        private TokenService tokenService;

        @Test
        @DisplayName("Should register user successfully with 201 Created")
        void shouldRegisterUserSuccessfully() throws Exception {
                var response = new UserCreatedResponse("1", "BRL");
                when(registerUserUseCase.execute(anyString(), anyString(), anyString(), anyString()))
                                .thenReturn(response);

                String json = """
                                {
                                    "name": "Test User",
                                    "email": "test@example.com",
                                    "password": "password123",
                                    "preferredCurrency": "BRL"
                                }
                                """;

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value("1"))
                                .andExpect(jsonPath("$.preferredCurrency").value("BRL"));
        }

        @Test
        @DisplayName("Should return 400 when registration email is invalid")
        void shouldReturn400WhenRegistrationEmailIsInvalid() throws Exception {
                String json = """
                                {
                                    "name": "Test User",
                                    "email": "invalid-email",
                                    "password": "password123",
                                    "preferredCurrency": "BRL"
                                }
                                """;

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 409 when email already exists")
        void shouldReturn409WhenEmailAlreadyExists() throws Exception {
                when(registerUserUseCase.execute(anyString(), anyString(), anyString(), anyString()))
                                .thenThrow(new EmailAlreadyExistsException("Email already exists"));

                String json = """
                                {
                                    "name": "Test User",
                                    "email": "exists@example.com",
                                    "password": "password123",
                                    "preferredCurrency": "BRL"
                                }
                                """;

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should login successfully with 200 OK")
        void shouldLoginSuccessfully() throws Exception {
                var response = new AuthResponse("access-token", "refresh-token", "Bearer", 7200L);
                when(authenticateUserUseCase.execute(anyString(), anyString())).thenReturn(response);

                String json = """
                                {
                                    "email": "test@example.com",
                                    "password": "password123"
                                }
                                """;

                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken").value("access-token"));
        }

        @Test
        @DisplayName("Should return 401 when login credentials are invalid")
        void shouldReturn401WhenLoginCredentialsAreInvalid() throws Exception {
                when(authenticateUserUseCase.execute(anyString(), anyString()))
                                .thenThrow(new UnauthorizedException("Invalid credentials"));

                String json = """
                                {
                                    "email": "wrong@example.com",
                                    "password": "wrong"
                                }
                                """;

                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should refresh token successfully with 200 OK")
        void shouldRefreshTokenSuccessfully() throws Exception {
                var response = new AuthResponse("new-access-token", "new-refresh-token", "Bearer", 7200L);
                when(refreshAuthTokenUseCase.execute(anyString())).thenReturn(response);

                String json = """
                                {
                                    "refreshToken": "valid-refresh-token"
                                }
                                """;

                mockMvc.perform(post("/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));
        }

        @Test
        @DisplayName("Should logout successfully with 204 No Content")
        void shouldLogoutSuccessfully() throws Exception {
                String json = """
                                {
                                    "refreshToken": "valid-refresh-token"
                                }
                                """;

                mockMvc.perform(post("/auth/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isNoContent());
        }
}
