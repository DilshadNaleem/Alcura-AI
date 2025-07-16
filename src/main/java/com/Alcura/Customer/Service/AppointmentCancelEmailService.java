package com.Alcura.Customer.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class AppointmentCancelEmailService
{
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendAppointmentCancellation(String customerEmail, String date,
                                            String doctorName, String uniqueId, String time) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Appointment Cancel Request Alcura <" + fromEmail + ">");
        message.setTo(customerEmail);
        message.setSubject("Appointment Cancellation Confirmation");

        String emailContent = String.format(
                "Dear Patient,\n\n" +
                        "Your appointment has been successfully cancelled.\n\n" +
                        "Appointment Details:\n" +
                        "Doctor: %s\n" +
                        "Date: %s\n" +
                        "Time: %s\n" +
                        "Appointment ID: %s\n\n" +
                        "Thank you for using our service.\n\n" +
                        "Best regards,\n" +
                        "Alcura Medical Team",
                doctorName, date, time, uniqueId
        );

        message.setText(emailContent);
        mailSender.send(message);
    }
}
