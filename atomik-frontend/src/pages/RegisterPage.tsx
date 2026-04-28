import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ApiError } from "../lib/api";
import { registerUser } from "../services/auth-api";

const currencyOptions = ["BRL", "USD", "EUR"];

type RegisterFormState = {
  name: string;
  email: string;
  password: string;
  preferredCurrency: string;
};

const initialState: RegisterFormState = {
  name: "",
  email: "",
  password: "",
  preferredCurrency: "BRL",
};

export function RegisterPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState(initialState);
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const validationError = validate(form);
    if (validationError) {
      setError(validationError);
      return;
    }

    setIsSubmitting(true);
    setError("");

    try {
      await registerUser(form);
      navigate("/login?registered=1", { replace: true });
    } catch (err) {
      if (err instanceof ApiError) {
        if (err.status === 409) {
          setError("E-mail já cadastrado. Entre no portal ou use outro endereço.");
        } else if (err.status === 400) {
          setError("Dados inválidos. Revise nome, e-mail, senha e moeda.");
        } else {
          setError(err.message);
        }
      } else {
        setError("Falha inesperada ao criar conta.");
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="auth-shell">
      <div className="grain-overlay" />
      <div className="orb orb-left" />
      <div className="orb orb-right" />

      <section className="auth-stage">
        <aside className="auth-editorial-card">
          <span className="eyebrow">Etapa 3 ativa</span>
          <h1>Abertura de conta com entrada limpa e backend real.</h1>
          <p>
            Este formulário já fala com Atomik API. Resposta `201` segue para login.
            Resposta `409` volta como erro visível. Sem teatro, sem mock.
          </p>

          <div className="auth-side-stack">
            <article className="auth-note-card">
              <span className="auth-note-label">Campos</span>
              <strong>Nome, e-mail, senha e moeda preferida</strong>
            </article>
            <article className="auth-note-card accent">
              <span className="auth-note-label">Saída</span>
              <strong>Conta criada. Próximo passo: entrar e abrir sessão.</strong>
            </article>
          </div>
        </aside>

        <section className="auth-form-card">
          <div className="auth-card-header">
            <Link className="auth-backlink" to="/">
              Voltar para landing
            </Link>
            <span className="preview-chip">Cadastro</span>
          </div>

          <form className="auth-form" onSubmit={handleSubmit}>
            <label className="field">
              <span>Nome</span>
              <input
                autoComplete="name"
                name="name"
                placeholder="Seu nome completo"
                value={form.name}
                onChange={(event) => setForm((current) => ({ ...current, name: event.target.value }))}
              />
            </label>

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
                autoComplete="new-password"
                name="password"
                type="password"
                placeholder="No mínimo 6 caracteres"
                value={form.password}
                onChange={(event) => setForm((current) => ({ ...current, password: event.target.value }))}
              />
            </label>

            <label className="field">
              <span>Moeda preferida</span>
              <select
                name="preferredCurrency"
                value={form.preferredCurrency}
                onChange={(event) =>
                  setForm((current) => ({ ...current, preferredCurrency: event.target.value }))
                }
              >
                {currencyOptions.map((currency) => (
                  <option key={currency} value={currency}>
                    {currency}
                  </option>
                ))}
              </select>
            </label>

            {error ? <p className="form-feedback form-feedback-error">{error}</p> : null}

            <button className="button button-primary button-block" disabled={isSubmitting} type="submit">
              {isSubmitting ? "Criando conta..." : "Criar conta"}
            </button>
          </form>

          <footer className="auth-footer">
            <span>Já tem acesso?</span>
            <Link to="/login">Entrar no portal</Link>
          </footer>
        </section>
      </section>
    </main>
  );
}

function validate(form: RegisterFormState) {
  if (!form.name.trim() || !form.email.trim() || !form.password.trim()) {
    return "Preencha todos campos obrigatórios.";
  }

  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    return "Informe e-mail válido.";
  }

  if (form.password.trim().length < 6) {
    return "Senha deve ter ao menos 6 caracteres.";
  }

  return "";
}
