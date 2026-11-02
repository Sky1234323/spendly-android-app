package com.example.spendly;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static final long DAY = 24L * 60 * 60 * 1000;

    public static int dp(Context c, int value) {
        return Math.round(value * c.getResources().getDisplayMetrics().density);
    }

    public static String money(double v) {
        if (v == Math.rint(v)) {
            return "\u20B9" + String.format(Locale.getDefault(), "%,.0f", v);
        }
        return "\u20B9" + String.format(Locale.getDefault(), "%,.2f", v);
    }

    // 1250 becomes 1.3k, used on top of chart bars
    public static String compact(double v) {
        if (v >= 1000) {
            return String.format(Locale.getDefault(), "%.1fk", v / 1000);
        }
        return String.format(Locale.getDefault(), "%.0f", v);
    }

    public static double sum(List<Expense> list) {
        double total = 0;
        for (Expense e : list) {
            total += e.amount;
        }
        return total;
    }

    public static long dayStart(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public static long daysAgoStart(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -days);
        return dayStart(c.getTimeInMillis());
    }

    public static long monthStart() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        return dayStart(c.getTimeInMillis());
    }

    public static String formatDate(long time) {
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(time));
    }

    public static String friendlyDate(long time) {
        long d = dayStart(time);
        if (d == dayStart(System.currentTimeMillis())) return "Today";
        if (d == daysAgoStart(1)) return "Yesterday";
        return new SimpleDateFormat("dd MMM", Locale.getDefault()).format(new Date(time));
    }

    public static String dayLabel(long time) {
        return new SimpleDateFormat("EEE", Locale.getDefault()).format(new Date(time));
    }

    public static String monthLabel() {
        return new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date());
    }

    public static String greeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 12) return "Good morning";
        if (hour < 17) return "Good afternoon";
        return "Good evening";
    }
}
