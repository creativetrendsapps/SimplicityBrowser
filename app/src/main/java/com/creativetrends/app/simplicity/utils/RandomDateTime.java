package com.creativetrends.app.simplicity.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class RandomDateTime {
    public static void main(String[] args) {

        SimpleDateFormat dfDateTime  = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        int year = RandomDateTime.randBetween(2018, 2019);// Here you can set Range of years you need
        int month = RandomDateTime.randBetween(0, 11);
        int hour = RandomDateTime.randBetween(9, 22); //Hours will be displayed in between 9 to 22
        int min = RandomDateTime.randBetween(0, 59);


        GregorianCalendar gc = new GregorianCalendar(year, month, 1);
        int day = RandomDateTime.randBetween(1, gc.getActualMaximum(Calendar.DAY_OF_MONTH));

        gc.set(year, month, day, hour, min);

        System.out.println(dfDateTime.format(gc.getTime()));

    }


    public static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }
}
