package com.Alcura.Admin.Controller.MedicinePredictionModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/Admin")
public class AddImagesController {

    private static final Logger logger = LoggerFactory.getLogger(AddImagesController.class);
    private static final String FLASK_API_UTL = "http://localhost:5000/api/Medicine/images/";

    @GetMapping("/MedicineAddImages")
    public String addImageForm(@RequestParam String medicineName, Model model)
    {
        model.addAttribute("medicineName", medicineName);
        logger.info("Displaying the image upload form for medicine: {}", medicineName);
        return "/Admin/MedicinePredictionModel/MedicineAddImages";
    }

    @PostMapping("/MedicineUploadImages")
    @ResponseBody
    public ResponseEntity<Map<String,Object>> uploadImages(
            @RequestParam String medicineName,
            @RequestParam String imageType,
            @RequestParam("images")MultipartFile[] files)
    {
        Map<String,Object> response = new HashMap<>();

        if (files.length == 0)
        {
            response.put("success", false);
            response.put("error", "Please select at least one images to upload");
            return ResponseEntity.badRequest().body(response);
        }

        try
        {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();
            body.add("dataset_type", imageType);

            for (MultipartFile file: files)
            {
                if(!file.isEmpty())
                {
                    ByteArrayResource resource = new ByteArrayResource(file.getBytes())
                    {
                        @Override
                        public String getFilename() {
                            return file.getOriginalFilename();
                        }
                    };
                    body.add("images", resource);
                }
            }


            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    FLASK_API_UTL + medicineName,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful())
            {
                response.put("success", true);
                response.put("message", "Images Upload Successfully");
                return ResponseEntity.ok(response);
            }
            else
            {
                response.put("success", false);
                response.put("error", "Failed to upload images");
                return ResponseEntity.status(responseEntity.getStatusCode()).body(response);
            }
        }

        catch (Exception e)
        {
            logger.error("Exception occurred while uploading images for disease: {}", medicineName, e);
            response.put("success", false);
            response.put("error", "Failed to upload images: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
