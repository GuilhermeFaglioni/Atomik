import { apiRequest } from "../lib/api";
import type { AuthResponse, LoginPayload, RegisterPayload } from "../types/auth";

export function registerUser(payload: RegisterPayload) {
  return apiRequest("/auth/register", {
    method: "POST",
    body: payload,
  });
}

export function loginUser(payload: LoginPayload) {
  return apiRequest<AuthResponse>("/auth/login", {
    method: "POST",
    body: payload,
  });
}

export function logoutUser(refreshToken: string) {
  return apiRequest("/auth/logout", {
    method: "POST",
    body: { refreshToken },
  });
}
