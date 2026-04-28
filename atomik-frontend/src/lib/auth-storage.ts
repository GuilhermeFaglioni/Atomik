import type { AuthResponse, SessionRecord } from "../types/auth";

const STORAGE_KEY = "atomik.session";

function isBrowser() {
  return typeof window !== "undefined";
}

export function saveSession(session: AuthResponse) {
  if (!isBrowser()) {
    return;
  }

  const payload = decodeJwtPayload(session.accessToken);
  const userId = typeof payload?.sub === "string" ? payload.sub : "";
  const email = typeof payload?.email === "string" ? payload.email : null;

  if (!userId) {
    clearSession();
    throw new Error("Access token sem subject utilizável.");
  }

  const record: SessionRecord = {
    ...session,
    expiresAt: Date.now() + session.expiresIn * 1000,
    userId,
    email,
  };

  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(record));
}

export function getSession() {
  if (!isBrowser()) {
    return null;
  }

  const raw = window.localStorage.getItem(STORAGE_KEY);
  if (!raw) {
    return null;
  }

  try {
    const parsed = JSON.parse(raw) as SessionRecord;
    if (!parsed.accessToken || !parsed.refreshToken || parsed.expiresAt <= Date.now()) {
      clearSession();
      return null;
    }

    return parsed;
  } catch {
    clearSession();
    return null;
  }
}

export function clearSession() {
  if (!isBrowser()) {
    return;
  }

  window.localStorage.removeItem(STORAGE_KEY);
}

export function isAuthenticated() {
  return getSession() !== null;
}

function decodeJwtPayload(token: string) {
  try {
    const payload = token.split(".")[1];
    if (!payload) {
      return null;
    }

    const normalized = payload.replace(/-/g, "+").replace(/_/g, "/");
    const padding = (4 - (normalized.length % 4)) % 4;
    const decoded = window.atob(normalized.padEnd(normalized.length + padding, "="));
    return JSON.parse(decoded) as { sub?: string; email?: string };
  } catch {
    return null;
  }
}
