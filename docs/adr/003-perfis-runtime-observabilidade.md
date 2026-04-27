# ADR 003: Perfis de Runtime e Observabilidade Minima

**Status:** Aceito
**Data:** 27-04-2026

## Contexto

`show-sql` estava ligado globalmente. Testes e execucao comum herdavam ruido de SQL/log sem diferenciar ambiente. API tambem nao carregava identificador de correlacao por requisicao, o que dificultava rastrear falhas em log. Parte das consultas de lista ainda usava excecao para representar colecao vazia.

## Decisao

Adotar:

1. perfis `dev`, `test`, `prod` com comportamento de log/SQL distinto
2. `X-Request-Id` por requisicao, propagado em header de resposta e MDC
3. `500` padronizado com log explicito para excecao inesperada
4. consultas de lista retornando `[]` em vez de excecao quando ausencia de itens nao for erro de negocio

## Consequencias

### Positivas

- menos overhead e menos ruido fora de `dev`
- rastreabilidade melhor para incidentes
- semantica HTTP melhor para endpoints de listagem
- suite de testes mais limpa e previsivel

### Negativas

- observabilidade ainda e minima; sem metricas/Actuator
- H2 ainda gera warning de DDL em `budgets.month/year`
- profile default `dev` ainda privilegia experiencia local; deploy deve explicitar `SPRING_PROFILES_ACTIVE=prod`
