# 📈 B3 Market Sync API

Microserviço responsável por sincronizar dados reais do mercado financeiro (via Brapi API) para o cache do simulador da B3.

## 🛠️ Tecnologias Utilizadas
* **Java 21** & **Spring Boot 3.3.5**
* **Spring Cloud OpenFeign**: Consumo da API externa Brapi.
* **Spring Data Redis**: Interface de escrita de alta performance para o cache.
* **Spring Scheduling**: Gerenciamento de tarefas periódicas para atualização de cotações.
* **Jackson JSR310**: Serialização correta de tipos de data Java 8 (`LocalDateTime`).

## ⚙️ Funcionamento
O serviço atua como um **Producer** de dados para o Redis (`b3-market-cache`). Ele não expõe endpoints REST de consulta, pois o `b3-matching-engine-api` consome os dados diretamente do cache para garantir baixa latência.

### Fluxo de Sincronização
1. **Verificação de Horário**: O sistema valida se o mercado está aberto (Seg-Sex, 10h-18h).
2. **Coleta de Dados**: Itera sobre a lista de tickers configurada e busca o preço atualizado na Brapi.
3. **Persistência**: Salva os dados no Redis com a chave `market:price:{TICKER}`.
4. **TTL (Time-To-Live)**: Cada chave possui expiração de 5 minutos para evitar que o simulador use preços obsoletos caso a sincronização falhe.

## 📋 Pré-requisitos
* **Redis**: Container `broker-market-data-cache` ativo na porta `6379`.
* **Brapi Token**: Chave de acesso válida para a API Brapi.

## 🔧 Configuração (application.yaml)
```yaml
app:
  brapi:
    url: [https://brapi.dev/api](https://brapi.dev/api)
    token: ${BRAPI_TOKEN}
    tickers: "PETR4,VALE3,ITUB4,PETR3" # Tickers monitorados
```

## 🐳 Docker
Para rodar via Docker, utilize o comando na raiz do projeto:

```Bash
docker build -t b3-market-sync-api .
```
O container deve estar na mesma rede do Redis (```finance-network```) para que o host ```broker-market-data-cache``` seja resolvido corretamente.

## 🔍 Monitoramento do Cache
Para validar se os dados estão sendo gravados, acesse o Redis e utilize:

```Bash 
# Via Redis CLI
GET market:price:PETR4
```

O formato armazenado será:
```JSON
{
  "ticker": "PETR4",
  "regularMarketPrice": 42.16,
  "regularMarketChangePercent": 1.2,
  "regularMarketTime": "2026-03-08T07:20:27"
}
```