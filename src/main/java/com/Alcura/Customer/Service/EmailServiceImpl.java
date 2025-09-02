package com.Alcura.Customer.Service;

import com.Alcura.Customer.Service.Interfaces.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationEmail(String toEmail, String otp) {
        String subject = "Verify Your Account";
        String content = "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }" +
                ".container { background-color: #fff; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }" +
                ".otp { font-size: 24px; font-weight: bold; color: #2E86C1; }" +
                ".footer { margin-top: 20px; font-size: 12px; color: #888; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<h2>Account Verification</h2>" +
                "<p>Your verification code is:</p>" +
                "<div class='otp'>" + otp + "</div>" +
                "<p>Please use this code to verify your account.</p>" +
                "<div class='footer'>If you did not request this, please ignore this email.</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        sendHtmlEmail(toEmail, subject, content);
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        String subject = "Reset Your Password";
        String content = "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px; }" +
                ".container { background-color: #fff; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }" +
                ".btn { background-color: #28a745; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block; margin-top: 15px; }" +
                ".footer { margin-top: 20px; font-size: 12px; color: #888; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<h2>Password Reset Request</h2>" +
                "<p>To reset your password, click the button below:</p>" +
                "<a class='btn' href='" + resetLink + "'>Reset Password</a>" +
                "<div class='footer'>If you did not request this, please ignore this email.</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        sendHtmlEmail(toEmail, subject, content);
    }

    private void sendHtmlEmail(String toEmail, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "Alcura Support");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // Set true to indicate HTML content

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // You can log this or rethrow a custom exception
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
