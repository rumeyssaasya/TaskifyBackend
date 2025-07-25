package com.rumer.taskify.repository;

public interface TaskRepository {
    // Bu arayüz, Task modelini yönetmek için gerekli CRUD işlemlerini sağlar.
    // Spring Data JPA otomatik olarak bu arayüzü implement eder.
    // Örneğin, save(), findById(), findAll(), delete() gibi metodlar kullanılabilir.
}
