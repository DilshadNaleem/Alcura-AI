package com.Alcura.Admin.Controller.Doctor;

import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Model.DoctorAvailability;
import com.Alcura.Doctor.Repository.DoctorAvailabilityRepository;
import com.Alcura.Doctor.Repository.DoctorRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Controller
public class ApproveAvailabilityController {

    private static final Logger logger = LoggerFactory.getLogger(ApproveAvailabilityController.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String UNAVAILABLE_TEXT = "Unavailable";

    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public ApproveAvailabilityController(DoctorAvailabilityRepository availabilityRepository,
                                         DoctorRepository doctorRepository,
                                         JavaMailSender mailSender) {
        this.availabilityRepository = availabilityRepository;
        this.doctorRepository = doctorRepository;
        this.mailSender = mailSender;
    }

    @PostMapping("/Admin/ApproveAvailability")
    public void approveSelectedAvailabilities(
            @RequestParam("selectedIds") String selectedIds,
            @RequestParam("doctorId") String doctorId,
            HttpServletResponse response) throws IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            logger.info("Starting approval process for doctor ID: {}", doctorId);

            // Find the doctor by ID
            Doctor doctor = doctorRepository.findByuniqueId(doctorId);
            if (doctor == null) {
                String errorMsg = "Doctor not found with ID: " + doctorId;
                logger.error(errorMsg);
                out.println("<script type='text/javascript'>alert('Error: " + errorMsg + "'); window.location='/Admin/Manage_Availability';</script>");
                return;
            }

            // Convert comma-separated string to list of Long IDs
            List<Long> ids = Arrays.stream(selectedIds.split(","))
                    .filter(id -> !id.trim().isEmpty())
                    .map(Long::parseLong)
                    .toList();

            if (ids.isEmpty()) {
                String errorMsg = "No availability slots selected for approval";
                logger.error(errorMsg);
                out.println("<script type='text/javascript'>alert('Error: " + errorMsg + "'); window.location='/Admin/Manage_Availability';</script>");
                return;
            }

            logger.debug("Processing {} availability slots for doctor {}", ids.size(), doctorId);

            // Find all availabilities by IDs
            List<DoctorAvailability> availabilities = availabilityRepository.findAllById(ids);

            if (availabilities.isEmpty()) {
                String errorMsg = "No availability slots found with the provided IDs";
                logger.error(errorMsg);
                out.println("<script type='text/javascript'>alert('Error: " + errorMsg + "'); window.location='/Admin/Manage_Availability';</script>");
                return;
            }

            // Update status to "Confirmed" for each availability
            availabilities.forEach(availability -> {
                availability.setStatus("Confirmed");
                availability.setAvailable(true);
            });

            // Save all updated availabilities
            availabilityRepository.saveAll(availabilities);
            logger.info("Successfully updated {} availability slots to Confirmed status", availabilities.size());

            // Send email to doctor with the approved availability details
            try {
                sendApprovalEmail(doctor, availabilities);
                logger.info("Successfully sent approval email to doctor {}", doctor.getEmail());
                out.println("<script type='text/javascript'>alert('Successfully approved " + availabilities.size() + " availability slots and notified the doctor.'); window.location='/Admin/Manage_Availability';</script>");
            } catch (Exception e) {
                logger.error("Failed to send approval email to doctor {}: {}", doctor.getEmail(), e.getMessage(), e);
                out.println("<script type='text/javascript'>alert('Availabilities approved but failed to send email: " + e.getMessage() + "'); window.location='/Admin/Manage_Availability';</script>");
            }
        } catch (Exception e) {
            logger.error("Error processing approval request: {}", e.getMessage(), e);
            out.println("<script type='text/javascript'>alert('Error processing request: " + e.getMessage() + "'); window.location='/Admin/Manage_Availability';</script>");
        } finally {
            out.close();
        }
    }

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.watermark.image.path:/static/images/alcura-logo.png}")
    private String watermarkImagePath;

    private void sendApprovalEmail(Doctor doctor, List<DoctorAvailability> availabilities) throws MessagingException {
        try {
            logger.debug("Preparing approval email for doctor {}", doctor.getEmail());

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("Alcura Schedule <" + fromEmail + ">");
            helper.setTo(doctor.getEmail());
            helper.setSubject("Your Availability Has Been Approved");

            // Create PDF
            byte[] pdfBytes = generateApprovalPdf(doctor, availabilities);

            // HTML email content
            String htmlContent = "<html><body style='font-family: Arial, sans-serif; line-height: 1.6;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                    "<h2 style='color: #2c3e50;'>Availability Approval Notification</h2>" +
                    "<p>Dear Dr. " + doctor.getLastName() + ",</p>" +
                    "<p>Your availability slots have been approved. Please find the details in the attached PDF.</p>" +
                    "<p style='color: #7f8c8d; margin-top: 20px;'>Thank you,<br>Alcura Admin Team</p>" +
                    "</div></body></html>";

            helper.setText(htmlContent, true);

            // Create DataSource from byte array
            ByteArrayDataSource dataSource = new ByteArrayDataSource(pdfBytes, "application/pdf");
            helper.addAttachment("Approved_Availabilities.pdf", dataSource);

            logger.debug("Sending approval email to {}", doctor.getEmail());
            mailSender.send(mimeMessage);
            logger.info("Approval email with PDF successfully sent to {}", doctor.getEmail());

        } catch (MessagingException e) {
            logger.error("Failed to send email to {}: {}", doctor.getEmail(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while sending email: {}", e.getMessage(), e);
            throw new MessagingException("Failed to send email", e);
        }
    }

    private byte[] generateApprovalPdf(Doctor doctor, List<DoctorAvailability> availabilities) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36); // Set margins

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            // Add watermark
            addWatermark(writer, document);

            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("Approved Availability Slots", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Add doctor info
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
            Paragraph doctorInfo = new Paragraph("Doctor: Dr. " +
                    getSafeString(doctor.getFirstName()) + " " + getSafeString(doctor.getLastName()), subtitleFont);
            doctorInfo.setSpacingAfter(15);
            document.add(doctorInfo);

            // Create table with 5 columns (added Status column)
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setWidths(new float[]{1.5f, 2f, 1.5f, 1.5f, 1.5f});

            // Table headers
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
            addTableHeader(table, "Day", headerFont);
            addTableHeader(table, "Time Slot", headerFont);
            addTableHeader(table, "Valid From", headerFont);
            addTableHeader(table, "Valid To", headerFont);
            addTableHeader(table, "Status", headerFont);

            // Table content
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font unavailableFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.RED);

            for (DoctorAvailability availability : availabilities) {
                // Day of Week
                addTableCell(table, availability.getDayOfWeek() != null ?
                                availability.getDayOfWeek().toString() : UNAVAILABLE_TEXT,
                        contentFont);

                // Time Slot
                addTableCell(table,
                        (availability.getStartTime() != null && availability.getEndTime() != null) ?
                                availability.getStartTime() + " - " + availability.getEndTime() :
                                UNAVAILABLE_TEXT,
                        contentFont);

                // Valid From
                addTableCell(table, availability.getValidFrom() != null ?
                                availability.getValidFrom().format(DATE_FORMATTER) :
                                UNAVAILABLE_TEXT,
                        contentFont);

                // Valid To
                addTableCell(table, availability.getValidTo() != null ?
                                availability.getValidTo().format(DATE_FORMATTER) :
                                UNAVAILABLE_TEXT,
                        contentFont);

                // Status
                String status = availability.getStatus() != null ?
                        availability.getStatus() : "PENDING";
                addTableCell(table, status,
                        "Confirmed".equalsIgnoreCase(status) ? contentFont : unavailableFont);
            }

            document.add(table);

            // Add footer
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.GRAY);
            Paragraph footer = new Paragraph("\nGenerated by Alcura Admin System", footerFont);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            logger.error("Failed to generate PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF", e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    private void addWatermark(PdfWriter writer, Document document) throws DocumentException, IOException {
        try {
            // Load watermark image from classpath
            ClassPathResource imgFile = new ClassPathResource(watermarkImagePath);
            if (imgFile.exists()) {
                Image watermark = Image.getInstance(imgFile.getURL());

                // Set watermark properties
                watermark.setAbsolutePosition(
                        (document.getPageSize().getWidth() - watermark.getScaledWidth()) / 2,
                        (document.getPageSize().getHeight() - watermark.getScaledHeight()) / 2
                );
                watermark.setTransparency(new int[]{0*80, 0*80});

                // Add to every page
                PdfContentByte canvas = writer.getDirectContentUnder();
                for (int i = 1; i <= writer.getPageNumber(); i++) {
                    canvas.addImage(watermark);
                }
            } else {
                logger.warn("Watermark image not found at path: {}", watermarkImagePath);
            }
        } catch (Exception e) {
            logger.error("Error adding watermark: {}", e.getMessage(), e);
            // Continue without watermark if there's an error
        }
    }

    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell header = new PdfPCell(new Phrase(text, font));
        header.setBackgroundColor(BaseColor.DARK_GRAY);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setPadding(5);
        table.addCell(header);
    }

    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }

    private String getSafeString(String value) {
        return value != null ? value : "";
    }
}