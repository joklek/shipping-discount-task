package com.joklek.vinted.service;

import com.joklek.vinted.model.PackageSize;
import com.joklek.vinted.model.ShippingCarrier;
import com.joklek.vinted.model.ShippingDiscountResponse;
import com.joklek.vinted.model.SuccessOrRaw;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

// TODO maybe should only store successful ones?
public class ShippingInfoRepo {

    private final List<SuccessOrRaw<ShippingDiscountResponse>> processedShipments;

    public ShippingInfoRepo() {
        this.processedShipments = new ArrayList<>();
    }

    public ShippingInfoRepo(List<SuccessOrRaw<ShippingDiscountResponse>> processedShipments) {
        this.processedShipments = processedShipments;
    }

    public void addSuccess(ShippingDiscountResponse success) {
        this.processedShipments.add(SuccessOrRaw.success(success));
    }

    public void addError(String error) {
        this.processedShipments.add((SuccessOrRaw<ShippingDiscountResponse>) SuccessOrRaw.error(error));
    }

    public List<SuccessOrRaw<ShippingDiscountResponse>> getProcessedShipments() {
        return List.copyOf(this.processedShipments);
    }

    public List<ShippingDiscountResponse> findShipmentsOnMonthForCarrierAndMonth(int year, Month month, ShippingCarrier carrier, PackageSize size) {
        return this.findShipmentsOnMonth(year, month).stream()
                .filter(shipment -> shipment.shippingCarrier().equals(carrier))
                .filter(shipment -> shipment.packageSize().equals(size))
                .toList();
    }

    public List<ShippingDiscountResponse> findShipmentsOnMonth(int year, Month month) {
        return this.processedShipments.stream()
                .filter(shipmentLine -> shipmentLine.getSuccess().isPresent())
                .map(successfulShipment -> successfulShipment.getSuccess().get())
                .filter(shipment -> shipment.date().getYear() == year && shipment.date().getMonth().equals(month))
                .toList();
    }
}
