package com.rumer.taskify.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


@Service
public class EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${app.base-url:http://localhost:}")
    private String baseUrl;

    @Value("${app.frontend-base-url:http://localhost:5173}")
    private String frontendBaseUrl;
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String token) {
        String subject = "Taskify Hesap Doğrulama";
        String verificationLink = baseUrl + "/auth/verify?token=" + token;
        String body = "Merhaba,\n\nHesabınızı doğrulamak için lütfen aşağıdaki linke tıklayın:\n" 
                  + verificationLink + "\n\nBu link 5 dakika geçerlidir.\n\nBu mail adresi otomatik gönderilmiştir, yanıt vermeyiniz."+"\nhttps://rumer.tr/";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendResetPasswordEmail(String toEmail, String token) {
        String resetLink = frontendBaseUrl + "/auth/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Şifre Sıfırlama Talebi");
        message.setText(
            "Merhaba,\n\n" +
            "Şifrenizi sıfırlamak için lütfen aşağıdaki linke tıklayın:\n" +
            resetLink + "\n\n" +
            "Bu link 1 saat boyunca geçerlidir.\n\n" +
            "Eğer bu talebi siz yapmadıysanız, bu e-postayı dikkate almayabilirsiniz.\n\n" +
            "İyi günler." +
            "https://rumer.tr/"
        );

        mailSender.send(message);
    }
}
