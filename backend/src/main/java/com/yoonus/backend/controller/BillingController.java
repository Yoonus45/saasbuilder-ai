package com.yoonus.backend.controller;

import com.yoonus.backend.dto.BillingPlanResponse;
import com.yoonus.backend.dto.SubscriptionStatusResponse;
import com.yoonus.backend.service.BillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping("/plans")
    public ResponseEntity<List<BillingPlanResponse>> listPlans() {
        return ResponseEntity.ok(billingService.listPlans());
    }

    @GetMapping("/status")
    public ResponseEntity<SubscriptionStatusResponse> getStatus(Authentication authentication) {
        return ResponseEntity.ok(billingService.getSubscriptionStatus(authentication.getName()));
    }

    @PostMapping("/subscribe/{planId}")
    public ResponseEntity<SubscriptionStatusResponse> subscribe(Authentication authentication, @PathVariable String planId) {
        return ResponseEntity.ok(billingService.subscribe(authentication.getName(), planId));
    }

    @PostMapping("/cancel")
    public ResponseEntity<SubscriptionStatusResponse> cancel(Authentication authentication) {
        return ResponseEntity.ok(billingService.cancelSubscription(authentication.getName()));
    }
}
