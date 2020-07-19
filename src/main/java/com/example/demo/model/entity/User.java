package com.example.demo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Data
@Table
@Entity
public class User {
    @Id
    @Column
    private String id;

    @Column
    private String name;

    @Column
    private String username;

    @JsonIgnore
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
        id = UUID.randomUUID().toString();
        createdTime = new Date();
    }

    @PreUpdate
    public void preUpdate(){
        updatedTime = new Date();
    }


}
