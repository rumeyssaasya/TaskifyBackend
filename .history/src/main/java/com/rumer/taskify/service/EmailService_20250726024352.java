package com.rumer.taskify.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String token) {
        String subject = "Taskify Hesap Doğrulama";
        String verificationLink = "http://localhost:8080/auth/verify?token=" + token;
        String body = "Merhaba,\n\nHesabınızı doğrulamak için lütfen aşağıdaki linke tıklayın:\n" 
                  + verificationLink + "\n\nBu mail adresi otomatik gönderilmiştir, lütfen maile yanıt vermeyin.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
