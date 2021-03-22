package com.newland.tianyan.gateway.filter.ratelimit;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.ratelimit.AbstractRateLimiter;
import org.springframework.cloud.gateway.support.ConfigurationService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.style.ToStringCreator;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.validation.annotation.Validated;

import com.alibaba.fastjson.JSON;
import com.newland.tianyan.gateway.model.RateLimitKey;
import com.newland.tianyan.gateway.service.IRedisService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 基于redis实现多租户动态限流
 * @author sj
 *
 */
public class DynamicRedisRateLimiter extends AbstractRateLimiter<DynamicRedisRateLimiter.Config> implements ApplicationContextAware {
 
	/**
	 * Redis Rate Limiter property name.
	 */
	public static final String CONFIGURATION_PROPERTY_NAME = "redis-rate-limiter";
	/**
	 * Redis Script name.
	 */
	public static final String REDIS_SCRIPT_NAME = "redisRequestRateLimiterScript";

	/**
	 * Remaining Rate Limit header name.
	 */
	public static final String REMAINING_HEADER = "X-RateLimit-Remaining";

	/**
	 * Replenish Rate Limit header name.
	 */
	public static final String REPLENISH_RATE_HEADER = "X-RateLimit-Replenish-Rate";

	/**
	 * Burst Capacity header name.
	 */
	public static final String BURST_CAPACITY_HEADER = "X-RateLimit-Burst-Capacity";

	/**
	 * Requested Tokens header name.
	 */
	public static final String REQUESTED_TOKENS_HEADER = "X-RateLimit-Requested-Tokens";
	
	private Log log = LogFactory.getLog(getClass());

	private ReactiveStringRedisTemplate redisTemplate;

	private RedisScript<List<Long>> script;

	private AtomicBoolean initialized = new AtomicBoolean(false);

	private Config defaultConfig;

	// configuration properties
	/**
	 * Whether or not to include headers containing rate limiter information, defaults to
	 * true.
	 */
	private boolean includeHeaders = true;

	/**
	 * The name of the header that returns number of remaining requests during the current
	 * second.
	 */
	private String remainingHeader = REMAINING_HEADER;

	/** The name of the header that returns the replenish rate configuration. */
	private String replenishRateHeader = REPLENISH_RATE_HEADER;

	/** The name of the header that returns the burst capacity configuration. */
	private String burstCapacityHeader = BURST_CAPACITY_HEADER;

	/** The name of the header that returns the requested tokens configuration. */
	private String requestedTokensHeader = REQUESTED_TOKENS_HEADER;
 
//    public volatile static Map<String, RateLimitConfig> rateLimitConfigMap = new HashMap<>();
 
    @Autowired
    IRedisService redisConfigService;
    
    public DynamicRedisRateLimiter(ReactiveStringRedisTemplate redisTemplate,
			RedisScript<List<Long>> script, ConfigurationService configurationService) {
		super(Config.class, CONFIGURATION_PROPERTY_NAME, configurationService);
		this.redisTemplate = redisTemplate;
		this.script = script;
		this.initialized.compareAndSet(false, true);
	}

	/**
	 * This creates an instance with default static configuration, useful in Java DSL.
	 * @param defaultReplenishRate how many tokens per second in token-bucket algorithm.
	 * @param defaultBurstCapacity how many tokens the bucket can hold in token-bucket
	 * algorithm.
	 */
	public DynamicRedisRateLimiter(int defaultReplenishRate, int defaultBurstCapacity) {
		super(Config.class, CONFIGURATION_PROPERTY_NAME, (ConfigurationService) null);
		this.defaultConfig = new Config().setReplenishRate(defaultReplenishRate)
				.setBurstCapacity(defaultBurstCapacity);
	}

	/**
	 * This creates an instance with default static configuration, useful in Java DSL.
	 * @param defaultReplenishRate how many tokens per second in token-bucket algorithm.
	 * @param defaultBurstCapacity how many tokens the bucket can hold in token-bucket
	 * algorithm.
	 * @param defaultRequestedTokens how many tokens are requested per request.
	 */
	public DynamicRedisRateLimiter(int defaultReplenishRate, int defaultBurstCapacity,
			int defaultRequestedTokens) {
		this(defaultReplenishRate, defaultBurstCapacity);
		this.defaultConfig.setRequestedTokens(defaultRequestedTokens);
	}
 
	static List<String> getKeys(String id) {
		String prefix = "request_rate_limiter.{" + id;
		String tokenKey = prefix + "}.tokens";
		String timestampKey = prefix + "}.timestamp";
		return Arrays.asList(tokenKey, timestampKey);
	}
 
	public boolean isIncludeHeaders() {
		return includeHeaders;
	}

	public void setIncludeHeaders(boolean includeHeaders) {
		this.includeHeaders = includeHeaders;
	}

	public String getRemainingHeader() {
		return remainingHeader;
	}

	public void setRemainingHeader(String remainingHeader) {
		this.remainingHeader = remainingHeader;
	}

	public String getReplenishRateHeader() {
		return replenishRateHeader;
	}

	public void setReplenishRateHeader(String replenishRateHeader) {
		this.replenishRateHeader = replenishRateHeader;
	}

	public String getBurstCapacityHeader() {
		return burstCapacityHeader;
	}

	public void setBurstCapacityHeader(String burstCapacityHeader) {
		this.burstCapacityHeader = burstCapacityHeader;
	}

	public String getRequestedTokensHeader() {
		return requestedTokensHeader;
	}

	public void setRequestedTokensHeader(String requestedTokensHeader) {
		this.requestedTokensHeader = requestedTokensHeader;
	}
	
