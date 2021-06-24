package com.joklek.shipping.service.rules;

import com.joklek.shipping.model.ShippingDiscountResponse;
import com.joklek.shipping.model.ShippingInfo;
import com.joklek.shipping.service.ShippingInfoRepo;

import java.math.BigDecimal;
import java.time.Month;
import java.util.List;
import java.util.Optional;

public class DiscountAccumulationLimitRule implements DiscountRule {

    private static final BigDecimal DISCOUNT_LIMIT = BigDecimal.valueOf(10);

    private final ShippingInfoRepo shippingInfoRepo;

    public DiscountAccumulationLimitRule(ShippingInfoRepo shippingInfoRepo) {
        this.shippingInfoRepo = shippingInfoRepo;
    }

    @Override
    public boolean isApplicableFor(ShippingInfo shippingInfo) {
        return true;
    }

    @Override
    public BigDecimal getSuggestedPrice(ShippingInfo shippingInfo, BigDecimal initialPrice, BigDecimal currentSuggestedPrice) {
        var date = shippingInfo.date();
        var year = date.getYear();
        var month = date.getMonth();

        var shipmentsOnMonth = this.findShipmentsOnMonth(year, month);

        var totalDiscountsThisMonth = shipmentsOnMonth.stream()
                .map(ShippingDiscountResponse::discount)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var currentSuggestedDiscount = initialPrice.subtract(currentSuggestedPrice);
        var discountBalance = DISCOUNT_LIMIT.subtract(totalDiscountsThisMonth);

        if (discountBalance.compareTo(currentSuggestedDiscount) >= 0) {
            return currentSuggestedPrice;
        } else {
            return initialPrice.subtract(discountBalance);
        }
    }

    private List<ShippingDiscountResponse> findShipmentsOnMonth(int year, Month month) {
        return this.shippingInfoRepo.findShipmentsOnMonth(year, month);
    }
}
