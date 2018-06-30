package knickknacker.tcp;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class TimeConverter {
    public static String getCurrentDateString() {
        Date date = new Date();
        return date.toString();
    }

    public static Date getCurrentDate() {
        return new Date();
    }

    public static Date stringToDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", new Locale("us"));
        ParsePosition pos = new ParsePosition(0);
        return format.parse(date, pos);
    }
}
