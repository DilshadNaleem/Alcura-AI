package com.Alcura.Customer.Controller;

import com.Alcura.Customer.DTO.DiseaseClassificationResult;
import com.Alcura.Customer.DTO.DiseaseInfo;
import com.Alcura.Customer.DTO.MedicineClassificationResult;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.annotations.WhereJoinTable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;
import java.util.Map;

@Controller
@RequestMapping("/Customer")
public class DiseaseWebController
{
    private final DiseaseClassifierAPIController diseaseClassifierAPIController;

    public DiseaseWebController(DiseaseClassifierAPIController diseaseClassifierAPIController)
    {
        this.diseaseClassifierAPIController = diseaseClassifierAPIController;
    }

    @PostMapping("DiseaseClassifier")
    public String classify(@RequestParam("file")MultipartFile file, RedirectAttributes redirectAttributes)
    {
        try
        {
            ResponseEntity<String> response = diseaseClassifierAPIController.classifyDisease(file);

            if (response.getStatusCode() == HttpStatus.OK)
            {
                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature());
                Map<String, Object> responseMap = mapper.readValue(response.getBody(),Map.class);

                Map<String,Object> diseaseInfoMap = (Map<String, Object>) responseMap.get("disease_info");
                DiseaseInfo diseaseInfo = mapper.convertValue(diseaseInfoMap,DiseaseInfo.class);

                DiseaseClassificationResult result = new DiseaseClassificationResult(
                        (String)  responseMap.get("predicted_class"),
                        (Double) responseMap.get("confidence"),
                        diseaseInfo
                );
                redirectAttributes.addFlashAttribute("result", result);
                redirectAttributes.addFlashAttribute("status","");

                if(!file.isEmpty())
                {
                    String imageBase64 = Base64.getEncoder().encodeToString(file.getBytes());
                    redirectAttributes.addFlashAttribute("imagePreview",
                            "data:image/jpeg;base64," + imageBase64);
                }

            } else
            {
                redirectAttributes.addFlashAttribute("status",
                        "Classification Failed: " + response.getBody());
            }
        }

        catch (Exception e)
        {
            redirectAttributes.addFlashAttribute("status", "Error: " + e.getMessage());
        }
        return "redirect:/Customer/DiseaseClassifier";
    }
}
