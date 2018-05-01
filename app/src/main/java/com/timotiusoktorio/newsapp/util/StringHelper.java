package com.timotiusoktorio.newsapp.util;

import android.content.Context;

import com.timotiusoktorio.newsapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class StringHelper {

    private static final long DAYS_IN_MILLIS = 86400000;
    private static final long HOURS_IN_MILLIS = 3600000;
    private static final long MINUTES_IN_MILLIS = 60000;

    /**
     * Helper method to format a Date in DateTime String format into an elapsed time representation.
     * Example: "2016-07-26T16:45:08Z" -> "2 hours ago"
     *
     * @param context  - Application Context
     * @param dateTime - Date in DateTime String format
     * @return Elapsed time representation (String)
     * @throws ParseException if SimpleDateFormat fails to parse date
     */
    public static String formatDateTimeToElapsedTimeString(Context context, String dateTime) throws ParseException {
        String elapsedTimeString;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date newsDate = format.parse(dateTime);
        long elapsedTime = System.currentTimeMillis() - newsDate.getTime();
        if (elapsedTime < DAYS_IN_MILLIS) {
            // The news is published today.
            if (elapsedTime < HOURS_IN_MILLIS) {
                // The news is published less than an hour ago.
                if (elapsedTime < MINUTES_IN_MILLIS) {
                    // The news is published less than 1 minute ago.
                    elapsedTimeString = context.getString(R.string.elapsed_time_seconds);
                } else {
                    // The news is published more than 1 minute ago.
                    // Set the elapsed time string to the number of minutes elapsed.
                    long minutesElapsed = elapsedTime / MINUTES_IN_MILLIS;
                    elapsedTimeString = String.format(context.getString(R.string.string_format_elapsed_time_minutes), minutesElapsed);
                }
            } else {
                // The news is published more than an hour ago.
                // Set the elapsed time string to the number of hours elapsed.
                long hoursElapsed = elapsedTime / HOURS_IN_MILLIS;
                elapsedTimeString = String.format(context.getString(R.string.string_format_elapsed_time_hours), hoursElapsed);
            }
        } else {
            // The news is published yesterday or more than 1 day ago.
            long daysElapsed = elapsedTime / DAYS_IN_MILLIS;
            if (daysElapsed == 1) {
                elapsedTimeString = context.getString(R.string.elapsed_time_yesterday);
            } else {
                elapsedTimeString = String.format(context.getString(R.string.string_format_elapsed_time_days), daysElapsed);
            }
        }
        return elapsedTimeString;
    }
}