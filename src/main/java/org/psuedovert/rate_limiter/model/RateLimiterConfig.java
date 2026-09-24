package org.psuedovert.rate_limiter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RateLimiterConfig {
    private int maxRequests;
    private int windowInSeconds;
}
