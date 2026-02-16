package com.origin.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="password")
    private String password;

    @Column(name="avatar")
    private String avatar;

    @Column(name="email")
    private String email;

    @Column(name="status",columnDefinition = "tinyint default 1")
    private Integer status;

    @Column(name="create_time", updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private LocalDateTime create_time;

    @Column(name="update_time")
    @UpdateTimestamp
    private LocalDateTime update_time;
}
