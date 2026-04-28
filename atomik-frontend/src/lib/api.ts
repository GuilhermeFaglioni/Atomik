import { clearSession, getSession } from "./auth-storage";

type ApiRequestOptions = {
  method?: "GET" | "POST" | "PUT" | "PATCH" | "DELETE";
  body?: unknown;
  authenticated?: boolean;
};

export class ApiError extends Error {
  status: number;

  constructor(message: string, status: number) {
    super(message);
    this.name = "ApiError";
    this.status = status;
  }
}

const API_BASE_URL = (import.meta.env.VITE_API_URL as string | undefined)?.trim() || "/api";

export async function apiRequest<T>(path: string, options: ApiRequestOptions = {}) {
  const headers = new Headers({
    "Content-Type": "application/json",
  });

  if (options.authenticated) {
    const session = getSession();
    if (!session?.accessToken) {
      handleUnauthorizedResponse();
      throw new ApiError("Sessão ausente. Entre novamente.", 401);
    }

    headers.set("Authorization", `Bearer ${session.accessToken}`);
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: options.method ?? "GET",
    headers,
    body: options.body ? JSON.stringify(options.body) : undefined,
  });

  if (!response.ok) {
    if (response.status === 401) {
      handleUnauthorizedResponse();
    }

    const message = await readErrorMessage(response);
    throw new ApiError(message, response.status);
  }

  if (response.status === 204) {
    return null as T;
  }

  return (await response.json()) as T;
}

async function readErrorMessage(response: Response) {
  try {
    const payload = (await response.json()) as { message?: string };
    return payload.message || "Falha na comunicação com Atomik API.";
  } catch {
    return "Falha na comunicação com Atomik API.";
  }
}

function handleUnauthorizedResponse() {
  clearSession();

  if (typeof window !== "undefined" && window.location.pathname !== "/login") {
    const nextUrl = new URL("/login", window.location.origin);
    nextUrl.searchParams.set("reason", "session-expired");
    window.location.assign(nextUrl.toString());
  }
}
