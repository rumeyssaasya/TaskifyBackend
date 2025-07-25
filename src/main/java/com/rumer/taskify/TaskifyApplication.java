package com.rumer.taskify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class TaskifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskifyApplication.class, args);
	}

	@GetMapping("/")
	public String home() {
		return "🚀 Taskify çalışıyor! Giriş başarılı.";
	}
}
