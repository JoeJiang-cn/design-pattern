package com.joe.pattern.format;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

/**
 * @author Joe
 * TODO description
 * 2021/11/3 15:37
 */
public class DateFormatTest {
    public static void main(String[] args) {
        // 日本必须加variant，https://docs.oracle.com/javase/7/docs/technotes/guides/intl/calendar.doc.html
        // System.out.println(formatWithSimpleDateFormat("GGGGYYYY-MM-dd,EEEE", new Locale("ja","JP","JP"), new Date()));
        // 泰国不用加variant
        // System.out.println(formatWithSimpleDateFormat("YYYY-MM-dd,EEEE", new Locale("th","TH"), new Date()));
        // simpleDateFormat不支持民国年
        // System.out.println(formatWithSimpleDateFormat("YYYY-MM-dd,EEEE", new Locale("zh","TW"), new Date()));

        System.out.println(dateFormat(new Date(), "GGyyy-MM-dd, EEEE", Locale.TAIWAN, "m"));
    }

    private static String formatWithSimpleDateFormat(String pattern, Locale locale, Date date) {
        return new SimpleDateFormat(pattern, locale).format(date);
    }

    private static String dateFormat(Date date, String pattern, Locale locale, String chronology) {
        if (pattern == null || "".equals(pattern)) {
            return date.toString();
        }
        DateTimeFormatter df = new DateTimeFormatterBuilder().appendPattern(pattern).toFormatter(locale);
        if ("j".equals(chronology)) {
            df = df.withChronology(JapaneseChronology.INSTANCE);
        } else if ("m".equals(chronology)) {
            df = df.withChronology(MinguoChronology.INSTANCE);
        } else if ("t".equals(chronology)) {
            df = df.withChronology(ThaiBuddhistChronology.INSTANCE);
        }
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime.format(df);
    }

}
