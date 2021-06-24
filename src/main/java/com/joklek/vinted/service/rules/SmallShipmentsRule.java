package com.joklek.vinted.service.rules;

import com.joklek.vinted.model.PackageSize;
import com.joklek.vinted.model.Pair;
import com.joklek.vinted.model.ShippingInfo;
import com.joklek.vinted.service.ShippingPriceProvider;

import java.math.BigDecimal;
import java.util.Comparator;

public class SmallShipmentsRule implements DiscountRule {

    private final ShippingPriceProvider shippingPriceProvider = new ShippingPriceProvider(); // TODO inject

    @Override
    public boolean isApplicableFor(ShippingInfo shippingInfo) {
        return PackageSize.SMALL.equals(shippingInfo.packageSize());
    }

    @Override
    public BigDecimal suggestedPrice(ShippingInfo shippingInfo) {
        return this.shippingPriceProvider.getPrices(PackageSize.SMALL).stream()
                .map(Pair::second)
                .min(Comparator.naturalOrder()).orElseThrow();
    }
}
