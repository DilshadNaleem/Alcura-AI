package com.Alcura.Admin.Controller;

import com.Alcura.Admin.Service.DeleteCustomerService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;

@RestController
@RequestMapping("/Admin")
public class DeleteCustomerControl
{
    private final DeleteCustomerService deleteCustomerService;

    public DeleteCustomerControl(DeleteCustomerService deleteCustomerService)
    {
        this.deleteCustomerService = deleteCustomerService;
    }

    @RequestMapping("/DeleteCustomers/{id}")
    public void DeleteCustomer(@PathVariable String id, HttpServletResponse response, HttpSession session) throws IOException
    {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");

        String email = (String) session.getAttribute("email");
        if (email == null )
        {
            out.println("<script type= 'text/javascript'>");
            out.println("alert('Session Expired Please Login');");
            out.println("window.location.href='/Admin/Signing';");
            out.println("</script>");
            return;
        }
         deleteCustomerService.deleteCustomerByUniqueId(id,out);
    }
}
