package com.Alcura.Customer.Service;

import com.Alcura.Customer.Service.Interfaces.AppointmentEmailService;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
                                             String doctorName, String appointmentId, String time,
                                             Float appointmentPrice, String paymentMethod) {
        try {
            validateEmail(toEmail);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress(fromEmail, "Alcura Appointment Team"));
            helper.setTo(toEmail);
            helper.setSubject("Appointment Confirmation - ID: " + appointmentId);

            String formattedDate = appointmentDate.format(DATE_FORMATTER);

            String emailContent = String.format("""
                <html>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                    <div style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 20px; border-radius: 10px;">
                        <h2 style="color: #2a7ae2;">Appointment Confirmation</h2>
                        <p>Dear Patient,</p>
                        <p>We are pleased to confirm your appointment has been scheduled. Please find the details below:</p>

                        <table style="width: 100%%; border-collapse: collapse; margin-top: 15px;">
                            <tr>
                                <td style="padding: 8px; font-weight: bold;">Doctor:</td>
                                <td style="padding: 8px;">%s</td>
                            </tr>
                            <tr style="background-color: #f9f9f9;">
                                <td style="padding: 8px; font-weight: bold;">Date:</td>
                                <td style="padding: 8px;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px; font-weight: bold;">Time:</td>
                                <td style="padding: 8px;">%s</td>
                            </tr>
                            <tr style="background-color: #f9f9f9;">
                                <td style="padding: 8px; font-weight: bold;">Appointment ID:</td>
                                <td style="padding: 8px;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px; font-weight: bold;">Price:</td>
                                <td style="padding: 8px;">Rs. %.2f</td>
                            </tr>
                            <tr style="background-color: #f9f9f9;">
                                <td style="padding: 8px; font-weight: bold;">Payment Method:</td>
                                <td style="padding: 8px;">%s</td>
                            </tr>
                        </table>

                        <p style="margin-top: 20px;">Thank you for choosing our service.</p>
                        <p style="color: #888;">Best regards,<br/>Alcura Team</p>
                    </div>
                </body>
                </html>
                """, doctorName, formattedDate, time, appointmentId, appointmentPrice, paymentMethod);

            helper.setText(emailContent, true); // true = isHtml
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
