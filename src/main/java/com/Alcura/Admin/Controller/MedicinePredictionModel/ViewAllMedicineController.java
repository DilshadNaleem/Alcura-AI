package com.Alcura.Admin.Controller.MedicinePredictionModel;

import com.Alcura.Admin.DTO.MedicineListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;



@Controller
@RequestMapping("/Admin")
public class ViewAllMedicineController
{
    private  static final Logger logger = LoggerFactory.getLogger(ViewAllMedicineController.class);
    private final RestTemplate restTemplate;

    public ViewAllMedicineController(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }


    @GetMapping("/ViewAllMedicine")
    public String viewAllMedicine(Model model)
    {
        final String apiURl = "http://localhost:5000/medicine/getAllMedicine";
        try
        {
            ResponseEntity<MedicineListResponse> response = restTemplate.getForEntity(apiURl, MedicineListResponse.class);
            if(response.getBody() != null && response.getBody().getAllMedicine() != null)
            {
                logger.info("Medicine retired " + response.getBody().getAllMedicine());
                model.addAttribute("medicine", response.getBody().getAllMedicine());
            }
            else
            {   logger.info("Medicine not found");
                model.addAttribute("error", "No Medicine found");
            }
        }
        catch (RestClientException e)
        {
            logger.info("Error failed to fetch" + e);
            model.addAttribute("error", "Failed to fetch " +e.getMessage());
        }

        return "/Admin/MedicinePredictionModel/MedicineDisease_List";
    }
}
