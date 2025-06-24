package com.Alcura.Admin.Controller.MedicinePredictionModel;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
@RequestMapping("/Admin")
public class MedicineTrainingController
{
    private static final String API_URL = "http://localhost:5000/api/Medicinetrain";
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/Training")
    public String showform()
    {
        return "/Admin/MedicinePredictionModel/MedicineTraining";
    }

    @PostMapping("/Start-Training")
    public String startTraining (@RequestParam(value = "epochs", defaultValue = "10") int epochs,
                                 Model model)
    {
        try
        {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("epochs", String.valueOf(epochs));

            HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(params, headers);

            Map<String, Object> response = restTemplate.postForObject(
                    API_URL,
                    request,
                    Map.class
            );

            if (response.containsKey("error"))
            {
                throw new RuntimeException((String) response.get("error"));
            }

            model.addAttribute("trainingResult", response);
            model.addAttribute("success", true);
            model.addAttribute("message", response.get("message"));
            model.addAttribute("epochs", epochs);

            if (response.containsKey("chart"))
            {
                model.addAttribute("chartData", response.get("chart"));
            }

            if (response.containsKey("history"))
            {
                Map<String,Object> history = (Map<String, Object>) response.get("history");
                model.addAttribute("accuracy", history.get("accuracy"));
                model.addAttribute("loss", history.get("loss"));
                model.addAttribute("valAccuracy", history.get("val_accuracy"));
                model.addAttribute("valLoss", history.get("val_loss"));
            }
        }
        catch (Exception e)
        {
            model.addAttribute("success", false);
            model.addAttribute("message", "Training failed: " + e.getMessage());
            model.addAttribute("epochs", epochs);

            e.printStackTrace();
        }

        return "/Admin/MedicinePredictionModel/MedicineTraining";
    }
}
