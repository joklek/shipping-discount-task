package com.joklek.shipping.service.rules;

import com.joklek.shipping.model.ShippingInfo;
import com.joklek.shipping.service.ShippingInfoRepo;

import java.math.BigDecimal;

import static com.joklek.shipping.model.PackageSize.LARGE;
import static com.joklek.shipping.model.ShippingCarrier.LA_POSTE;

public class ThirdLargeForLaPosteRule implements DiscountRule {

    private static final int FREE_DELIVERY_NTH_TIME = 3;

    private final ShippingInfoRepo shippingInfoRepo;

    public ThirdLargeForLaPosteRule(ShippingInfoRepo shippingInfoRepo) {
        this.shippingInfoRepo = shippingInfoRepo;
    }

    @Override
    public boolean isApplicableFor(ShippingInfo shippingInfo) {
        return LARGE.equals(shippingInfo.packageSize()) &&
                LA_POSTE.equals(shippingInfo.shippingCarrier());
    }

    @Override
    public BigDecimal getSuggestedPrice(ShippingInfo shippingInfo, BigDecimal initialPrice, BigDecimal currentSuggestedPrice) {
        var date = shippingInfo.date();
        var year = date.getYear();
        var month = date.getMonth();

        var shipmentsOnMonth = this.shippingInfoRepo.findShipmentsOnMonthForCarrierAndMonth(year, month, LA_POSTE, LARGE);

        var shipmentThisMonth = shipmentsOnMonth.size() + 1;
        if (shipmentThisMonth == FREE_DELIVERY_NTH_TIME) {
            return BigDecimal.ZERO;
        } else {
            return currentSuggestedPrice;
        }
    }
}
