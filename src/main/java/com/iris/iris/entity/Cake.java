package com.iris.iris.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Cake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate deliveryDate;

    private String postCode;

    private String address;

    private String detail;

    private String division;

    private String receiver;

    @Enumerated(EnumType.STRING)
    private CakeType cakeType;

    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;
}
