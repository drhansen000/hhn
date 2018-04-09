package project.hhn_mobile;

import java.util.Arrays;
import java.util.List;

/**
 * Created by drhan on 4/9/2018.
 */

public class AppTime {
    int day;

    AppTime() {
        int day = 0;
    }

    AppTime(int day) {
        this.day = day;
    }

    List<String> getList() {
        if (day == 7) {
            // Closed on Sunday
            List<String> appTimes = Arrays.asList("Closed");
            return appTimes;
        } else if (day == 6) {
            // Open until 12 on Saturday
            List<String> appTimes = Arrays.asList(
                    "9:00 am",
                    "9:30 am",
                    "10:00 am",
                    "10:30 am",
                    "11:00 am",
                    "11:30 am"
            );
            return appTimes;
        } else {
            // Weekday
            List<String> appTimes = Arrays.asList(
                    "9:00 am",
                    "9:30 am",
                    "10:00 am",
                    "10:30 am",
                    "11:00 am",
                    "11:30 am",
                    "12:00 pm",
                    "12:30 pm",
                    "1:00 pm",
                    "1:30 pm",
                    "2:00 pm",
                    "2:30 pm",
                    "3:00 pm",
                    "3:30 pm",
                    "4:00 pm",
                    "4:30 pm"
            );
            return appTimes;
        }
    }
}
