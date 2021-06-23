package com.joklek.vinted.service;

import com.joklek.vinted.model.PackageSize;
import com.joklek.vinted.model.ShippingCarrier;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

import static com.joklek.vinted.model.PackageSize.LARGE;
import static com.joklek.vinted.model.PackageSize.MEDIUM;
import static com.joklek.vinted.model.PackageSize.SMALL;
import static com.joklek.vinted.model.ShippingCarrier.LA_POSTE;
import static com.joklek.vinted.model.ShippingCarrier.MONDIAL_RELAY;

public class ShippingPriceProvider {

    private final Map<Pair<ShippingCarrier, PackageSize>, BigDecimal> priceMap = Map.of(
            Pair.of(LA_POSTE, SMALL), BigDecimal.valueOf(1.5),
            Pair.of(LA_POSTE, MEDIUM), BigDecimal.valueOf(4.9),
            Pair.of(LA_POSTE, LARGE), BigDecimal.valueOf(6.9),
            Pair.of(MONDIAL_RELAY, SMALL), BigDecimal.valueOf(2),
            Pair.of(MONDIAL_RELAY, MEDIUM), BigDecimal.valueOf(3),
            Pair.of(MONDIAL_RELAY, LARGE), BigDecimal.valueOf(4)
    );

    public BigDecimal getPrice(ShippingCarrier shippingCarrier, PackageSize packageSize) {
        return this.priceMap.get(Pair.of(shippingCarrier, packageSize));
    }

    private record Pair<A, B>(A first, B second) {

        public static <A, B> Pair<A, B> of(A first, B second) {
            return new Pair<>(first, second);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Pair<?, ?> pair = (Pair<?, ?>) o;
            return this.first.equals(pair.first) && this.second.equals(pair.second);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.first, this.second);
        }
    }
}
