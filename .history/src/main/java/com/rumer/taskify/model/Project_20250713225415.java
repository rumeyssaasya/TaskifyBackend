package com.rumer.taskify.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Project {
    @ManyToOne
    @JsonIgnore // Kullanıcı bilgilerini JSON'da gösterme, sadece id'si yeterli
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Projeyi oluşturan kullanıcı
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY) // id otomatik artan
    private Long id;
    private String projectName; // Proje adı
    private String description; // Proje açıklaması
    private String status; // Proje durumu (örn. aktif, tamamlandı)
    private String startDate; // Proje başlangıç tarihi
    private String endDate; // Proje bitiş tarihi
    @OneToMany(mappedBy = "project") // Task sınıfında project ile ilişkilendirildi
    @JsonManagedReference
    List<Task> tasks; // Projeye ait görevler
}
