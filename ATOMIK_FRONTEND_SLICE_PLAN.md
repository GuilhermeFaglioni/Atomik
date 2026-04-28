# Plano Frontend Slice 1

## Objetivo

Entregar frontend mínimo testável ponta a ponta para validar experiência inicial e integração real com backend já pronto.

Escopo deste slice:

1. landing page
2. cadastro
3. login
4. guarda de token
5. rota protegida `/app`
6. home interna com placeholders

---

## Resultado Esperado

Ao final:

- visitante abre homepage
- visitante navega para cadastro
- cadastro chama backend real `POST /auth/register`
- usuário navega para login
- login chama backend real `POST /auth/login`
- frontend salva `accessToken` e `refreshToken`
- frontend libera acesso a `/app`
- usuário deslogado não entra em `/app`
- `/app` mostra shell interna e blocos vazios/mockados

---

## Premissas

- backend `atomik-api` já expõe:
  - `POST /auth/register`
  - `POST /auth/login`
  - `POST /auth/refresh`
  - `POST /auth/logout`
- JWT já usa `userId` como principal
- não faz parte deste slice:
  - dashboard com dados reais
  - analytics
  - sync offline
  - CRUDs completos no frontend

---

## Arquitetura Recomendada

Frontend SPA simples.

Peças mínimas:

- roteamento
- cliente HTTP
- camada de auth
- storage de sessão
- guards de rota
- layout público
- layout autenticado

Estrutura sugerida:

```text
frontend/
  src/
    app/
    pages/
    components/
    services/
    hooks/
    context/
    routes/
    types/
    utils/
```

---

## Etapa 1. Bootstrap do Frontend

### Objetivo

Subir aplicação frontend com base limpa e pronta para autenticação.

### Ações

1. Escolher stack
   - recomendado: React + Vite + TypeScript

2. Criar base do projeto
   - roteamento
   - CSS global
   - aliases se necessário

3. Configurar variável de ambiente
   - `VITE_API_URL=http://localhost:8080`

4. Criar estrutura de pastas
   - `pages`
   - `components`
   - `services`
   - `routes`
   - `context`

### Critério de aceite

- frontend sobe local
- homepage simples renderiza
- URL da API vem de env

---

## Etapa 2. Landing Page

### Objetivo

Criar entrada pública do produto.

### Conteúdo mínimo

- hero com proposta do produto
- CTA `Criar conta`
- CTA `Entrar`
- seção com 3 blocos de benefício
- preview visual com cards placeholders

### Ações

1. Criar página `/`
2. Criar header público
3. Criar botões para `/register` e `/login`
4. Criar seção preview com mock:
   - saldo total
   - gastos do mês
   - metas/orçamentos

### Critério de aceite

- `/` navegável
- links públicos funcionam
- layout responsivo básico

---

## Etapa 3. Cadastro

### Objetivo

Cadastrar usuário real no backend.

### Campos

- nome
- email
- senha
- moeda preferida

### Integração

- `POST /auth/register`

### Ações

1. Criar página `/register`
2. Criar form controlado
3. Adicionar validação básica client-side
   - campos obrigatórios
   - email válido
   - senha mínima
4. Criar service de auth
5. Tratar respostas:
   - `201` sucesso
   - `409` email já existe
   - `400` payload inválido
6. Em sucesso:
   - redirecionar para `/login`
   - ou exibir CTA para entrar

### Critério de aceite

- cadastro válido cria usuário
- erro de email duplicado aparece bem
- fluxo não quebra com erro HTTP

---

## Etapa 4. Login

### Objetivo

Autenticar usuário e abrir sessão no frontend.

### Campos

- email
- senha

### Integração

- `POST /auth/login`

### Ações

1. Criar página `/login`
2. Criar form controlado
3. Consumir endpoint de login
4. Tratar respostas:
   - `200` sucesso
   - `401` credenciais inválidas
   - `400` request inválido
5. Salvar:
   - `accessToken`
   - `refreshToken`
   - metadados mínimos de sessão
6. Redirecionar para `/app`

### Critério de aceite

- login válido entra
- login inválido mostra erro
- usuário autenticado chega em `/app`

---

## Etapa 5. Guarda de Token

### Objetivo

Persistir sessão de forma simples e previsível.

### Estratégia inicial

Usar `localStorage` ou `sessionStorage`.

Recomendação para este slice:

- `localStorage` para rapidez de teste

### Dados a guardar

- `accessToken`
- `refreshToken`
- `tokenType`
- `expiresIn`

