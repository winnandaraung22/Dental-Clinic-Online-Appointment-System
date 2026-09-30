package com.example.gentletouchdentalclinic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class AppointmentReminderReceiver
        extends BroadcastReceiver {

    @Override
    public void onReceive(
            Context context,
            Intent intent) {

        String doctorName =
                intent.getStringExtra(
                        "doctorName"
                );

        String appointmentTime =
                intent.getStringExtra(
                        "appointmentTime"
                );

        int notificationId =
                intent.getIntExtra(
                        "notificationId",
                        1000
                );

        AppointmentNotificationHelper
                .showAppointmentReminder(
                        context,
                        notificationId,
                        doctorName,
                        appointmentTime
                );
    }
}