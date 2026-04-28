import {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react";
import { clearSession, getSession, saveSession } from "../lib/auth-storage";
import { logoutUser } from "../services/auth-api";
import type { AuthResponse, SessionRecord } from "../types/auth";

type AuthContextValue = {
  session: SessionRecord | null;
  isAuthenticated: boolean;
  login: (response: AuthResponse) => void;
  logout: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<SessionRecord | null>(() => getSession());

  useEffect(() => {
    setSession(getSession());
  }, []);

  function handleLogin(response: AuthResponse) {
    saveSession(response);
    setSession(getSession());
  }

  async function handleLogout() {
    const currentSession = getSession();

    try {
      if (currentSession?.refreshToken) {
        await logoutUser(currentSession.refreshToken);
      }
    } catch {
      // Best effort logout. Local session must still die.
    } finally {
      clearSession();
      setSession(null);
    }
  }

  return (
    <AuthContext.Provider
      value={{
        session,
        isAuthenticated: session !== null,
        login: handleLogin,
        logout: handleLogout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must run inside AuthProvider");
  }

  return context;
}
