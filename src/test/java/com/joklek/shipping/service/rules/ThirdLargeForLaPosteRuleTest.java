package com.joklek.shipping.service.rules;

import com.joklek.shipping.model.PackageSize;
import com.joklek.shipping.model.ShippingCarrier;
import com.joklek.shipping.model.ShippingDiscountResponse;
import com.joklek.shipping.model.ShippingInfo;
import com.joklek.shipping.service.ShippingInfoRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.joklek.shipping.model.PackageSize.LARGE;
import static com.joklek.shipping.model.PackageSize.MEDIUM;
import static com.joklek.shipping.model.PackageSize.SMALL;
import static com.joklek.shipping.model.ShippingCarrier.LA_POSTE;
import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;

class ThirdLargeForLaPosteRuleTest {

    @Test
    void getSuggestedPrice__whenThirdShipmentInMonth__thenPriceShouldBeZero() {
        var shipmentsInRepo = List.of(
                new ShippingDiscountResponse(LocalDate.parse("2021-06-24"), LARGE, LA_POSTE, BigDecimal.valueOf(10)),
                new ShippingDiscountResponse(LocalDate.parse("2021-06-25"), LARGE, LA_POSTE, BigDecimal.valueOf(10))
        );
        var rule = new ThirdLargeForLaPosteRule(new ShippingInfoRepo(shipmentsInRepo));
        var shippingInfo = new ShippingInfo(LocalDate.parse("2021-06-26"), LARGE, LA_POSTE);
        var suggestedPrice = rule.getSuggestedPrice(shippingInfo, BigDecimal.valueOf(10), BigDecimal.valueOf(10));

        assertThat(rule.isApplicableFor(shippingInfo)).isTrue();
        assertThat(suggestedPrice).isZero();
    }

    @Test
    void getSuggestedPrice__whenFirstShipmentInMonth__thenPriceShouldBePrevious() {
        var shipmentsInRepo = List.of(
                new ShippingDiscountResponse(LocalDate.parse("2021-06-24"), LARGE, LA_POSTE, BigDecimal.valueOf(10)),
                new ShippingDiscountResponse(LocalDate.parse("2021-06-25"), LARGE, LA_POSTE, BigDecimal.valueOf(10))
        );
        var rule = new ThirdLargeForLaPosteRule(new ShippingInfoRepo(shipmentsInRepo));
        var shippingInfo = new ShippingInfo(LocalDate.parse("2021-07-01"), LARGE, LA_POSTE);
        var initialPrice = BigDecimal.valueOf(11);
        var currentSuggestedPrice = BigDecimal.valueOf(9);
        var suggestedPrice = rule.getSuggestedPrice(shippingInfo, initialPrice, currentSuggestedPrice);

        assertThat(rule.isApplicableFor(shippingInfo)).isTrue();
        assertThat(suggestedPrice).isEqualByComparingTo(currentSuggestedPrice);
    }

    /**
     * Test to ensure, that the rule is triggered only on the first time, not every 3rd
     */
    @Test
    void getSuggestedPrice__whenSixthShipmentInMonth__thenPriceShouldBePrevious() {
        var shipmentsInRepo = List.of(
                new ShippingDiscountResponse(LocalDate.parse("2021-06-24"), LARGE, LA_POSTE, BigDecimal.valueOf(10)),
                new ShippingDiscountResponse(LocalDate.parse("2021-06-24"), LARGE, LA_POSTE, BigDecimal.valueOf(10)),
                new ShippingDiscountResponse(LocalDate.parse("2021-06-24"), LARGE, LA_POSTE, BigDecimal.valueOf(10)),
                new ShippingDiscountResponse(LocalDate.parse("2021-06-24"), LARGE, LA_POSTE, BigDecimal.valueOf(10)),
                new ShippingDiscountResponse(LocalDate.parse("2021-06-25"), LARGE, LA_POSTE, BigDecimal.valueOf(10))
        );
        var rule = new ThirdLargeForLaPosteRule(new ShippingInfoRepo(shipmentsInRepo));
        var shippingInfo = new ShippingInfo(LocalDate.parse("2021-06-26"), LARGE, LA_POSTE);
        var initialPrice = BigDecimal.valueOf(11);
        var currentSuggestedPrice = BigDecimal.valueOf(9);
        var suggestedPrice = rule.getSuggestedPrice(shippingInfo, initialPrice, currentSuggestedPrice);

        assertThat(rule.isApplicableFor(shippingInfo)).isTrue();
        assertThat(suggestedPrice).isEqualByComparingTo(currentSuggestedPrice);
    }

    @Test
    void getSuggestedPrice__whenThirdShipmentInMonthButOthersNotLarge__thenPriceShouldNotBeZero() {
        var shipmentsInRepo = List.of(
                new ShippingDiscountResponse(LocalDate.parse("2021-06-24"), MEDIUM, LA_POSTE, BigDecimal.valueOf(10)),
                new ShippingDiscountResponse(LocalDate.parse("2021-06-25"), SMALL, LA_POSTE, BigDecimal.valueOf(10))
        );
        var rule = new ThirdLargeForLaPosteRule(new ShippingInfoRepo(shipmentsInRepo));
        var shippingInfo = new ShippingInfo(LocalDate.parse("2021-06-26"), LARGE, LA_POSTE);
        var suggestedPrice = rule.getSuggestedPrice(shippingInfo, BigDecimal.valueOf(12), BigDecimal.valueOf(12));

        assertThat(rule.isApplicableFor(shippingInfo)).isTrue();
        assertThat(suggestedPrice).isEqualByComparingTo(BigDecimal.valueOf(12));
    }

    private static Stream<Arguments> alternatePackagingSource() {
        return Stream.concat(
                Arrays.stream(PackageSize.values())
                        .filter(not(LARGE::equals))
                        .map(size -> Arguments.of(size, LA_POSTE)),
                Arrays.stream(ShippingCarrier.values())
                        .filter(not(LA_POSTE::equals))
                        .flatMap(carrier -> Arrays.stream(PackageSize.values())
                                .map(size -> Arguments.of(size, carrier))));
    }

    @ParameterizedTest
    @MethodSource("alternatePackagingSource")
    void isApplicableFor__otherThanLaPosteAndLarge__shouldNotBeApplicable(PackageSize size, ShippingCarrier carrier) {
        var rule = new ThirdLargeForLaPosteRule(new ShippingInfoRepo());
        var shippingInfo = new ShippingInfo(LocalDate.parse("2021-06-26"), size, carrier);

        assertThat(rule.isApplicableFor(shippingInfo)).isFalse();
    }
}