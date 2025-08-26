package com.Alcura.AdminTest.Hospital;

import com.Alcura.Admin.Controller.Hospital.AddHospitalController;
import com.Alcura.Admin.Service.AddHospitalService;
import com.Alcura.Customer.Model.Hospital;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertNull;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@ExtendWith(MockitoExtension.class)
public class AddHospitalControllerTest
{
    @Mock
    private AddHospitalService hospitalService;

    private MockHttpServletResponse response;
    private PrintWriter writer;
    String hospitalName = "hospitalName";
    BigDecimal latitude = new BigDecimal("100.8484894894");
    BigDecimal longitude = new BigDecimal("1048.897489748748");
    String contactEmail = "test@gmail.com";
    String address = "address";
    String phoneNumber = "0725958832";
    String description = "Description";
    MultipartFile file = new MockMultipartFile(
            "file",
            "filename.txt",
            "text/plain",
            "Hello World".getBytes()
    );

    @InjectMocks
    private AddHospitalController hospitalController;

    @BeforeEach
    void setUp() throws UnsupportedEncodingException {

        response = new MockHttpServletResponse();
        writer = new PrintWriter(response.getWriter());
     }


     @Test
    void AddHospital_SameContactNumber_ShowErrorMessage() throws IOException
     {
         when(hospitalService.phoneNumberExists(phoneNumber)).thenReturn(true);
        hospitalController.addHospital(hospitalName,latitude,longitude,contactEmail,address,phoneNumber,description,file,response);

         String content = response.getContentAsString();

         assertTrue("Error Message", content.contains("alert('Error: This number is already registered');"));
         assertTrue("Redirect", content.contains("window.location.href='/Admin/AddNewHospital';"));
     }

     @Test
    void AddHospital_DuplicateEmailExists_ShowErrorMessage() throws IOException
     {
         when(hospitalService.emailExists(contactEmail)).thenReturn(true);
         hospitalController.addHospital(hospitalName,latitude,longitude,contactEmail,address,phoneNumber,description,file,response);

         String count = response.getContentAsString();
         assertTrue("Show Error Message", count.contains("alert('Error: This email is already registered');"));
         assertTrue("Redirecting", count.contains("window.location.href='/Admin/AddNewHospital';"));
     }

     @Test
    void AddHospital_Success_ShouldShowSuccessMessage() throws IOException
     {
         String uniqueId = "HospitalUniqueId";

         Hospital hospital = new Hospital();
         hospital.setUniqueId(uniqueId);
         hospital.setImage(file.getBytes());
         hospital.setPhoneNumber(phoneNumber);
         hospital.setName(hospitalName);
         hospital.setLongitude(longitude);
         hospital.setLatitude(latitude);
         hospital.setContactEmail(contactEmail);
         hospital.setDescription(description);

         when(hospitalService.registerHospital(hospitalName,latitude,longitude,contactEmail,address,phoneNumber,description,file)).thenReturn(hospital);
         hospitalController.addHospital(hospitalName,latitude,longitude,contactEmail,address,phoneNumber,description,file,response);
         String content = response.getContentAsString();
         assertTrue("Success Message", content.contains("alert('Hospital registered successfully with ID: " + hospital.getUniqueId() +"')"));
         assertTrue("Redirecting", content.contains("window.location.href='/Admin/AddNewHospital';"));
     }
}
