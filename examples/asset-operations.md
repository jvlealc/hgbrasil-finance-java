## Example - Asset Operations

### Single Asset

Fetch a single asset by symbol with null-safe handling.

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.AssetOperations;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.AssetResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.AssetResult;

public class AssetExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            AssetOperations assetOps = client.getAssetOperations();

            // Request a single asset
            AssetResponse response = assetOps.getBySymbol("PETR4");

            // Null-safe access using Optional
            AssetResult result = response.findFirstResult()
                    .orElseThrow(() -> new IllegalStateException("Asset not found"));

            System.out.println("--- Asset Data ---");
            System.out.println("Name: " + result.name());
            System.out.println("Price: R$ " + result.price());
            System.out.println("Change (%): " + result.changePercent());
            System.out.println("Last updated: " + result.updatedAt());

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```

---

### Multiple Assets

Fetch multiple assets by symbols with null-safe handling and filter by price.

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.AssetOperations;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilApiException;
import io.github.jvlealc.hgbrasil.finance.client.model.AssetResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.AssetResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AssetBatchExample {
    public static void main(String[] args) {

        try (HGBrasilClient client = HGBrasilClient.builder()
                .apiKey("YOUR_API_KEY")
                .build()) {

            AssetOperations assetOps = client.getAssetOperations();
            List<String> symbols = List.of("PETR4", "VALE3", "BPAC11");

            // Request multiple assets
            AssetResponse response = assetOps.getBySymbols(symbols);

            // Null-safe map
            Map<String, AssetResult> results = response.getSafeResults();

            if (results.isEmpty()) return; // No assets found

            // Filter assets with price >= 50 
            Map<String, AssetResult> filtered = results.entrySet()
                    .stream()
                    .filter(entry -> {
                        AssetResult asset = entry.getValue();
                        return asset.price() != null &&
                                asset.price().compareTo(BigDecimal.valueOf(50)) >= 0;
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            if (filtered.isEmpty()) return; // No assets match the filter

            filtered.forEach((symbol, asset) ->
                    System.out.println(symbol + " -> R$ " + asset.price())
            );

        } catch (HGBrasilApiException e) {
            throw new RuntimeException("Failed to fetch data from API", e);
        }
    }
}
```
