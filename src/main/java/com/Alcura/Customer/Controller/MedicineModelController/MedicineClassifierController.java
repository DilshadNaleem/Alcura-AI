package com.Alcura.Customer.Controller.MedicineModelController;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/medicine")
public class MedicineClassifierController
{
    private final String FLASK_API_URL = "http://localhost:5000/classify";

    @PostMapping("/classify")
    public ResponseEntity<String> classifyMedicine(@RequestParam("file")MultipartFile file) {
        try {


            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    FLASK_API_URL,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            return response;
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"status\":\"error\",\"message\":\"Failed to process file: " + e.getMessage() + "\"}");
        }
    }

    private static class MultipartInputStreamFileResource extends org.springframework.core.io.InputStreamResource
    {
        private final String filename;

        MultipartInputStreamFileResource(java.io.InputStream inputStream,
                                         String filename)
        {
            super(inputStream);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public long contentLength() throws IOException {
            return -1;
        }
    }

}
