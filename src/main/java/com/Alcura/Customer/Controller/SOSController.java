package com.Alcura.Customer.Controller;

import com.Alcura.Customer.DTO.SOSRequest;
import com.Alcura.Customer.DTO.TrackingSession;
import com.Alcura.Customer.Model.Hospital;
import com.Alcura.Customer.Repository.HospitalRepository;
import com.Alcura.Customer.RestControllerAdvice.HospitalNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/sos")
public class SOSController
{
    @Autowired
    private HospitalRepository hospitalRepo;
    private final Map<String, TrackingSession> activeSessions = new ConcurrentHashMap<>();


    @PostMapping("/start")
    public ResponseEntity<?> startSOSTracking (@RequestBody SOSRequest request)
    {
        Hospital nearest = hospitalRepo.findNearest(
                request.getLatitude(),
                request.getLongitude()
        ).orElseThrow(() -> new HospitalNotFoundException());

        String trackingId = UUID.randomUUID().toString();
        activeSessions.put(trackingId, new TrackingSession(
                trackingId,
                nearest.getId(),
                request.getLatitude(),
                request.getLongitude()
        ));

        Map<String,String> response = new TreeMap<>();
        response.put("trackingId", trackingId);
        response.put("hospital", nearest.getName());
        response.put("contact", nearest.getContactEmail());
        response.put("hospitalLat", nearest.getLatitude().toString());
        response.put("hospitalLng", nearest.getLongitude().toString());

        return ResponseEntity.ok(response);
    }
}
