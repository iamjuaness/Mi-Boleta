package com.microservice.auth.service.interfaces;

public interface MailService {
    void sendMail(String to, String subject, String text);
}
