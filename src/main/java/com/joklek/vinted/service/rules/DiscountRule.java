package com.joklek.vinted.service.rules;

import com.joklek.vinted.model.ShippingInfo;

import java.math.BigDecimal;

public interface DiscountRule {

    boolean isApplicableFor(ShippingInfo shippingInfo);

    BigDecimal getSuggestedPrice(ShippingInfo shippingInfo, BigDecimal initialPrice, BigDecimal currentSuggestedPrice);
}
