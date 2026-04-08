## Example - Split Operations (Split/Reverse Split)

#### Fetch asset split details by ticker.

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.SplitOperations;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.SplitResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.SplitResult;

import java.util.List;

public class SplitExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            SplitOperations splitOps = client.getSplitOperations();

            // Request split details
            SplitResponse response = splitOps.getByTicker("B3:TIMS3");

            // Null-safe list
            List<SplitResult> results = response.getSafeResults();

            if (results.isEmpty()) return;

            results.forEach(result -> {
                System.out.printf("[%s] %s%n", result.ticker(), result.name());
                result.findFirstEvent().ifPresent(event ->
                        System.out.printf("Type: %s | Ratio: %s | Com Date: %s | Status: %s%n",
                                event.type(),
                                event.ratio(),
                                event.comDate(),
                                event.status()
                        )
                );
            });

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```

## Split History Events

#### Fetch split history events by ticker, and using date range filters (`startDate` and `endDate`).

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.SplitOperations;
import io.github.jvlealc.hgbrasil.finance.client.model.SplitResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.SplitResult;

import java.time.LocalDate;
import java.util.List;

public class SplitDateRangeExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            SplitOperations splitOps = client.getSplitOperations();

            String ticker = "B3:TIMS3";
            LocalDate startDate = LocalDate.of(2022, 1, 10);
            LocalDate endDate = LocalDate.of(2026, 1, 10);

            // Request split history events (date range)
            SplitResponse response = splitOps.getHistorical(ticker, startDate, endDate);

            // Null-safe list
            List<SplitResult> results = response.getSafeResults();

            if (results.isEmpty()) return;

            results.forEach(result -> {
                System.out.printf("[%s] %s%n", result.ticker(), result.name());
                result.getSafeEvents().forEach(event ->
                        System.out.printf("Type: %s | Ratio: %s | Com Date: %s | Status: %s%n",
                                event.type(),
                                event.ratio(),
                                event.comDate(),
                                event.status()
                        )
                );
            });

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```

#### Fetch split history events by ticker, and using `daysAgo` query parameter.

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.SplitOperations;
import io.github.jvlealc.hgbrasil.finance.client.model.SplitResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.SplitResult;

import java.util.List;

public class SplitDaysAgoExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            SplitOperations splitOps = client.getSplitOperations();

            String ticker = "B3:TIMS3";
            int daysAgo = 365;

            // Request split history events (last 365 days)
            SplitResponse response = splitOps.getHistorical(ticker, daysAgo);

            // Null-safe list
            List<SplitResult> results = response.getSafeResults();

            if (results.isEmpty()) return;

            System.out.printf("[%s] %s%n", results.get(0).ticker(), results.get(0).name());

            results.stream()
                    .flatMap(result -> result.getSafeEvents().stream())
                    .forEach(event ->
                            System.out.printf("Type: %s | Ratio: %s | Com Date: %s | Status: %s%n",
                                    event.type(),
                                    event.ratio(),
                                    event.comDate(),
                                    event.status()
                            )
                    );

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```

#### Fetch split history events by ticker, and using `date` query parameter.

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.SplitOperations;
import io.github.jvlealc.hgbrasil.finance.client.model.SplitResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.SplitResult;

import java.time.LocalDate;
import java.util.List;

public class SplitDateExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            SplitOperations splitOps = client.getSplitOperations();

            String ticker = "B3:TIMS3";
            LocalDate date = LocalDate.of(2025, 7, 2);

            // Request split history events (specific date)
            SplitResponse response = splitOps.getHistorical(ticker, date);

            // Null-safe list
            List<SplitResult> results = response.getSafeResults();

            if (results.isEmpty()) return;

            System.out.printf("[%s] %s%n", results.get(0).ticker(), results.get(0).name());

            results.stream()
                    .flatMap(result -> result.getSafeEvents().stream())
                    .forEach(event ->
                            System.out.printf("Type: %s | Ratio: %s | Com Date: %s | Status: %s%n",
                                    event.type(),
                                    event.ratio(),
                                    event.comDate(),
                                    event.status()
                            )
                    );

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```

---

### Multiple Asset Splits

#### Fetch splits for multiple assets by ticker.

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.SplitOperations;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.SplitResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.SplitResult;

import java.util.List;

public class SplitBatchExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            SplitOperations splitOps = client.getSplitOperations();

            List<String> tickers = List.of("B3:TIMS3", "B3:BBAS3");

            // Request multiple split details
            SplitResponse response = splitOps.getByTickers(tickers);

            // Null-safe list
            List<SplitResult> results = response.getSafeResults();
            
            if (results.isEmpty()) return;

            results.forEach(result -> {
                System.out.printf("[%s] %s%n", result.ticker(), result.name());
                result.findFirstEvent().ifPresent(event ->
                        System.out.printf("Type: %s | Ratio: %s | Com Date: %s | Status: %s%n",
                                event.type(),
                                event.ratio(),
                                event.comDate(),
                                event.status()
                        )
                );
            });

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```

## Split History Events

**Note:** All history events parameters available for single-asset queries (such as `daysAgo` and `date`) also apply to multiple assets. Simply provide multiple tickers (List or varargs array) instead of a single one.

#### Fetch split history events for multiple assets by ticker using `startDate` and `endDate`.

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.SplitOperations;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.SplitResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.SplitResult;

import java.time.LocalDate;
import java.util.List;

public class SplitBatchDateRangeExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            SplitOperations splitOps = client.getSplitOperations();

            String[] tickers = {"B3:TIMS3", "B3:VALE3"};
            LocalDate startDate = LocalDate.of(2025, 1, 10);
            LocalDate endDate = LocalDate.of(2026, 1, 10);

            // Request split history events for multiple assets
            SplitResponse response = splitOps.getHistorical(startDate, endDate, tickers);

            // Null-safe list
            List<SplitResult> results = response.getSafeResults();
            
            if (results.isEmpty()) return;

            results.forEach(result -> {
                System.out.printf("[%s] %s%n", result.ticker(), result.name());

                result.getSafeEvents().forEach(event ->
                        System.out.printf("Type: %s | Ratio: %s | Com Date: %s | Status: %s%n",
                                event.type(),
                                event.ratio(),
                                event.comDate(),
                                event.status()
                        )
                );
            });

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```
