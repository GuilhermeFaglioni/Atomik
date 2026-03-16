# Manual Prático: Criando Rotas com Clean Architecture e DDD

Este é o roteiro definitivo (de "dentro para fora") para implementar uma nova funcionalidade (rota) seguindo os princípios de Clean Architecture e Domain-Driven Design (DDD).

A regra de ouro: **Dependências sempre apontam para o centro (Domínio)**.

---

## Passo 1: O Coração do Negócio (Camada de Domínio)
Comece sempre pelo negócio ("O que precisa ser feito?"), ignorando completamente frameworks, banco de dados ou a web.

### 1.1. Modelagem (Entities e Value Objects)
*   **O que é**: As estruturas que representam conceitos reais do seu negócio (ex: `User`, `Budget`, `Email`). Devem conter validações próprias para nunca existirem num estado inválido.
*   **Onde fica**: `domain/model/`
*   **Papel**: Garantir as Invariantes. Se um `Email` for criado com `new Email("abc")`, ele mesmo deve lançar um erro. O domínio se autoprotege.

### 1.2. Contratos e Portas (Interfaces)
*   **O que é**: As promessas que o Domínio faz, mas que não sabe como cumprir. São as interfaces de repositórios e serviços externos.
*   **Onde fica**: `domain/repository/` e `domain/service/`
*   **Papel**: O domínio avisa "Eu preciso conseguir buscar um usuário por e-mail no Banco de Dados" (`UserRepository`). Ele não importa o Spring Data JPA aqui.

### 1.3. Regras de Quebra (Exceptions)
*   **O que é**: Exceções específicas do seu negócio, como `SaldoInsuficienteException` ou `UnauthorizedException`.
*   **Onde fica**: `domain/exception/`
*   **Papel**: Evitar que a aplicação use exceções genéricas do Java ou de frameworks externos (`SQLException`) para explicar um problema de negócio.

---

## Passo 2: A Orquestração (Camada de Aplicação)
Agora que as peças do xadrez (Domínio) existem, precisamos de um jogador para movê-las.

### 2.1. O Caso de Uso (Use Case / Application Service)
*   **O que é**: Uma classe com UM único método público (ex: `execute()`) contendo o fluxo passo-a-passo da funcionalidade (buscar no banco, validar regra no domínio, salvar no banco).
*   **Onde fica**: `application/usecases/`
*   **Papel**: Orquestrar o fluxo. O Use Case conecta o banco de dados (através das interfaces) com as entidades do domínio. Ele não toma decisões complexas, ele delega ao domínio. Em Clean Arch, cada rota deve ter um "UseCase" específico (ex: `CreateBudgetUseCase`).

### 2.2. Os Entregadores (DTOs de Saída)
*   **O que é**: Objetos simples (records/classes) usados para devolver informações prontas para quem chamou o Use Case.
*   **Onde fica**: `application/dto/`
*   **Papel**: O Front-end não precisa receber a entidade rica `User` inteira (que pode ter o hash da senha). O Use Case mapeia o `User` para um `UserResponseDTO` contendo apenas o Nome e E-mail.

---

## Passo 3: O Subsolo e a "Mão na Massa" (Camada de Infraestrutura)
Aqui o mundo ideal do Domínio colide com o mundo sujo da tecnologia. É a hora de plugar o banco de dados e as bibliotecas.

### 3.1. O Banco Sujo (Entities do JPA)
*   **O que é**: As classes mapeadas para tabelas do banco usando `@Entity`, `@Column`.
*   **Onde fica**: `infrastructure/persistence/`
*   **Papel**: Representar fielmente as tabelas do PostgreSQL/MySQL. Não contêm regras de negócio.

### 3.2. Os Tradutores (Adapters e Mappers)
*   **O que é**: As classes que "Implementam" as interfaces deixadas no Domínio (Passo 1.2).
*   **Onde fica**: `infrastructure/adapter/` e `infrastructure/persistence/` (para os Mappers)
*   **Papel**: O `DatabaseUserRepositoryAdapter` assina o contrato e injeta o `JpaRepository`. Quando o Use Case pede para buscar um usuário, o Adapter vai no banco, pega a Entity (suja), pede para o `UserMapper` traduzir para a Entidade de Domínio (pura) e a devolve pro Use Case.

### 3.3. As Bibliotecas Externas (Serviços e Configurações)
*   **O que é**: Integrações com APIs, Geração de JWT, Logs, Spring Security, Filas RabbitMQ.
*   **Onde fica**: `infrastructure/security/`, `infrastructure/messaging/`, `infrastructure/config/`
*   **Papel**: Realizar o trabalho braçal e técnico que o domínio considerou indigno.

---

## Passo 4: O Maître do Restaurante (Camada de Apresentação)
A fronteira que permite que a Internet (ou um Terminal, ou um Celular) converse com a sua aplicação.

### 4.1. O Recebedor (Request DTOs)
*   **O que é**: Objetos (preferencialmente records) anotados com validações sintáticas (`@NotBlank`, `@Email`) para receber o JSON do cliente.
*   **Onde fica**: `application/dto/` ou `presentation/dto/`
*   **Papel**: Filtrar o lixo. Se o JSON não tiver os campos mínimos, o sistema rejeita a requisição antes mesmo de entrar no controlador.

### 4.2. A "Borda" Web (Controllers)
*   **O que é**: As classes anotadas com `@RestController`. Devem ser extremamente "magras".
*   **Onde fica**: `presentation/controllers/`
*   **Papel**: Receber o RequestDTO, passá-lo para o Use Case e transformar a resposta do Use Case num HTTP Status code adequado (200 OK, 201 Created). O Controller **não tem lógica de negócios**.

### 4.3. O Para-Raios Web (Global Exception Handler)
*   **O que é**: A classe anotada com `@RestControllerAdvice`.
*   **Onde fica**: `presentation/advice/`
*   **Papel**: O Use Case explode bombas (Exceptions). O Handler captura essas bombas no ar, impede que o sistema caia, e devolve um JSON bonito de erro (RFC 7807 ou custom) com o status HTTP correto (400, 401, 404).

---

## Resumo em "Regras de Ouro"

**Posso fazer isso?**
1. O Domain puxar o `org.springframework...`? **NUNCA**. O Domínio é java puro.
2. O Controller acessar o Repository (banco)? **NUNCA**. O Controller fala apenas com o Use Case.
3. O Use Case acessar o Repository? **SIM**, mas apenas através da **Interface** (O Contrato), nunca através do JpaRepository direto.
4. Passar a `Entity` do banco para o Front-end? **NUNCA**. Traduza-a para um DTO no Use Case.
5. Inserir validação de `@Email` (Javax) direto no Domain `Email.java`? **Pode**, apenas se for o pacote `jakarta.validation.constraints`, e não depender pesadamente do módulo `spring-boot-starter-validation`. Mas o DDD puro manda fazer a validação na mão via Construtor.
