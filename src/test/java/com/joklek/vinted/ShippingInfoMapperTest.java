package com.joklek.vinted;

import com.joklek.vinted.model.PackageSize;
import com.joklek.vinted.model.ShippingCarrier;
import com.joklek.vinted.service.ShippingInfoMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShippingInfoMapperTest {

    private final ShippingInfoMapper shippingInfoMapper = new ShippingInfoMapper();

    private static Stream<Arguments> shippingLineSource() {
        return Arrays.stream(ShippingCarrier.values())
                .flatMap(carrier -> Arrays.stream(PackageSize.values())
                        .map(packageSize -> Arguments.of("2015-02-01", packageSize, carrier)));
    }

    @ParameterizedTest
    @MethodSource("shippingLineSource")
    void convert(String date, PackageSize size, ShippingCarrier carrier) {
        var rawLine = String.format("%s %s %s", date, size.getShortVersion(), carrier.getShortVersion());
        var convert = this.shippingInfoMapper.convert(rawLine);

        assertThat(convert.date()).isEqualTo(date);
        assertThat(convert.packageSize()).isEqualTo(size);
        assertThat(convert.shippingCarrier()).isEqualTo(carrier);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2015-02-02 S ML",
            "2015-02-02 R MR",
            "2015-13-02 S MR"
    })
    void convert__whenInvalidMemberFormat__shouldThrowException(String invalidLine) {
        // TODO add more diversified exceptions so they could be validated, preferably, the exceptions should be defined in this project
        assertThatThrownBy(() -> this.shippingInfoMapper.convert(invalidLine));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "   ",
            "2015-02-02 S",
            "2015-02-03 MR",
            "2015-02-04 CUSPS"
    })
    void convert__whenInvalidNumberOfArguments__shouldThrowException(String invalidLine) {
        assertThatThrownBy(() -> this.shippingInfoMapper.convert(invalidLine))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("There should be only 3 elements in the given line");
    }
}