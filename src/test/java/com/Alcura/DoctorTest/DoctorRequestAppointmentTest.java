package com.Alcura.DoctorTest;

import com.Alcura.Admin.Model.DoctorPrice;
import com.Alcura.Admin.Repository.DoctorPriceRepo;
import com.Alcura.Doctor.Controller.RequestAppointmentFeeController;
import com.Alcura.Doctor.Service.DoctorFeeUniqueId;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DoctorRequestAppointmentTest {

    @Mock
    private DoctorPriceRepo doctorPriceRepo;

    @Mock
    private DoctorFeeUniqueId doctorFeeUniqueId;

    @Mock
    private HttpSession session;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Logger logger;

    @InjectMocks
    private RequestAppointmentFeeController controller;

    private String email = "doctor@example.com";
    private DoctorPrice mockDoctorPrice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockDoctorPrice = new DoctorPrice();
        mockDoctorPrice.setHospital_price(100.0);
    }

    @Test
    void testShowForm() {
        String result = controller.showform();
        assertEquals("/Doctor/RequestAppointmentPrice", result);
    }

    @Test
    void testUpdateForm_Success() throws Exception {

        when(session.getAttribute("email")).thenReturn(email);

        when(doctorPriceRepo.findTopByOrderByIdDesc()).thenReturn(Optional.of(mockDoctorPrice));

        when(doctorFeeUniqueId.createDoctorPrice(any(DoctorPrice.class))).thenReturn(mockDoctorPrice);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        String result = controller.updateForm(150.0f, "Consultation fee", session, response);

        assertNull(result);
        verify(doctorPriceRepo, times(1)).save(any(DoctorPrice.class));
    }

    @Test
    void testUpdateForm_NoSession() throws Exception {
        when(session.getAttribute("email")).thenReturn(null);

        String result = controller.updateForm(150.0f, "Consultation fee", session, response);

        assertEquals("/Doctor/Signing", result);
        verify(doctorPriceRepo, never()).save(any(DoctorPrice.class));
    }

    @Test
    void testUpdateForm_NoPreviousPrice() throws Exception {
        when(session.getAttribute("email")).thenReturn(email);
        when(doctorPriceRepo.findTopByOrderByIdDesc()).thenReturn(Optional.empty());
        when(doctorFeeUniqueId.createDoctorPrice(any(DoctorPrice.class))).thenReturn(mockDoctorPrice);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        String result = controller.updateForm(200.0f, "New fee", session, response);

        assertNull(result);
        verify(doctorPriceRepo, times(1)).save(any(DoctorPrice.class));
    }

    @Test
    void testUpdateForm_ExceptionHandling() throws Exception {
        when(session.getAttribute("email")).thenReturn(email);
        when(doctorPriceRepo.findTopByOrderByIdDesc()).thenThrow(new RuntimeException("Database error"));

        String result = controller.updateForm(150.0f, "Consultation fee", session, response);

        assertNull(result);
        verify(doctorPriceRepo, never()).save(any(DoctorPrice.class));
    }
}