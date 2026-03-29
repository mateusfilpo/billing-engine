# 💰 Billing Engine

Motor de cobrança recorrente construído com **Java 25**, **Spring Boot 4** e **PostgreSQL**, aplicando **Arquitetura Hexagonal** e **Domain-Driven Design**.

O projeto simula o core de sistemas como Stripe e Vindi — gerenciando clientes, planos, assinaturas, faturas e pagamentos com idempotência, prorate e retry automático.

---

## 🏗️ Arquitetura

```
┌─────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE                       │
│  ┌──────────────┐  ┌─────────────┐  ┌───────────────┐  │
│  │  Controllers │  │ JPA Adapters│  │ FakeGateway   │  │
│  │  (REST API)  │  │ (Postgres)  │  │ (Pagamento)   │  │
│  └──────┬───────┘  └──────┬──────┘  └───────┬───────┘  │
│         │                 │                 │           │
│─────────┼─── Ports In ────┼── Ports Out ────┼───────────│
│         │                 │                 │           │
│  ┌──────▼─────────────────▼─────────────────▼───────┐   │
│  │                  DOMAIN                          │   │
│  │  Customer · Plan · Subscription · Invoice        │   │
│  │  Payment · PaymentEvent · ProrateCalculator      │   │
│  │  Services · Use Cases · Business Rules           │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

**Princípio**: O domínio não conhece frameworks. Controllers e JPA são adaptadores substituíveis.

---

## 🔄 Fluxo de Cobrança

```
                    ┌─────────────────┐
                    │    Scheduler    │
                    │  (00:00 diário) │
                    └────────┬────────┘
                             │
              ┌──────────────▼──────────────┐
              │ Busca assinaturas vencendo   │
              │ hoje (currentPeriodEnd)      │
              └──────────────┬──────────────┘
                             │
                 ┌───────────▼───────────┐
                 │  cancelAtPeriodEnd?   │
                 └───┬──────────────┬────┘
                 Sim │              │ Não
                     ▼              ▼
              ┌──────────┐  ┌──────────────┐
              │ Cancela  │  │ Gera Invoice │
              └──────────┘  └──────┬───────┘
                                   │
                            ┌──────▼───────┐
                            │ Abre Invoice │
                            │ (DRAFT→OPEN) │
                            └──────┬───────┘
                                   │
                         ┌─────────▼─────────┐
                         │ Processa Payment  │
                         │ via Gateway       │
                         └────┬─────────┬────┘
                         PAID │         │ FAILED
                              ▼         ▼
                    ┌──────────┐ ┌────────────┐
                    │ Renova   │ │ Agenda     │
                    │ período  │ │ retry +24h │
                    └──────────┘ └────────────┘
```

---

## 📋 Entidades do Domínio

| Entidade | Responsabilidade | State Machine |
|---|---|---|
| **Customer** | Cliente com nome, email e documento | — |
| **Plan** | Plano com preço e ciclo de cobrança | `ACTIVE` → `INACTIVE` |
| **Subscription** | Vínculo customer↔plan com período | `ACTIVE` → `SUSPENDED` → `CANCELLED` |
| **Invoice** | Fatura com itens e total calculado | `DRAFT` → `OPEN` → `PAID` / `VOID` / `UNCOLLECTIBLE` |
| **InvoiceItem** | Linha da fatura (quantidade × preço) | — |
| **Payment** | Tentativa de pagamento com idempotência | `PENDING` → `PROCESSING` → `PAID` / `FAILED` |
| **PaymentEvent** | Log imutável de cada transição | — |

---

## 🛠️ Conceitos Técnicos

### Idempotência
Cada pagamento recebe uma `idempotency_key` gerada como `PAY-{invoiceId}-{attemptNumber}`. Se o sistema cair e reiniciar, a constraint `UNIQUE` impede cobrança duplicada.

### Prorate
Se um cliente troca de plano no meio do ciclo, o sistema calcula proporcionalmente:
```
Plano A (R$100/mês) → Plano B (R$200/mês), troca no dia 15 de 30
  Crédito: R$100 × 15/30 = R$50.00
  Débito:  R$200 × 15/30 = R$100.00
  Fatura de ajuste: R$50.00
```

### Retry Logic
Pagamentos falhos agendam `nextRetryAt = now + 24h`. O scheduler das 06:00 busca payments com `status=FAILED AND nextRetryAt <= now` usando um índice parcial otimizado.

### Event Sourcing (Simplificado)
Cada transição de estado do `Payment` gera um `PaymentEvent` imutável (`INITIATED → PROCESSING → PAID/FAILED`), criando um histórico completo de auditoria.

---

## 🔌 API Endpoints

### Customer
| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/customers` | Criar cliente |
| `GET` | `/api/customers/{id}` | Buscar por ID |
| `PUT` | `/api/customers/{id}` | Atualizar cliente |

