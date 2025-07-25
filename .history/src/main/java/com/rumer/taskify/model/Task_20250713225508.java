//otomatik olarak veritabanında tablo oluşturur
package com.rumer.taskify.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Entity                // Bu sınıf veritabanında tablo olacak
@Data                 // Getter, setter, toString, vs. hepsini ekler
@NoArgsConstructor    // Boş constructor oluştur
@AllArgsConstructor   // Tüm alanlar için constructor oluştur
public class Task {
    @ManyToOne
    @JsonIgnore // Kullanıcı bilgilerini JSON'da gösterme, sadece id'si yeterli
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Id                                     // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // id otomatik artan
    private Long id;
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = true) // Proje ile ilişkilendirildi
    @JsonBackReference // Bu anotasyon, JSON serileştirmede döngüsel referansları önler
    private Project project;               // Görevin ait olduğu proje
    private String title;                   // Görev başlığı
    private String description;             // Görev açıklaması
    private boolean completed;              // Görev Tamamlandı mı?
}
