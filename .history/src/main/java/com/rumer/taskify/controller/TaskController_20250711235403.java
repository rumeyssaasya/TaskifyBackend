package com.rumer.taskify.controller;

import com.rumer.taskify.model.User;
import com.rumer.taskify.model.Task;
import com.rumer.taskify.repository.UserRepository;
import com.rumer.taskify.repository.TaskRepository;
import com.rumer.taskify.service.TaskService;

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

    // Kullanıcının task'larını getir
    @GetMapping
    public List<Task> getAllTasks(Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        return taskRepository.findByUser(user);
}

    // ID ile görev getir
    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    // Yeni görev oluştur
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        task.setUser(user);
        taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    // Görev güncelle
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task updatedTask, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Task existingTask = taskRepository.findById(id).orElseThrow();

        if (!existingTask.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bu task sizin değil.");
        }

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());

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
