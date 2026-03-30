package io.github.jvlealc.hgbrasil.finance.client.model;

class BitcoinResponseTest implements HGBrasilResponseContractTest {

    @Override
    public HGBrasilResponse createResponse(Boolean validKey, Boolean fromCache) {
        return new BitcoinResponse("default", validKey, null, 0.0D, fromCache);
    }
}