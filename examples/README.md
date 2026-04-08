# Examples Gallery

Welcome to the **HG Brasil Finance Client Examples Gallery**.

This directory contains simple and practical examples demonstrating how to use each SDK domain. Before diving into specific operations, please review the built-in utility methods below.

## Utility Methods

The SDK provides built-in utility methods to ensure safe (*null-safe*) retrieval of data from API responses, avoiding the dreaded `NullPointerException` in your application.

These include:

- `findFirstResult()`: Returns an `Optional` containing the response model (which maps the JSON `results` object), or `Optional.empty()` if null. This is particularly useful for requests that return a single asset.
- `getSafeResults()`: Returns a `List<T>` or a `Map<K, V>` (depending on the operation domain) containing data from the JSON response, or an empty collection if the result is null.

### Temporal Collections

Models containing time-based data (such as Events, Series, and Samples) have specific methods for safe extraction. They always return a populated list or an empty list, never `null`:

- `getSafeEvents()`
- `getSafeSeries()`
- `getSafeSamples()`

### Error Handling

The HG Brasil financial API can return fully failed responses or partial errors (e.g., when requesting multiple assets and one of them is invalid). The SDK captures these anomalies and maps them to the `ApiError` record.

> **Note:** Not all API *endpoints* follow the same failure response pattern; therefore, the presence of the `ApiError` list varies depending on the operation domain.

For responses that support error mapping, you can use:

- `hasErrors()`: Returns `true` if the error list exists and is not empty. Excellent for conditionals and *fail-fast* scenarios.
- `getSafeErrors()`: Returns the complete list of errors (`List<ApiError>`) or an empty list if no errors are mapped. 