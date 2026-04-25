# 📈 B3 Market Sync API

Microservice responsible for synchronizing real market prices from [brapi.dev](https://brapi.dev) into the Redis cache, feeding the B3 Matching Engine with up-to-date asset prices.

> 📘 This service is part of a series of articles documenting the **My Broker B3** ecosystem.
> Follow the full series on [dev.to/rvneto](https://dev.to/rvneto).

---

## 🚀 Features

- **Scheduled Sync**: Fetches real market prices every 30 minutes (configurable)
- **Market Hours Guard**: Only runs on weekdays between 10:00 and 18:00 (Sao Paulo time)
- **Redis Cache**: Stores prices with a 5-minute TTL to prevent stale data in the Matching Engine
- **Rate Limit Protection**: 200ms delay between Brapi API calls to respect the free plan limits
- **REST API**: Endpoints to query cached prices directly, documented via Swagger UI

---

## 🛠️ Tech Stack

| Technology | Usage |
| :--- | :--- |
| **Java 21** + **Spring Boot 3.5.11** | Service core |
| **Spring Cloud OpenFeign** | Declarative HTTP client for brapi.dev |
| **Spring Data Redis** | High-performance cache write/read |
| **Spring Scheduling** | Periodic price synchronization |
| **Jackson JSR310** | Java 8 Date/Time serialization support |
| **SpringDoc OpenAPI** | Swagger UI documentation |

---

## 📋 Architecture & Flow
[brapi.dev] ◀── Feign Client ── [MarketSyncScheduler]
│
market:price:{TICKER}
│
[Redis]
│
[B3 Matching Engine]

**Sync Flow:**
1. Scheduler fires every 30 minutes
2. Checks if market is open (Mon-Fri, 10:00-18:00 Sao Paulo time)
3. Iterates over the configured ticker list
4. Fetches current price from brapi.dev via Feign
5. Stores result in Redis with key `market:price:{TICKER}` and 5-minute TTL

---

## 🌐 REST API Endpoints

Base URL: `http://localhost:8096/api/v1`

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| GET | `/quotes` | List cached prices for all configured tickers |
| GET | `/quotes/{ticker}` | Get cached price for a specific ticker |

📄 **Swagger UI**: [http://localhost:8096/swagger-ui.html](http://localhost:8096/swagger-ui.html)
📄 **OpenAPI Spec**: [http://localhost:8096/api-docs](http://localhost:8096/api-docs)

---

## 🔧 Environment Variables

| Variable | Description | Default |
| :--- | :--- | :--- |
| `BRAPI_TOKEN` | brapi.dev API token (required) | — |
| `REDIS_HOST` | Redis host | `localhost` |
| `REDIS_PORT` | Redis port | `6381` |

> ⚠️ `BRAPI_TOKEN` has no default value and must be explicitly provided.

---

## ⚙️ Configuration

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
    interval: 1800000  # 30 minutes in milliseconds
```

## 🗄️ Redis Cache Format
```json
{
  "ticker": "PETR4",
  "regularMarketPrice": 42.16,
  "regularMarketChangePercent": 1.2,
  "regularMarketTime": "2026-03-08T10:20:27"
}
```
Key pattern: `market:price:{TICKER}` — TTL: 5 minutes

## 🐳 Running with Docker
```bash
docker build -t b3-market-sync-api .
```
```bash
docker run --network finance-network \
  -e BRAPI_TOKEN=your_token_here \
  -e REDIS_HOST=b3-market-cache \
  -e REDIS_PORT=6381 \
  b3-market-sync-api
```

## 🚦 Health Check
Spring Actuator is enabled for health monitoring:

- Endpoint: `GET /actuator/health`
- Port: `8096`
