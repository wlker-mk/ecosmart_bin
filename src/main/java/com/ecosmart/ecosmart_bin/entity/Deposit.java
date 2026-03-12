package com.ecosmart.ecosmart_bin.entity;

import jakarta.persistence.*;

@Entity
public class Deposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double poids;

    private int points;

    private String date;

    @ManyToOne
    private User user;

    @ManyToOne
    private Bin bin;
}
