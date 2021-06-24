package com.joklek.vinted;

import com.joklek.vinted.model.PackageSize;
import com.joklek.vinted.model.ShippingCarrier;
import com.joklek.vinted.model.ShippingInfo;

import java.time.LocalDate;

public class ShippingInfoMapper {

    private static final String SEPARATOR = "\\s+"; // Separate at multiple whitespaces in line, just in case of user error

    public ShippingInfo convert(String raw) {
        var splitString = raw.split(SEPARATOR);
        if (splitString.length != 3) {
            throw new IllegalArgumentException("There should be only 3 elements in the given line");
        }
        var date = this.convertToDate(splitString[0]);
        var packageSize = this.convertToSize(splitString[1]);
        var shippingCarrier = this.convertToShippingCarrier(splitString[2]);
        return new ShippingInfo(date, packageSize, shippingCarrier);
    }

    private LocalDate convertToDate(String rawDate) {
        return LocalDate.parse(rawDate);
    }

    private PackageSize convertToSize(String rawSize) {
        return PackageSize.convertFromRaw(rawSize);
    }

    private ShippingCarrier convertToShippingCarrier(String rawShippingCarrier) {
        return ShippingCarrier.convertFromRaw(rawShippingCarrier);
    }
}
