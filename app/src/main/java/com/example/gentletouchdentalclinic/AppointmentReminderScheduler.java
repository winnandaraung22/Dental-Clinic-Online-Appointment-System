package com.example.gentletouchdentalclinic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppointmentReminderScheduler {
    public static void scheduleReminder(
            Context context,
            String appointmentId,
            String appointmentDate,
            String appointmentTime,
            String doctorName) {

        try {

            SimpleDateFormat formatter =
                    new SimpleDateFormat(
                            "yyyy-MM-dd hh:mm a",
                            Locale.ENGLISH
                    );

            String dateTime =
                    appointmentDate + " " + appointmentTime;

            Date appointmentDateTime =
                    formatter.parse(dateTime);

            if (appointmentDateTime == null) {
                return;
            }

            // Reminder = 2 hours before appointment
            long reminderTime =
                    appointmentDateTime.getTime()
                            - (2 * 60 * 60 * 1000);

            if (reminderTime <= System.currentTimeMillis()) {

                return;
            }

            AlarmManager alarmManager =
                    (AlarmManager)
                            context.getSystemService(
                                    Context.ALARM_SERVICE
                            );

            if (alarmManager == null) {
                return;
            }

            Intent intent =
                    new Intent(
                            context,
                            AppointmentReminderReceiver.class
                    );

            intent.putExtra(
                    "appointmentId",
                    appointmentId
            );

            intent.putExtra(
                    "doctorName",
                    doctorName
            );

            intent.putExtra(
                    "appointmentTime",
                    appointmentTime
            );

            int notificationId =
                    appointmentId.hashCode();

            intent.putExtra(
                    "notificationId",
                    notificationId
            );

            PendingIntent pendingIntent =
                    PendingIntent.getBroadcast(
                            context,
                            notificationId,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT |
                                    PendingIntent.FLAG_IMMUTABLE
                    );

            alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTime,
                    pendingIntent
            );


        }
        catch (Exception e) {

            e.printStackTrace();

            Toast.makeText(
                    context,
                    "Failed to schedule reminder: "
                            + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}