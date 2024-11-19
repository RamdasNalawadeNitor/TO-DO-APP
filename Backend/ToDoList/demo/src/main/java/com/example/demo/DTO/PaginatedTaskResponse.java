package com.example.demo.DTO;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PaginatedTaskResponse {
    private List<TaskDTO> content; // The list of tasks
    private int totalPages; // Total number of pages
    private long totalElements; // Total number of tasks
    private int size; // Number of tasks per page
    private int number; // Current page number

    public PaginatedTaskResponse(List<TaskDTO> content, int totalPages, long totalElements, int size, int number) {
        this.content = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.size = size;
        this.number = number;
    }
}
