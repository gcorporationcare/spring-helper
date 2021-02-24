package com.gcorp.common;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.base.Strings;

/**
 * Assign a request identifier to an HTTP request. Later the request id will be
 * used for logging purpose
 * 
 */
public class RequestIdGenerator extends HandlerInterceptorAdapter {

	protected String requestIdHeaderName() {
		return "Request-Id";
	}

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) {
		String uid = UUID.randomUUID().toString();
		MDC.put("RequestId", uid);
		String header = httpServletResponse.getHeader(requestIdHeaderName());
		if (Strings.isNullOrEmpty(header)) {
			httpServletResponse.setHeader(requestIdHeaderName(), uid);
		}
		return true;
	}
}