	/**
	 * Used when setting default configuration in constructor.
	 * @param context the ApplicationContext object to be used by this object
	 * @throws BeansException if thrown by application context methods
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		if (initialized.compareAndSet(false, true)) {
			if (this.redisTemplate == null) {
				this.redisTemplate = context.getBean(ReactiveStringRedisTemplate.class);
			}
			this.script = context.getBean(REDIS_SCRIPT_NAME, RedisScript.class);
			if (context.getBeanNamesForType(ConfigurationService.class).length > 0) {
				setConfigurationService(context.getBean(ConfigurationService.class));
			}
		}
	}
	
	/* for testing */ Config getDefaultConfig() {
		return defaultConfig;
	}
	
	/**
	 * This uses a basic token bucket algorithm and relies on the fact that Redis scripts
	 * execute atomically. No other operations can run between fetching the count and
	 * writing the new count.
	 */
	@Override
	public Mono<Response> isAllowed(String routeId, String id) {
		if (!this.initialized.get()) {
			throw new IllegalStateException("DynamicRedisRateLimiter is not initialized");
		}
		RateLimitKey rateLimitKey = JSON.parseObject(id, RateLimitKey.class);
		Config routeConfig = getLimitConfig(rateLimitKey, routeId);
		
		// How many requests per second do you want a user to be allowed to do?
		int replenishRate = routeConfig.getReplenishRate();

		// How much bursting do you want to allow?
		int burstCapacity = routeConfig.getBurstCapacity();

		// How many tokens are requested per request?
		int requestedTokens = routeConfig.getRequestedTokens();

		try {
			List<String> keys = getKeys(rateLimitKey.getEnvironment() + "." + rateLimitKey.getAccount() + "." + routeId);

			// The arguments to the LUA script. time() returns unixtime in seconds.
			List<String> scriptArgs = Arrays.asList(replenishRate + "",
					burstCapacity + "", getTime(1) + "",
					requestedTokens + "");
			// allowed, tokens_left = redis.eval(SCRIPT, keys, args)
			Flux<List<Long>> flux = this.redisTemplate.execute(this.script, keys,
					scriptArgs);
			// .log("redisratelimiter", Level.FINER);
			return flux.onErrorResume(throwable -> {
				if (log.isDebugEnabled()) {
					log.debug("Error calling rate limiter lua", throwable);
				}
				return Flux.just(Arrays.asList(1L, -1L));
			}).reduce(new ArrayList<Long>(), (longs, l) -> {
				longs.addAll(l);
				return longs;
			}).map(results -> {
				boolean allowed = results.get(0) == 1L;
				Long tokensLeft = results.get(1);

				Response response = new Response(allowed,
						getHeaders(routeConfig, tokensLeft));

				if (log.isDebugEnabled()) {
					log.debug("response: " + response);
				}
				if(allowed) {
					log.info("-----------------"+ routeId + "--------------------------------");
				}
				return response;
			});
		}
		catch (Exception e) {
			/*
			 * We don't want a hard dependency on Redis to allow traffic. Make sure to set
			 * an alert so you know if this is happening too much. Stripe's observed
			 * failure rate is 0.01%.
			 */
			log.error("Error determining if user allowed from redis", e);
		}
		return Mono.just(new Response(true, getHeaders(routeConfig, -1L)));
	}

    public static  long getTime(int type){
        long time = Instant.now().getEpochSecond();
        switch (type){
            case 1:
                break;
            case 2:
                time = time / (60);
                break;
            case 3:
                time = time / (60 * 60);
                break;
            case 4:
                time = time / (60 * 60 * 24);
                break;
            default:
            	return time;
        }
        return time;
    }

    /**
     * 从redis中获取配置信息
     * @param routeId
     * @return
     */
    private Config getLimitConfig(RateLimitKey rateLimitKey, String routeId) {
    	Config routeConfig = redisConfigService.getAccountLimit(rateLimitKey, routeId);
    	routeConfig = routeConfig == null ? getConfig().getOrDefault(routeId, defaultConfig) : routeConfig;
    	if (routeConfig == null) {
			throw new IllegalArgumentException(
					"No RateLimit Configuration found for route " + routeId);
		}
		return routeConfig;
    }
 
 
    @NotNull
	public Map<String, String> getHeaders(Config config, Long tokensLeft) {
		Map<String, String> headers = new HashMap<>(16);
		if (isIncludeHeaders()) {
			headers.put(this.remainingHeader, tokensLeft.toString());
			headers.put(this.replenishRateHeader,
					String.valueOf(config.getReplenishRate()));
			headers.put(this.burstCapacityHeader,
					String.valueOf(config.getBurstCapacity()));
			headers.put(this.requestedTokensHeader,
					String.valueOf(config.getRequestedTokens()));
		}
		return headers;
	}
    
    

    @Validated
	public static class Config {

		@Min(1)
		private int replenishRate;

		@Min(0)
		private int burstCapacity = 1;

		@Min(1)
		private int requestedTokens = 1;

		public int getReplenishRate() {
			return replenishRate;
		}

		public Config setReplenishRate(int replenishRate) {
			this.replenishRate = replenishRate;
			return this;
		}

		public int getBurstCapacity() {
			return burstCapacity;
		}

		public Config setBurstCapacity(int burstCapacity) {
			this.burstCapacity = burstCapacity;
			return this;
		}

		public int getRequestedTokens() {
			return requestedTokens;
		}

		public Config setRequestedTokens(int requestedTokens) {
			this.requestedTokens = requestedTokens;
			return this;
		}

		@Override
		public String toString() {
			return new ToStringCreator(this).append("replenishRate", replenishRate)
					.append("burstCapacity", burstCapacity)
					.append("requestedTokens", requestedTokens).toString();

		}
	}
}