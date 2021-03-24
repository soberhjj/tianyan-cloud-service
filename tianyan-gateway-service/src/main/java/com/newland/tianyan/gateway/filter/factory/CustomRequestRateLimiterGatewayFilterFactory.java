package com.newland.tianyan.gateway.filter.factory;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.setResponseStatus;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.codec.Charsets;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.HttpStatusHolder;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;

import com.alibaba.fastjson.JSON;
import com.newland.tianya.commons.base.model.JsonErrorObject;
import com.newland.tianya.commons.base.utils.LogIdUtils;
import com.newland.tianyan.gateway.constant.ErrorCodeEnum;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * User Request Rate Limiter filter. See https://stripe.com/blog/rate-limiters and
 */
@ConfigurationProperties("spring.cloud.gateway.filter.request-rate-limiter")
@Slf4j
public class CustomRequestRateLimiterGatewayFilterFactory extends
		AbstractGatewayFilterFactory<CustomRequestRateLimiterGatewayFilterFactory.Config> {

	/**
	 * Key-Resolver key.
	 */
	public static final String KEY_RESOLVER_KEY = "keyResolver";

	private static final String EMPTY_KEY = "____EMPTY_KEY__";

	private final RateLimiter defaultRateLimiter;

	private final KeyResolver defaultKeyResolver;

	/**
	 * Switch to deny requests if the Key Resolver returns an empty key, defaults to true.
	 */
	private boolean denyEmptyKey = true;

	/** HttpStatus to return when denyEmptyKey is true, defaults to FORBIDDEN. */
	private String emptyKeyStatusCode = HttpStatus.FORBIDDEN.name();

	public CustomRequestRateLimiterGatewayFilterFactory(RateLimiter defaultRateLimiter,
			KeyResolver defaultKeyResolver) {
		super(Config.class);
		this.defaultRateLimiter = defaultRateLimiter;
		this.defaultKeyResolver = defaultKeyResolver;
	}

	public KeyResolver getDefaultKeyResolver() {
		return defaultKeyResolver;
	}

	public RateLimiter getDefaultRateLimiter() {
		return defaultRateLimiter;
	}

	public boolean isDenyEmptyKey() {
		return denyEmptyKey;
	}

	public void setDenyEmptyKey(boolean denyEmptyKey) {
		this.denyEmptyKey = denyEmptyKey;
	}

	public String getEmptyKeyStatusCode() {
		return emptyKeyStatusCode;
	}

	public void setEmptyKeyStatusCode(String emptyKeyStatusCode) {
		this.emptyKeyStatusCode = emptyKeyStatusCode;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GatewayFilter apply(Config config) {
		KeyResolver resolver = getOrDefault(config.keyResolver, defaultKeyResolver);
		RateLimiter<Object> limiter = getOrDefault(config.rateLimiter,
				defaultRateLimiter);
		boolean denyEmpty = getOrDefault(config.denyEmptyKey, this.denyEmptyKey);
		HttpStatusHolder emptyKeyStatus = HttpStatusHolder
				.parse(getOrDefault(config.emptyKeyStatus, this.emptyKeyStatusCode));

		return (exchange, chain) -> {
			Route route = exchange
					.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);

			return resolver.resolve(exchange).defaultIfEmpty(EMPTY_KEY).flatMap(key -> {
				if (EMPTY_KEY.equals(key)) {
					if (denyEmpty) {
						setResponseStatus(exchange, emptyKeyStatus);
						return exchange.getResponse().setComplete();
					}
					return chain.filter(exchange);
				}
				return limiter.isAllowed(route.getId(), key).flatMap(response -> {

					for (Map.Entry<String, String> header : response.getHeaders()
							.entrySet()) {
						exchange.getResponse().getHeaders().add(header.getKey(),
								header.getValue());
					}

					if (response.isAllowed()) {
						return chain.filter(exchange);
					}
					log.info("request: {} qps limit reache", key);
					ServerHttpResponse rs = exchange.getResponse();
					rs.setStatusCode(config.getStatusCode());
                    rs.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
					byte[] datas = JSON.toJSONString(JsonErrorObject.builder().logId(LogIdUtils.traceId())
			                .errorCode(ErrorCodeEnum.QPS_OVER_QUOTA.getErrorCode())
			                .errorMsg(ErrorCodeEnum.QPS_OVER_QUOTA.getErrorMsg()).build())
							.getBytes(StandardCharsets.UTF_8);
					DataBuffer buffer = rs.bufferFactory().wrap(datas);
					return rs.writeWith(Mono.just(buffer));
				});
			});
		};
	}

	private <T> T getOrDefault(T configValue, T defaultValue) {
		return (configValue != null) ? configValue : defaultValue;
	}

	public static class Config {

		private KeyResolver keyResolver;

		private RateLimiter rateLimiter;

		private HttpStatus statusCode = HttpStatus.TOO_MANY_REQUESTS;

		private Boolean denyEmptyKey;

		private String emptyKeyStatus;

		public KeyResolver getKeyResolver() {
			return keyResolver;
		}

		public Config setKeyResolver(KeyResolver keyResolver) {
			this.keyResolver = keyResolver;
			return this;
		}

		public RateLimiter getRateLimiter() {
			return rateLimiter;
		}

		public Config setRateLimiter(RateLimiter rateLimiter) {
			this.rateLimiter = rateLimiter;
			return this;
		}

		public HttpStatus getStatusCode() {
			return statusCode;
		}

		public Config setStatusCode(HttpStatus statusCode) {
			this.statusCode = statusCode;
			return this;
		}

		public Boolean getDenyEmptyKey() {
			return denyEmptyKey;
		}

		public Config setDenyEmptyKey(Boolean denyEmptyKey) {
			this.denyEmptyKey = denyEmptyKey;
			return this;
		}

		public String getEmptyKeyStatus() {
			return emptyKeyStatus;
		}

		public Config setEmptyKeyStatus(String emptyKeyStatus) {
			this.emptyKeyStatus = emptyKeyStatus;
			return this;
		}

	}

}
