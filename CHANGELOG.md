# Changelog

Todas as mudanças notáveis para o projeto **Atomik** serão documentadas neste arquivo.

O formato é baseado no [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), e este projeto adere ao [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

### Adicionado (Clean Architecture & Auth System)

#### 🏛️ Camada de Domínio (Domain)
- **Entidades:** Implementada entidade `User` e nova entidade `RefreshToken`.
- **Value Objects:** Adicionado Value Object `Email` para encapsular validação de formato.
- **Interfaces de Repositorio:** Criadas interfaces `UserRepository` e `RefreshTokenRepository` para desacoplamento de infraestrutura.
- **Serviços de Domínio:** Definidas interfaces `TokenService` e `PasswordHasherService`.
- **Exceções de Negócio:** Criadas `EmailAlreadyExistsException` e `UnauthorizedException`.

#### 🚀 Camada de Aplicação (Application)
- **Use Cases:** 
  - `RegisterUserUseCase`: Orquestra o registro de novos usuários.
  - `AuthenticateUserUseCase`: Orquestra o login e geração de tokens.
- **DTOs (Data Transfer Objects):** 
  - `RegisterRequestDTO`, `LoginRequestDTO` para entrada de dados.
  - `UserCreatedResponse`, `AuthResponse` para saída formatada.

#### 🛠️ Camada de Infraestrutura (Infrastructure)
- **Adaptadores:** 
  - `DatabaseUserRepositoryAdapter`: Implementação concreta usando JPA.
  - `RedisRefreshTokenAdapter`: (Placeholder/Draft) para gestão de tokens no Redis.
- **Segurança:**
  - `JwtTokenService`: Implementação de tokens JWT (Spring Security).
  - `PasswordHasherEncryption`: Implementação de hash de senhas (BCrypt).
  - `JwtAuthenticationFilter`: Filtro de autenticação para interceptação de requisições.
  - `SecurityConfig`: Configurações de filtros, permissões e beans de segurança.
- **Persistência:** Repositório JPA real em `JpaUserRepository`.

#### 🌐 Camada de Apresentação (Presentation)
- **Controller:** `AuthController` com endpoints `/auth/register` e `/auth/login`.
- **Tratamento de Erros:** `GlobalExceptionHandler` para capturar exceções de domínio e converter em respostas HTTP amigáveis via `ErrorMessageDTO`.

### Alterado
- **UserEntity:** Ajustado para refletir as mudanças no modelo de domínio de Usuário.
- **application.yml:** Configurações de segurança e acesso ao banco.
