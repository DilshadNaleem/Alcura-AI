package com.Alcura.Doctor.DTO;

public class AvailabilityDTO
{
    private String dayOfWeek;
    private boolean available;
    private String startTime;
    private String endTime;
    private String validFrom;

    public AvailabilityDTO(String dayOfWeek, boolean available, String startTime, String endTime, String validFrom) {
        this.dayOfWeek = dayOfWeek;
        this.available = available;
        this.startTime = startTime;
        this.endTime = endTime;
        this.validFrom = validFrom;
    }

    // Getters and setters
    public String getDayOfWeek() { return dayOfWeek; }
    public boolean isAvailable() { return available; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getValidFrom() { return validFrom; }
}

