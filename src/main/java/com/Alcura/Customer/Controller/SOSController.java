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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/sos")
public class SOSController {
    @Autowired
    private HospitalRepository hospitalRepo;

    @Autowired
    private EmergencyRequestRepository emergencyRequestRepo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private static final Logger logger = LoggerFactory.getLogger(SOSController.class);


    private final Map<String, TrackingSession> activeSessions = new ConcurrentHashMap<>();

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

    @GetMapping("/hospital/image/{identifier}")
    public ResponseEntity<byte[]> getHospitalImage(@PathVariable String identifier) {
        logger.debug("IMAGE ENDPOINT HIT - Identifier: {}", identifier);

        Hospital hospital;
        try {
            // First try to parse as Long ID
            Long hospitalId = Long.parseLong(identifier);
            hospital = hospitalRepo.findById(hospitalId)
                    .orElseThrow(() -> {
                        logger.error("HOSPITAL NOT FOUND BY ID: {}", hospitalId);
                        return new HospitalNotFoundException("Hospital not found");
                    });
        } catch (NumberFormatException e) {
            // If not a number, try to find by name
            hospital = hospitalRepo.findByName(identifier)
                    .orElseThrow(() -> {
                        logger.error("HOSPITAL NOT FOUND BY NAME: {}", identifier);
                        return new HospitalNotFoundException("Hospital not found");
                    });
        }

        if (hospital.getImage() == null) {
            logger.warn("NO IMAGE DATA FOR HOSPITAL: {}", identifier);
            return ResponseEntity.notFound().build();
        }

        logger.debug("Returning image data - Size: {} bytes", hospital.getImage().length);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(hospital.getImage());
    }

}