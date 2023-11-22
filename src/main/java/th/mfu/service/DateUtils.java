package th.mfu.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.springframework.stereotype.Service;

@Service
public class DateUtils {
    public Date getDateByWeekAndDayLabel(Long week, String dayLabel, String startDateStr, String endDateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar calendar = Calendar.getInstance();
        try {
            if (week < 0) { return null; }
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);
            calendar.setTime(startDate);
            // Increment the date by the given number of weeks
            calendar.add(Calendar.WEEK_OF_YEAR, week.intValue());
            // Find the first occurrence of the desired day in the week
            int dayOfWeek = getDayOfWeek(dayLabel);
            while (calendar.getTime().before(endDate)) {
                if (calendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
                    return calendar.getTime();
                }
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    private int getDayOfWeek(String dayLabel) {
        switch (dayLabel) {
            case "Sun":
                return Calendar.SUNDAY;
            case "Mon":
                return Calendar.MONDAY;
            case "Tue":
                return Calendar.TUESDAY;
            case "Wed":
                return Calendar.WEDNESDAY;
            case "Thu":
                return Calendar.THURSDAY;
            case "Fri":
                return Calendar.FRIDAY;
            case "Sat":
                return Calendar.SATURDAY;
            default:
                return -1;
        }
    }
}