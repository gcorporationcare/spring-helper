package com.github.gcorporationcare.web.security;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IpLockingService {
	@Setter
	private long limit;
	@Autowired
	HttpServletRequest request;
	LoadingCache<String, Integer> attemptsCache;

	public IpLockingService() {
		attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS)
				.build(new CacheLoader<String, Integer>() {
					public Integer load(String key) {
						return 0;
					}
				});
	}

	/**
	 * Tag current IP as success and clear it from cache
	 */
	public void success() {
		if (!ipBlockingEnabled()) {
			return;
		}
		attemptsCache.invalidate(getIpAddress());
	}

	/**
	 * Tag current IP as failed and register it in cache
	 */
	public void fail() {
		if (!ipBlockingEnabled()) {
			return;
		}
		final String key = getIpAddress();
		int attempts = getAttempts();
		attempts++;
		attemptsCache.put(key, attempts);
	}

	/**
	 * Check if current IP is blocked
	 * 
	 * @return true if blocked
	 */
	public boolean isIpBlocked() {
		if (!ipBlockingEnabled()) {
			return false;
		}
		return getAttempts() >= limit;
	}

	private boolean ipBlockingEnabled() {
		return limit > 0;
	}

	private String getIpAddress() {
		return request.getRemoteAddr();
	}

	private int getAttempts() {
		try {
			return attemptsCache.get(getIpAddress());
		} catch (ExecutionException e) {
			log.error("Failing at execution because of {}", e);
			return 0;
		}
	}
}
