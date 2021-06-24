package com.joklek.vinted;

import com.joklek.vinted.model.ShippingDiscountResponse;
import com.joklek.vinted.model.ShippingInfo;
import com.joklek.vinted.model.SuccessOrRaw;
import com.joklek.vinted.service.ShippingInfoMapper;
import com.joklek.vinted.service.ShippingInfoRepo;
import com.joklek.vinted.service.ShippingPriceProvider;
import com.joklek.vinted.service.ShippingSuggestedPriceProvider;

import java.util.List;

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

    public List<SuccessOrRaw<ShippingDiscountResponse>> process(List<String> rawLines) {

        for (String rawLine : rawLines) {
            ShippingInfo shippingInfo;
            try {
                shippingInfo = this.shippingInfoMapper.convert(rawLine);
            } catch (Exception e) {
                this.shippingInfoRepo.addError(rawLine);
                continue;
            }
            var initialPrice = this.shippingPriceProvider.getPrice(shippingInfo.shippingCarrier(), shippingInfo.packageSize());
            var finalPrice = this.shippingSuggestedPriceProvider.findSuggestedPrice(shippingInfo, initialPrice);
            var discount = initialPrice.subtract(finalPrice);

            this.shippingInfoRepo.addSuccess(new ShippingDiscountResponse(shippingInfo, finalPrice, discount));
        }
        return this.shippingInfoRepo.getProcessedShipments();
    }
}
