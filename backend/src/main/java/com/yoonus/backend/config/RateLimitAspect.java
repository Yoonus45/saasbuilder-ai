package com.yoonus.backend.config;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Component
public class RateLimitAspect {

    private final ConcurrentHashMap<String, RateLimitEntry> requestCounts = new ConcurrentHashMap<>();

    @Before("@annotation(rateLimit)")
    public void enforceRateLimit(RateLimit rateLimit) throws Exception {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return;
        }

        String clientId = attrs.getRequest().getRemoteAddr();
        String key = clientId;

        RateLimitEntry entry = requestCounts.compute(key, (k, v) -> {
            if (v == null) {
                return new RateLimitEntry(rateLimit.maxRequests(), rateLimit.windowSeconds());
            }
            if (System.currentTimeMillis() - v.windowStart > v.windowMs) {
                return new RateLimitEntry(rateLimit.maxRequests(), rateLimit.windowSeconds());
            }
            return v;
        });

        if (!entry.tryConsume()) {
            throw new RateLimitedException("Rate limit exceeded");
        }
    }

    public static class RateLimitEntry {
        private final long windowMs;
        private long windowStart;
        private final int maxRequests;
        private AtomicInteger count;

        public RateLimitEntry(int maxRequests, int windowSeconds) {
            this.maxRequests = maxRequests;
            this.windowMs = windowSeconds * 1000L;
            this.windowStart = System.currentTimeMillis();
            this.count = new AtomicInteger(0);
        }

        public synchronized boolean tryConsume() {
            if (System.currentTimeMillis() - windowStart > windowMs) {
                windowStart = System.currentTimeMillis();
                count = new AtomicInteger(0);
            }
            return count.incrementAndGet() <= maxRequests;
        }
    }

    public static class RateLimitedException extends RuntimeException {
        public RateLimitedException(String message) {
            super(message);
        }
    }
}
