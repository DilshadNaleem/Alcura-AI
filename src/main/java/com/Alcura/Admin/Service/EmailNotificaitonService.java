package com.Alcura.Admin.Service;

import com.Alcura.Admin.Service.Interfaces.PriceApprovalObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificaitonService implements PriceApprovalObserver
{
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;


    @Override
    public void update(String email, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Alcura Price Update <" + fromEmail +">");
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
