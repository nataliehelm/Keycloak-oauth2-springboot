package com.dh.usersservice.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserBillsDTO {

    private String id;
    private String username;
    private String email;
    private String firstName;
    private List<BillsDTO> bills;

    public UserBillsDTO(String id, String username, String email, String firstName, List<BillsDTO> bills) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.bills = bills;
    }
}
