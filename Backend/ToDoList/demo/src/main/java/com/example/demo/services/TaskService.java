package com.example.demo.services;

import com.example.demo.DTO.TaskDTO;
import com.example.demo.entities.Task;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public interface TaskService
{
    List<TaskDTO> getTasksById(Long userId, int page,int size);

    Task changeStatus(TaskDTO taskdto, Long id);

    Task addNewTask(TaskDTO taskdto, Long userId) throws IOException;

    void deleteTask(Long userId, Long taskId);

    void updateTask(Long id, TaskDTO taskDTO);

    List<TaskDTO> getTasksSortedByDueDate(Long userId);

    List<TaskDTO> getTasksByDueDate(Long userId, LocalDate dueDate);

    Task getProgressByTaskId(Long id);

    long countTasksByUserId(Long id);

    List<TaskDTO> getCompletedTask(Long userId);

    List<TaskDTO> getUnCompletedTask(Long userId);

    List<TaskDTO> getInProgressTask(Long userId);

    List<TaskDTO> getTasksSortedByDescription(Long userId);

    void updateTaskFilePath(Task task);

    Task getTaskById(Long taskId);

    List<TaskDTO> searchTasks(Long userId, String taskName);
}
