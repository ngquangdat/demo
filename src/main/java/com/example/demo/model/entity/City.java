package com.example.demo.model.entity;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table
@Entity
public class City {
    @Id
    @Column
    private Integer id;

    @Column
    private String name;
}
