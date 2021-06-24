package com.joklek.vinted.service.rules;

import com.joklek.vinted.model.ShippingInfo;

import java.math.BigDecimal;

public interface DiscountRule {
    boolean isApplicableFor(ShippingInfo shippingInfo);

    BigDecimal suggestedPrice(ShippingInfo shippingInfo);
}
