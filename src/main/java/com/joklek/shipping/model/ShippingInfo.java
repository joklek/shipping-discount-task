package com.joklek.shipping.model;

import java.time.LocalDate;

public record ShippingInfo(
        LocalDate date,
        PackageSize packageSize,
        ShippingCarrier shippingCarrier) {
}
