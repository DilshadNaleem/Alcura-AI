package com.Alcura.Customer.Service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AppointmentCancelEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendAppointmentCancellation(String customerEmail, String date,
                                            String doctorName, String uniqueId, String time) {
        try {
            if (!StringUtils.hasText(customerEmail)) {
                throw new IllegalArgumentException("Email address cannot be empty");
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress(fromEmail, "Alcura Appointment Team"));
            helper.setTo(customerEmail);
            helper.setSubject("Appointment Cancellation Confirmation");

            String emailContent = String.format("""
                <html>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                    <div style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 20px; border-radius: 10px;">
                        <h2 style="color: #e74c3c;">Appointment Cancelled</h2>
                        <p>Dear Patient,</p>
                        <p>We confirm that your appointment has been <strong>successfully cancelled</strong>.</p>

                        <h3 style="margin-top: 20px;">Appointment Details:</h3>
                        <table style="width: 100%%; border-collapse: collapse; margin-top: 10px;">
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
                        </table>

                        <p style="margin-top: 20px;">Thank you for using our service.</p>
                        <p style="color: #888;">Best regards,<br/>Alcura Medical Team</p>
                    </div>
                </body>
                </html>
                """, doctorName, date, time, uniqueId);

            helper.setText(emailContent, true); // true = is HTML
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send appointment cancellation email", e);
        }
    }
}
