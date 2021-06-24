package com.joklek.vinted;

import com.joklek.vinted.service.ShippingInfoMapper;
import com.joklek.vinted.service.ShippingPriceProvider;
import com.joklek.vinted.service.ShippingSuggestedPriceProvider;
import com.joklek.vinted.service.rules.SmallShipmentsRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class Main {

    private static final String DEFAULT_INPUT_NAME = "input.txt";
    private static final ShippingPriceCalculator shippingPriceCalculator = new ShippingPriceCalculator(
            new ShippingInfoMapper(),
            new ShippingPriceProvider(),
            new ShippingSuggestedPriceProvider(List.of(new SmallShipmentsRule())));

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        String fileName = args.length != 0 ? args[0] : DEFAULT_INPUT_NAME;

        var filepath = Path.of(fileName);
        if (Files.notExists(filepath)) {
            System.out.printf("File in given path %s does not exist%n", filepath);
            return;
        }

        List<String> fileLines;
        try {
            fileLines = Files.readAllLines(filepath);
        } catch (IOException e) {
            System.out.printf("Something wrong happened while reading the files in path %s%n", filepath);
            return;
        }

        var processedShipping = shippingPriceCalculator.process(fileLines);
        for (var shipping : processedShipping) {
            if (shipping.getError().isPresent()) {
                System.out.printf("%s Ignored %n", shipping.getError().get());
            } else {
                var shippingInfo = shipping.getSuccess().get();
                if (shippingInfo.discount().isEmpty()) {
                    System.out.printf("%s %s %s %.2f - %n", shippingInfo.date(), shippingInfo.packageSize().getShortVersion(), shippingInfo.shippingCarrier().getShortVersion(), shippingInfo.price());
                } else {
                    System.out.printf("%s %s %s %.2f %.2f %n", shippingInfo.date(), shippingInfo.packageSize().getShortVersion(), shippingInfo.shippingCarrier().getShortVersion(), shippingInfo.price(), shippingInfo.discount().get());
                }
            }
        }
    }
}
