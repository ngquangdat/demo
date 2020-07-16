package com.example.demo.model.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Table
@Entity
public class User {
    @Id
    @Column
    private Integer id;

    @Column
    private String name;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String phone;

    @Column
    private Integer point;

    @Column
    private String image;

    @Column
    private Date createdTime;

    @Column
    private Date updatedTime;

    @PrePersist
    public void prePersist(){
        createdTime = new Date();
    }

    @PreUpdate
    public void preUpdate(){
        updatedTime = new Date();
    }


}
