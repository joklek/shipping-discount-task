package com.joklek.shipping.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ShippingDiscountResponse(LocalDate date, PackageSize packageSize,
                                       ShippingCarrier shippingCarrier,
                                       BigDecimal price, BigDecimal discount) {

    public ShippingDiscountResponse(LocalDate date, PackageSize packageSize, ShippingCarrier shippingCarrier, BigDecimal price, BigDecimal discount) {
        this.date = date;
        this.packageSize = packageSize;
        this.shippingCarrier = shippingCarrier;
        this.price = price.stripTrailingZeros();
        this.discount = discount.stripTrailingZeros();
    }

    public ShippingDiscountResponse(LocalDate date, PackageSize packageSize, ShippingCarrier shippingCarrier, BigDecimal price) {
        this(date, packageSize, shippingCarrier, price, BigDecimal.ZERO);
    }

    public ShippingDiscountResponse(ShippingInfo shippingInfo, BigDecimal price, BigDecimal discount) {
        this(shippingInfo.date(), shippingInfo.packageSize(), shippingInfo.shippingCarrier(), price, discount);
    }
}
