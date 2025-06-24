package com.Alcura.Admin.Controller.MedicinePredictionModel;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.MultipartConfig;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;

@Controller
@MultipartConfig
@RequestMapping("/Admin")
public class MedicinePredictionController
{
    private static final String API_URL = "http://localhost:5000/api/Medicine/Predict";
    private final RestTemplate restTemplate;

    public MedicinePredictionController()
    {
        this.restTemplate = createRestTemplateWithNonNumericSupport();
    }

    private RestTemplate createRestTemplateWithNonNumericSupport()
    {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature());

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        restTemplate.getMessageConverters().add(0, converter);

        return restTemplate;
    }

    @GetMapping("/MedicinePrediction")
    public String showPredictionForm(Model model) {
        return "/Admin/MedicinePredictionModel/PredictMedicine";
    }

    @PostMapping("/MedicinePrediction")
    public String predictDisease(@RequestParam("image")MultipartFile multipartFile,
                                 Model model)
    {
        try {
            if (multipartFile.isEmpty())
            {
                model.addAttribute("error", "Please select an Image file");
                return "/Admin/MedicinePredictionModel/PredictMedicine";
            }

            File tempFile = File.createTempFile("upload-", multipartFile.getOriginalFilename());
            multipartFile.transferTo(tempFile);
            tempFile.deleteOnExit();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new FileSystemResource(tempFile));

            Map<String, Object> response = restTemplate.postForObject(
                    API_URL,
                    new HttpEntity<>(body, headers),
                    Map.class
            );

            if(response == null)
            {
                throw new RuntimeException("Empty resource from API");
            }

            if (response.containsKey("error"))
            {
                throw new RuntimeException(response.get("error").toString());
            }

            model.addAttribute("predictedClass", response.getOrDefault("predicted_class", "Unknown"));
            model.addAttribute("confidence", response.getOrDefault("confidence", 0.0));
            model.addAttribute("isRecognized", response.getOrDefault("is_recognized",false));

            if (response.containsKey("medicine_info"))
            {
                model.addAttribute("medicine_info", response.get("medicine_info"));

                Map<String, Object> medicineInfo = (Map<String, Object>) response.get("medicine_info");
                model.addAttribute("ScientificName", medicineInfo.get("Scientific_Name"));
                model.addAttribute("SourceOfInformation", medicineInfo.get("Source_of_information"));
                model.addAttribute("administration", medicineInfo.get("administration"));
                model.addAttribute("className", medicineInfo.get("class_name"));
                model.addAttribute("contraindications", medicineInfo.get("contraindications"));
                model.addAttribute("dosage", medicineInfo.get("dosage"));
                model.addAttribute("dosageForm", medicineInfo.get("dosage_form"));
                model.addAttribute("Indications", medicineInfo.get("indications"));
                model.addAttribute("maxDose", medicineInfo.get("max_dose"));
                model.addAttribute("precautions", medicineInfo.get("precautions"));
                model.addAttribute("price", medicineInfo.get("price"));
                model.addAttribute("seriousEffects", medicineInfo.get("serious_effects"));
                model.addAttribute("SideEffects", medicineInfo.get("side_effects"));
                model.addAttribute("use", medicineInfo.get("use"));
            }
            return "/Admin/MedicinePredictionModel/PredictMedicine";
        }
        catch (Exception e)
        {
            e.printStackTrace();
            model.addAttribute("error", "Error Processing image: " + e.getMessage());
            return "/Admin/MedicinePredictionModel/PredictMedicine";
        }
    }
}
