# Taskify Backend

Taskify Backend, görev yönetim uygulamasının sunucu tarafını sağlayan Spring Boot tabanlı bir projedir. Kullanıcı yönetimi, projeler ve görevlerin takibi, profil ve şifre yönetimi gibi işlevleri REST API üzerinden sunar.

## Özellikler

- **Kullanıcı Yönetimi:** Kayıt, giriş ve profil güncelleme.  
- **Görev ve Proje Yönetimi:** Proje ve görevlerin eklenmesi, güncellenmesi ve listelenmesi.  
- **Profil Resmi:** Kullanıcıların profil resimlerini yönetebilmesi.  
- **Şifre Değiştirme:** Mevcut şifrenin doğrulanması ve güvenli şekilde yeni şifre belirleme.  
- **JWT Kimlik Doğrulama:** Güvenli oturum ve kullanıcı doğrulama.  
- **REST API:** Frontend ve mobil uygulamalarla iletişim için standart API uç noktaları.

## Kullanım

- Frontend veya diğer istemciler, REST API uç noktaları aracılığıyla projeleri ve görevleri yönetebilir.  
- Profil ve şifre güncellemeleri `/profile` ve `/profile/change-password` uç noktaları üzerinden yapılabilir.  
- JWT token ile güvenli erişim sağlanır.

## Link
  https://taskify.rumer.tr/

## Lisans

MIT Lisansı
