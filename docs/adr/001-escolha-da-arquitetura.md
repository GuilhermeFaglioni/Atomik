# ADR 001: Escolha da Arquitetura do Sistema

**Status:** Aceito
**Data:** 09-03-2026
**Autor:** Guilherme Faglioni

## Contexto
Precisamos de um sistema financeiro que seja altamente testável e cujas regras de negócio (cálculo de saldo, validações de transações) sejam independentes de frameworks externos e do banco de dados.

## Decisão
Utilizaremos a **Clean Architecture (Arquitetura Limpa)**. O projeto será dividido em:
1. **Domain:** Entidades puras e regras de negócio.
2. **Application:** Casos de uso e orquestração.
3. **Infrastructure:** Detalhes técnicos (Spring Boot, JPA, Liquibase).
4. **Presentation:** Controllers REST e DTOs.

## Consequências
- **Positivas:** Facilidade em criar testes unitários para o core financeiro, desacoplamento do Spring Boot, facilidade em trocar tecnologias de infraestrutura.
- **Negativas:** Maior número de classes iniciais (boilerplate) e curva de aprendizado para manter a separação de camadas.