package org.psuedovert.rate_limiter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class User {
    private String userId;
    private UserTier tier;

}
