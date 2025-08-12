package com.Alcura.Admin.Service;

import com.Alcura.Admin.Service.Interfaces.PriceApprovalObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificaitonService implements PriceApprovalObserver
{
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void update(String email, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hypermarket403@gmail.com");
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
