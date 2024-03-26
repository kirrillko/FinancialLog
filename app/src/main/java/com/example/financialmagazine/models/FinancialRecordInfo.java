package com.example.financialmagazine.models;

public class FinancialRecordInfo {
    private int id;
    private Category category;
    private double amount;
    private long timestamp;

    public FinancialRecordInfo(int id, Category category, double amount, long timestamp) {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
