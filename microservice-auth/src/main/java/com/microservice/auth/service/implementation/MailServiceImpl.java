package com.microservice.auth.service.implementation;

import com.microservice.auth.presentation.advice.ErrorResponseException;
import com.microservice.auth.service.interfaces.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;



@Service
public class MailServiceImpl implements MailService {


    final JavaMailSender mailSender;
    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendMail(String to, String subject, String text) {
        // Crear un MimeMessage que soporta HTML
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            // Usar MimeMessageHelper para facilitar la configuración del mensaje
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // El segundo argumento 'true' indica que el contenido es HTML
            helper.setFrom("tu_correo@gmail.com"); // Cambia esto por tu dirección de correo

            // Enviar el mensaje
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // Manejar excepción
            throw new ErrorResponseException("Error al enviar correo: " + e.getMessage());
        }
    }
}
