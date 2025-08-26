package com.Alcura.DoctorTest;

import com.Alcura.Doctor.Controller.DoctorAvailabilityController;
import com.Alcura.Doctor.DTO.AvailabilityDTO;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Model.DoctorAvailability;
import com.Alcura.Doctor.Repository.DoctorAvailabilityRepository;
import com.Alcura.Doctor.Repository.DoctorRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DoctorAvailabilityControllerTest {

    @Mock
    private DoctorAvailabilityRepository doctorAvailabilityRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private HttpSession session;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private DoctorAvailabilityController controller;

    private Doctor doctor;
    private String email = "doctor@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctor = new Doctor();
        doctor.setEmail(email);
        when(session.getAttribute("email")).thenReturn(email);
        when(doctorRepository.findByemail(email)).thenReturn(doctor);
    }

    @Test
    void testViewSetAvailabilityForm_WithValidSession() {
        when(session.getAttribute("email")).thenReturn(email);

        String result = controller.viewSetAvailabilityForm(session);

        assertEquals("/Doctor/SetAvailability", result);
    }

    @Test
    void testViewSetAvailabilityForm_WithInvalidSession() {
        when(session.getAttribute("email")).thenReturn(null);

        String result = controller.viewSetAvailabilityForm(session);

        assertEquals("redirect:/Doctor/Signing", result);
    }

    @Test
    void testGetAvailability_WithValidSession() {
        LocalDate[] nextWeekDates = controller.getNextWeekDates();
        List<DoctorAvailability> availabilities = Arrays.asList(
                createMockAvailability(DayOfWeek.MONDAY, nextWeekDates[0], true, "09:00", "17:00"),
                createMockAvailability(DayOfWeek.TUESDAY, nextWeekDates[1], false, null, null)
        );

        when(doctorAvailabilityRepository.findByDoctorAndValidFromBetween(
                any(Doctor.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(availabilities);

        ResponseEntity<List<AvailabilityDTO>> response = controller.getAvailability(session);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetAvailability_WithInvalidSession() {
        when(session.getAttribute("email")).thenReturn(null);

        ResponseEntity<List<AvailabilityDTO>> response = controller.getAvailability(session);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void testGetAvailability_DoctorNotFound() {
        when(doctorRepository.findByemail(email)).thenReturn(null);

        ResponseEntity<List<AvailabilityDTO>> response = controller.getAvailability(session);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testSaveAvailability_WithValidData() {
        Map<String, String> params = new HashMap<>();
        params.put("uniqueId", "test-id");
        params.put("available", "on");
        params.put("mondayStart", "09:00");
        params.put("mondayEnd", "17:00");
        params.put("tuesdayUnavailable", "on");

        String result = controller.saveAvailability("test-id", params, session, redirectAttributes);

        assertEquals("redirect:/Doctor/SetAvailability", result);
        verify(doctorAvailabilityRepository, times(7)).save(any(DoctorAvailability.class));
        verify(redirectAttributes).addAttribute(eq("success"), anyString());
    }

    @Test
    void testSaveAvailability_WithInvalidSession() {
        when(session.getAttribute("email")).thenReturn(null);

        String result = controller.saveAvailability("test-id", new HashMap<>(), session, redirectAttributes);

        assertEquals("redirect:/Doctor/Signing", result);
    }

    @Test
    void testSaveAvailability_DoctorNotFound() {
        when(doctorRepository.findByemail(email)).thenReturn(null);

        String result = controller.saveAvailability("test-id", new HashMap<>(), session, redirectAttributes);

        assertEquals("redirect:/Doctor/SetAvailability", result);
        verify(redirectAttributes).addAttribute(eq("error"), anyString());
    }

    @Test
    void testGetNextWeekDates() {
        LocalDate[] dates = controller.getNextWeekDates();

        assertEquals(7, dates.length);
        assertTrue(dates[0].getDayOfWeek().equals(DayOfWeek.MONDAY));
        assertTrue(dates[6].getDayOfWeek().equals(DayOfWeek.SUNDAY));

        for (int i = 0; i < 6; i++) {
            assertTrue(dates[i].isBefore(dates[i + 1]));
        }
    }

    private DoctorAvailability createMockAvailability(DayOfWeek day, LocalDate date,
                                                      boolean available, String start, String end) {
        DoctorAvailability availability = new DoctorAvailability();
        availability.setDayOfWeek(day);
        availability.setValidFrom(date);
        availability.setAvailable(available);

        if (start != null) {
            availability.setStartTime(java.sql.Time.valueOf(LocalTime.parse(start)));
        }
        if (end != null) {
            availability.setEndTime(java.sql.Time.valueOf(LocalTime.parse(end)));
        }

        return availability;
    }
}