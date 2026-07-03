package com.yoonus.backend.service.impl;

import com.yoonus.backend.dto.BillingPlanResponse;
import com.yoonus.backend.dto.SubscriptionStatusResponse;
import com.yoonus.backend.entity.Subscription;
import com.yoonus.backend.entity.SubscriptionStatus;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.exception.ResourceNotFoundException;
import com.yoonus.backend.repository.SubscriptionRepository;
import com.yoonus.backend.repository.UserRepository;
import com.yoonus.backend.service.BillingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BillingServiceImpl implements BillingService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    public BillingServiceImpl(UserRepository userRepository, SubscriptionRepository subscriptionRepository) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public List<BillingPlanResponse> listPlans() {
        return List.of(
                new BillingPlanResponse("free", "Free", "$0/mo", "Up to 5 AI generations per month", 5),
                new BillingPlanResponse("pro", "Pro", "$29/mo", "Unlimited AI generations and project exports", 1000)
        );
    }

    @Override
    public SubscriptionStatusResponse getSubscriptionStatus(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Optional<Subscription> subscription = subscriptionRepository.findFirstByUserOrderByCreatedAtDesc(user);
        return subscription.map(this::mapToResponse)
                .orElse(new SubscriptionStatusResponse("free", SubscriptionStatus.TRIALING, null));
    }

    @Override
    public SubscriptionStatusResponse subscribe(String email, String planId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SubscriptionStatus status = SubscriptionStatus.ACTIVE;
        Subscription subscription = new Subscription(user, planId, status);
        subscriptionRepository.save(subscription);

        return mapToResponse(subscription);
    }

    @Override
    public SubscriptionStatusResponse cancelSubscription(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Subscription subscription = subscriptionRepository.findFirstByUserOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new ResourceNotFoundException("No subscription found"));

        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscriptionRepository.save(subscription);

        return mapToResponse(subscription);
    }

    private SubscriptionStatusResponse mapToResponse(Subscription subscription) {
        return new SubscriptionStatusResponse(
                subscription.getPlan(),
                subscription.getStatus(),
                subscription.getStripeSubscriptionId()
        );
    }
}
