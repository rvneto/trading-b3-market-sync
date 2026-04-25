# 📈 B3 Market Sync API

Microserviço responsável por sincronizar preços reais do mercado financeiro via [brapi.dev](https://brapi.dev) para o cache Redis, alimentando o B3 Matching Engine com cotações atualizadas.

> 📘 Este serviço faz parte de uma série de artigos documentando o ecossistema **My Broker B3**.
> Acompanhe a série completa em [dev.to/rvneto](https://dev.to/rvneto).

---

## 🚀 Funcionalidades

- **Sincronização Agendada**: Busca preços reais a cada 30 minutos (configurável)
- **Guard de Horário de Pregão**: Executa apenas em dias úteis entre 10h e 18h (horário de Brasília)
- **Cache Redis**: Armazena preços com TTL de 5 minutos para evitar dados obsoletos no Matching Engine
- **Proteção de Rate Limit**: Delay de 200ms entre chamadas à API Brapi para respeitar o plano free
- **API REST**: Endpoints para consultar preços em cache, documentados via Swagger UI

---

## 🛠️ Stack Tecnológica

| Tecnologia | Uso |
| :--- | :--- |
| **Java 21** + **Spring Boot 3.5.11** | Core do serviço |
| **Spring Cloud OpenFeign** | Cliente HTTP declarativo para brapi.dev |
| **Spring Data Redis** | Leitura e escrita de alta performance no cache |
| **Spring Scheduling** | Sincronização periódica de preços |
| **Jackson JSR310** | Suporte a tipos de data Java 8 |
| **SpringDoc OpenAPI** | Documentação via Swagger UI |

---

## 📋 Arquitetura e Fluxo
[brapi.dev] ◀── Feign Client ── [MarketSyncScheduler]
│
market:price:{TICKER}
│
[Redis]
│
[B3 Matching Engine]

**Fluxo de Sincronização:**
1. Scheduler dispara a cada 30 minutos
2. Verifica se o mercado está aberto (Seg-Sex, 10h-18h horário de SP)
3. Itera sobre a lista de tickers configurada
4. Busca o preço atual na brapi.dev via Feign
5. Salva no Redis com chave `market:price:{TICKER}` e TTL de 5 minutos

---

## 🌐 Endpoints REST

Base URL: `http://localhost:8096/api/v1`

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| GET | `/quotes` | Lista preços em cache de todos os tickers configurados |
| GET | `/quotes/{ticker}` | Retorna o preço em cache de um ticker específico |

📄 **Swagger UI**: [http://localhost:8096/swagger-ui.html](http://localhost:8096/swagger-ui.html)
📄 **OpenAPI Spec**: [http://localhost:8096/api-docs](http://localhost:8096/api-docs)

---

## 🔧 Variáveis de Ambiente

| Variável | Descrição | Padrão |
| :--- | :--- | :--- |
| `BRAPI_TOKEN` | Token da API brapi.dev (obrigatório) | — |
| `REDIS_HOST` | Host do Redis | `localhost` |
| `REDIS_PORT` | Porta do Redis | `6381` |

> ⚠️ `BRAPI_TOKEN` não possui valor padrão e deve ser fornecido explicitamente.

---

## ⚙️ Configuração

```yaml
app:
  brapi:
    url: https://brapi.dev/api
    token: ${BRAPI_TOKEN}
    tickers:
      - "PETR4"
      - "PETR3"
      - "VALE3"
  sync:
    interval: 1800000  # 30 minutos em milissegundos
```

## 🗄️ Formato do Cache Redis
```json
{
  "ticker": "PETR4",
  "regularMarketPrice": 42.16,
  "regularMarketChangePercent": 1.2,
  "regularMarketTime": "2026-03-08T10:20:27"
}
```
Padrão da chave: `market:price:{TICKER}` — TTL: 5 minutos

## 🐳 Rodando com Docker
```bash
docker build -t b3-market-sync-api .
```
```bash
docker run --network finance-network \
  -e BRAPI_TOKEN=seu_token_aqui \
  -e REDIS_HOST=b3-market-cache \
  -e REDIS_PORT=6381 \
  b3-market-sync-api
```

## 🚦 Health Check
O Spring Actuator está habilitado para monitoramento:

- Endpoint: `GET /actuator/health`
- Porta: `8096`
