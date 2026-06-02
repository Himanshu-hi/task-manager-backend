package com.taskmanager.service.impl;

import com.taskmanager.dto.request.TaskRequest;
import com.taskmanager.dto.response.TaskResponse;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import com.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : Task.TaskStatus.TODO)
                .priority(request.getPriority() != null ? request.getPriority() : Task.TaskPriority.MEDIUM)
                .dueDate(request.getDueDate())
                .user(user)
                .build();

        return mapToResponse(taskRepository.save(task));
    }

    @Override
    public TaskResponse getTaskById(Long taskId, Long userId, boolean isAdmin) {
        Task task;
        if (isAdmin) {
            task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        } else {
            task = taskRepository.findByIdAndUserId(taskId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        }
        return mapToResponse(task);
    }

    @Override
    public Page<TaskResponse> getUserTasks(Long userId, Pageable pageable) {
        return taskRepository.findByUserId(userId, pageable).map(this::mapToResponse);
    }

    @Override
    public Page<TaskResponse> getUserTasksByStatus(Long userId, Task.TaskStatus status, Pageable pageable) {
        return taskRepository.findByUserIdAndStatus(userId, status, pageable).map(this::mapToResponse);
    }

    @Override
    public Page<TaskResponse> searchTasks(Long userId, String keyword, Pageable pageable) {
        return taskRepository.searchByUserIdAndKeyword(userId, keyword, pageable).map(this::mapToResponse);
    }

    @Override
    public Page<TaskResponse> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long taskId, TaskRequest request, Long userId, boolean isAdmin) {
        Task task = getTaskEntity(taskId, userId, isAdmin);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        return mapToResponse(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId, Long userId, boolean isAdmin) {
        Task task = getTaskEntity(taskId, userId, isAdmin);
        taskRepository.delete(task);
    }

    @Override
    @Transactional
    public TaskResponse patchTaskStatus(Long taskId, Task.TaskStatus status, Long userId, boolean isAdmin) {
        Task task = getTaskEntity(taskId, userId, isAdmin);
        task.setStatus(status);
        return mapToResponse(taskRepository.save(task));
    }

    private Task getTaskEntity(Long taskId, Long userId, boolean isAdmin) {
        if (isAdmin) {
            return taskRepository.findById(taskId)
                    .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        }
        return taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
    }

    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .userId(task.getUser().getId())
                .username(task.getUser().getUsername())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
