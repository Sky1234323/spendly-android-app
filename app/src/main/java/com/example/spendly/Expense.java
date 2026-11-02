package com.example.spendly;

public class Expense {
    public final long id;
    public final String title;
    public final double amount;
    public final String category;
    public final long date;

    public Expense(long id, String title, double amount, String category, long date) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }
}