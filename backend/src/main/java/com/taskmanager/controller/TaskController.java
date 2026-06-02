package com.taskmanager.controller;

import com.taskmanager.dto.request.TaskRequest;
import com.taskmanager.dto.response.ApiResponse;
import com.taskmanager.dto.response.TaskResponse;
import com.taskmanager.entity.Task;
import com.taskmanager.security.UserDetailsImpl;
import com.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "CRUD operations for tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        TaskResponse task = taskService.createTask(request, currentUser.getId());
        return ResponseEntity.status(201).body(ApiResponse.created(task, "Task created successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all tasks for current user (paginated)")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getMyTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) Task.TaskStatus status,
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TaskResponse> tasks;

        if (search != null && !search.isBlank()) {
            tasks = taskService.searchTasks(currentUser.getId(), search, pageable);
        } else if (status != null) {
            tasks = taskService.getUserTasksByStatus(currentUser.getId(), status, pageable);
        } else {
            tasks = taskService.getUserTasks(currentUser.getId(), pageable);
        }

        return ResponseEntity.ok(ApiResponse.success(tasks, "Tasks retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        boolean isAdmin = currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        TaskResponse task = taskService.getTaskById(id, currentUser.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.success(task));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task (full update)")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        boolean isAdmin = currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        TaskResponse task = taskService.updateTask(id, request, currentUser.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.success(task, "Task updated successfully"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status only")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        boolean isAdmin = currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        Task.TaskStatus status;
        try {
            status = Task.TaskStatus.valueOf(body.get("status").toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid status value. Valid values: TODO, IN_PROGRESS, DONE, CANCELLED");
        }
        TaskResponse task = taskService.patchTaskStatus(id, status, currentUser.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.success(task, "Task status updated"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        boolean isAdmin = currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        taskService.deleteTask(id, currentUser.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.success(null, "Task deleted successfully"));
    }
}
