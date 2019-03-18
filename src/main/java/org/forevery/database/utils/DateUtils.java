package org.forevery.database.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    /**
     * 获取现在的时间
     *
     * @return 字符串
     */
    public static Date getNowDate(String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(getNowDateString(format));
        } catch (ParseException e) {
            return new Date();
        }
    }

    /**
     * 获取现在的时间
     *
     * @return 字符串
     */
    public static long getNowDateLong() {
        Date date = new Date();
        return date.getTime();
    }

    /**
     * 获取现在的时间
     *
     * @param format yyyy-MM-dd HH:mm:ss
     * @return 字符串
     */
    public static String getNowDateString(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static void main(String[] args) {
        Log.i(new Date().getTime());
    }
}
