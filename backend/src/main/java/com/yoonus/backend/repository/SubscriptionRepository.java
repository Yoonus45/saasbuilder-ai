package com.yoonus.backend.repository;

import com.yoonus.backend.entity.Subscription;
import com.yoonus.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findFirstByUserOrderByCreatedAtDesc(User user);

    java.util.List<Subscription> findAllByUser(User user);
}
