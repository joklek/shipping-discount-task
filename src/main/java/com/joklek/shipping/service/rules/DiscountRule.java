package com.joklek.shipping.service.rules;

import com.joklek.shipping.model.ShippingInfo;

import java.math.BigDecimal;

public interface DiscountRule {

    boolean isApplicableFor(ShippingInfo shippingInfo);

    BigDecimal getSuggestedPrice(ShippingInfo shippingInfo, BigDecimal initialPrice, BigDecimal currentSuggestedPrice);
}
