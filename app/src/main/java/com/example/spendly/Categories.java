package com.example.spendly;

import android.graphics.drawable.GradientDrawable;

import androidx.core.graphics.ColorUtils;

public class Categories {

    public static final String[] NAMES = {
            "Food", "Travel", "Shopping", "Bills", "Health", "Fun", "Other"
    };

    // Food, Travel, Shopping, Bills, Health, Fun, Other
    public static final String[] EMOJIS = {
            "\uD83C\uDF54", "\uD83D\uDE8C", "\uD83D\uDECD", "\uD83D\uDCA1",
            "\uD83D\uDC8A", "\uD83C\uDFAC", "\uD83D\uDCE6"
    };

    public static final int[] COLORS = {
            0xFFF97316, 0xFF3B82F6, 0xFFEC4899, 0xFFEAB308,
            0xFFEF4444, 0xFF8B5CF6, 0xFF64748B
    };

    public static int index(String name) {
        for (int i = 0; i < NAMES.length; i++) {
            if (NAMES[i].equals(name)) return i;
        }
        return NAMES.length - 1;
    }

    public static String emoji(String name) {
        return EMOJIS[index(name)];
    }

    public static int color(String name) {
        return COLORS[index(name)];
    }

    // Soft coloured circle behind the emoji
    public static GradientDrawable circle(String name) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setColor(ColorUtils.setAlphaComponent(color(name), 38));
        return d;
    }
}