### Ações

1. Criar `AuthStorageService`
2. Criar helpers:
   - `saveSession`
   - `getSession`
   - `clearSession`
   - `isAuthenticated`
3. Criar `AuthContext` ou store simples
4. Hidratar sessão ao carregar app

### Critério de aceite

- recarregar página mantém sessão
- logout limpa sessão
- rota protegida responde ao estado real

---

## Etapa 6. Rota Protegida `/app`

### Objetivo

Impedir acesso à área interna sem autenticação.

### Ações

1. Criar `ProtectedRoute`
2. Se sem sessão:
   - redirecionar para `/login`
3. Se com sessão:
   - renderizar children
4. Proteger `/app`

### Critério de aceite

- usuário sem token não entra em `/app`
- usuário com token entra
- refresh de página mantém acesso enquanto sessão existir

---

## Etapa 7. Home Interna com Placeholders

### Objetivo

Entregar área autenticada navegável, mesmo sem dados reais.

### Conteúdo mínimo

- header interno
- botão logout
- saudação
- cards placeholders:
  - saldo total
  - receitas
  - despesas
  - orçamentos
- bloco `Próximas features`

### Ações

1. Criar layout `/app`
2. Criar header autenticado
3. Criar botão logout
   - limpar storage
   - opcional chamar `POST /auth/logout`
4. Criar dashboard vazio/mockado
5. Exibir estado `sem dados ainda`

### Critério de aceite

- `/app` renderiza shell coerente
- logout volta usuário para `/login`
- placeholders deixam claro que fluxo auth já está pronto

---

## Etapa 8. Cliente HTTP e Interceptação

### Objetivo

Preparar integração futura sem retrabalho.

### Ações

1. Criar client HTTP central
2. Injetar `Authorization: Bearer <token>`
3. Preparar hook para refresh futuro
4. Em `401`:
   - opção inicial simples: limpar sessão e redirecionar login
   - opção seguinte: chamar `/auth/refresh`

### Critério de aceite

- requisições autenticadas usam token salvo
- erro auth não deixa app em estado quebrado

---

## Etapa 9. Logout

### Objetivo

Encerrar sessão com comportamento previsível.

### Ações

1. Chamar `POST /auth/logout` com `refreshToken`
2. Mesmo se request falhar:
   - limpar sessão local
   - redirecionar para `/login`

### Critério de aceite

- logout sempre remove acesso local
- fluxo backend/local fecha sessão sem travar interface

---

## Sequência Recomendada de Implementação

1. Bootstrap frontend
2. Landing page
3. Cliente HTTP
4. Cadastro
5. Login
6. Storage/auth context
7. Protected route
8. `/app` placeholder
9. Logout

---

## Componentes Sugeridos

- `PublicHeader`
- `HeroSection`
- `FeatureCards`
- `AuthForm`
- `InputField`
- `ProtectedRoute`
- `AppLayout`
- `DashboardPlaceholderCard`
- `EmptyStatePanel`

---

## Serviços Sugeridos

- `authApi.register(data)`
- `authApi.login(data)`
- `authApi.refresh(refreshToken)`
- `authApi.logout(refreshToken)`
- `authStorage.saveSession(session)`
- `authStorage.getSession()`
- `authStorage.clearSession()`

---

## Tipos Sugeridos

```ts
type RegisterPayload = {
  name: string;
  email: string;
  password: string;
  preferredCurrency?: string;
};

type LoginPayload = {
  email: string;
  password: string;
};

type AuthSession = {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
};
```

---

## Riscos

- CORS pode bloquear frontend se backend não estiver configurado
- ausência de refresh automático pode expirar sessão em uso prolongado
- backend usa auth real; frontend deve tratar `401/409/400` corretamente

---

## Dependências de Backend

Validar antes de iniciar frontend:

- backend rodando local
- CORS liberado para origem frontend
- endpoints `/auth/register`, `/auth/login`, `/auth/logout`, `/auth/refresh` acessíveis

---

## Checklist de Aceite Final

- [ ] `/` renderiza landing page
- [ ] `/register` cadastra usuário real
- [ ] `/login` autentica usuário real
- [ ] tokens ficam guardados localmente
- [ ] `/app` é protegida
- [ ] logout limpa sessão
- [ ] interface interna mostra placeholders coerentes
- [ ] fluxo completo funciona com backend local

---

## Próximo Passo Após Este Slice

Depois deste slice:

1. integração de refresh automático
2. categorias/contas no frontend
3. criação de transações
4. dashboard com dados reais
