package com.taskmanager.task.task.repository;

import com.taskmanager.task.task.entity.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task>  findByUserId(String userId);
    Optional<Task> findByIdAndUserId(String id, String userId);
    List<Task> findByUserIdAndStatusIgnoreCase(String userId, String status);
    List<Task> findByUserIdAndPriorityIgnoreCase(String userId, String priority);
    List<Task> findByDueDateBeforeAndStatusNotIgnoreCase(
            LocalDateTime dateTime,
            String status
    );
}
