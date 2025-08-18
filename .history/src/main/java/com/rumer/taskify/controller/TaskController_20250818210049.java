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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/tasks")
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
            TaskResponse.ProjectSummary projectSummary = null;
            if (project != null) {
                projectSummary = new TaskResponse.ProjectSummary(project.getId(), project.getProjectName());
            }
            TaskResponse response = new TaskResponse();
            response.setId(task.getId());
            response.setTitle(task.getTitle());
            response.setDescription(task.getDescription());
            response.setStatus(task.getStatus());
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
        User user = userRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // Maksimum karakter sınırı
        final int MAX_DESCRIPTION_LENGTH = 1000;
        final int MAX_TITLE_LENGTH = 255;

        // Validation
        if (taskRequest.getDescription() != null && taskRequest.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Açıklama en fazla " + MAX_DESCRIPTION_LENGTH + " karakter olabilir.");
        }

        if (taskRequest.getTitle() != null && taskRequest.getTitle().length() > MAX_TITLE_LENGTH) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Başlık en fazla " + MAX_TITLE_LENGTH + " karakter olabilir.");
        }

        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus());
        task.setUser(user);

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
    public ResponseEntity<?> updateTask(
        @PathVariable Long id, 
        @RequestBody TaskRequest taskRequest,
        Principal principal) {
        
        User user = userRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        Task existingTask = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Görev bulunamadı"));

        Project project = existingTask.getProject();
        if (project == null) {
            // Eğer görevün bağlı olduğu proje yoksa sadece görevin sahibi güncelleyebilir
            if (!existingTask.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Yetkiniz yok.");
            }
        } else {
            // Proje sahibi veya collaborator mu kontrol et
            boolean isOwner = project.getUser().getId().equals(user.getId());
            boolean isCollaborator = project.getCollaborators() != null && project.getCollaborators().contains(user);

            if (!isOwner && !isCollaborator) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Yetkiniz yok.");
            }
        }

        // Güncellemeler
        existingTask.setTitle(taskRequest.getTitle());
        existingTask.setDescription(taskRequest.getDescription());
        existingTask.setStatus(taskRequest.getStatus());

        if (taskRequest.getProjectId() != null) {
            if (taskRequest.getProjectId() == 0) {
                existingTask.setProject(null);
            } else {
                Project newProject = projectRepository.findById(taskRequest.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Proje bulunamadı"));
                existingTask.setProject(newProject);
            }
        }

        Task updatedTask = taskRepository.save(existingTask);
        return ResponseEntity.ok(updatedTask);
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
