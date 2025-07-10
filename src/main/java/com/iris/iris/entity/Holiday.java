package com.iris.iris.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Holiday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String receiver;

    @Enumerated(EnumType.STRING)
    private Present present;

    private String address;

    private String detail;

    private String postCode;
}
