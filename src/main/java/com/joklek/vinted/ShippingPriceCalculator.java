package com.joklek.vinted;

import com.joklek.vinted.model.ShippingDiscountResponse;
import com.joklek.vinted.model.ShippingInfo;
import com.joklek.vinted.model.SuccessOrRaw;
import com.joklek.vinted.service.ShippingInfoMapper;
import com.joklek.vinted.service.ShippingPriceProvider;
import com.joklek.vinted.service.ShippingSuggestedPriceProvider;

import java.util.List;
import java.util.stream.Collectors;

public class ShippingPriceCalculator {

    private final ShippingInfoMapper shippingInfoMapper;
    private final ShippingPriceProvider shippingPriceProvider;
    private final ShippingSuggestedPriceProvider shippingSuggestedPriceProvider;

    public ShippingPriceCalculator(ShippingInfoMapper shippingInfoMapper, ShippingPriceProvider shippingPriceProvider, ShippingSuggestedPriceProvider shippingSuggestedPriceProvider) {
        this.shippingInfoMapper = shippingInfoMapper;
        this.shippingPriceProvider = shippingPriceProvider;
        this.shippingSuggestedPriceProvider = shippingSuggestedPriceProvider;
    }

    public List<SuccessOrRaw<ShippingDiscountResponse>> process(List<String> rawLines) {

        return rawLines.stream().map(rawLine -> {
            ShippingInfo shippingInfo;
            try {
                shippingInfo = this.shippingInfoMapper.convert(rawLine);
            } catch (Exception e) {
                return (SuccessOrRaw<ShippingDiscountResponse>) SuccessOrRaw.error(rawLine);
            }
            var initialPrice = this.shippingPriceProvider.getPrice(shippingInfo.shippingCarrier(), shippingInfo.packageSize());
            var suggestedPrice = this.shippingSuggestedPriceProvider.findSuggestedPrice(shippingInfo);
            var finalPrice = suggestedPrice.orElse(initialPrice);
            var discount = initialPrice.subtract(finalPrice);
            return SuccessOrRaw.success(new ShippingDiscountResponse(shippingInfo, finalPrice, discount));
        }).collect(Collectors.toList());
    }
}
