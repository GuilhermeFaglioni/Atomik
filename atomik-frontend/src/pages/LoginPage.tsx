import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { ApiError } from "../lib/api";
import { loginUser } from "../services/auth-api";

type LoginFormState = {
  email: string;
  password: string;
};

const initialState: LoginFormState = {
  email: "",
  password: "",
};

export function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();
  const [form, setForm] = useState(initialState);
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const showCreatedBanner = new URLSearchParams(location.search).get("registered") === "1";
  const showExpiredBanner = new URLSearchParams(location.search).get("reason") === "session-expired";

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!form.email.trim() || !form.password.trim()) {
      setError("Informe e-mail e senha.");
      return;
    }

    setIsSubmitting(true);
    setError("");

    try {
      const response = await loginUser(form);
      login(response);
      navigate("/app", { replace: true });
    } catch (err) {
      if (err instanceof ApiError) {
        if (err.status === 401) {
          setError("Credenciais inválidas. Revise e tente outra vez.");
        } else if (err.status === 400) {
          setError("Requisição inválida. Revise formato do e-mail e senha.");
        } else {
          setError(err.message);
        }
      } else {
        setError("Falha inesperada ao autenticar.");
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="auth-shell auth-shell-login">
      <div className="grain-overlay" />
      <div className="orb orb-left" />
      <div className="orb orb-right" />

      <section className="auth-stage auth-stage-login">
        <section className="auth-form-card auth-form-card-login">
          <div className="auth-card-header">
            <Link className="auth-backlink" to="/">
              Voltar para landing
            </Link>
            <span className="preview-chip">Login</span>
          </div>

          <div className="auth-header-copy">
            <span className="eyebrow">Etapa 4 ativa</span>
            <h1>Entrar, guardar sessão, abrir área protegida.</h1>
            <p>
              Login já consome Atomik API, grava tokens no navegador e manda usuário para
              shell interna.
            </p>
          </div>

          <form className="auth-form" onSubmit={handleSubmit}>
            <label className="field">
              <span>E-mail</span>
              <input
                autoComplete="email"
                name="email"
                type="email"
                placeholder="nome@dominio.com"
                value={form.email}
                onChange={(event) => setForm((current) => ({ ...current, email: event.target.value }))}
              />
            </label>

            <label className="field">
              <span>Senha</span>
              <input
                autoComplete="current-password"
                name="password"
                type="password"
                placeholder="Sua senha"
                value={form.password}
                onChange={(event) => setForm((current) => ({ ...current, password: event.target.value }))}
              />
            </label>

            {showCreatedBanner ? (
              <p className="form-feedback form-feedback-success">
                Conta criada. Agora entre no portal para abrir sessão.
              </p>
            ) : null}

            {showExpiredBanner ? (
              <p className="form-feedback form-feedback-warning">
                Sessão expirada ou inválida. Entre novamente.
              </p>
            ) : null}

            {error ? <p className="form-feedback form-feedback-error">{error}</p> : null}

            <button className="button button-primary button-block" disabled={isSubmitting} type="submit">
              {isSubmitting ? "Entrando..." : "Entrar"}
            </button>
          </form>

          <footer className="auth-footer">
            <span>Ainda sem conta?</span>
            <Link to="/register">Criar conta</Link>
          </footer>
        </section>

        <aside className="auth-status-panel">
          <article className="status-rune-card">
            <span className="auth-note-label">Guarda local</span>
            <strong>`accessToken`, `refreshToken`, `tokenType`, `expiresIn`</strong>
          </article>
          <article className="status-rune-card">
            <span className="auth-note-label">Saída esperada</span>
            <strong>Rota `/app` protegida responde ao estado real da sessão.</strong>
          </article>
          <article className="status-rune-card accent">
            <span className="auth-note-label">Slice atual</span>
            <strong>Autenticação viva. Dashboard real entra depois.</strong>
          </article>
        </aside>
      </section>
    </main>
  );
}
