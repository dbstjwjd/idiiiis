package com.iris.iris.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GiftDTO {
    private String receiver;

    private String address;

    private String detail;

    private String postCode;

    private Long personId;
}
