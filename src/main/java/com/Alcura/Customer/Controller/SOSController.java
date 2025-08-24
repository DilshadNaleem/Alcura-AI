package com.Alcura.Customer.Controller;

import com.Alcura.Customer.DTO.EmergencyRequest;
import com.Alcura.Customer.DTO.SOSRequest;
import com.Alcura.Customer.DTO.TrackingSession;
import com.Alcura.Customer.Model.Hospital;
import com.Alcura.Customer.Repository.HospitalRepository;
import com.Alcura.Customer.Repository.EmergencyRequestRepository;
import com.Alcura.Customer.RestControllerAdvice.HospitalNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/sos")
public class SOSController {

    private HospitalRepository hospitalRepo;

    private EmergencyRequestRepository emergencyRequestRepo;


    private SimpMessagingTemplate messagingTemplate;
    private static final Logger logger = LoggerFactory.getLogger(SOSController.class);

    private final Map<String, TrackingSession> activeSessions = new ConcurrentHashMap<>();

    public SOSController(HospitalRepository hospitalRepo,
                         EmergencyRequestRepository emergencyRequestRepository,
                         SimpMessagingTemplate messagingTemplate)
    {
        this.hospitalRepo = hospitalRepo;
        this.emergencyRequestRepo = emergencyRequestRepository;
        this.messagingTemplate = messagingTemplate;
    }
    @PostMapping("/start")
    public ResponseEntity<?> startSOSTracking(@RequestBody SOSRequest request) {
        // Find nearest hospital
        Hospital nearest = hospitalRepo.findNearest(
                request.getLatitude(),
                request.getLongitude()
        ).orElseThrow(() -> new HospitalNotFoundException("No nearby hospitals found"));


        // Generate tracking ID
        String trackingId = UUID.randomUUID().toString();

        // Create and save emergency request to database
        EmergencyRequest emergencyRequest = new EmergencyRequest();
        emergencyRequest.setTrackingId(trackingId);
        emergencyRequest.setLatitude(request.getLatitude());
        emergencyRequest.setLongitude(request.getLongitude());
        emergencyRequest.setHospitalId(nearest.getId());
        emergencyRequest.setHospitalName(nearest.getName());
        emergencyRequest.setRequestTime(LocalDateTime.now());
        emergencyRequest.setStatus("ACTIVE");
        emergencyRequestRepo.save(emergencyRequest);

        // Create tracking session
        TrackingSession session = new TrackingSession(
                trackingId,
                nearest.getId(),
                request.getLatitude(),
                request.getLongitude()
        );
        activeSessions.put(trackingId, session);

        // Prepare notification data
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "EMERGENCY_ALERT");
        notification.put("trackingId", trackingId);
        notification.put("userLat", request.getLatitude());
        notification.put("userLng", request.getLongitude());
        notification.put("hospital", nearest.getName());
        notification.put("hospitalId", nearest.getId().toString());
        notification.put("hospitalLat", nearest.getLatitude());
        notification.put("hospitalLng", nearest.getLongitude());
        notification.put("contact", nearest.getContactEmail());
        notification.put("timestamp", System.currentTimeMillis());

        // Send to admin dashboard
        messagingTemplate.convertAndSend("/topic/admin/emergencies", notification);



        // Prepare response
        Map<String, String> response = new HashMap<>();
        response.put("trackingId", trackingId);
        response.put("hospital", nearest.getName());
        response.put("contact", nearest.getContactEmail());
        response.put("hospitalLat", String.valueOf(nearest.getLatitude()));
        response.put("hospitalLng", String.valueOf(nearest.getLongitude()));
        response.put("address", nearest.getAddress());
        response.put("phoneNumber", nearest.getPhoneNumber());
        response.put("description", nearest.getDescription());

        logger.debug("Found nearest hospital: {}", nearest.getAddress(),
                nearest.getPhoneNumber(),
                nearest.getDescription());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveSessions() {
        return ResponseEntity.ok(activeSessions);
    }



    @GetMapping("/hospital/image/{name}")
    public ResponseEntity<byte[]> getHospitalImage(@PathVariable String name) {
        logger.debug("Attempting to retrieve image for hospital: {}", name);

        try {
            Optional<Hospital> hospital = hospitalRepo.findByName(name);

            if (hospital.isEmpty()) {
                logger.error("Hospital not found in database for name: {}", name);
                return ResponseEntity.notFound().build();
            }

            Hospital foundHospital = hospital.get();
            byte[] imageData = foundHospital.getImage();

            if (imageData == null) {
                logger.warn("Hospital '{}' exists but has no image data (image is null)", name);
                return ResponseEntity.notFound().build();
            }

            if (imageData.length == 0) {
                logger.warn("Hospital '{}' has empty image data (0 bytes)", name);
                return ResponseEntity.notFound().build();
            }

            logger.debug("Successfully retrieved image for hospital '{}' - Type: JPEG, Size: {} bytes",
                    name, imageData.length);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageData);

        } catch (Exception e) {
            logger.error("Unexpected error while retrieving image for hospital '{}': {}", name, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }



    private ResponseEntity<byte[]> handleFoundHospitalImage(Hospital hospital, String identifier) {
        if (hospital.getImage() == null) {
            logger.warn("NO IMAGE DATA FOR HOSPITAL: {}", identifier);
            return ResponseEntity.notFound().build();
        }

        logger.debug("Returning image data for hospital {} - Size: {} bytes",
                identifier, hospital.getImage().length);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(hospital.getImage());
    }

}