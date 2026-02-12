package com.origin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="friend")
@Data
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private int id;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "number")
    private Long number;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "hometown")
    private String hometown;

    @Column(name = "tags")
    private String tags;

    @Column(name = "note")
    private String note;

    @Column(name = "last_modified")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastModified;

    @Column(name = "age")
    private Integer age;
}
