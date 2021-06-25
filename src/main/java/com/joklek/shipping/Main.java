package com.joklek.shipping;

import com.joklek.shipping.service.ShippingInfoMapper;
import com.joklek.shipping.service.ShippingInfoRepo;
import com.joklek.shipping.service.ShippingPriceProvider;
import com.joklek.shipping.service.ShippingSuggestedPriceProvider;
import com.joklek.shipping.service.rules.DiscountAccumulationLimitRule;
import com.joklek.shipping.service.rules.SmallShipmentsRule;
import com.joklek.shipping.service.rules.ThirdLargeForLaPosteRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class Main {

    private static final String DEFAULT_INPUT_NAME = "input.txt";

    private static final ShippingPriceCalculator shippingPriceCalculator;

    // Imagine that this block is automagically autowired by Spring or something else
    static {
        var shippingPriceProvider = new ShippingPriceProvider();
        var shippingInfoRepo = new ShippingInfoRepo();
        shippingPriceCalculator = new ShippingPriceCalculator(
                new ShippingInfoMapper(), shippingPriceProvider, new ShippingSuggestedPriceProvider(List.of(
                new SmallShipmentsRule(shippingPriceProvider),
                new ThirdLargeForLaPosteRule(shippingInfoRepo),
                new DiscountAccumulationLimitRule(shippingInfoRepo))), shippingInfoRepo
        );
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        String fileName = args.length != 0 ? args[0] : DEFAULT_INPUT_NAME;

        var filepath = Path.of(fileName);
        if (Files.notExists(filepath)) {
            System.out.printf("File in given path \"%s\" does not exist%n", filepath);
            return;
        }

        try {
            Files.lines(filepath)
                    .forEach(Main::consumeRawLine);
        } catch (IOException e) {
            System.out.printf("Something wrong happened while reading the files in path \"%s\"%n", filepath);
            return;
        }
    }

    private static void consumeRawLine(String rawLine) {
        var processedShipment = shippingPriceCalculator.process(rawLine);
        if (processedShipment.isEmpty()) {
            System.out.printf("%s Ignored %n", rawLine);
        } else {
            var shipment = processedShipment.get();
            if (shipment.discount().isEmpty()) {
                System.out.printf("%s %s %s %.2f - %n", shipment.date(), shipment.packageSize().getShortVersion(), shipment.shippingCarrier().getShortVersion(), shipment.price());
            } else {
                System.out.printf("%s %s %s %.2f %.2f %n", shipment.date(), shipment.packageSize().getShortVersion(), shipment.shippingCarrier().getShortVersion(), shipment.price(), shipment.discount().get());
            }
        }
    }
}
