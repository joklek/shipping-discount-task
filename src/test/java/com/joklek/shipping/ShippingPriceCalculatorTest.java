package com.joklek.shipping;

import com.joklek.shipping.model.ShippingDiscountResponse;
import com.joklek.shipping.service.ShippingInfoMapper;
import com.joklek.shipping.service.ShippingInfoRepo;
import com.joklek.shipping.service.ShippingPriceProvider;
import com.joklek.shipping.service.ShippingSuggestedPriceProvider;
import com.joklek.shipping.service.rules.DiscountAccumulationLimitRule;
import com.joklek.shipping.service.rules.SmallShipmentsRule;
import com.joklek.shipping.service.rules.ThirdLargeForLaPosteRule;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.joklek.shipping.model.PackageSize.LARGE;
import static com.joklek.shipping.model.PackageSize.MEDIUM;
import static com.joklek.shipping.model.PackageSize.SMALL;
import static com.joklek.shipping.model.ShippingCarrier.LA_POSTE;
import static com.joklek.shipping.model.ShippingCarrier.MONDIAL_RELAY;
import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

class ShippingPriceCalculatorTest {

    private final ShippingPriceCalculator shippingPriceCalculator;

    public ShippingPriceCalculatorTest() {
        var shippingPriceProvider = new ShippingPriceProvider();
        var shippingInfoRepo = new ShippingInfoRepo();
        this.shippingPriceCalculator = new ShippingPriceCalculator(
                shippingInfoRepo,
                new ShippingInfoMapper(),
                shippingPriceProvider,
                new ShippingSuggestedPriceProvider(List.of(
                        new SmallShipmentsRule(shippingPriceProvider),
                        new ThirdLargeForLaPosteRule(shippingInfoRepo),
                        new DiscountAccumulationLimitRule(shippingInfoRepo))));
    }

    @Test
    void process__fromExample() {
        var rawLines = this.rawLinesFromExample();
        var responseFromExample = this.responseFromExample();

        var processedLines = rawLines.stream().map(this.shippingPriceCalculator::process).toList();

        assertThat(processedLines).containsExactlyElementsOf(responseFromExample);
    }

    private List<String> rawLinesFromExample() {
        List<String> rawLines = new ArrayList<>();
        rawLines.add("2015-02-01 S MR");
        rawLines.add("2015-02-02 S MR");
        rawLines.add("2015-02-03 L LP");
        rawLines.add("2015-02-05 S LP");
        rawLines.add("2015-02-06 S MR");
        rawLines.add("2015-02-06 L LP");
        rawLines.add("2015-02-07 L MR");
        rawLines.add("2015-02-08 M MR");
        rawLines.add("2015-02-09 L LP");
        rawLines.add("2015-02-10 L LP");
        rawLines.add("2015-02-10 S MR");
        rawLines.add("2015-02-10 S MR");
        rawLines.add("2015-02-11 L LP");
        rawLines.add("2015-02-12 M MR");
        rawLines.add("2015-02-13 M LP");
        rawLines.add("2015-02-15 S MR");
        rawLines.add("2015-02-17 L LP");
        rawLines.add("2015-02-17 S MR");
        rawLines.add("2015-02-24 L LP");
        rawLines.add("2015-02-29 CUSPS");
        rawLines.add("2015-03-01 S MR");
        return rawLines;
    }

