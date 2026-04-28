import { apiRequest } from "../lib/api";
import type { CurrentUserResponse } from "../types/auth";

export function getCurrentUser(userId: string) {
  return apiRequest<CurrentUserResponse>(`/users/${userId}`, {
    authenticated: true,
  });
}
