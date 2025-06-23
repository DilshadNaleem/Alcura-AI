package com.Alcura.Admin.Controller.MedicinePredictionModel;

import com.Alcura.Admin.Controller.DiseasePredictionModel.EditDiseaseNameController;
import com.Alcura.Admin.DTO.MedicineInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/Admin")
public class EditMedicineNameController {
    private static final Logger logger = LoggerFactory.getLogger(EditMedicineNameController.class);
    private final String API_URL = "http://localhost:5000/api/medicine/SearchByName/";
    private final String POST_API_URL = "http://localhost:5000/api/edit/Medicine/";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public EditMedicineNameController(RestTemplate restTemplate,
                                      ObjectMapper objectMapper)
    {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/EditMedicine")
    public String editMedicine(@RequestParam String medicineName, Model model)
    {
        String requestUrl = API_URL + medicineName;
        logger.info("Sending GET Request for Python : {}", requestUrl);

        try
        {
            ResponseEntity<MedicineInfo> response = restTemplate.getForEntity(
                    requestUrl,
                    MedicineInfo.class
            );

            logger.info("Recieved response from Flask API: {}", response.getStatusCode(), response.getBody());

            if(response.getStatusCode() == HttpStatus.OK && response.getBody() != null)
            {
                model.addAttribute("medicine", response.getBody());
                model.addAttribute("oldName", medicineName);
                return "/Admin/MedicinePredictionModel/Edit-Medicine";
            }
        }
        catch (Exception e)
        {
            logger.error("Error fetching medicine data for editing : {}, Error: {}", requestUrl,
                    e.getMessage(), e);
        }

        String encodedName = medicineName.replace(" ", "+");
        String redirectUrl = "redirect:/Admin/MedicinePredictionModel/ViewAllMedicines?error=Medicine+" + encodedName + "+not+found";
        logger.info("Redirecting to: {}", redirectUrl);
        return redirectUrl;
    }

    @PostMapping("/EditMedicine")
    public String updateMedicine(
            @ModelAttribute("medicine") MedicineInfo updatedMedicine,
            @RequestParam String oldName,
            RedirectAttributes redirectAttributes)
    {
        String requestUrl = POST_API_URL + oldName;
        logger.info("Sending POST request to FLASK API: {}", requestUrl);

        try
        {
            Map<String,Object> requestBody = new HashMap<>();
            requestBody.put("new_name", updatedMedicine.getClassName());
            requestBody.put("administration", updatedMedicine.getAdministration());
            requestBody.put("scientificname", updatedMedicine.getScientificName());
            requestBody.put("dosage", updatedMedicine.getDosage());
            requestBody.put("contraindications", updatedMedicine.getContraindications());
            requestBody.put("dosageform", updatedMedicine.getDosageForm());
            requestBody.put("indications", updatedMedicine.getIndications());
            requestBody.put("maxdose", updatedMedicine.getMaxDose());
            requestBody.put("precautions", updatedMedicine.getPrecautions());
            requestBody.put("price",updatedMedicine.getPrice());
            requestBody.put("serious", updatedMedicine.getSeriousEffects());
            requestBody.put("side", updatedMedicine.getSideEffects());
            requestBody.put("soi", updatedMedicine.getSourceOfInformation());
            requestBody.put("use", updatedMedicine.getUse());

            logger.info("Request body being sent: {}", requestBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String,Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            logger.info("Recieved response from FLASK API: {}, Body: {}",
                    response.getStatusCode(), response.getBody());

            if(response.getStatusCode().is2xxSuccessful())
            {
                redirectAttributes.addFlashAttribute("success", "Medicine Updated Succesfully!");
            }
            else
            {
                redirectAttributes.addFlashAttribute("error","Failed to update medicine: " + response.getBody());
            }
        }

        catch (Exception e)
        {
            logger.error("Error updating mediicne - URL: {}, Error: {}",
                    requestUrl, e.getMessage(), e);
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error updating medicine " + e.getMessage());
        }

        return "redirect:/Admin/MedicinePredictionModel/ViewAllMedicines";
    }
}
