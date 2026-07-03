package com.yoonus.backend.dto;

import com.yoonus.backend.entity.SubscriptionStatus;

public class SubscriptionStatusResponse {

    private String plan;
    private SubscriptionStatus status;
    private String stripeSubscriptionId;

    public SubscriptionStatusResponse() {
    }

    public SubscriptionStatusResponse(String plan, SubscriptionStatus status, String stripeSubscriptionId) {
        this.plan = plan;
        this.status = status;
        this.stripeSubscriptionId = stripeSubscriptionId;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public String getStripeSubscriptionId() {
        return stripeSubscriptionId;
    }

    public void setStripeSubscriptionId(String stripeSubscriptionId) {
        this.stripeSubscriptionId = stripeSubscriptionId;
    }
}
