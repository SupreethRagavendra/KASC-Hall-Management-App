package com.kasc.hall;

public class Booking {

    private String date;
    private String department;
    private String eventName;
    private String facultyId;
    private String facultyName;
    private String hallName;
    private String timeSlot;
    private String year;

    // Default constructor (required for Firebase)
    public Booking() {
    }

    public String getDate() {
        return date;
    }

    public String getDepartment() {
        return department;
    }

    public String getEventName() {
        return eventName;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public String getHallName() {
        return hallName;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public String getYear() {
        return year;
    }

    // Constructor with parameters (required for Firebase)
    public Booking(String date, String department, String eventName, String facultyId,
                   String facultyName, String hallName, String timeSlot, String year) {
        this.date = date;
        this.department = department;
        this.eventName = eventName;
        this.facultyId = facultyId;
        this.facultyName = facultyName;
        this.hallName = hallName;
        this.timeSlot = timeSlot;
        this.year = year;
    }
}
