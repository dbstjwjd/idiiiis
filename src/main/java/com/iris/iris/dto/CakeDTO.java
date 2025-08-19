package com.iris.iris.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CakeDTO {
    private LocalDate deliveryDate;

    private String postCode;

    private String address;

    private String detail;

    private String division;

    private String receiver;

    private Long personId;
}
