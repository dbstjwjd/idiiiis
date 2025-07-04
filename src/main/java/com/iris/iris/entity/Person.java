package com.iris.iris.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "person")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Person {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "department", nullable = false)
    private String department;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "employee_number", nullable = false, unique = true)
    private String employeeNumber;

    @OneToOne
    private Holiday holiday;
}
