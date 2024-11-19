package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Task
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private boolean status;

    private LocalDate dueDate;

    private int progress;

    private String filePath;

    @ManyToOne()
    @JoinColumn(name = "addedBy")
    private Users addedBy;
}
