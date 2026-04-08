## Example - Ibovespa Operations

### Ibovespa Details

Fetch an Ibovespa details with intraday daily data

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.IbovespaOperations;
import io.github.jvlealc.hgbrasil.finance.client.model.IbovespaResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.IbovespaResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

class IbovespaExamples {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            IbovespaOperations ibovespaOps = client.getIbovespaOperations();

            // Request Ibovespa details
            IbovespaResponse response = ibovespaOps.getIbovespa();

            // Null-safe list
            List<IbovespaResult> results = response.getSafeResults();

            if (results.isEmpty()) return;

            System.out.println("--- Ibovespa Daily Details ---");

            results.forEach((result) -> {
                LocalDate date = result.date();
                BigDecimal last = result.last();
                BigDecimal change = result.changePercent();

                System.out.printf("Date: %s | Current score: %s | Change: %s%%%n",
                        date != null ? date : "N/A",
                        last != null ? last : "N/A",
                        change != null ? change : "N/A"
                );

                // Intraday data (first point only)
                result.getSafeData().stream().findFirst().ifPresent(intradayPoint -> {
                    LocalDateTime time = intradayPoint.date();
                    BigDecimal points = intradayPoint.points();

                    System.out.printf("  Intraday -> Time: %s | Score: %s%n",
                            time != null ? time : "N/A",
                            points != null ? points : "N/A"
                    );
                });
            });

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```
