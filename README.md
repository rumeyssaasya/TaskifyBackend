# TaskifyBackend
Spring Boot ile geliştirilmiş JWT tabanlı görev yönetim API'si. CRUD işlemleri ve kimlik doğrulama içerir.

Taskify, kullanıcıların görev oluşturup yönetebileceği bir görev takip uygulamasıdır. Bu repository, projenin Spring Boot ile geliştirilmiş backend API yapısını içerir.

## Özellikler

- Kullanıcı kayıt ve giriş (JWT ile kimlik doğrulama)
- Görev ekleme, listeleme, güncelleme, silme (CRUD işlemleri)
- Her kullanıcı sadece kendi görevlerine erişebilir
- Spring Security ile yetkilendirme
- Katmanlı ve temiz mimari
- RESTful API standartlarına uygun yapı

## Kullanılan Teknolojiler

- Java 21  
- Spring Boot  
- Spring Security  
- JWT (JSON Web Token)  
- Maven
- JPA
- PostgreSQL

## Proje Yapısı

- `controller` → API uç noktalarını barındırır  
- `service` → İş mantığı  
- `repository` → Veritabanı işlemleri  
- `model` → Entity ve DTO sınıfları  
- `security` → JWT, filtreler ve güvenlik yapılandırması

## Kurulum

1. Repoyu klonla:
   ```bash
   git clone https://github.com/rumeyssaasya/TaskifyBackend.git
   cd TaskifyBackend

- spring.datasource.url=jdbc:mysql://localhost:3306/taskify
- spring.datasource.username=root
- spring.datasource.password=senin_sifren
- jwt.secret=ozel_bir_jwt_secret

2. Projeyi çalıştır:
   ```bash
   ./mvnw spring-boot:run 

POST	/auth/register	Yeni kullanıcı kaydı
POST	/auth/login	Giriş ve token alma
GET	/tasks	Görevleri listeleme
POST	/tasks	Yeni görev oluşturma
PUT	/tasks/{id}	Görev güncelleme
DELETE	/tasks/{id}	Görev silme
