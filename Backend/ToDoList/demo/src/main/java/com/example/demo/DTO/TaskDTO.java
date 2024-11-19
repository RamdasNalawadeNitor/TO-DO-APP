package com.example.demo.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDate;

@Getter
@Setter
@ToString
public class TaskDTO
{
    private Long id;

    private String description;

    private boolean status;

    private LocalDate dueDate;

    private int progress;

    private String filePath;
}
