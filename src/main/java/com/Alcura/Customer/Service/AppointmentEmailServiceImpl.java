package com.Alcura.Customer.Service;

import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Service.Interfaces.AppointmentEmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.AddressException;

@Service
public class AppointmentEmailServiceImpl implements AppointmentEmailService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public AppointmentEmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendAppointmentConfirmaation(String toEmail, LocalDate appointmentDate,
                                             String doctorName, String appointmentId, String time) {
        try {
            // Validate email
            validateEmail(toEmail);

            // Create and send email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("Appointment Confirmation Alcura <" + fromEmail + ">");
            message.setTo(toEmail);
            message.setSubject("Appointment Confirmation " + appointmentId);

            // Properly formatted message
            String formattedDate = appointmentDate.format(DATE_FORMATTER);
            String emailContent = String.format(
                    "Dear Patient,\n\n" +
                            "This is to confirm your appointment has been scheduled:\n\n" +
                            "Doctor: %s\n" +
                            "Date: %s\n" +
                            "at: %s\n" +
                            "Appointment ID: %s\n\n" +
                            "Thank you for choosing our service.\n\n" +
                            "Best regards,\n" +
                            "Alcura Team",
                    doctorName,
                    formattedDate,
                    time,
                    appointmentId
            );

            message.setText(emailContent);
            javaMailSender.send(message);

        } catch (AddressException e) {
            throw new IllegalArgumentException("Invalid email address: " + toEmail, e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send appointment confirmation email", e);
        }
    }

    private void validateEmail(String email) throws AddressException {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Email address cannot be empty");
        }

        String trimmedEmail = email.trim();
        InternetAddress emailAddr = new InternetAddress(trimmedEmail);
        emailAddr.validate();
    }
}