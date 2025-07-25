package com.rumer.taskify.repository;
import com.rumer.taskify.model.Task;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long>{
    // Bu arayüz, Task modelini yönetmek için gerekli CRUD işlemlerini sağlar.
    // Spring Data JPA otomatik olarak bu arayüzü implement eder.
    // Örneğin, save(), findById(), findAll(), delete() gibi metodlar kullanılabilir.
}
