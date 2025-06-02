package com.Alcura.Customer.Controller;

import com.Alcura.Customer.Model.Customer;
import com.Alcura.Customer.Service.Interfaces.CustomerEditService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/Customer")
public class EditProfileController
{
    @Autowired (required = true)
    private CustomerEditService customerEditService;

    @GetMapping("/GetProfile")
    @ResponseBody
    public ResponseEntity<?> getProfile (HttpSession session)
    {
        String sessionEmail = (String)  session.getAttribute("email");

        if(sessionEmail == null)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not logged In",
                            "redirectUrl","/"));
        }

        Customer customer = customerEditService.getCustomerDetails(sessionEmail).getBody();

        if(customer != null)
        {
            return ResponseEntity.ok(customer);
        }
        else
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Customer not found!",
                            "redirectUrl", "/"
                    ));
        }
    }

    @RequestMapping(value = "/UpdateProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String,String>> updateProfile (
            @RequestParam(value = "firstName", required = false) String firstname,
            @RequestParam(value = "lastName", required = false) String lastname,
            @RequestParam(value = "contactNumber", required = false) String contactNumber,
            @RequestParam(value = "nic", required = false) String nic,
            @RequestParam(value = "image", required = false)MultipartFile image,
            HttpSession session)
    {
        String sessionEmail = (String) session.getAttribute("email");
        if(sessionEmail == null)
        {
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Session Expired!. Please Try again After Login",
                            "redirectUrl","/"));
        }

        try
        {
            ResponseEntity<String> serviceResponse = customerEditService.updateCustomerDetails(
                    sessionEmail, firstname, lastname, contactNumber, nic, image);

            if(serviceResponse.getStatusCode() .is2xxSuccessful())
            {
                return ResponseEntity.ok(Map.of(
                        "success", "Profile Updated Successfully!",
                        "redirectUrl", "/Customer/Dashboard"
                ));
            }
            else
            {
                return ResponseEntity.status(serviceResponse.getStatusCode())
                        .body(Map.of("error", serviceResponse.getBody()));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error updating profile: " + e.getMessage()));
        }
    }
}
