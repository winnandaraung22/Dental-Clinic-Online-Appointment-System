package com.example.gentletouchdentalclinic;

public class SearchResult {
    private String doctorId;
    private String doctorName;
    private String matchedText;
    private String matchedType;

    private String appointmentId;
    private String appointmentDate;
    private String appointmentTime;
    private String status;

    public SearchResult(
            String doctorId,
            String doctorName,
            String matchedText,
            String matchedType
    ) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.matchedText = matchedText;
        this.matchedType = matchedType;
    }
    public SearchResult(
            String appointmentId,
            String doctorName,
            String matchedText,
            String matchedType,
            String appointmentDate,
            String appointmentTime,
            String status
    ) {

        this.appointmentId = appointmentId;
        this.doctorName = doctorName;
        this.matchedText = matchedText;
        this.matchedType = matchedType;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }
    public String getMatchedText() {
        return matchedText;
    }
    public String getMatchedType() {
        return matchedType;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public String getStatus() {
        return status;
    }
}