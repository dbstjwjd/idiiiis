package com.iris.iris.dto;

import com.iris.iris.entity.Present;
import lombok.Data;

@Data
public class HolidayDTO {
    private String receiver;

    private Present present;

    private String address;

    private String detail;

    private String postCode;

    private Long personId;
}
