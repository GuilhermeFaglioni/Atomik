# ADR 002: Gerenciamento de Esquema de Banco de Dados

**Status:** Aceito
**Data:** 09-03-2026

## Contexto
O Hibernate (ddl-auto) não provê histórico de alterações nem controle fino sobre a evolução do banco de dados em diferentes ambientes.

## Decisão
Utilizaremos o **Liquibase** para gerenciar todas as alterações de esquema via scripts de migração (ChangeLogs).

## Consequências
- **Positivas:** Controle total sobre o SQL executado, possibilidade de Rollback, paridade exata entre ambientes de desenvolvimento e produção.
- **Negativas:** Requer disciplina para criar arquivos de migração para cada alteração no banco.