    private List<Optional<ShippingDiscountResponse>> responseFromExample() {
        List<Optional<ShippingDiscountResponse>> response = new ArrayList<>();
        response.add(Optional.of(new ShippingDiscountResponse(LocalDate.parse("2015-02-01"), SMALL, MONDIAL_RELAY, valueOf(1.5), valueOf(0.5))));
        response.add(Optional.of(new ShippingDiscountResponse(LocalDate.parse("2015-02-02"), SMALL, MONDIAL_RELAY, valueOf(1.5), valueOf(0.5))));
        response.add(Optional.of(new ShippingDiscountResponse(LocalDate.parse("2015-02-03"), LARGE, LA_POSTE, valueOf(6.9), BigDecimal.ZERO)));
        response.add(Optional.of(new ShippingDiscountResponse(LocalDate.parse("2015-02-05"), SMALL, LA_POSTE, valueOf(1.5), BigDecimal.ZERO)));
        response.add(Optional.of(new ShippingDiscountResponse(LocalDate.parse("2015-02-06"), SMALL, MONDIAL_RELAY, valueOf(1.5), BigDecimal.valueOf(0.5))));
        response.add(Optional.of(new ShippingDiscountResponse(LocalDate.parse("2015-02-06"), LARGE, LA_POSTE, valueOf(6.9), BigDecimal.ZERO)));
        response.add(Optional.of(new ShippingDiscountResponse(LocalDate.parse("2015-02-07"), LARGE, MONDIAL_RELAY, valueOf(4.0), BigDecimal.ZERO)));
        response.add(Optional.of(new ShippingDiscountResponse(LocalDate.parse("2015-02-08"), MEDIUM, MONDIAL_RELAY, valueOf(3.0), BigDecimal.ZERO)));
        response.add(Optional.of(new ShippingDiscountResponse(LocalDate.parse("2015-02-09"), LARGE, LA_POSTE, BigDecimal.ZERO, BigDecimal.valueOf(6.9))));
        response.add(Optional.of(new ShippingDiscountResponse(LocalDate.parse("2015-02-10"), LARGE, LA_POSTE, BigDecimal.valueOf(6.9), BigDecimal.ZERO)));
        response.add(Optional.of(new ShippingDiscountResponse(LocalDate.parse("2015-02-10"), SMALL, MONDIAL_RELAY, BigDecimal.valueOf(1.5), BigDecimal.valueOf(0.5))));
        response.add(Optional.of(new ShippingDiscountResponse(LocalDate.parse("2015-02-10"), SMALL, MONDIAL_RELAY, BigDecimal.valueOf(1.5), BigDecimal.valueOf(0.5))));
        response.add(Optional.of(new ShippingDiscountResponse(LocalDate.parse("2015-02-11"), LARGE, LA_POSTE, BigDecimal.valueOf(6.9), BigDecimal.ZERO)));
        response.add(Optional.of(new ShippingDiscountResponse(LocalDate.parse("2015-02-12"), MEDIUM, MONDIAL_RELAY, BigDecimal.valueOf(3.0), BigDecimal.ZERO)));
        response.add(Optional.of(new ShippingDiscountResponse(LocalDate.parse("2015-02-13"), MEDIUM, LA_POSTE, BigDecimal.valueOf(4.9), BigDecimal.ZERO)));
        response.add(Optional.of(new ShippingDiscountResponse(LocalDate.parse("2015-02-15"), SMALL, MONDIAL_RELAY, BigDecimal.valueOf(1.5), BigDecimal.valueOf(0.5))));
        response.add(Optional.of(new ShippingDiscountResponse(LocalDate.parse("2015-02-17"), LARGE, LA_POSTE, BigDecimal.valueOf(6.9), BigDecimal.ZERO)));
        response.add(Optional.of(new ShippingDiscountResponse(LocalDate.parse("2015-02-17"), SMALL, MONDIAL_RELAY, BigDecimal.valueOf(1.9), BigDecimal.valueOf(0.1))));
        response.add(Optional.of(new ShippingDiscountResponse(LocalDate.parse("2015-02-24"), LARGE, LA_POSTE, BigDecimal.valueOf(6.9), BigDecimal.ZERO)));
        response.add(Optional.empty());
        response.add(Optional.of(new ShippingDiscountResponse(LocalDate.parse("2015-03-01"), SMALL, MONDIAL_RELAY, BigDecimal.valueOf(1.5), BigDecimal.valueOf(0.5))));
        return response;
    }
}