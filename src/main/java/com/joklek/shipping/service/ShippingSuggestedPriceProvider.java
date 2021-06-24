package com.joklek.shipping.service;

import com.joklek.shipping.model.ShippingInfo;
import com.joklek.shipping.service.rules.DiscountRule;

import java.math.BigDecimal;
import java.util.List;

public class ShippingSuggestedPriceProvider {

    private final List<DiscountRule> discountRules;

    public ShippingSuggestedPriceProvider(List<DiscountRule> discountRules) {
        this.discountRules = discountRules;
    }

    public BigDecimal findSuggestedPrice(ShippingInfo shippingInfo, BigDecimal initialPrice) {
        var currentSuggestedPrice = initialPrice;
        for (DiscountRule discountRule : this.discountRules) {
            if (!discountRule.isApplicableFor(shippingInfo)) {
                continue;
            }
            currentSuggestedPrice = discountRule.getSuggestedPrice(shippingInfo, initialPrice, currentSuggestedPrice);
        }
        return currentSuggestedPrice;
    }
}
