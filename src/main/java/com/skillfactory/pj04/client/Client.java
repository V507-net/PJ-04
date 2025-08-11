package com.skillfactory.pj04.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @Column(name = "id")
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id; //clients id

    @Column(name = "balance")
    public double balance;

}




