package com.Alcura.Customer.Service;

import com.Alcura.Customer.Model.Customer;
import com.Alcura.Customer.Repository.CustomerRepository;
import com.Alcura.Customer.Service.Interfaces.CustomerEditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CustomerEditServiceImpl implements CustomerEditService
{
    @Autowired
    CustomerRepository customerRepository;

    @Override
    public ResponseEntity <Customer> getCustomerDetails(String email)
    {
        Customer customer = customerRepository.findByEmail(email);

        if(customer == null)
        {
            return ResponseEntity.notFound().build();
        }

        customer.setPassword(null);
        return ResponseEntity.ok(customer);
    }

    @Override
    public ResponseEntity<String> updateCustomerDetails(String email, String firstname, String lastname, String contactNumber, String nic, MultipartFile image) {
        Customer customer = customerRepository.findByEmail(email);

        if(customer == null)
        {
            return ResponseEntity.badRequest().body("Customer not found");
        }

        customer.setFirstName(firstname);
        customer.setLastName(lastname);
        customer.setContactNumber(contactNumber);
        customer.setNic(nic);

        if(image != null && !image.isEmpty())
        {
            try
            {
                String originalFilename = image.getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

                String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/Customer/uploads/";
                Files.createDirectories(Paths.get(uploadDir));

                String fileName = email + "_" + System.currentTimeMillis() + "_" + image.getOriginalFilename();
                Path filePath = Paths.get(uploadDir + fileName);

                Files.write(filePath, image.getBytes());

                customer.setImage("/Customer/uploads/" + fileName);
            }
            catch ( Exception e)
            {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Failed to save images:" + e.getMessage());
            }
        }

        customerRepository.save(customer);
        return ResponseEntity.ok("Profile Updated Successfully!");
    }
}
