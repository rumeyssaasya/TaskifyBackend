package com.rumer.taskify.controller;

import com.rumer.taskify.model.User;
import com.rumer.taskify.dto.TaskRequest;
import com.rumer.taskify.dto.TaskResponse;
import com.rumer.taskify.model.Project;
import com.rumer.taskify.model.Task;
import com.rumer.taskify.repository.UserRepository;
import com.rumer.taskify.repository.TaskRepository;
import com.rumer.taskify.service.TaskService;
import com.rumer.taskify.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    // Kullanıcının task'larını getir
    @GetMapping
    public List<TaskResponse> getAllTasks(Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        List<Task> tasks = taskRepository.findByUser(user);

        return tasks.stream().map(task -> {
            Project project = task.getProject();
            TaskResponse projectSummary = null;
            if (project != null) {
                projectSummary = new TaskResponse(project.getId(), project.getName());
            }
            TaskResponse response = new TaskResponse();
            response.setId(task.getId());
            response.setTitle(task.getTitle());
            response.setDescription(task.getDescription());
            response.setCompleted(task.isCompleted());
            response.setProject(projectSummary);
            return response;
        }).toList();
    }


    // ID ile görev getir
    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    // Yeni görev oluştur
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskRequest taskRequest, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setCompleted(taskRequest.isCompleted());
        task.setProject(taskRequest.getProjectId() != null ? projectRepository.findById(taskRequest.getProjectId())
                .orElseThrow(() -> new RuntimeException("Proje bulunamadı")) : null);
        task.setUser(user);

        // Eğer proje ID null değilse ve veritabanında varsa ilişkilendir
        if (taskRequest.getProjectId() != null && taskRequest.getProjectId() != 0) {
            Project project = projectRepository.findById(taskRequest.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Proje bulunamadı"));
            task.setProject(project);
    }

    taskRepository.save(task);
    return ResponseEntity.status(HttpStatus.CREATED).body(task);
}

    // Görev güncelle
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody TaskRequest taskRequest, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Task existingTask = taskRepository.findById(id).orElseThrow();

        if (!existingTask.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Task Güncellenemez.");
        }

        existingTask.setTitle(taskRequest.getTitle());
        existingTask.setDescription(taskRequest.getDescription());
        existingTask.setCompleted(taskRequest.isCompleted());

        // Proje güncellemesi isteğe bağlı
        if (taskRequest.getProjectId() != null && taskRequest.getProjectId() != 0) {
            Project project = projectRepository.findById(taskRequest.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Proje bulunamadı"));
            existingTask.setProject(project);
        } else {
            existingTask.setProject(null); // İstersen böyle proje bağlantısını kaldırabilirsin
        }

        taskRepository.save(existingTask);
        return ResponseEntity.ok(existingTask);
    }


    // Görev sil
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Task task = taskRepository.findById(id).orElseThrow();

        if (!task.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bu task sizin değil.");
        }

        taskRepository.delete(task);
        return ResponseEntity.noContent().build();
    }
}
// Bu controller, kullanıcıların görevlerini yönetmelerine olanak tanır.
// Kullanıcı kimliği doğrulandıktan sonra, kullanıcının görevlerini listeleyebilir
