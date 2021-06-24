package com.joklek.vinted.service;

import com.joklek.vinted.model.PackageSize;
import com.joklek.vinted.model.Pair;
import com.joklek.vinted.model.ShippingCarrier;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ShippingPriceProviderTest {

    private final ShippingPriceProvider shippingPriceProvider = new ShippingPriceProvider();

    private static Stream<Arguments> shippingConfigurationsSource() {
        return Arrays.stream(ShippingCarrier.values())
                .flatMap(carrier -> Arrays.stream(PackageSize.values())
                        .map(packageSize -> Arguments.of(packageSize, carrier)));
    }

    @ParameterizedTest
    @MethodSource("shippingConfigurationsSource")
    void getPrice(PackageSize size, ShippingCarrier carrier) {
        var price = this.shippingPriceProvider.getPrice(carrier, size);
        assertThat(price).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(PackageSize.class)
    void getPrices(PackageSize size) {
        var prices = this.shippingPriceProvider.getPrices(size);

        assertThat(prices).hasSameSizeAs(ShippingCarrier.values());
        assertThat(prices).extracting(Pair::first).doesNotContainNull();
        assertThat(prices).extracting(Pair::second).doesNotContainNull();
        assertThat(prices).extracting(Pair::first).containsExactlyInAnyOrder(ShippingCarrier.values());
    }
}