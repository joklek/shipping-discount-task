package com.joklek.vinted.service;

import com.joklek.vinted.model.PackageSize;
import com.joklek.vinted.model.ShippingCarrier;
import com.joklek.vinted.model.ShippingDiscountResponse;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class ShippingInfoRepo {

    private final List<ShippingDiscountResponse> processedShipments;

    public ShippingInfoRepo() {
        this.processedShipments = new ArrayList<>();
    }

    public ShippingInfoRepo(List<ShippingDiscountResponse> processedShipments) {
        this.processedShipments = processedShipments;
    }

    public void addSuccess(ShippingDiscountResponse success) {
        this.processedShipments.add(success);
    }

    public List<ShippingDiscountResponse> findShipmentsOnMonthForCarrierAndMonth(int year, Month month, ShippingCarrier carrier, PackageSize size) {
        return this.findShipmentsOnMonth(year, month).stream()
                .filter(shipment -> shipment.shippingCarrier().equals(carrier))
                .filter(shipment -> shipment.packageSize().equals(size))
                .toList();
    }

    public List<ShippingDiscountResponse> findShipmentsOnMonth(int year, Month month) {
        return this.processedShipments.stream()
                .filter(shipment -> shipment.date().getYear() == year && shipment.date().getMonth().equals(month))
                .toList();
    }
}
