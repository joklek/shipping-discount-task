package com.joklek.vinted;

import com.joklek.vinted.model.ShippingInfo;
import com.joklek.vinted.service.ShippingPriceProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class Main {

    private static final String DEFAULT_INPUT_NAME = "input.txt";
    private static final ShippingInfoMapper shippingInfoMapper = new ShippingInfoMapper();
    private static final ShippingPriceProvider shippingPriceProvider = new ShippingPriceProvider();

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

        for (String line : fileLines) {
            ShippingInfo converted;
            try {
                converted = shippingInfoMapper.convert(line);
            } catch (Exception e) {
                System.out.printf("%s Ignored %n", line);
                continue;
            }
            var price = shippingPriceProvider.getPrice(converted.shippingCarrier(), converted.packageSize());
            System.out.printf("%s %s %s %.2f %n", converted.date(), converted.packageSize().getShortVersion(), converted.shippingCarrier().getShortVersion(), price);
        }
    }
}
