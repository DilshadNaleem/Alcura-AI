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

            logger.info("Received response from Flask API: {}", response.getStatusCode(), response.getBody());

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
            requestBody.put("Scientific_Name", updatedMedicine.getScientificName());  // Changed from "scientificname"
            requestBody.put("dosage", updatedMedicine.getDosage());
            requestBody.put("contraindications", updatedMedicine.getContraindications());
            requestBody.put("dosage_form", updatedMedicine.getDosageForm());  // Changed from "dosageform"
            requestBody.put("indications", updatedMedicine.getIndications());
            requestBody.put("max_dose", updatedMedicine.getMaxDose());  // Changed from "maxdose"
            requestBody.put("precautions", updatedMedicine.getPrecautions());
            requestBody.put("price", updatedMedicine.getPrice());
            requestBody.put("serious_effects", updatedMedicine.getSeriousEffects());  // Changed from "serious"
            requestBody.put("side_effects", updatedMedicine.getSideEffects());  // Changed from "side"
            requestBody.put("Source_of_information", updatedMedicine.getSourceOfInformation());  // Changed from "soi"
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

            logger.info("Received response from FLASK API: {}, Body: {}",
                    response.getStatusCode(), response.getBody());

            if(response.getStatusCode().is2xxSuccessful())
            {
                redirectAttributes.addFlashAttribute("success", "Medicine Updated Successfully!");
            }
            else
            {
                redirectAttributes.addFlashAttribute("error","Failed to update medicine: " + response.getBody());
            }
        }
        catch (Exception e)
        {
            logger.error("Error updating medicine - URL: {}, Error: {}",
                    requestUrl, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error updating medicine: " + e.getMessage());
        }

        return "redirect:/Admin/medicineDescription";
    }
}