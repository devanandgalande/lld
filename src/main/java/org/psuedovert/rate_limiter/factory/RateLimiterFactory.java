package org.psuedovert.rate_limiter.factory;

import org.psuedovert.rate_limiter.limiter.FixedWindowRateLimiter;
import org.psuedovert.rate_limiter.limiter.RateLimiter;
import org.psuedovert.rate_limiter.limiter.SlidingWindowLogRateLimiter;
import org.psuedovert.rate_limiter.limiter.TokenBucketRateLimiter;
import org.psuedovert.rate_limiter.model.RateLimiterConfig;
import org.psuedovert.rate_limiter.model.RateLimiterType;

public class RateLimiterFactory {

    public static RateLimiter createRateLimiter(RateLimiterType type, RateLimiterConfig config) {
        return switch (type) {
            case TOKEN_BUCKET -> new TokenBucketRateLimiter(config);
            case FIXED_WINDOW -> new FixedWindowRateLimiter(config);
            case SLIDING_WINDOW_LOG -> new SlidingWindowLogRateLimiter(config);
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
    }
}
