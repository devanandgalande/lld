package org.psuedovert.rate_limiter.limiter;

import lombok.AllArgsConstructor;
import org.psuedovert.rate_limiter.model.RateLimiterConfig;
import org.psuedovert.rate_limiter.model.RateLimiterType;

@AllArgsConstructor
public abstract class RateLimiter {
    protected final RateLimiterConfig config;
    protected final RateLimiterType type;

    public abstract boolean allowRequest(String userId);

}
