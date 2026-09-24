package org.psuedovert.rate_limiter.limiter;

import org.psuedovert.rate_limiter.model.RateLimiterConfig;
import org.psuedovert.rate_limiter.model.RateLimiterType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class FixedWindowRateLimiter extends RateLimiter{
    private final Map<String, Integer> requestCount;
    private final Map<String, Long> windowStart;

    public FixedWindowRateLimiter(RateLimiterConfig config) {
        super(config, RateLimiterType.FIXED_WINDOW);
        requestCount = new ConcurrentHashMap<>();
        windowStart = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allowRequest(String userId) {
        AtomicBoolean allowRequest = new AtomicBoolean(false);
        long currentRequestWindow = System.currentTimeMillis() / 1000 / config.getWindowInSeconds();

        requestCount.compute(userId, (id, count) -> {
            long previousWindow = windowStart.getOrDefault(id, 0L);

            if(currentRequestWindow != previousWindow) {
                //window expired -> create new window & reset counter
                allowRequest.set(true);
                windowStart.put(id, currentRequestWindow);
                return 1;   //first request in new window
            }
            if (count == null) count = 0;
            if (count < config.getMaxRequests()) {
                allowRequest.set(true);
                return count + 1;
            }
            return count;
        });
        return allowRequest.get();
    }
}
