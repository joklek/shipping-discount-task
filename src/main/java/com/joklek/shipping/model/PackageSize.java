package com.joklek.shipping.model;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public enum PackageSize {
    SMALL("S"), MEDIUM("M"), LARGE("L");

    private final String shortVersion;
    private static final Map<String, PackageSize> shortToFull = Arrays.stream(PackageSize.values())
            .collect(Collectors.toMap(PackageSize::getShortVersion, full -> full));

    PackageSize(String shortVersion) {
        this.shortVersion = shortVersion;
    }

    public String getShortVersion() {
        return this.shortVersion;
    }

    public static PackageSize convertFromRaw(String rawShort) {
        return Objects.requireNonNull(shortToFull.get(rawShort));
    }
}
