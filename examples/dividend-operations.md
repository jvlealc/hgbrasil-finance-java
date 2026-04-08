## Example - Dividend Operations

#### Fetch dividend details by ticker.

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.DividendOperations;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.DividendResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.DividendResult;

import java.util.List;

public class DividendExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            DividendOperations dividendOps = client.getDividendOperations();

            // Request dividend details
            DividendResponse response = dividendOps.getByTicker("B3:PETR4");

            // Null-safe list
            List<DividendResult> results = response.getSafeResults();
            
            if (results.isEmpty()) return;

            results.forEach(result -> {
                String yield12mPercent = (result.summary() != null) ? result.summary().yield12mPercent().toPlainString() : "N/A";
                
                System.out.printf("[%s] %s | Currency: %s | Yield 12m: %s%%%n",
                        result.symbol(),
                        result.name(),
                        result.currency(),
                        yield12mPercent
                );
            });

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```

## Dividend History Series

#### Fetch dividend history series by ticker, and using date range filters (`startDate` and `endDate`).

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.DividendOperations;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.DividendResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.DividendResult;

import java.time.LocalDate;
import java.util.List;

public class DividendDateRangeExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            DividendOperations dividendOps = client.getDividendOperations();

            String ticker = "B3:PETR4";
            LocalDate startDate = LocalDate.of(2025, 1, 10);
            LocalDate endDate = LocalDate.of(2026, 1, 10);

            // Request dividend history series (date range)
            DividendResponse response = dividendOps.getHistorical(ticker, startDate, endDate);

            // Null-safe list
            List<DividendResult> results = response.getSafeResults();
            
            if (results.isEmpty()) return;

            System.out.println("Ticker: " + results.get(0).ticker());

            results.stream()
                    .flatMap(result -> result.getSafeSeries().stream())
                    .forEach(serie ->
                            System.out.printf("  Type: %s | Amount: %s | Date: %s | Status: %s%n",
                                    serie.type(),
                                    serie.amount(),
                                    serie.comDate(),
                                    serie.status()
                            )
                    );

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```

#### Fetch dividend history series by ticker, and using `daysAgo` query parameter.

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.DividendOperations;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.DividendResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.DividendResult;

import java.util.List;

public class DividendDaysAgoExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            DividendOperations dividendOps = client.getDividendOperations();

            String ticker = "B3:PETR4";
            int daysAgo = 180;

            // Request dividend history series (last 180 days)
            DividendResponse response = dividendOps.getHistorical(ticker, daysAgo);

            // Null-safe list
            List<DividendResult> results = response.getSafeResults();
            
            if (results.isEmpty()) return;

            System.out.println("Ticker: " + results.get(0).ticker());

            results.stream()
                    .flatMap(result -> result.getSafeSeries().stream())
                    .forEach(serie ->
                            System.out.printf("  Type: %s | Amount: %s | Date: %s | Status: %s%n",
                                    serie.type(),
                                    serie.amount(),
                                    serie.comDate(),
                                    serie.status()
                            )
                    );

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```

#### Fetch dividend history series by ticker, and using `date` query parameter.

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.DividendOperations;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.DividendResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.DividendResult;

import java.time.LocalDate;
import java.util.List;

public class DividendDateExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            DividendOperations dividendOps = client.getDividendOperations();

            String ticker = "B3:PETR4";
            LocalDate date = LocalDate.of(2026, 1, 8);

            // Request dividend history series (specific date)
            DividendResponse response = dividendOps.getHistorical(ticker, date);

            // Null-safe list
            List<DividendResult> results = response.getSafeResults();
            
            if (results.isEmpty()) return;

            System.out.println("Ticker: " + results.get(0).ticker());

            results.stream()
                    .flatMap(result -> result.getSafeSeries().stream())
                    .forEach(serie ->
                            System.out.printf("  Type: %s | Amount: %s | Date: %s | Status: %s%n",
                                    serie.type(),
                                    serie.amount(),
                                    serie.comDate(),
                                    serie.status()
                            )
                    );

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```

---

### Multiple Asset Dividends

#### Fetch dividends for multiple assets by ticker.

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.DividendOperations;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.DividendResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.DividendResult;

import java.util.List;

public class DividendBatchExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            DividendOperations dividendOps = client.getDividendOperations();

            List<String> tickers = List.of("B3:PETR4", "B3:ABEV3");

            // Request multiple dividend details
            DividendResponse response = dividendOps.getByTickers(tickers);

            // Null-safe list
            List<DividendResult> results = response.getSafeResults();
            
            if (results.isEmpty()) return;

            results.forEach(result -> {
                String yield12mPercent = (result.summary() != null) ? result.summary().yield12mPercent().toPlainString() : "N/A";

                System.out.printf("[%s] %s | Currency: %s | Yield 12m: %s%%%n",
                        result.symbol(),
                        result.name(),
                        result.currency(),
                        yield12mPercent
                );
            });

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```

## Dividend History Series

**Note:** All history series parameters available for single-asset queries (such as `daysAgo` and `date`) also apply to multiple assets. Simply provide multiple tickers (List or varargs array) instead of a single one.

#### Fetch dividend history series for multiple assets by ticker using `startDate` and `endDate`.

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.DividendOperations;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.DividendResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.DividendResult;

import java.time.LocalDate;
import java.util.List;

public class DividendBatchDateRangeExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            DividendOperations dividendOps = client.getDividendOperations();

            String[] tickers = {"B3:PETR4", "B3:VALE3"};
            LocalDate startDate = LocalDate.of(2025, 1, 10);
            LocalDate endDate = LocalDate.of(2026, 1, 10);

            // Request multiple dividend history series
            DividendResponse response = dividendOps.getHistorical(startDate, endDate, tickers);

            // Null-safe list
            List<DividendResult> results = response.getSafeResults();
            
            if (results.isEmpty()) return;

            results.forEach(result -> {
                System.out.println("Ticker: " + result.ticker());
                result.getSafeSeries().forEach(serie ->
                        System.out.printf("  Type: %s | Amount: %s | Date: %s | Status: %s%n",
                                serie.type(),
                                serie.amount(),
                                serie.comDate(),
                                serie.status()
                        )
                );
            });

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```
