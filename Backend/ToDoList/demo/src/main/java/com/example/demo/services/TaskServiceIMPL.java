package com.example.demo.services;

import com.example.demo.DTO.TaskDTO;
import com.example.demo.entities.Task;
import com.example.demo.entities.Users;
import com.example.demo.repositories.TaskRepository;
import com.example.demo.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskServiceIMPL implements TaskService
{
    @Autowired
    private TaskRepository taskRepo;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FileService fileService;

    private Set<Long> notifiedTasks = new HashSet<>(); // Store notified task IDs


    //    @Cacheable(value = "taskList", key = "#id")
    @Override
    public List<TaskDTO> getTasksById(Long id, int page, int size) {
        System.out.println("1st time called");
        Optional<Users> optionalUsers = userRepository.findById(id);

        if (!optionalUsers.isPresent()) {
            return Collections.emptyList();
        }

        Users users = optionalUsers.get();
        List<Task> taskList = users.getList();

        // Check for tasks due tomorrow
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Task> tasksDueTomorrow = taskList.stream()
                .filter(task -> task.getDueDate() != null && task.getDueDate().isEqual(tomorrow))
                .filter(task -> !notifiedTasks.contains(task.getId())) // Filter out already notified tasks
                .collect(Collectors.toList());

        // Send email notifications for tasks due tomorrow
        if (!tasksDueTomorrow.isEmpty()) {
            sendDueDateReminderEmail(users, tasksDueTomorrow);
            // Mark tasks as notified
            tasksDueTomorrow.forEach(task -> notifiedTasks.add(task.getId()));
        }

        int start = Math.min(page * size, taskList.size());
        int end = Math.min(start + size, taskList.size());

        List<Task> pagedTasks = taskList.subList(start, end);

        return pagedTasks.stream()
                .map(task -> mapper.map(task, TaskDTO.class))
                .collect(Collectors.toList());
    }

    private void sendDueDateReminderEmail(Users user, List<Task> tasks) {
        String recipientEmail = user.getEmail(); // Assuming Users has an email field
        String subject = "Reminder: Your Tasks are Due Tomorrow!";
        StringBuilder emailBody = new StringBuilder("Dear " + user.getName() + ",\n\n")
                .append("This is a friendly reminder that you have the following tasks due tomorrow:\n\n");

        for (Task task : tasks) {
            emailBody.append("- Description: ").append(task.getDescription())
                    .append("\n  Due Date: ").append(task.getDueDate()).append("\n");
        }

        emailBody.append("\nPlease take a moment to review your tasks and ensure that you're on track to complete them.\n\n")
                .append("If you need any assistance or have any questions, feel free to reach out.\n\n")
                .append("Best regards,\n")
                .append("Task Force Team");

        // Send the email using EmailService
        emailService.sendSimpleEmail(recipientEmail, emailBody.toString(), subject);
    }

//    @CacheEvict(value = "taskList", key = "#id2")
    @Override
    public Task changeStatus(TaskDTO taskdto, Long id) {
        Optional<Task> optionalTask = taskRepo.findById(id);
        Task task = optionalTask.get();
        task.setStatus(true);
        task.setProgress(taskdto.getProgress());
        System.out.println("Status updated success !");
        return taskRepo.save(task);
    }

//    @CacheEvict(value = "taskList", key = "#id3")
    @Override
    public Task addNewTask(TaskDTO taskdto, Long userId) throws IOException {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = mapper.map(taskdto, Task.class);
        task.setAddedBy(user);
        task.setDueDate(taskdto.getDueDate());
        task.setStatus(taskdto.isStatus());
        task.setProgress(taskdto.getProgress());

        // Save the file and get the path
//        String filePathStored = fileService.uploadFile(file);
        task.setFilePath("");

        // Save the task to the database
        return taskRepo.save(task);
    }



    //    @CacheEvict(value = "taskList", key = "#id4")
    @Override
    public void deleteTask(Long userId, Long taskId) {
        Optional<Task> taskOptional = taskRepo.findById(taskId);

        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            System.out.println("Found task: " + task);
            if (task.getAddedBy().getId().equals(userId)) {
                taskRepo.deleteById(taskId);
                System.out.println("Task deleted successfully!");
            } else {
                System.err.println("Task does not belong to the specified user.");
                throw new RuntimeException("Task does not belong to the specified user");
            }
        } else {
            System.err.println("Task not found with ID: " + taskId);
            throw new RuntimeException("Task not found with ID: " + taskId);
        }
    }

