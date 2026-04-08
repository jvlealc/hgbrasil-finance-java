## Example - Exchange Operations

### Currency Exchange Rates

Fetch a currency exchange rates in BRL

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.ExchangeOperations;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.CurrenciesResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.Currency;

import java.math.BigDecimal;
import java.util.Map;

public class CurrenciesExamples {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            ExchangeOperations exchangeOps = client.getExchangeOperations();

            // Request currencies
            CurrenciesResponse response = exchangeOps.getCurrencies();

            // Null-safe map
            Map<String, Currency> rates = response.results()
                    .currencies()
                    .getSafeRates();

            if (rates.isEmpty()) return;

            System.out.println("--- Currency Rates (BRL) ---");

            rates.forEach((symbol, currency) -> {
                BigDecimal buy = currency.buy();
                BigDecimal variation = currency.variation();

                System.out.printf("[%s] %s | Buy: R$ %s | Change: %s%%%n",
                        symbol,
                        currency.name(),
                        buy != null ? buy : "N/A",
                        variation != null ? variation : "N/A"
                );
            });

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```

---

### Bitcoin Prices

Fetch Bitcoin prices on the majors exchanges

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.ExchangeOperations;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.BitcoinExchange;
import io.github.jvlealc.hgbrasil.finance.client.model.BitcoinResponse;

import java.math.BigDecimal;
import java.util.Map;

public class BitcoinExamples {
    public static void main(String[] args) {

        try ( HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build() ) {

            ExchangeOperations exchangeOps = client.getExchangeOperations();

            // Request bitcoin prices
            BitcoinResponse response = exchangeOps.getBitcoin();

            // Null-safe map
            Map<String, BitcoinExchange> exchanges = response.results()
                    .getSafeBitcoin();
            
            if (exchanges.isEmpty()) return;

            System.out.println("--- Bitcoin Price on Exchanges ---");
            
            exchanges.forEach((broker, exchange) -> {
                String source = exchange.getSafeFormat().stream()
                        .findFirst()
                        .orElse("N/A");

                BigDecimal price = exchange.last();
                BigDecimal variation = exchange.variation();

                System.out.printf("Broker: %s | Source: %s | Current price: %s | Change: %s%%%n",
                        broker,
                        source,
                        price != null ? price : "N/A",
                        variation != null ? variation : "N/A"
                );
            });

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```
