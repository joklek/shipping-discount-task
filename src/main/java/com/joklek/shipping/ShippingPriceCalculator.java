package com.joklek.shipping;

import com.joklek.shipping.model.ShippingDiscountResponse;
import com.joklek.shipping.model.ShippingInfo;
import com.joklek.shipping.service.ShippingInfoMapper;
import com.joklek.shipping.service.ShippingInfoRepo;
import com.joklek.shipping.service.ShippingPriceProvider;
import com.joklek.shipping.service.ShippingSuggestedPriceProvider;

import java.util.Optional;

public class ShippingPriceCalculator {

    private final ShippingInfoRepo shippingInfoRepo;
    private final ShippingInfoMapper shippingInfoMapper;
    private final ShippingPriceProvider shippingPriceProvider;
    private final ShippingSuggestedPriceProvider shippingSuggestedPriceProvider;

    public ShippingPriceCalculator(ShippingInfoRepo shippingInfoRepo, ShippingInfoMapper shippingInfoMapper, ShippingPriceProvider shippingPriceProvider, ShippingSuggestedPriceProvider shippingSuggestedPriceProvider) {
        this.shippingInfoRepo = shippingInfoRepo;
        this.shippingInfoMapper = shippingInfoMapper;
        this.shippingPriceProvider = shippingPriceProvider;
        this.shippingSuggestedPriceProvider = shippingSuggestedPriceProvider;
    }

    public Optional<ShippingDiscountResponse> process(String rawLine) {

        ShippingInfo shippingInfo;
        try {
            shippingInfo = this.shippingInfoMapper.convert(rawLine);
        } catch (Exception e) {
            return Optional.empty();
        }
        var initialPrice = this.shippingPriceProvider.getPrice(shippingInfo.shippingCarrier(), shippingInfo.packageSize());
        var finalPrice = this.shippingSuggestedPriceProvider.findSuggestedPrice(shippingInfo, initialPrice);
        var discount = initialPrice.subtract(finalPrice);

        var calculatedDiscount = new ShippingDiscountResponse(shippingInfo, finalPrice, discount);
        this.shippingInfoRepo.addSuccess(calculatedDiscount);
        return Optional.of(calculatedDiscount);
    }
}
