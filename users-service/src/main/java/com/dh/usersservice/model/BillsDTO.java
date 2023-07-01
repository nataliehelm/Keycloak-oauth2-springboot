package com.dh.usersservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillsDTO {

    private String idBill;

    private String customerBill;

    private String productBill;

    private Double totalPrice;

    public BillsDTO(String idBill, String customerBill, String productBill, Double totalPrice) {
        this.idBill = idBill;
        this.customerBill = customerBill;
        this.productBill = productBill;
        this.totalPrice = totalPrice;
    }
}
