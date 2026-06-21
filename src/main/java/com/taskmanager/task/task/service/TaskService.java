package com.taskmanager.task.task.service;

import com.taskmanager.task.task.entity.Task;
import com.taskmanager.task.task.entity.User;
import com.taskmanager.task.task.repository.TaskRepository;
import com.taskmanager.task.task.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @CacheEvict(value = "tasks", key = "#username")
    public Task createTask(Task task, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        task.setUserId(user.getId());
        task.setUserEmail(user.getEmail());

        if(task.getStatus() == null || task.getStatus().isEmpty()){
            task.setStatus("PENDING");
        } else {
            task.setStatus(task.getStatus().toUpperCase());
        }

        if (task.getPriority() == null || task.getPriority().isEmpty()){
            task.setPriority("MEDIUM");
        } else {
            task.setPriority(task.getPriority().toUpperCase());
        }

        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        Task savedTask = taskRepository.save(task);

        emailService.sendEmail(
                savedTask.getUserEmail(),
                "Task Created Successfully",
                "Hello,\n\nYour task has been created successfully.\n\n" +
                        "Title: " + savedTask.getTitle() + "\n" +
                        "Description: " + savedTask.getDescription() + "\n" +
                        "Status: " + savedTask.getStatus() + "\n" +
                        "Priority: " + savedTask.getPriority() + "\n" +
                        "Due Date: " + savedTask.getDueDate() + "\n\n" +
                        "Thanks,\nTaskFlow Team"
        );
        return savedTask;
    }
     @Cacheable(value = "tasks", key = "#username")
    public List<Task> getMyTasks(String username){
         System.out.println("Fetching tasks from MongoDB...");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findByUserId(user.getId());
    }

    public Task getTaskById(String taskId, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @CacheEvict(value = "tasks", key = "#username")
    public Task updateTask(String taskId, Task updatedTask, String username) {

        Task existingTask = getTaskById(taskId, username);

        if (updatedTask.getTitle() != null && !updatedTask.getTitle().isEmpty()) {
            existingTask.setTitle(updatedTask.getTitle());
        }

        if (updatedTask.getDescription() != null && !updatedTask.getDescription().isEmpty()) {
            existingTask.setDescription(updatedTask.getDescription());
        }

        if (updatedTask.getStatus() != null && !updatedTask.getStatus().isEmpty()) {
            existingTask.setStatus(updatedTask.getStatus().toUpperCase());
        }

        if (updatedTask.getPriority() != null && !updatedTask.getPriority().isEmpty()) {
            existingTask.setPriority(updatedTask.getPriority().toUpperCase());
        }

        if (updatedTask.getDueDate() != null) {
            existingTask.setDueDate(updatedTask.getDueDate());
        }

        existingTask.setUpdatedAt(LocalDateTime.now());

        return taskRepository.save(existingTask);
    }

    @CacheEvict(value = "tasks", key = "#username")
    public void deleteTask(String taskId, String username) {

        Task existingTask = getTaskById(taskId, username);

        taskRepository.delete(existingTask);
    }


    public List<Task> getTasksByStatus(String username, String status) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findByUserIdAndStatusIgnoreCase(
                user.getId(),
                status
        );
    }

    public List<Task> getTasksByPriority(String username, String priority) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findByUserIdAndPriorityIgnoreCase(
                user.getId(),
                priority
        );
    }
}
