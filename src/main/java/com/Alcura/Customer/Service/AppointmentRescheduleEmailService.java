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
public class AppointmentRescheduleEmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEmail(String customerEmail, String date, String time, String uniqueId,
                          String doctorName, String reason) {
        try {
            // Validate email
            if (!StringUtils.hasText(customerEmail)) {
                throw new IllegalArgumentException("Email address cannot be empty");
            }

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress(fromEmail, "Alcura Appointment Team"));
            helper.setTo(customerEmail);
            helper.setSubject("Appointment Reschedule Request");

            String emailContent = String.format("""
                <html>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                    <div style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 20px; border-radius: 10px;">
                        <h2 style="color: #e67e22;">Appointment Reschedule Request Sent</h2>
                        <p>Dear Patient,</p>
                        <p>Your request to reschedule your appointment has been successfully received. Please wait for further confirmation.</p>

                        <h3 style="margin-top: 20px;">Appointment Details:</h3>
                        <table style="width: 100%%; border-collapse: collapse; margin-top: 10px;">
                            <tr>
                                <td style="padding: 8px; font-weight: bold;">Appointment ID:</td>
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
                                <td style="padding: 8px; font-weight: bold;">Doctor Name:</td>
                                <td style="padding: 8px;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px; font-weight: bold;">Reason:</td>
                                <td style="padding: 8px;">%s</td>
                            </tr>
                        </table>

                        <p style="margin-top: 20px;">Thank you for your patience.</p>
                        <p style="color: #888;">Best regards,<br/>Alcura Medical Team</p>
                    </div>
                </body>
                </html>
                """, uniqueId, date, time, doctorName, reason);

            helper.setText(emailContent, true); // true = isHtml
            javaMailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send reschedule confirmation email", e);
        }
    }
}
