## Example - Indicator Operations

### Brazilian Economic Indicators

#### Fetch indicator details by ticker.

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.IndicatorOperations;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorResult;

import java.util.List;

public class IndicatorExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            IndicatorOperations indicatorOps = client.getIndicatorOperations();

            // Request indicator details
            IndicatorResponse response = indicatorOps.getByTicker("IBGE:IPCA");

            // Null-safe list
            List<IndicatorResult> results = response.getSafeResults();

            if (results.isEmpty()) return;

            results.forEach(result -> {
                System.out.printf("Name: %s | Unit: %s | Category: %s | Periodicity: %s%n",
                        result.name(),
                        result.unit(),
                        result.category(),
                        result.periodicity()
                );
            });

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```

## Indicator History Series

#### Fetch indicator history series by ticker, and using date range filters (`startDate` and `endDate`).

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.IndicatorOperations;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorResult;

import java.time.LocalDate;
import java.util.List;

public class IndicatorDateRangeExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            IndicatorOperations indicatorOps = client.getIndicatorOperations();

            String ticker = "IBGE:IPCA";
            LocalDate startDate = LocalDate.of(2025, 1, 10);
            LocalDate endDate = LocalDate.of(2026, 1, 10);

            // Request indicator history series (date range)
            IndicatorResponse response = indicatorOps.getHistorical(ticker, startDate, endDate);

            // Null-safe list
            List<IndicatorResult> results = response.getSafeResults();

            if (results.isEmpty()) return;

            System.out.println("Name: " + results.get(0).fullName());

            results.stream()
                    .flatMap(result -> result.getSafeSeries().stream())
                    .forEach(serie ->
                            System.out.printf("  Period: %s | Value: %s%n",
                                    serie.period(),
                                    serie.value()
                            )
                    );

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```

#### Fetch indicator history series by ticker, and using `daysAgo` query parameter.

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.IndicatorOperations;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorResult;

import java.util.List;

public class IndicatorDaysAgoExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            IndicatorOperations indicatorOps = client.getIndicatorOperations();

            String ticker = "IBGE:IPCA";
            int daysAgo = 180;

            // Request indicator history series (last 180 days)
            IndicatorResponse response = indicatorOps.getHistorical(ticker, daysAgo);

            // Null-safe list
            List<IndicatorResult> results = response.getSafeResults();

            if (results.isEmpty()) return;

            System.out.println("Name: " + results.get(0).fullName());

            results.stream()
                    .flatMap(result -> result.getSafeSeries().stream())
                    .forEach(serie ->
                            System.out.printf("  Period: %s | Value: %s%n",
                                    serie.period(),
                                    serie.value()
                            )
                    );

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```

#### Fetch indicator history series by ticker, and using `date` query parameter.

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.IndicatorOperations;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorResult;

import java.time.LocalDate;
import java.util.List;

public class IndicatorDateExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            IndicatorOperations indicatorOps = client.getIndicatorOperations();

            String ticker = "IBGE:IPCA";
            LocalDate date = LocalDate.of(2026, 1, 8);

            // Request indicator history series (specific date)
            IndicatorResponse response = indicatorOps.getHistorical(ticker, date);

            // Null-safe list
            List<IndicatorResult> results = response.getSafeResults();

            if (results.isEmpty()) return;

            System.out.println("Name: " + results.get(0).fullName());

            results.stream()
                    .flatMap(result -> result.getSafeSeries().stream())
                    .forEach(serie ->
                            System.out.printf("  Period: %s | Value: %s%n",
                                    serie.period(),
                                    serie.value()
                            )
                    );

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```

---

### Multiple Indicators

#### Fetch multiple indicators by ticker.

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.IndicatorOperations;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorResult;

import java.util.List;

public class IndicatorBatchExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            IndicatorOperations indicatorOps = client.getIndicatorOperations();

            String[] tickers = {"IBGE:IPCA", "BCB:SELICMETA"};

            // Request multiple indicator details
            IndicatorResponse response = indicatorOps.getByTickers(tickers);

            // Null-safe list
            List<IndicatorResult> results = response.getSafeResults();

            if (results.isEmpty()) return;

            results.forEach(result -> {
                System.out.printf("[%s] %s%n", result.ticker(), result.fullName());
                result.getSafeSeries().forEach(serie ->
                        System.out.printf("  Period: %s | Value: %s%n",
                                serie.period(),
                                serie.value()
                        )
                );
            });

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```

## Multiple Indicator History Series

**Note:** All history series parameters available for single-indicator queries (such as `daysAgo` and `date`) also apply to multiple indicators. Simply provide multiple tickers (List or varargs array) instead of a single one.

#### Fetch history series for multiple indicators by ticker using `startDate` and `endDate`.

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.IndicatorOperations;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorResult;

import java.time.LocalDate;
import java.util.List;

public class IndicatorBatchDateRangeExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            IndicatorOperations indicatorOps = client.getIndicatorOperations();

            List<String> tickers = List.of("IBGE:IPCA", "BCB:SELICMETA");
            LocalDate startDate = LocalDate.of(2025, 1, 10);
            LocalDate endDate = LocalDate.of(2026, 1, 10);

            // Request history series for multiple indicators
            IndicatorResponse response = indicatorOps.getHistorical(tickers, startDate, endDate);

            // Null-safe list
            List<IndicatorResult> results = response.getSafeResults();

            if (results.isEmpty()) return;

            results.forEach(result -> {
                System.out.printf("[%s] %s%n", result.ticker(), result.fullName());
                result.getSafeSeries().forEach(serie ->
                        System.out.printf("  Period: %s | Value: %s%n",
                                serie.period(),
                                serie.value()
                        )
                );
            });

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```
