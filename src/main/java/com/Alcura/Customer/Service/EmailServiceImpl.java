package com.Alcura.Customer.Service;

import com.Alcura.Customer.Service.Interfaces.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService
{
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl (JavaMailSender mailSender)
    {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Alcura Registration <" + fromEmail + ">");
        message.setTo(toEmail);
        message.setSubject("Verification Your Account");
        message.setText("Your Verification OTP is " + otp +
                "\n Please use this code to verify your account.");

        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Alcura Reset Password Link <" + fromEmail + ">");
        message.setTo(toEmail);
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the link below: \n" + resetLink +
                "\n\n If you didint request this, Please ignore this email");
        mailSender.send(message);
    }
}
