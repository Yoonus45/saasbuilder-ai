package com.yoonus.backend.dto;

public class BillingPlanResponse {

    private String planId;
    private String name;
    private String price;
    private String description;
    private int monthlyLimit;

    public BillingPlanResponse() {
    }

    public BillingPlanResponse(String planId, String name, String price, String description, int monthlyLimit) {
        this.planId = planId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.monthlyLimit = monthlyLimit;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(int monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }
}
