package com.joklek.vinted.service.rules;

import com.joklek.vinted.model.ShippingDiscountResponse;
import com.joklek.vinted.model.ShippingInfo;
import com.joklek.vinted.model.SuccessOrRaw;
import com.joklek.vinted.service.ShippingInfoRepo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.joklek.vinted.model.PackageSize.LARGE;
import static com.joklek.vinted.model.PackageSize.SMALL;
import static com.joklek.vinted.model.ShippingCarrier.LA_POSTE;
import static com.joklek.vinted.model.ShippingCarrier.MONDIAL_RELAY;
import static org.assertj.core.api.Assertions.assertThat;

class DiscountAccumulationLimitRuleTest {

    @Test
    void getSuggestedPrice__whenNoShipmentsThatMonth__thenShouldAllowFullPrice() {
        var rule = new DiscountAccumulationLimitRule(new ShippingInfoRepo());

        var initialPrice = BigDecimal.valueOf(10);
        var currentSuggestedPrice = BigDecimal.valueOf(6);
        var suggestedPrice = rule.getSuggestedPrice(new ShippingInfo(LocalDate.parse("2021-06-26"), SMALL, MONDIAL_RELAY), initialPrice, currentSuggestedPrice);

        assertThat(suggestedPrice).isEqualTo(currentSuggestedPrice);
    }

    @Test
    void getSuggestedPrice__whenDiscountBiggerThanLimit__thenShouldAllowToLimit() {
        var rule = new DiscountAccumulationLimitRule(new ShippingInfoRepo());

        var initialPrice = BigDecimal.valueOf(22);
        var currentSuggestedPrice = BigDecimal.valueOf(6);
        var suggestedPrice = rule.getSuggestedPrice(new ShippingInfo(LocalDate.parse("2021-06-26"), SMALL, MONDIAL_RELAY), initialPrice, currentSuggestedPrice);

        assertThat(suggestedPrice).isEqualTo(BigDecimal.valueOf(12));
    }

    @Test
    void getSuggestedPrice__whenDiscountEqualToMonthBalance__thenShouldAllowToLimit() {
        var rule = new DiscountAccumulationLimitRule(new ShippingInfoRepo(List.of(
                SuccessOrRaw.success(new ShippingDiscountResponse(LocalDate.parse("2021-06-24"), LARGE, LA_POSTE, BigDecimal.valueOf(10), BigDecimal.valueOf(3))),
                SuccessOrRaw.success(new ShippingDiscountResponse(LocalDate.parse("2021-06-24"), LARGE, LA_POSTE, BigDecimal.valueOf(10), BigDecimal.valueOf(5)))
        )));

        var initialPrice = BigDecimal.valueOf(6);
        var currentSuggestedPrice = BigDecimal.valueOf(4);
        var suggestedPrice = rule.getSuggestedPrice(new ShippingInfo(LocalDate.parse("2021-06-26"), SMALL, MONDIAL_RELAY), initialPrice, currentSuggestedPrice);

        assertThat(suggestedPrice).isEqualTo(BigDecimal.valueOf(4));
    }
}