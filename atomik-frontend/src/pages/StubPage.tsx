import { Link } from "react-router-dom";

type StubPageProps = {
  eyebrow: string;
  title: string;
  description: string;
  actionLabel: string;
};

export function StubPage({
  eyebrow,
  title,
  description,
  actionLabel,
}: StubPageProps) {
  return (
    <main className="stub-shell">
      <div className="grain-overlay" />
      <section className="stub-card">
        <span className="eyebrow">{eyebrow}</span>
        <h1>{title}</h1>
        <p>{description}</p>
        <Link className="button button-primary" to="/">
          {actionLabel}
        </Link>
      </section>
    </main>
  );
}
