package com.Alcura.Admin.Controller.MedicinePredictionModel;

import com.Alcura.Admin.DTO.MedicineInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/Admin")
public class GetAllMedicineDescriptionController
{
    private String rtn = "/Admin/MedicinePredictionModel/ViewAllMedicines" ;
    private static final Logger logger = LoggerFactory.getLogger(GetAllMedicineDescriptionController.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    public GetAllMedicineDescriptionController(RestTemplate restTemplate,
                                               ObjectMapper objectMapper)
    {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/medicineDescription")
    public String getAllMedicineWithDescription(Model model)
    {
        final String ApiUrl = "http://localhost:5000/medicine/getAllMedicine&Description";

        try
        {

            objectMapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
            ResponseEntity<String> rawResponse = restTemplate.getForEntity(ApiUrl, String.class);
            logger.info("RAW API RESPOMSE: {}", rawResponse.getBody());

            MedicineInfo[] medicineArray = objectMapper.readValue(rawResponse.getBody() , MedicineInfo[].class);

            if(medicineArray != null && medicineArray.length > 0)
            {
                List<MedicineInfo> medicine = Arrays.asList(medicineArray);
                model.addAttribute("medicine", medicine);
                logger.info("Successfully parsed {} medicines ", medicine.size());
            } else
            {
                logger.warn("Recieved empty or null response from API");
                model.addAttribute("error", "No medicines found");
            }

        }
        catch (Exception e)
        {
            logger.error("Failed to fetch diseases: {}" , e.getMessage());
            logger.error("Stack Trace ", e);
            model.addAttribute("error", "Failed to load " + e.getMessage());
        }

        return rtn;
    }

    @GetMapping("/medicine/search")
    public String searchMedicine(@RequestParam String medicineName, Model model, HttpSession session)
    {
        final String apiUrl = "http://localhost:5000/api/medicine/SearchByName/" + medicineName;

        try
        {
            MedicineInfo medicineInfo = restTemplate.getForObject(apiUrl, MedicineInfo.class);

            if(medicineInfo != null)
            {
                model.addAttribute("medicine", Collections.singleton(medicineInfo));
            }

            else
            {
                model.addAttribute("error", "Disease not found!");
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("Failed to fetch medicine {} " , e.getMessage());
            model.addAttribute("error", e.getMessage());
        }

        return rtn;
    }
}
