package com.joklek.vinted.service;

import com.joklek.vinted.model.ShippingInfo;
import com.joklek.vinted.service.rules.DiscountRule;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ShippingSuggestedPriceProvider {

    private final List<DiscountRule> discountRules;

    public ShippingSuggestedPriceProvider(List<DiscountRule> discountRules) {
        this.discountRules = discountRules;
    }

    public Optional<BigDecimal> findSuggestedPrice(ShippingInfo shippingInfo) {
        return this.discountRules.stream()
                .filter(rule -> rule.isApplicableFor(shippingInfo))
                .findFirst()
                .map(applicableRule -> applicableRule.suggestedPrice(shippingInfo));
    }
}
