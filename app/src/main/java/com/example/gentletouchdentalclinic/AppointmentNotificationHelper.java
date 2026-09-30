package com.example.gentletouchdentalclinic;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AppointmentNotificationHelper {

    public static final String CHANNEL_ID =
            "appointment_reminders";

    public static void createNotificationChannel(
            Context context) {

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Appointment Reminders",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            channel.setDescription(
                    "Reminders for upcoming dental appointments"
            );

            NotificationManager manager =
                    context.getSystemService(
                            NotificationManager.class
                    );

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static void showAppointmentReminder(
            Context context,
            int notificationId,
            String doctorName,
            String appointmentTime) {

        createNotificationChannel(context);

        String title =
                "Dental Appointment Reminder";

        String message =
                "Your appointment with " +
                        doctorName +
                        " is in 2 hours at " +
                        appointmentTime +
                        ".";

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        CHANNEL_ID
                )
                        .setSmallIcon(
                                R.drawable.notification
                        )
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(message)
                        )
                        .setPriority(
                                NotificationCompat.PRIORITY_HIGH
                        )
                        .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >=
        Build.VERSION_CODES.TIRAMISU){

            if(context.checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED){

                return;
            }
        }

        NotificationManagerCompat
                .from(context)
                .notify(
                        notificationId,
                        builder.build()
                );
    }
}