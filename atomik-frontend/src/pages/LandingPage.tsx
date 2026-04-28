import { Link } from "react-router-dom";

const highlights = [
  {
    title: "Finanças diárias como ritual",
    text: "Fluxo de caixa, orçamentos e movimentações apresentados como painel operacional, não como mais um dashboard estéril.",
  },
  {
    title: "Estrutura real de verdade",
    text: "Contas, categorias, fluxos recorrentes e trilha de auditoria já existem na fundação do backend.",
  },
  {
    title: "Do atrito ao controle",
    text: "Um lugar para cadastrar, entrar e depois acessar área protegida assim que próximo slice chegar.",
  },
];

const previewMetrics = [
  { label: "Reserva atual", value: "R$ 42.800", tone: "up" },
  { label: "Saída planejada", value: "R$ 6.540", tone: "down" },
  { label: "Recorrências", value: "12 ativas", tone: "neutral" },
];

export function LandingPage() {
  return (
    <main className="landing-shell">
      <div className="grain-overlay" />
      <div className="orb orb-left" />
      <div className="orb orb-right" />

      <header className="topbar">
        <Link className="brandmark" to="/">
          <span className="brandmark-symbol">A</span>
          <span className="brandmark-text">
            <strong>Atomik</strong>
            <small>superfície de comando financeiro</small>
          </span>
        </Link>

        <nav className="topnav">
          <a href="#preview">Visão</a>
          <a href="#features">Estrutura</a>
          <Link to="/login">Entrar</Link>
        </nav>
      </header>

      <section className="hero-grid">
        <div className="hero-copy">
          <span className="eyebrow">Slice 01 em construção</span>
          <h1>
            Sistema financeiro com
            <span className="headline-cut"> peso editorial </span>
            e backend sólido.
          </h1>
          <p className="hero-description">
            Atomik começa com onboarding objetivo: landing pública, rota de
            cadastro, rota de login e caminho para shell protegida. Este
            primeiro slice do frontend transforma base já pronta do backend em
            algo que já pode ser visto e testado.
          </p>

          <div className="hero-actions">
            <Link className="button button-primary" to="/register">
              Criar conta
            </Link>
            <Link className="button button-secondary" to="/login">
              Entrar no portal
            </Link>
          </div>

          <div className="hero-ledger">
            <span>Backend pronto:</span>
            <ul>
              <li>Autenticação JWT com principal em userId</li>
              <li>Fluxo de refresh e logout</li>
              <li>Contas, orçamentos, categorias e transações</li>
            </ul>
          </div>
        </div>

        <aside className="hero-preview" id="preview">
          <div className="preview-frame">
            <div className="preview-header">
              <span className="preview-chip">estado de prévia</span>
              <span className="preview-date">27 ABR 2026</span>
            </div>

            <div className="preview-balance">
              <small>Painel operacional</small>
              <h2>Arquitetura financeira, não caos de planilha.</h2>
            </div>

            <div className="metric-ribbon">
              {previewMetrics.map((metric) => (
                <article key={metric.label} className={`metric-card ${metric.tone}`}>
                  <span>{metric.label}</span>
                  <strong>{metric.value}</strong>
                </article>
              ))}
            </div>

            <div className="preview-board">
              <article className="board-card tall">
                <span className="board-label">Pulso mensal</span>
                <div className="board-bars" aria-hidden="true">
                  <span style={{ height: "44%" }} />
                  <span style={{ height: "62%" }} />
                  <span style={{ height: "37%" }} />
                  <span style={{ height: "78%" }} />
                  <span style={{ height: "58%" }} />
                  <span style={{ height: "86%" }} />
                </div>
              </article>

              <article className="board-card">
                <span className="board-label">Estado do fluxo</span>
                <p>Contas, recorrências e trilha de auditoria já estão conectadas no backend.</p>
              </article>

              <article className="board-card accent">
                <span className="board-label">Próximo passo</span>
                <p>Conectar cadastro, login, guarda de token e depois liberar shell protegida.</p>
              </article>
            </div>
          </div>
        </aside>
      </section>

      <section className="feature-section" id="features">
        <div className="section-title">
          <span className="eyebrow">Por que este slice importa</span>
          <h2>Primeiro rosto público. Depois núcleo operacional.</h2>
        </div>

        <div className="feature-grid">
          {highlights.map((item, index) => (
            <article
              key={item.title}
              className="feature-card"
              style={{ animationDelay: `${index * 120}ms` }}
            >
              <span className="feature-index">0{index + 1}</span>
              <h3>{item.title}</h3>
              <p>{item.text}</p>
            </article>
          ))}
        </div>
      </section>
    </main>
  );
}
