package com.example.gentletouchdentalclinic;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

public class Staff {

    private String id;
    private String fullName;
    private String gender;
    private String address;
    private String email;
    private String phone;
    private String position;
    private String dateOfBirth;
    private String hireDate;

    public Staff(){

    }

    public Staff(
            String id,
            String fullName,
            String gender,
            String address,
            String email,
            String phone,
            String position,
            String dateOfBirth,
            String hireDate
    ){

        this.id = id;
        this.fullName = fullName;
        this.gender = gender;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.position = position;
        this.dateOfBirth = dateOfBirth;
        this.hireDate = hireDate;

    }



    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }


    public String getFullName(){
        return fullName;
    }

    @PropertyName("Gender")
    public String getGender(){
        return gender;
    }

    public String getAddress(){
        return address;
    }

    public String getEmail(){
        return email;
    }

    public String getPhone(){
        return phone;
    }

    public String getPosition(){
        return position;
    }

    public String getDateOfBirth(){
        return dateOfBirth;
    }

    public String getHireDate(){
        return hireDate;
    }

    public void setFullName(String fullName){
        this.fullName = fullName;
    }


    @PropertyName("Gender")
    public void setGender(String gender){
        this.gender = gender;
    }


    public void setAddress(String address){
        this.address = address;
    }


    public void setEmail(String email){
        this.email = email;
    }


    public void setPhone(String phone){
        this.phone = phone;
    }


    public void setPosition(String position){
        this.position = position;
    }


    public void setDateOfBirth(String dateOfBirth){
        this.dateOfBirth = dateOfBirth;
    }


    public void setHireDate(String hireDate){
        this.hireDate = hireDate;
    }


}