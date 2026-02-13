package com.origin.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="schedule")
@Data
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    private String who;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime start_time;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime end_time;

    @Column(name = "location")
    private String location;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    private String color;

    @Column(name = "is_completed")
    private Boolean is_completed = false;

    @Column(name = "reminder_time")
    private LocalDateTime reminder_time;
}