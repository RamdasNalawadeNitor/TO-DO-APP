package com.example.demo.controllers;

import com.example.demo.DTO.TaskDTO;
import com.example.demo.entities.ApiResponse;
import com.example.demo.entities.Task;
import com.example.demo.services.FileService;
import com.example.demo.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping()
@CrossOrigin
public class TaskController
{
    @Autowired
    private TaskService taskService;

    @Autowired
    private FileService fileService;

    @GetMapping("/tasks/{id}")
    public ResponseEntity<Map<String, Object>> getTasksById(
            @PathVariable Long id,
            @RequestParam() int page,
            @RequestParam() int size) {

        Map<String, Object> response = new HashMap<>();
        List<TaskDTO> tasks = taskService.getTasksById(id, page, size);
        long totalTasks = taskService.countTasksByUserId(id);

        response.put("tasks", tasks);
        response.put("totalTasks", totalTasks);
        System.out.println(totalTasks);
        return ResponseEntity.ok(response);
    }

    @PutMapping("{id}/status")
    public ResponseEntity<Task> updateStatus(@RequestBody TaskDTO taskDTO, @PathVariable Long id)
    {
        Task task = taskService.changeStatus(taskDTO, id);
        return ResponseEntity.ok(task);
    }

    @PostMapping(value = "/task/{userId}")
    public ResponseEntity<ApiResponse> addNewTask(
            @PathVariable Long userId,
            @ModelAttribute TaskDTO taskDTO) throws IOException
    {
        Task task = taskService.addNewTask(taskDTO, userId);

        if (task != null) {
            return ResponseEntity.ok(new ApiResponse("New task added"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Unable to add new task"));
        }
    }

    @DeleteMapping("/task/{userId}/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long userId, @PathVariable Long taskId) {
        try {
            taskService.deleteTask(userId, taskId);
            return ResponseEntity.ok("Task Deleted");
        } catch (Exception e) {
            System.err.println("Error deleting task: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete task");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        try {
            taskService.updateTask(id, taskDTO);
            return ResponseEntity.ok("Task updated");
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/sorted/{id}")
    public ResponseEntity<List<TaskDTO>> getTasksSortedByDueDate(@PathVariable Long id) {
        List<TaskDTO> tasks = taskService.getTasksSortedByDueDate(id);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/due-date")
    public ResponseEntity<List<TaskDTO>> getTasksByDueDate(
            @RequestParam(required = false) Long userId,
            @RequestParam LocalDate dueDate) {
        List<TaskDTO> tasks = taskService.getTasksByDueDate(userId, dueDate);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/task/progress/{id}")
    public ResponseEntity<?> getProgress(@PathVariable Long id)
    {
        taskService.getProgressByTaskId(id);
        return ResponseEntity.ok("Task progress fetched");
    }

    @GetMapping("/tasks/completed/{id}")
    public ResponseEntity<List<TaskDTO>> getCompletedTasks(@PathVariable Long id)
    {
        List<TaskDTO> tasks = taskService.getCompletedTask(id);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/uncompleted/{id}")
    public ResponseEntity<List<TaskDTO>> getUnCompletedTasks(@PathVariable Long id)
    {
        List<TaskDTO> tasks = taskService.getUnCompletedTask(id);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/inprogress/{id}")
    public ResponseEntity<List<TaskDTO>> getInProgressTasks(@PathVariable Long id)
    {
        List<TaskDTO> tasks = taskService.getInProgressTask(id);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/downloadfile/{id}")
    public ResponseEntity<Resource> downloadTaskFile(@PathVariable Long id)
    {
        Resource resource = fileService.loadFileAsResource(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/sortedbydescription/{id}")
    public ResponseEntity<List<TaskDTO>> getTasksSortedByDescription(@PathVariable Long id)
    {
        List<TaskDTO> tasks = taskService.getTasksSortedByDescription(id);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/task/{id}/preview")
    public ResponseEntity<String> previewFile(@PathVariable Long id) {
        try {
            String filePreview = fileService.previewFile(id);
            return ResponseEntity.ok(filePreview);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to preview file");
        }
    }

    @PostMapping(value = "/{taskId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> uploadFile(
            @PathVariable Long taskId,
            @RequestPart MultipartFile file) {

        // Check if the file is empty
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("File cannot be empty."));
        }

        // Check if the task exists
        Task task = taskService.getTaskById(taskId);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Task not found"));
        }

        try {
            // Upload the file using FileService
            String filePathStored = fileService.uploadFile(file);

            // Update the task with the new file path
            task.setFilePath(filePathStored);
            taskService.updateTaskFilePath(task); // Update the task in the service layer

            return ResponseEntity.ok(new ApiResponse("File uploaded successfully"));
        } catch (IOException e) {
            // Handle file upload errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("File upload failed: " + e.getMessage()));
        } catch (Exception e) {
            // Handle any other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/tasks/search/{id}")
    public ResponseEntity<List<TaskDTO>> searchTasks
            (@PathVariable Long id,
             String taskName)
    {
        List<TaskDTO> tasks = taskService.searchTasks(id, taskName);
        return ResponseEntity.ok(tasks);
    }
}

