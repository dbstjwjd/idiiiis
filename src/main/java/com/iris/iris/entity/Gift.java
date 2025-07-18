package com.iris.iris.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Gift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String receiver;

    private String address;

    private String detail;

    private String postCode;

    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;
}
