package com.joklek.vinted.model;

import java.util.Arrays;

public enum ShippingCarrier {
    MONDIAL_RELAY("MR"), LA_POSTE("LP");

    private final String shortVersion;

    ShippingCarrier(String shortVersion) {
        this.shortVersion = shortVersion;
    }

    public String getShortVersion() {
        return this.shortVersion;
    }

    public static ShippingCarrier convertFromRaw(String rawSize) {
        return Arrays.stream(ShippingCarrier.values())
                .filter(shippingCarrier -> shippingCarrier.getShortVersion().equals(rawSize))
                .findFirst()
                .orElseThrow();
    }
}
