import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { getCurrentUser } from "../services/user-api";
import type { CurrentUserResponse } from "../types/auth";

const placeholders = [
  {
    label: "Saldo total",
    title: "R$ 0,00",
    text: "Sem dados ainda. Card reservado para consolidado real de caixa e competência vencida.",
  },
  {
    label: "Receitas",
    title: "R$ 0,00",
    text: "Faixa pronta para entradas reais assim que painel autenticado ganhar leitura de transações.",
  },
  {
    label: "Despesas",
    title: "R$ 0,00",
    text: "Fluxo de auth já fecha ciclo. Próximo passo pluga custos, categorias e filtros.",
  },
  {
    label: "Orçamentos",
    title: "0 ativos",
    text: "Espaço reservado para limites mensais, consumo e alertas do backend.",
  },
];

const nextFeatures = [
  "Contas com saldo por moeda",
  "Categorias com cor e ícone",
  "Orçamentos por mês",
  "Transações com competência futura",
  "Recorrências com status e vencimento",
];

export function AppHomePage() {
  const navigate = useNavigate();
  const { logout, session } = useAuth();
  const [currentUser, setCurrentUser] = useState<CurrentUserResponse | null>(null);
  const [isLoadingUser, setIsLoadingUser] = useState(true);
  const [userLoadError, setUserLoadError] = useState("");

  useEffect(() => {
    let isMounted = true;

    async function loadCurrentUser() {
      if (!session?.userId) {
        setIsLoadingUser(false);
        return;
      }

      try {
        const response = await getCurrentUser(session.userId);
        if (isMounted) {
          setCurrentUser(response);
          setUserLoadError("");
        }
      } catch (error) {
        if (isMounted) {
          const message =
            error instanceof Error ? error.message : "Falha ao carregar dados da sessão.";
          setUserLoadError(message);
        }
      } finally {
        if (isMounted) {
          setIsLoadingUser(false);
        }
      }
    }

    void loadCurrentUser();

    return () => {
      isMounted = false;
    };
  }, [session?.userId]);

  async function handleLogout() {
    await logout();
    navigate("/login", { replace: true });
  }

  return (
    <main className="app-shell">
      <div className="grain-overlay" />

      <header className="app-topbar">
        <Link className="brandmark" to="/">
          <span className="brandmark-symbol">A</span>
          <span className="brandmark-text">
            <strong>Atomik</strong>
            <small>shell protegida inicial</small>
          </span>
        </Link>

        <div className="app-topbar-actions">
          <div className="session-pill">
            <span>sessão ativa</span>
            <strong>{session?.tokenType || "Bearer"}</strong>
          </div>
          <button className="button button-secondary" onClick={handleLogout} type="button">
            Sair
          </button>
        </div>
      </header>

      <section className="app-grid">
        <aside className="app-aside">
          <span className="eyebrow">Área interna</span>
          <h1>{isLoadingUser ? "Carregando identidade autenticada." : `Olá, ${currentUser?.name || "operador"}.`}</h1>
          <p>
            Rota protegida já responde ao estado real. Este painel existe para provar sessão,
            navegação interna e primeira leitura autenticada no backend.
          </p>

          {userLoadError ? <p className="form-feedback form-feedback-error">{userLoadError}</p> : null}

          <div className="app-aside-stack">
            <article className="auth-note-card">
              <span className="auth-note-label">Access token</span>
              <strong>{truncate(session?.accessToken)}</strong>
            </article>
            <article className="auth-note-card">
              <span className="auth-note-label">Usuário autenticado</span>
              <strong>{currentUser?.email || session?.email || "carregando..."}</strong>
            </article>
            <article className="auth-note-card">
              <span className="auth-note-label">Moeda preferida</span>
              <strong>{currentUser?.preferredCurrency || "n/d"}</strong>
            </article>
          </div>
        </aside>

        <section className="app-board">
          <div className="app-board-header">
            <span className="preview-chip">Home interna placeholder</span>
            <p>Auth pronto. Visual já mostra onde domínio financeiro real vai entrar.</p>
          </div>

          <div className="app-placeholder-grid">
            {placeholders.map((item) => (
              <article key={item.label} className="app-placeholder-card">
                <span className="board-label">{item.label}</span>
                <h2>{item.title}</h2>
                <p>{item.text}</p>
              </article>
            ))}
          </div>

          <section className="feature-future-panel">
            <div className="feature-future-header">
              <span className="eyebrow">Próximas features</span>
              <p>Sem dados ainda. Mas arquitetura visual e auth já não são mais placeholder frágil.</p>
            </div>

            <div className="feature-future-list">
              {nextFeatures.map((item, index) => (
                <article key={item} className="future-item">
                  <span className="feature-index">{String(index + 1).padStart(2, "0")}</span>
                  <strong>{item}</strong>
                </article>
              ))}
            </div>
          </section>
        </section>
      </section>
    </main>
  );
}

function truncate(value?: string) {
  if (!value) {
    return "n/d";
  }

  return `${value.slice(0, 16)}...${value.slice(-8)}`;
}
