package com.Alcura.AdminTest.Profit;

import com.Alcura.Admin.Controller.Profit.ManagePriceControllerForAppointmentController;
import com.Alcura.Admin.Model.DoctorPrice;
import com.Alcura.Admin.Repository.DoctorPriceRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ManagePriceControllerForAppointmentControllerTest {

    @Mock
    private DoctorPriceRepo doctorPriceRepo;

    @InjectMocks
    private ManagePriceControllerForAppointmentController priceController;

    private MockHttpSession session;
    private PrintWriter writer;
    private StringWriter stringWriter;
    private MockHttpServletResponse response;
    private Double range = 100d;

    @BeforeEach
    void setUp() throws UnsupportedEncodingException {
        session = new MockHttpSession();
        session.setAttribute("email", "admin@example.com"); // Add session attribute

        response = new MockHttpServletResponse();
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
    }

    @Test
    void ManagePrice_ShouldShowSuccessMessage() throws Exception {

        DoctorPrice existingPrice1 = new DoctorPrice();
        existingPrice1.setId(1);
        existingPrice1.setHospital_price(50.0);

        DoctorPrice existingPrice2 = new DoctorPrice();
        existingPrice2.setId(2);
        existingPrice2.setHospital_price(75.0);

        List<DoctorPrice> mockPrices = Arrays.asList(existingPrice1, existingPrice2);


        when(doctorPriceRepo.findAll()).thenReturn(mockPrices);
        when(doctorPriceRepo.saveAll(anyList())).thenReturn(mockPrices);


        priceController.percentage(range, session, writer);
        writer.flush();

        String content = stringWriter.toString();

        assertTrue(content.contains("alert('Details Saved to Database for all records');"),
                "Should show success message");
        assertTrue(content.contains("window.location.href ='/Admin/Manage_Percentage';"),
                "Should redirect to Manage_Percentage page");

        verify(doctorPriceRepo).findAll();
        verify(doctorPriceRepo).saveAll(mockPrices);
    }

    @Test
    void ManagePrice_WithNoExistingRecords_ShouldCreateNewRecord() throws Exception {


        when(doctorPriceRepo.findAll()).thenReturn(Arrays.asList());

        priceController.percentage(range, session, writer);
        writer.flush();

        verify(doctorPriceRepo).findAll();
        verify(doctorPriceRepo).save(any(DoctorPrice.class));
        verify(doctorPriceRepo, never()).saveAll(anyList());

        String content = stringWriter.toString();
        assertTrue(content.contains("alert('Details Saved to Database for all records');"));
    }

    @Test
    void ManagePrice_WithException_ShouldShowErrorMessage() throws Exception {
        // Mock repository to throw exception
        when(doctorPriceRepo.findAll()).thenThrow(new RuntimeException("Database error"));

        // Call the method
        priceController.percentage(range, session, writer);
        writer.flush();

        // Verify error message is shown
        String content = stringWriter.toString();
        assertTrue(content.contains("alert('Error occurred while saving');"),
                "Should show error message when exception occurs");
    }
}