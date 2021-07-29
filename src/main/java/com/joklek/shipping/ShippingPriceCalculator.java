package com.joklek.shipping;

import com.joklek.shipping.model.ShippingDiscountResponse;
import com.joklek.shipping.model.ShippingInfo;
import com.joklek.shipping.service.ShippingInfoMapper;
import com.joklek.shipping.service.ShippingInfoRepo;
import com.joklek.shipping.service.ShippingPriceProvider;
import com.joklek.shipping.service.ShippingSuggestedPriceProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class ShippingPriceCalculator {

    private static final Logger LOGGER = LogManager.getLogger(ShippingPriceCalculator.class);

    private final ShippingInfoMapper shippingInfoMapper;
    private final ShippingPriceProvider shippingPriceProvider;
    private final ShippingSuggestedPriceProvider shippingSuggestedPriceProvider;
    private final ShippingInfoRepo shippingInfoRepo;

    public ShippingPriceCalculator(ShippingInfoMapper shippingInfoMapper, ShippingPriceProvider shippingPriceProvider, ShippingSuggestedPriceProvider shippingSuggestedPriceProvider, ShippingInfoRepo shippingInfoRepo) {
        this.shippingInfoMapper = shippingInfoMapper;
        this.shippingPriceProvider = shippingPriceProvider;
        this.shippingSuggestedPriceProvider = shippingSuggestedPriceProvider;
        this.shippingInfoRepo = shippingInfoRepo;
    }

    public Optional<ShippingDiscountResponse> process(String rawLine) {

        ShippingInfo shippingInfo;
        try {
            shippingInfo = this.shippingInfoMapper.convert(rawLine);
        } catch (Exception e) {
            LOGGER.error("Cannot map line '{}' into valid shipping info", rawLine, e);
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
