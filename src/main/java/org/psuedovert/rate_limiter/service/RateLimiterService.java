package org.psuedovert.rate_limiter.service;

import org.psuedovert.rate_limiter.factory.RateLimiterFactory;
import org.psuedovert.rate_limiter.limiter.RateLimiter;
import org.psuedovert.rate_limiter.model.RateLimiterConfig;
import org.psuedovert.rate_limiter.model.RateLimiterType;
import org.psuedovert.rate_limiter.model.User;
import org.psuedovert.rate_limiter.model.UserTier;

import java.util.HashMap;
import java.util.Map;

public class RateLimiterService {
    private final Map<UserTier, RateLimiter> rateLimiters = new HashMap<>();

    public RateLimiterService() {
        rateLimiters.put(
                UserTier.FREE,
                RateLimiterFactory.createRateLimiter(
                        RateLimiterType.FIXED_WINDOW,
                        new RateLimiterConfig(2, 10)
                )
        );
        rateLimiters.put(
                UserTier.PREMIUM,
                RateLimiterFactory.createRateLimiter(
                        RateLimiterType.TOKEN_BUCKET,
                        new RateLimiterConfig(10, 10)
                )
        );
    }

    public boolean allowRequest(User user) {
        RateLimiter rateLimiter = rateLimiters.get(user.getTier());
        if (rateLimiter == null) {
            throw new IllegalArgumentException("RateLimiter is not configured for tier: " + user.getTier());
        }
        return rateLimiter.allowRequest(user.getUserId());
    }

}
