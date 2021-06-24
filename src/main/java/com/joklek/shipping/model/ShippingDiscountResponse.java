package com.joklek.shipping.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public final class ShippingDiscountResponse {

    private final LocalDate date;
    private final PackageSize packageSize;
    private final ShippingCarrier shippingCarrier;
    private final BigDecimal price;
    private final BigDecimal discount;

    public ShippingDiscountResponse(LocalDate date, PackageSize packageSize, ShippingCarrier shippingCarrier, BigDecimal price, BigDecimal discount) {
        this.date = date;
        this.packageSize = packageSize;
        this.shippingCarrier = shippingCarrier;
        this.price = price.stripTrailingZeros();
        this.discount = BigDecimal.ZERO.compareTo(discount) == 0 ? null : discount.stripTrailingZeros();
    }

    public ShippingDiscountResponse(LocalDate date, PackageSize packageSize, ShippingCarrier shippingCarrier, BigDecimal price) {
        this.date = date;
        this.packageSize = packageSize;
        this.shippingCarrier = shippingCarrier;
        this.price = price.stripTrailingZeros();
        this.discount = null;
    }

    public ShippingDiscountResponse(ShippingInfo shippingInfo, BigDecimal price, BigDecimal discount) {
        this.date = shippingInfo.date();
        this.packageSize = shippingInfo.packageSize();
        this.shippingCarrier = shippingInfo.shippingCarrier();
        this.price = price.stripTrailingZeros();
        this.discount = BigDecimal.ZERO.compareTo(discount) == 0 ? null : discount;
    }

    public LocalDate date() {
        return this.date;
    }

    public PackageSize packageSize() {
        return this.packageSize;
    }

    public ShippingCarrier shippingCarrier() {
        return this.shippingCarrier;
    }

    public BigDecimal price() {
        return this.price;
    }

    public Optional<BigDecimal> discount() {
        return Optional.ofNullable(this.discount);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (ShippingDiscountResponse) obj;
        return Objects.equals(this.date, that.date) &&
                Objects.equals(this.packageSize, that.packageSize) &&
                Objects.equals(this.shippingCarrier, that.shippingCarrier) &&
                Objects.equals(this.price, that.price) &&
                Objects.equals(this.discount, that.discount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.date, this.packageSize, this.shippingCarrier, this.price, this.discount);
    }

    @Override
    public String toString() {
        return "ShippingDiscountResponse{" +
                "date=" + this.date +
                ", packageSize=" + this.packageSize +
                ", shippingCarrier=" + this.shippingCarrier +
                ", price=" + this.price +
                ", discount=" + this.discount +
                '}';
    }
}
