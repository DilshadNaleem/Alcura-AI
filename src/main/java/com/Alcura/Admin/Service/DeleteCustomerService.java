package com.Alcura.Admin.Service;

import com.Alcura.Customer.Model.Customer;
import com.Alcura.Customer.Repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;

@Service
public class DeleteCustomerService
{
    private final Logger logger = LoggerFactory.getLogger(DeleteCustomerService.class);
    private final CustomerRepository customerRepository;

    public DeleteCustomerService(CustomerRepository customerRepository)
    {
        this.customerRepository = customerRepository;
    }


    public Customer getCustomerByUniqueId(String uniqueId)
    {
        return customerRepository.findByUniqueId(uniqueId);
    }

    @Transactional
    public void deleteCustomerByUniqueId(String uniqueId, PrintWriter out)
    {
        try
        {

            Customer customer = customerRepository.findByUniqueId(uniqueId);
            if (customer != null)
            {
                customerRepository.delete(customer);
                logger.info("Customer with ID {} deleted Successfully", uniqueId);
                out.println("<script type='text/javascript'>");
                out.println("alert('Customer with ID " + uniqueId + "Deleted Successfully!');");
                out.println("window.location.href='/Admin/ManageCustomers'");

                out.println("</script>");
            }
            else
            {
                logger.error("Customer with ID {} not found", uniqueId);
                out.println("<script type= 'text/javascript'>");
                out.println("alert('Customer with ID " + uniqueId + " not found!');");
                out.println("window.location.back();");
                out.println("</script>");
            }
        }
        catch (Exception e)
        {
            logger.error("Error deleteting customer with D {}: {}", uniqueId, e.getMessage());
            out.println("<script type='text/javascript'>");
            out.println("alert('Error deleting customer: " + e.getMessage().replace("'", "\\'") + "');");
            out.println("window.history.back();"); // Go back to previous page
            out.println("</script>");
        }
    }
}