//    @CacheEvict(value = "taskList", key = "#id5")
    public void updateTask(Long id, TaskDTO taskDTO) {
        Optional<Task> optionalTask = taskRepo.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setDescription(taskDTO.getDescription());
            task.setStatus(taskDTO.isStatus());
            task.setDueDate(taskDTO.getDueDate());
            task.setProgress(taskDTO.getProgress());
            taskRepo.save(task);
        } else {
            throw new RuntimeException("Task not found with id " + id);
        }
    }

    @Override
    public List<TaskDTO> getTasksSortedByDueDate(Long userId) {
        Optional<Users> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            return List.of();
        }

        Users user = optionalUser.get();
        List<Task> tasks = taskRepo.findByAddedByOrderByDueDate(user);
        return tasks.stream()
                .map(task -> mapper.map(task, TaskDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getTasksByDueDate(Long userId, LocalDate dueDate) {

        List<Task> tasks = taskRepo.findByDueDate(dueDate);

        // If userId is provided, filter tasks by userId
        if (userId != null) {
            Optional<Users> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                Users user = optionalUser.get();
                tasks = tasks.stream()
                        .filter(task -> task.getAddedBy().equals(user))
                        .collect(Collectors.toList());
            } else {
                tasks = List.of();
            }
        }

        // Convert tasks to TaskDTO and return
        return tasks.stream()
                .map(task -> mapper.map(task, TaskDTO.class))
                .collect(Collectors.toList());
    }

    public Task getProgressByTaskId(Long id)
    {
        Optional<Task> optionalTask = taskRepo.findById(id);
        Task task = optionalTask.get();
        System.out.println(task.getProgress());
        return taskRepo.save(task);
    }

    @Override
    public long countTasksByUserId(Long id) {
        return 8;
    }

    @Override
    public List<TaskDTO> getCompletedTask(Long userId) {
        List<Task> tasks = taskRepo.findByStatus(userId);

        if(userId != null)
        {
            Optional<Users> optionalUsers = userRepository.findById(userId);
            if(optionalUsers.isPresent())
            {
                Users users = optionalUsers.get();
                tasks = tasks.stream()
                        .filter(task -> task.getAddedBy().equals(users))
                        .filter(Task::isStatus)
                        .collect(Collectors.toList());
            }
            else {
                tasks = List.of();
            }
        }

        return tasks.stream()
                .map(task -> mapper.map(task, TaskDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getUnCompletedTask(Long userId)
    {
        List<Task> tasks = taskRepo.findByStatusO(userId);

        if(userId != null)
        {
            Optional<Users> optionalUsers = userRepository.findById(userId);

            if(optionalUsers.isPresent())
            {
                Users users = optionalUsers.get();
                tasks = tasks.stream()
                        .filter(task -> task.getAddedBy().equals(users))
                        .filter(task -> task.getProgress() == 0)
                        .collect(Collectors.toList());
            }
            else
            {
                tasks = List.of();
            }
        }
        return tasks.stream()
                .map(task -> mapper.map(task, TaskDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getInProgressTask(Long userId)
    {
        List<Task> tasks = taskRepo.findByStausInProgress(userId);

        if(userId != null)
        {
            Optional<Users> optionalUsers = userRepository.findById(userId);

            if(optionalUsers.isPresent()) {
                Users users = optionalUsers.get();
                tasks = tasks.stream()
                        .filter(task -> task.getAddedBy().equals(users))
                        .filter(task -> task.getProgress() >= 1 || task.getProgress() <= 99)
                        .collect(Collectors.toList());
            }
        }
        else
        {
            tasks = List.of();
        }
        return tasks.stream()
                .map(task -> mapper.map(task, TaskDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getTasksSortedByDescription(Long userId)
    {
        Optional<Users> optionalUsers = userRepository.findById(userId);
        if(!optionalUsers.isPresent())
            return List.of();

        Users users = optionalUsers.get();
        List<Task> tasks = taskRepo.findByAddedByOrderByDescription(users);
        return tasks.stream()
                .map(task -> mapper.map(task, TaskDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void updateTaskFilePath(Task task) {
        if (task == null || task.getId() == null) {
            throw new IllegalArgumentException("Task cannot be null and must have a valid ID");
        }

        Optional<Task> existingTaskOptional = taskRepo.findById(task.getId());
        if (existingTaskOptional.isPresent()) {
            Task existingTask = existingTaskOptional.get();
            existingTask.setFilePath(task.getFilePath());
            taskRepo.save(existingTask);
        } else {
            throw new RuntimeException("Task not found with ID: " + task.getId());
        }
    }

    public Task getTaskById(Long taskId) {
        return taskRepo.findById(taskId).orElse(null);
    }

    @Override
    public List<TaskDTO> searchTasks(Long userId, String taskName) {
        Users users = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Task> tasks = taskRepo.findByAddedByAndDescriptionContaining(users, taskName);

        return tasks.stream()
                .map(task -> mapper.map(task, TaskDTO.class))
                .collect(Collectors.toList());
    }

}
