package com.example.gentletouchdentalclinic;

public class DoctorSchedule {
    private String id; //doctorSchedules collection document ID
    private String doctorDocumentID;
    private String staffCode;
    private String doctorName;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String status;
    private String specialization;

    public DoctorSchedule() {

    }

    public DoctorSchedule(
            String doctorDocumentID,
            String staffCode,
            String doctorName,
            String dayOfWeek,
            String startTime,
            String endTime,
            String specialization,
            String status
    ) {

        this.doctorDocumentID = doctorDocumentID;
        this.staffCode = staffCode;
        this.doctorName = doctorName;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.specialization = specialization;
        this.status = status;

    }


    public String getId(){return id;}
    public String getDoctorDocumentID() {return doctorDocumentID;}
    public String getStaffCode() {return staffCode;}

    public String getDoctorName() {return doctorName;}

    public String getDayOfWeek() {return dayOfWeek;}

    public String getStartTime() {
        return startTime;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setId(String id){
        this.id = id;
    }
    public void setDoctorDocumentID(String doctorDocumentID) {this.doctorDocumentID = doctorDocumentID;}

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    public void setStartTime(String startTime) {this.startTime = startTime;}
    public void setEndTime(String endTime) {this.endTime = endTime;}

    public void setStatus(String status) {this.status = status;}

    public void setSpecialization(String specialization) {this.specialization = specialization;}
}
