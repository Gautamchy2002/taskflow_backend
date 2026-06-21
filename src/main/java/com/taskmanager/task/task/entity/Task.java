package com.taskmanager.task.task.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Document(collection = "tasks")
public class Task implements Serializable {

    @Id
    private String id;

    private String title;
    private String description;
    private String status;
    private String priority;

    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userId;
    private String userEmail;
}
