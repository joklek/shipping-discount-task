package com.joklek.shipping.model;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public enum ShippingCarrier {
    MONDIAL_RELAY("MR"), LA_POSTE("LP");

    private final String shortVersion;
    private static final Map<String, ShippingCarrier> shortToFull = Arrays.stream(ShippingCarrier.values())
            .collect(Collectors.toMap(ShippingCarrier::getShortVersion, full -> full));

    ShippingCarrier(String shortVersion) {
        this.shortVersion = shortVersion;
    }

    public String getShortVersion() {
        return this.shortVersion;
    }

    public static ShippingCarrier convertFromRaw(String rawShort) {
        return Objects.requireNonNull(shortToFull.get(rawShort));
    }
}
