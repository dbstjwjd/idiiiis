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

    private String division;

    private String receiver;

    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;
}
