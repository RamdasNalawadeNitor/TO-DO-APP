package com.example.demo.repositories;

import com.example.demo.entities.Task;
import com.example.demo.entities.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>
{
    Optional<Task> findById(Long id);

    Page<Task> getTasksById(Long userId, Pageable pageable);

//    List<Task> findByAddedBy(Users user, Pageable pageable);

    List<Task> findByAddedByOrderByDueDate(Users user);

    List<Task> findByDueDate(LocalDate dueDate);

    @Query("SELECT t FROM Task t WHERE t.status = true")
    List<Task> findByStatus(Long userId);

    @Query("SELECT t FROM Task t WHERE t.status = false")
    List<Task> findByStatusO(Long userId);

    @Query("SELECT t FROM Task t where t.progress between 1 and 99")
    List<Task> findByStausInProgress(Long userId);

    List<Task> findByAddedByOrderByDescription(Users user);

    List<Task> findByAddedByAndDescriptionContaining(Users user, String searchTerm);
}
