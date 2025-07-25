package com.rumer.taskify.controller;

import com.rumer.taskify.model.User;
import com.rumer.taskify.model.Task;
import com.rumer.taskify.repository.UserRepository;
import com.rumer.taskify.repository.TaskRepository;
import com.rumer.taskify.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

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
    public Task updateTask(@PathVariable Long id, @RequestBody Task task) {
        return taskService.updateTask(id, task);
    }

    // Görev sil
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
// Bu controller, kullanıcıların görevlerini yönetmelerine olanak tanır.
// Kullanıcı kimliği doğrulandıktan sonra, kullanıcının görevlerini listeleyebilir
