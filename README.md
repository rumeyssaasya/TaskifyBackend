# Taskify Backend

Taskify Backend, görev yönetim uygulamasının sunucu tarafını sağlayan Spring Boot tabanlı bir projedir. Kullanıcı yönetimi, projeler ve görevlerin takibi, profil ve şifre yönetimi gibi işlevleri REST API üzerinden sunar.
## Canlı Link 
https://taskify.rumer.tr/

## Özellikler

- **Kullanıcı Yönetimi:** Kayıt, giriş ve profil güncelleme
- **Mail Doğrulama:** Kayıt ve şifre unuttum gibi işlemler için mail ile doğrulama
- **Görev ve Proje Yönetimi:** Proje ve görevlerin eklenmesi, güncellenmesi ve listelenmesi  
- **Profil Resmi:** Kullanıcıların profil resimlerini yönetebilmesi  
- **Şifre Değiştirme:** Mevcut şifrenin doğrulanması ve güvenli şekilde yeni şifre belirleme  
- **JWT Kimlik Doğrulama:** Güvenli oturum ve kullanıcı doğrulama  
- **REST API:** Frontend ve mobil uygulamalarla iletişim için standart API uç noktaları  

## API Uç Noktaları
### Register
- **POST /auth/register** -Kullanıcı kaydı
  
### Authentication
- **POST /auth/login** – Kullanıcı girişi  
Dönen: JWT token döner

### Profile
- **GET /profile** – Kullanıcının profil bilgilerini getirir
- **PUT /profile** – Profil güncelleme
- **POST /profile/change-password** – Şifre değiştirme

### Projeler ve Görevler
- **GET /projects** – Kullanıcının projelerini listeler
- **POST /projects** – Yeni proje oluşturur
- **PUT /projects/{id}** – Projeyi günceller
- **DELETE /projects/{id}** – Projeyi siler
- **GET /tasks** – Kullanıcının görevlerini listeler
- **POST /tasks** – Yeni görev ekler
- **PUT /tasks/{id}** – Görevi günceller
- **DELETE /tasks/{id}** – Görevi siler

Not: Tüm uç noktalara erişim için JWT token gereklidir login ve register işlemleri hariç.

# Kullanım
- REST API uç noktaları aracılığıyla projeleri ve görevleri yönetebilirsiniz
- Profil ve şifre güncellemeleri /profile ve /profile/change-password üzerinden yapılabilir
- JWT token ile güvenli erişim sağlanır

Canlı Link


Lisans
MIT Lisansı
