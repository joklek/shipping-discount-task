package com.joklek.vinted.model;

import java.util.Arrays;

public enum PackageSize {
    SMALL("S"), MEDIUM("M"), LARGE("L");

    private final String shortVersion;

    PackageSize(String shortVersion) {
        this.shortVersion = shortVersion;
    }

    public String getShortVersion() {
        return this.shortVersion;
    }

    public static PackageSize convertFromRaw(String rawSize) {
        return Arrays.stream(PackageSize.values())
                .filter(packageSize -> packageSize.getShortVersion().equals(rawSize))
                .findFirst()
                .orElseThrow();
    }
}
