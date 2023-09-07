package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Table
@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String token;

    @Column
    private String accountId;

    @Column
    private Date expiredTime;

}
