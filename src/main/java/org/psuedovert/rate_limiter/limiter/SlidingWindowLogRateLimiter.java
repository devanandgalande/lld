package org.psuedovert.rate_limiter.limiter;

import org.psuedovert.rate_limiter.model.RateLimiterConfig;
import org.psuedovert.rate_limiter.model.RateLimiterType;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SlidingWindowLogRateLimiter extends RateLimiter{
    private final Map<String, Queue<Long>> requestLog = new ConcurrentHashMap<>();

    public SlidingWindowLogRateLimiter(RateLimiterConfig config) {
        super(config, RateLimiterType.SLIDING_WINDOW_LOG);
    }

    @Override
    public boolean allowRequest(String userId) {
        AtomicBoolean allowRequest = new AtomicBoolean(false);
        long now = System.currentTimeMillis();
        requestLog.compute(userId, (id, log) -> {
            if (log == null) log = new ArrayDeque<>();
            while (!log.isEmpty() && ((now-log.peek())/1000) >= config.getWindowInSeconds()) {
                log.poll();
            }
            if (log.size() < config.getMaxRequests()) {
                allowRequest.set(true);
                log.offer(now);
            }
            return log;
        });
        return allowRequest.get();
    }
}
