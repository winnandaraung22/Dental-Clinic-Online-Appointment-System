package com.example.gentletouchdentalclinic;

import com.google.firebase.firestore.PropertyName;

public class Doctor {

    private String id;
    private String staffCode;
    private String fullName;
    private String email;
    private String phone;
    private String specialization;
    private String qualification;
    private String experience;
    private String profileImage;
    private double rating;

    public Doctor(){

    }

    public Doctor(
            String id,
            String staffCode,
            String fullName,
            String email,
            String phone,
            String specialization,
            String qualification,
            String experience,
            String profileImage,
            double rating){

        this.id = id;
        this.staffCode = staffCode;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.qualification = qualification;
        this.experience = experience;
        this.profileImage = profileImage;
        this.rating = rating;
    }

    public String getId(){
        return id;
    }

    @PropertyName("staffAccessCode")
    public String getStaffCode() { return staffCode; }
    public String getFullName(){
        return fullName;
    }

    public String getEmail(){
        return email;
    }

    public String getPhone(){
        return phone;
    }

    public String getSpecialization(){
        return specialization;
    }

    public String getQualification(){
        return qualification;
    }

    public String getExperience(){
        return experience;
    }

    public String getProfileImage(){
        return profileImage;
    }
    public double getRating(){ return rating; }


    public void setId(String id){
        this.id = id;
    }
    @PropertyName("staffAccessCode")
    public void setStaffCode(String staffCode) { this.staffCode = staffCode; }
    public void setFullName(String fullName){
        this.fullName = fullName;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public void setSpecialization(String specialization){
        this.specialization = specialization;
    }

    public void setQualification(String qualification){
        this.qualification = qualification;
    }

    public void setExperience(String experience){
        this.experience = experience;
    }

    public void setProfileImage(String profileImage){
        this.profileImage = profileImage;
    }
    public void setRating(double rating) { this.rating = rating; }

}
