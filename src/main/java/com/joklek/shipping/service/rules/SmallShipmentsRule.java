package com.joklek.shipping.service.rules;

import com.joklek.shipping.model.PackageSize;
import com.joklek.shipping.model.Pair;
import com.joklek.shipping.model.ShippingInfo;
import com.joklek.shipping.service.ShippingPriceProvider;

import java.math.BigDecimal;
import java.util.Comparator;

public class SmallShipmentsRule implements DiscountRule {

    private final ShippingPriceProvider shippingPriceProvider;

    public SmallShipmentsRule(ShippingPriceProvider shippingPriceProvider) {
        this.shippingPriceProvider = shippingPriceProvider;
    }

    @Override
    public boolean isApplicableFor(ShippingInfo shippingInfo) {
        return PackageSize.SMALL.equals(shippingInfo.packageSize());
    }

    @Override
    public BigDecimal getSuggestedPrice(ShippingInfo shippingInfo, BigDecimal initialPrice, BigDecimal currentSuggestedPrice) {
        // TODO what if this suggests a higher price?
        return this.shippingPriceProvider.getPrices(PackageSize.SMALL).stream()
                .map(Pair::second)
                .min(Comparator.naturalOrder()).orElseThrow();
    }
}
