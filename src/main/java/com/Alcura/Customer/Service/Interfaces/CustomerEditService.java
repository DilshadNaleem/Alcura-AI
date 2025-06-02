package com.Alcura.Customer.Service.Interfaces;

import com.Alcura.Customer.Model.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface CustomerEditService
{
    ResponseEntity<String> updateCustomerDetails (String email, String firstname, String lastname, String contactNumber, String nic, MultipartFile image);
    ResponseEntity<Customer> getCustomerDetails (String email);
}
