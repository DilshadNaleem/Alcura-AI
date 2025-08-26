package com.Alcura.AdminTest.Doctor;

import com.Alcura.Admin.Controller.Doctor.ApproveAvailabilityController;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Model.DoctorAvailability;
import com.Alcura.Doctor.Repository.DoctorAvailabilityRepository;
import com.Alcura.Doctor.Repository.DoctorRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertTrue;


@ExtendWith(MockitoExtension.class)
public class ApproveAvailabilityControllerTest
{
    @Mock
    private DoctorAvailabilityRepository doctorAvailabilityRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private ApproveAvailabilityController approveAvailabilityController;


    private MockHttpSession session;
    private MockHttpServletResponse response;
    private String selectIds = "1,2,3";
    private String doctorId = "01";

    @BeforeEach
    void setUp()
    {
        this.session = new MockHttpSession();
        this.response = new MockHttpServletResponse();
    }

    @Test
    void ApproveAvailability_SessionNotFound_ShouldShowError() throws IOException
    {
        approveAvailabilityController.approveSelectedAvailabilities(selectIds,doctorId,response);
        assertNull(session.getAttribute("email"));
        String content = response.getContentAsString();
        String errorMsg = "Doctor not found with ID: " + doctorId;
        assertNotNull("Response content should be null", content);
        assertTrue("should show Error",content.contains("alert('Error: " + errorMsg+ "')"));
        assertTrue("Redirected", content.contains("window.location='/Admin/Manage_Availability';"));
    }

    @Test
    void ApproveAvailability_SelectIDNotFound_ShouldShowError() throws IOException
    {
        Doctor mockDoctor = new Doctor();
        mockDoctor.setUniqueId(doctorId);

        when(doctorRepository.findByuniqueId(doctorId)).thenReturn(mockDoctor);

        approveAvailabilityController.approveSelectedAvailabilities("",doctorId,response);
        String content = response.getContentAsString();
        String errorMsg = "No availability slots selected for approval";
        assertTrue("Should show Error Message", content.contains("alert('Error: " + errorMsg + "')"));
        assertTrue("Redirecting", content.contains("window.location='/Admin/Manage_Availability'"));
    }


    @Test
    void ApproveAvailability_Success_SuccessMessage() throws IOException, MessagingException {

        Doctor mockDoctor = new Doctor();
        mockDoctor.setUniqueId(doctorId);
        mockDoctor.setFirstName("John");
        mockDoctor.setLastName("Doe");
        mockDoctor.setEmail("john.doe@example.com");

        List<Long> ids = Arrays.stream(selectIds.split(","))
                .map(Long::parseLong)
                .toList();

        List<DoctorAvailability> mockAvailabilities = ids.stream()
                .map(id -> {
                    DoctorAvailability availability = new DoctorAvailability();
                    availability.setId(id);
                    availability.setStatus("Confirmed");
                    availability.setAvailable(true);
                    availability.setDayOfWeek(DayOfWeek.MONDAY);

                    availability.setStartTime(new Date(0, 0, 0, 9, 0));
                    availability.setEndTime(new Date(0, 0, 0, 17, 0));

                    availability.setValidFrom(LocalDate.now());
                    availability.setValidTo(LocalDate.now().plusMonths(1));
                    return availability;
                })
                .toList();

        JavaMailSender mailSenderMock = mock(JavaMailSender.class);
        MimeMessage mimeMessageMock = mock(MimeMessage.class);
        when(mailSenderMock.createMimeMessage()).thenReturn(mimeMessageMock);

        approveAvailabilityController = new ApproveAvailabilityController(
                doctorAvailabilityRepository,
                doctorRepository,
                mailSenderMock
        );

        when(doctorRepository.findByuniqueId(doctorId)).thenReturn(mockDoctor);
        when(doctorAvailabilityRepository.findAllById(ids)).thenReturn(mockAvailabilities);
        when(doctorAvailabilityRepository.saveAll(mockAvailabilities)).thenReturn(mockAvailabilities);

        approveAvailabilityController.approveSelectedAvailabilities(selectIds, doctorId, response);

        String content = response.getContentAsString();
        assertTrue("Should show success message",
                content.contains("alert('Successfully approved " + mockAvailabilities.size() + " availability slots"));
        assertTrue("Should redirect",
                content.contains("window.location='/Admin/Manage_Availability'"));

        // Verify email sending was attempted
        verify(mailSenderMock).send(mimeMessageMock);
    }
}
