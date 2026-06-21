package com.taskmanager.task.task.controller;

import com.taskmanager.task.task.entity.Task;
import com.taskmanager.task.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/create")
    public ResponseEntity<?> createTask(
            @RequestBody Task task,
            Authentication authentication
    ) {
        try {
            String username = authentication.getName();
            Task savedTask = taskService.createTask(task, username);
            return ResponseEntity.ok(savedTask);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getMyTasks(Authentication authentication) {
        try {
            String username = authentication.getName();
            List<Task> tasks = taskService.getMyTasks(username);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get/{taskId}")
    public ResponseEntity<?> getTaskById(
            @PathVariable String taskId,
            Authentication authentication
    ) {
        try {
            String username = authentication.getName();
            Task task = taskService.getTaskById(taskId, username);
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{taskId}")
    public ResponseEntity<?> updateTask(
            @PathVariable String taskId,
            @RequestBody Task task,
            Authentication authentication
    ) {
        try {
            String username = authentication.getName();
            Task updatedTask = taskService.updateTask(taskId, task, username);
            return ResponseEntity.ok(updatedTask);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<?> deleteTask(
            @PathVariable String taskId,
            Authentication authentication
    ) {
        try {
            String username = authentication.getName();
            taskService.deleteTask(taskId, username);
            return ResponseEntity.ok("Task deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get/status/{status}")
    public ResponseEntity<?> getTasksByStatus(
            @PathVariable String status,
            Authentication authentication
    ) {
        try {
            String username = authentication.getName();
            List<Task> tasks = taskService.getTasksByStatus(username, status);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get/priority/{priority}")
    public ResponseEntity<?> getTasksByPriority(
            @PathVariable String priority,
            Authentication authentication
    ) {
        try {
            String username = authentication.getName();
            List<Task> tasks = taskService.getTasksByPriority(username, priority);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}