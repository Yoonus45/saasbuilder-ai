package com.yoonus.backend.service;

import com.yoonus.backend.dto.BillingPlanResponse;
import com.yoonus.backend.dto.SubscriptionStatusResponse;
import com.yoonus.backend.entity.SubscriptionStatus;

import java.util.List;

public interface BillingService {

    List<BillingPlanResponse> listPlans();

    SubscriptionStatusResponse getSubscriptionStatus(String email);

    SubscriptionStatusResponse subscribe(String email, String planId);

    SubscriptionStatusResponse cancelSubscription(String email);
}
