package com.rumer.taskify.repository;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rumer.taskify.model.Task;
import com.rumer.taskify.model.User;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository  // Bu arayüz bir repository olarak işaretlenir
public interface TaskRepository extends JpaRepository<Task, Long>{
     @JsonManagedReference // Bu anotasyon, JSON serileştirmede döngüsel referansları önler
     List<Task> findByUser(User user);
    //Task entity adı long ise id tipi olduğu için alınır.
    
    // Bu arayüz, Task modelini yönetmek için gerekli CRUD işlemlerini sağlar.
    // Spring Data JPA otomatik olarak bu arayüzü implement eder.
    // Örneğin, save(), findById(), findAll(), delete() gibi metodlar kullanılabilir.
}
