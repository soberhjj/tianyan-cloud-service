package com.newland.tianyan.gateway.model;

import lombok.Data;

/**
 *      限流key
 * @author sj
 *
 */
@Data
public class RateLimitKey {

    private String environment;
    
    private String account;
    
}