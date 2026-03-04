package com.example.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "readers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String address;

    @OneToMany(mappedBy = "reader", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Loan> loans = new ArrayList<>();
}