package org.psuedovert.rate_limiter.limiter;

import org.psuedovert.rate_limiter.model.RateLimiterConfig;
import org.psuedovert.rate_limiter.model.RateLimiterType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TokenBucketRateLimiter extends RateLimiter{
    private final Map<String, Integer> tokens = new ConcurrentHashMap<>();
    private final Map<String, Long> lastRefillTime = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter(RateLimiterConfig config) {
        super(config, RateLimiterType.TOKEN_BUCKET);
    }

    @Override
    public boolean allowRequest(String userId) {
        AtomicBoolean allowed = new AtomicBoolean(false);
        tokens.compute(userId, (id, availableTokens) -> {
            int currentTokens = refillTokens(userId, System.currentTimeMillis());
            if (currentTokens > 0) {
                allowed.set(true);
                currentTokens -= 1;     //consume 1 token
            }
            return currentTokens;
        });
        return allowed.get();
    }

    private int refillTokens(String userId, long now) {
        double refillRate = (double) config.getWindowInSeconds()/config.getMaxRequests();

        lastRefillTime.putIfAbsent(userId, now);
        long lastRefill = lastRefillTime.get(userId);
        long elapsedSeconds = (now - lastRefill)/1000;

        int refillTokens = (int) (elapsedSeconds/refillRate);
        int currentTokens = tokens.getOrDefault(userId, config.getMaxRequests());
        currentTokens = Math.min(config.getMaxRequests(), currentTokens + refillTokens);

        if (refillTokens > 0) lastRefillTime.put(userId, now);
        return currentTokens;
    }
}
