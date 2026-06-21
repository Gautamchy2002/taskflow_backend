package com.taskmanager.task.task.scheduler;

import com.taskmanager.task.task.entity.Task;
import com.taskmanager.task.task.service.EmailService;
import com.taskmanager.task.task.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TaskReminderScheduler {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EmailService emailService;


    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Kolkata")
    public void sendTaskReminders() {

        System.out.println("Scheduler Started...");

        List<Task> overdueTasks =
                taskRepository.findByDueDateBeforeAndStatusNotIgnoreCase(
                        LocalDateTime.now(),
                        "COMPLETED"
                );

        System.out.println("Total Pending Due Tasks : "
                + overdueTasks.size());

        for (Task task : overdueTasks) {

            emailService.sendEmail(
                    task.getUserEmail(),
                    "Task Reminder - Pending Task",
                    "Hello,\n\n" +
                            "Your task is overdue.\n\n" +
                            "Title: " + task.getTitle() + "\n" +
                            "Priority: " + task.getPriority() + "\n" +
                            "Due Date: " + task.getDueDate() + "\n\n" +
                            "Please complete it as soon as possible.\n\n" +
                            "Thanks,\nTaskFlow Team"
            );

            System.out.println(
                    "Reminder Sent : "
                            + task.getTitle()
            );
        }
    }
}