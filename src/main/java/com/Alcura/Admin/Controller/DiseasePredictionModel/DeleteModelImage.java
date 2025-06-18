package com.Alcura.Admin.Controller.DiseasePredictionModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Controller
@RequestMapping("/Admin")
public class DeleteModelImage {
    private static final Logger logger = LoggerFactory.getLogger(DeleteModelImage.class);
    private final RestTemplate restTemplate;
    private final String API_URL = "http://localhost:5000/api/images/";

    public DeleteModelImage(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/deleteSelectedImages")
    public String deleteSelectedImages(
            @RequestParam("selectedImages") List<String> selectedImages,
            @RequestParam("datasetType") String datasetType,
            Model model) {

        if (selectedImages == null || selectedImages.isEmpty()) {
            model.addAttribute("error", "No images selected for deletion");
            logger.warn("Delete attempt with no selected images");
            return populateModelAndReturnView(datasetType, model);
        }

        List<String> failedDeletions = new ArrayList<>();
        int successCount = 0;
        boolean connectionError = false;

        for (String imageData : selectedImages) {
            try {
                String[] parts = imageData.split("\\|");
                if (parts.length != 2) { // Expecting exactly name|dataset
                    logger.warn("Malformed image data: {}", imageData);
                    failedDeletions.add(imageData + " (Invalid format)");
                    continue;
                }

                String imageName = sanitizeFilename(parts[0]);
                String imageDataset = parts[1];
                String diseaseName = datasetType;

                if (!imageDataset.equals("train") && !imageDataset.equals("validation")) {
                    logger.warn("Invalid dataset type: {}", imageDataset);
                    failedDeletions.add(imageName + " (Invalid dataset)");
                    continue;
                }

                String deleteUrl = API_URL + diseaseName + "/" + imageDataset + "/" + imageName;
                logger.info("Attempting to delete image at URL: {}", deleteUrl);

                try {
                    restTemplate.delete(deleteUrl);
                    successCount++;
                    logger.info("Successfully deleted image: {}", imageName);
                } catch (HttpClientErrorException e) {
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        logger.error("Image not found: {}", imageName);
                        failedDeletions.add(imageName + " (Not found)");
                    } else {
                        throw e;
                    }
                }
            } catch (HttpClientErrorException e) {
                logger.error("Client error deleting image: {} - {}", imageData, e.getStatusCode());
                failedDeletions.add(extractFilename(imageData) + " (Client Error: " + e.getStatusCode() + ")");
            } catch (HttpServerErrorException e) {
                logger.error("Server error deleting image: {} - {}", imageData, e.getStatusCode());
                failedDeletions.add(extractFilename(imageData) + " (Server Error: " + e.getStatusCode() + ")");
            } catch (ResourceAccessException e) {
                logger.error("Connection error deleting image: {} - {}", imageData, e.getMessage());
                failedDeletions.add(extractFilename(imageData) + " (Connection Error)");
                connectionError = true;
                break; // Stop trying if we can't connect to the API
            } catch (Exception e) {
                logger.error("Unexpected error deleting image: {} - {}", imageData, e.getMessage());
                failedDeletions.add(extractFilename(imageData) + " (Unexpected Error)");
            }
        }

        if (connectionError) {
            model.addAttribute("error", "Failed to connect to image service. Please try again later.");
        } else if (!failedDeletions.isEmpty()) {
            String errorMsg = String.format("Deleted %d images, but failed to delete %d: %s",
                    successCount, failedDeletions.size(), String.join(", ", failedDeletions));
            model.addAttribute("error", errorMsg);
        } else {
            model.addAttribute("success", String.format("Successfully deleted %d images", successCount));
        }

        return populateModelAndReturnView(datasetType, model);
    }

    private String populateModelAndReturnView(String datasetType, Model model) {
        model.addAttribute("dataname", Collections.singletonList(datasetType));
        // Note: You'll need to repopulate the 'data' attribute here with the current images
        // This might require calling a service to fetch the remaining images
        return "Admin/DiseasePredictionModel/ViewImages";
    }

    private String sanitizeFilename(String filename) {
        // Remove any path traversal attempts and special characters
        return filename.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    private String extractFilename(String imageData) {
        try {
            String[] parts = imageData.split("\\|");
            return parts.length > 0 ? parts[0] : "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }
}