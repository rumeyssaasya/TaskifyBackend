package com.rumer.taskify.service;

import com.rumer.taskify.model.Task;
import com.rumer.taskify.repository.TaskRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    //Constructor Injection
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}
