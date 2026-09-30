package com.example.gentletouchdentalclinic;

import java.util.ArrayList;

public class DoctorScheduleGroup {
    private String doctorName;
    private ArrayList<DoctorSchedule> schedules;

    public DoctorScheduleGroup(
            String doctorName,
            ArrayList<DoctorSchedule> schedules){

        this.doctorName = doctorName;
        this.schedules = schedules;

    }
    public String getDoctorName(){

        return doctorName;

    }
    public ArrayList<DoctorSchedule> getSchedules(){

        return schedules;

    }
}