### Plan
| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/plans` | Criar plano |
| `GET` | `/api/plans/{id}` | Buscar por ID |
| `GET` | `/api/plans` | Listar todos |
| `PUT` | `/api/plans/{id}` | Atualizar plano |

### Subscription
| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/subscriptions` | Criar assinatura |
| `GET` | `/api/subscriptions/{id}` | Buscar por ID |
| `GET` | `/api/subscriptions?customerId=` | Buscar por cliente |
| `POST` | `/api/subscriptions/{id}/cancel` | Cancelar assinatura |
| `POST` | `/api/subscriptions/{id}/change-plan` | Trocar plano (prorate) |

### Invoice
| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/invoices` | Gerar fatura |
| `GET` | `/api/invoices/{id}` | Buscar por ID |
| `GET` | `/api/invoices?customerId=` | Buscar por cliente |
| `POST` | `/api/invoices/{id}/open` | Abrir fatura (DRAFT→OPEN) |
| `POST` | `/api/invoices/{id}/pay` | Marcar como paga |

### Payment
| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/payments` | Processar pagamento |
| `GET` | `/api/payments/{id}` | Buscar por ID |
| `GET` | `/api/payments?invoiceId=` | Buscar por fatura |

---

## 🚀 Como Rodar

### Pré-requisitos
- **Java 25+**
- **Docker** (para PostgreSQL via Docker Compose)

### Executar
```bash
# Clonar o repositório
git clone https://github.com/mateusfilpo/billing-engine.git
cd billing-engine

# Rodar (Docker Compose sobe o PostgreSQL automaticamente)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

O profile `dev` ativa o **DataSeeder**, que cria automaticamente:
- 3 Planos (Básico R$29.90, Pro R$99.90, Enterprise R$999.00)
- 5 Clientes com dados brasileiros (via Datafaker)
- 5 Assinaturas ativas

### Testes
```bash
# Testes unitários (20 testes)
./mvnw test

# Testes unitários + integração com Testcontainers (25 testes)
./mvnw verify
```

---

## 🧰 Stack

| Tecnologia | Uso |
|---|---|
| **Java 25** | Linguagem + records, sealed classes, pattern matching |
| **Spring Boot 4** | Framework web + DI |
| **PostgreSQL 18** | Banco de dados relacional |
| **Flyway** | Versionamento de schema (3 migrations) |
| **Hibernate 7** | ORM com cascade e aggregate roots |
| **Testcontainers** | Testes de integração com PostgreSQL real |
| **Datafaker** | Seed de dados realistas em pt-BR |
| **Lombok** | Redução de boilerplate |
| **UUIDv7** | IDs ordenáveis por tempo |

---

## 📁 Estrutura do Projeto

```
src/main/java/br/com/filpo/billing/
├── domain/
│   ├── model/              # Entidades de domínio puras (Customer, Plan, Invoice...)
│   ├── exception/          # Exceções de negócio
│   ├── port/
│   │   ├── in/             # Interfaces de entrada (use cases), agrupadas por entidade
│   │   │   ├── customer/   # CreateCustomerUseCase, FindCustomerUseCase...
│   │   │   ├── plan/
│   │   │   ├── subscription/
│   │   │   ├── invoice/
│   │   │   └── payment/
│   │   └── out/            # Interfaces de saída (persistência, gateway)
│   │       ├── customer/   # FindCustomerPort, SaveCustomerPort
│   │       ├── plan/
│   │       ├── subscription/
│   │       ├── invoice/
│   │       └── payment/    # FindPaymentPort, SavePaymentPort, PaymentGateway
│   └── service/            # Implementações dos use cases, agrupadas por entidade
│       ├── customer/       # CreateCustomerService, FindCustomerService...
│       ├── plan/
│       ├── subscription/
│       ├── invoice/
│       └── payment/
├── infrastructure/
│   ├── adapter/
│   │   ├── in/web/         # Controllers, Mappers e DTOs por entidade
│   │   │   ├── customer/   # CustomerController, CustomerWebMapper, dto/
│   │   │   ├── plan/
│   │   │   ├── subscription/
│   │   │   ├── invoice/
│   │   │   └── payment/
│   │   └── out/
│   │       ├── persistence/ # JpaEntity + Repository + Adapter + Mapper por entidade
│   │       │   ├── customer/
│   │       │   ├── plan/
│   │       │   ├── subscription/
│   │       │   ├── invoice/
│   │       │   └── payment/
│   │       └── gateway/
│   │           └── payment/ # FakePaymentGateway
│   ├── scheduler/           # BillingScheduler (cron jobs)
│   └── seed/                # DataSeeder (Datafaker, profile dev)
└── BillingEngineApplication.java
```

> A estrutura segue o padrão **Package by Feature** dentro da Arquitetura Hexagonal:
> cada funcionalidade/entidade possui suas próprias interfaces, serviços, controllers e adaptadores de banco de dados
> organizados de forma coesa, o que favorece a navegação e o desenvolvimento guiado a domínio.
