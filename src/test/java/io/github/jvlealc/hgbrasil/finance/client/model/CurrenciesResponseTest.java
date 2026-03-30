package io.github.jvlealc.hgbrasil.finance.client.model;

class CurrenciesResponseTest implements HGBrasilResponseContractTest {

    @Override
    public HGBrasilResponse createResponse(Boolean validKey, Boolean fromCache) {
        return new CurrenciesResponse("default", validKey, null, 0.0D, fromCache);
    }
}