package com.example.gentletouchdentalclinic;

import com.google.firebase.Timestamp;

public class Appointment {

    private String id;
    private String patientId;
    private String patientName;
    private Long age;
    private String gender;
    private String phone;
    private String address;

    private String doctorId;
    private String doctorName;
    private String specialization;
    private String appointmentDate;
    private String appointmentDay;
    private String appointmentTime;
    private String appointmentType;
    private String reason;
    private String status;
    private Timestamp createdAt;

    public Appointment() {

    }

    public Appointment(
            String id,
            String patientId,
            String patientName,
            Long age,
            String gender,
            String phone,
            String address,
            String doctorId,
            String doctorName,
            String specialization,
            String appointmentDate,
            String appointmentDay,
            String appointmentTime,
            String appointmentType,
            String reason,
            String status,
            Timestamp createdAt
    ) {

        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.address = address;

        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.specialization = specialization;

        this.appointmentDate = appointmentDate;
        this.appointmentDay = appointmentDay;
        this.appointmentTime = appointmentTime;

        this.appointmentType = appointmentType;
        this.reason = reason;

        this.status = status;

        this.createdAt = createdAt;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }


    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }


    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }


    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }


    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }


    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {this.appointmentDate = appointmentDate;}

    public String getAppointmentDay() {
        return appointmentDay;
    }

    public void setAppointmentDay(String appointmentDay) {
        this.appointmentDay = appointmentDay;
    }


    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {this.appointmentTime = appointmentTime;}


    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {this.appointmentType = appointmentType;}


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}