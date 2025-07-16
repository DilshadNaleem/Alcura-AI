package com.Alcura.Customer.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class AppointmentRescheduleEmailService
{
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEmail(String customerEmail, String date, String time, String uniqueId,
                          String doctorName, String reason)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Appointment Reschedule Request Alcura <" + fromEmail + ">");
        message.setTo(customerEmail);
        message.setSubject("Appointment Reschedule Request");

        String emailContent = String.format(
                "Dear Patient, \n\n" +
                        "Your appointment reschedule request has been sent successfully.\n\n" +
                        "Stay Tuned for the Confirmation" +
                        "Appointment Details: \n" +
                        "Appointment ID: " + uniqueId + "\n" +
                        "Date: " + date + "\n" +
                        "Time: " + time + "\n" +
                        "Doctor Name: " + doctorName + "\n" +
                        "Reason: " + reason + "\n" +
                        "Best regards,\n" +
                        "Alcura Medical Team"
        );

        message.setText(emailContent);
        javaMailSender.send(message);
    }
}
