package fraternityandroid.greeklife;

import java.util.Calendar;

/**
 * Created by Jon Zlotnik on 2018-01-05.
 */

public class CalendarTools {

    public static boolean areOnSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static String weekDayToString(int day) {
        switch (day) {
            case 1:
                return "Sun";
            case 2:
                return "Mon";
            case 3:
                return "Tue";
            case 4:
                return "Wed";
            case 5:
                return "Thu";
            case 6:
                return "Fri";
            case 7:
                return "Sat";
            default:
                return "WTF?";
        }
    }

    public static String monthToString(int month) {
        switch (month) {
            case 0:
                return "Jan";
            case 1:
                return "Feb";
            case 2:
                return "Mar";
            case 3:
                return "Apr";
            case 4:
                return "May";
            case 5:
                return "Jun";
            case 6:
                return "Jul";
            case 7:
                return "Aug";
            case 8:
                return "Sep";
            case 9:
                return "Oct";
            case 10:
                return "Nov";
            case 11:
                return "Dec";
            default:
                return "Not a Month";
        }
    }
    public static String formatTime(Calendar cal) {

        String formattedTime = "";
        if ((Calendar.getInstance().getTimeInMillis()-cal.getTimeInMillis())<64800000){
            formattedTime += "Today ";
            int hour = cal.get(Calendar.HOUR);
            int minute = cal.get(Calendar.MINUTE);
            if (hour == 0) {
                hour = 12;
            }
            formattedTime += hour + ":";
            if (minute < 10) {
                formattedTime += "0" + minute;
            } else {
                formattedTime += minute;
            }

            if (cal.get(Calendar.AM_PM) == 1) {
                formattedTime += "pm";
            } else {
                formattedTime += "am";
            }
        } else {
            formattedTime = monthToString(cal.get(Calendar.MONTH))+" "+ cal.get(Calendar.DAY_OF_MONTH)+", "+cal.get(Calendar.YEAR);
        }
        return formattedTime;
    }

}
