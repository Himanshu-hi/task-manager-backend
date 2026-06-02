package com.taskmanager.service;

import com.taskmanager.dto.request.TaskRequest;
import com.taskmanager.dto.response.TaskResponse;
import com.taskmanager.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskResponse createTask(TaskRequest request, Long userId);
    TaskResponse getTaskById(Long taskId, Long userId, boolean isAdmin);
    Page<TaskResponse> getUserTasks(Long userId, Pageable pageable);
    Page<TaskResponse> getUserTasksByStatus(Long userId, Task.TaskStatus status, Pageable pageable);
    Page<TaskResponse> searchTasks(Long userId, String keyword, Pageable pageable);
    Page<TaskResponse> getAllTasks(Pageable pageable); // Admin
    TaskResponse updateTask(Long taskId, TaskRequest request, Long userId, boolean isAdmin);
    void deleteTask(Long taskId, Long userId, boolean isAdmin);
    TaskResponse patchTaskStatus(Long taskId, Task.TaskStatus status, Long userId, boolean isAdmin);
}
