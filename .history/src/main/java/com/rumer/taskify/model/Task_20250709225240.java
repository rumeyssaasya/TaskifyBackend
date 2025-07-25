package com.rumer.taskify.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
//otomatik olarak veritabanında tablo oluşturur
@Entity                // Bu sınıf veritabanında tablo olacak
@Data                 // Getter, setter, toString, vs. hepsini ekle
@NoArgsConstructor    // Boş constructor oluştur
@AllArgsConstructor   // Tüm alanlar için constructor oluştur
public class Task {
    @Id                                     // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // id otomatik artan
    private Long id;

    private String title;                   // Görev başlığı
    private boolean completed;              // Görev Tamamlandı mı?
}
