package com.example.gentletouchdentalclinic;

public class ChatUtils {

    public static String getAdminDoctorChatId(
            String adminId,
            String doctorId) {

        if (adminId.compareTo(doctorId) < 0) {
            return adminId + "_" + doctorId;
        } else {
            return doctorId + "_" + adminId;
        }
    }
}