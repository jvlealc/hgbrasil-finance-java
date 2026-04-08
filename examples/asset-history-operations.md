## Example - Asset History Operations (OHLCV)

#### Fetch asset history by ticker, and using date range filters (`startDate`, `endDate` and `sampleBy`).

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.AssetHistoryOperations;
import io.github.jvlealc.hgbrasil.finance.client.AssetSampleBy;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.AssetHistoryResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.AssetHistoryResult;

import java.time.LocalDate;
import java.util.List;

public class AssetHistoryDateRangeExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            AssetHistoryOperations historyOps = client.getAssetHistoryOperations();

            LocalDate startDate = LocalDate.of(2025, 1, 10);
            LocalDate endDate = LocalDate.of(2026, 1, 10);

            // Request asset history (date range)
            AssetHistoryResponse response = historyOps.getHistorical("B3:BPAC11", startDate, endDate, AssetSampleBy.ONE_MONTH);

            // Null-safe list
            List<AssetHistoryResult> results = response.getSafeResults();
            
            if (results.isEmpty()) return;

            System.out.println("Ticker: " + results.get(0).ticker());
            
            results.stream()
                    .flatMap(result -> result.getSafeSamples().stream())
                    .forEach(sample ->
                            System.out.printf("  Date: %s | Open: %s | High: %s | Low: %s | Close: %s | Volume: %s%n",
                                    sample.date(),
                                    sample.open(),
                                    sample.high(),
                                    sample.low(),
                                    sample.close(),
                                    sample.volume().toPlainString()
                            )
                    );

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```

#### Fetch asset history by ticker, and using `daysAgo` and `sampleBy` query parameters.

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.AssetHistoryOperations;
import io.github.jvlealc.hgbrasil.finance.client.AssetSampleBy;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.AssetHistoryResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.AssetHistoryResult;

import java.util.List;

public class AssetHistoryDaysAgoExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            AssetHistoryOperations historyOps = client.getAssetHistoryOperations();

            String ticker = "B3:BBAS3";
            int daysAgo = 30;

            // Request asset history (last 30 days)
            AssetHistoryResponse response = historyOps.getHistorical(ticker, daysAgo, AssetSampleBy.ONE_DAY);

            // Null-safe list
            List<AssetHistoryResult> results = response.getSafeResults();
            
            if (results.isEmpty()) return;

            System.out.println("Ticker: " + results.get(0).ticker());

            results.stream()
                    .flatMap(result -> result.getSafeSamples().stream())
                    .forEach(sample ->
                            System.out.printf("  Date: %s | Open: %s | High: %s | Low: %s | Close: %s | Volume: %s%n",
                                    sample.date(),
                                    sample.open(),
                                    sample.high(),
                                    sample.low(),
                                    sample.close(),
                                    sample.volume().toPlainString()
                            )
                    );

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```

#### Fetch asset history by ticker, and using `date` and `sampleBy` query parameters.

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.AssetHistoryOperations;
import io.github.jvlealc.hgbrasil.finance.client.AssetSampleBy;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.AssetHistoryResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.AssetHistoryResult;

import java.time.LocalDate;
import java.util.List;

public class AssetHistoryDateExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            AssetHistoryOperations historyOps = client.getAssetHistoryOperations();

            String ticker = "B3:PETR4";
            LocalDate date = LocalDate.of(2026, 1, 8);

            // Request asset history (specific date)
            AssetHistoryResponse response = historyOps.getHistorical(ticker, date, AssetSampleBy.ONE_HOUR);

            // Null-safe list
            List<AssetHistoryResult> results = response.getSafeResults();
            
            if (results.isEmpty()) return;

            System.out.println("Ticker: " + results.get(0).ticker());

            results.stream()
                    .flatMap(result -> result.getSafeSamples().stream())
                    .forEach(sample ->
                            System.out.printf("  Date: %s | Open: %s | High: %s | Low: %s | Close: %s | Volume: %s%n",
                                    sample.date(),
                                    sample.open(),
                                    sample.high(),
                                    sample.low(),
                                    sample.close(),
                                    sample.volume().toPlainString()
                            )
                    );

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```

---

### Multiple Assets

**Note:** All parameters available for single-asset queries (such as `daysAgo` and `date`) also apply to multiple assets. Simply provide multiple tickers (List or varargs array) instead of a single one.

#### Fetch multiple assets by tickers using `startDate`, `endDate` and `sampleBy` query parameters.

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.AssetHistoryOperations;
import io.github.jvlealc.hgbrasil.finance.client.AssetSampleBy;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.AssetHistoryResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.AssetHistoryResult;

import java.time.LocalDate;
import java.util.List;

public class AssetHistoryBatchExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            AssetHistoryOperations historyOps = client.getAssetHistoryOperations();

            List<String> tickers = List.of("B3:PETR4", "B3:VALE3");
            LocalDate startDate = LocalDate.of(2025, 1, 10);
            LocalDate endDate = LocalDate.of(2026, 1, 10);

            // Request multiple history assets
            AssetHistoryResponse response = historyOps.getHistorical(tickers, startDate, endDate, AssetSampleBy.ONE_MONTH);

            // Null-safe list
            List<AssetHistoryResult> results = response.getSafeResults();

            if (results.isEmpty()) return;

            results.forEach(result -> {
                System.out.println("Ticker: " + result.ticker());

                result.findFirstSample().ifPresent(sample ->
                        System.out.printf("  Date: %s | Open: %s | High: %s | Low: %s | Close: %s | Volume: %s%n",
                                sample.date(),
                                sample.open(),
                                sample.high(),
                                sample.low(),
                                sample.close(),
                                sample.volume().toPlainString()
                        )
                );
            });

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```
