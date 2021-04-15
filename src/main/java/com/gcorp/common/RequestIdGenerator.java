package com.gcorp.common;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import com.google.common.base.Strings;

import lombok.Getter;

/**
 * Assign a request identifier to an HTTP request. Later the request id will be
 * used for logging purpose
 * 
 */
public class RequestIdGenerator implements HandlerInterceptor {

	@Getter
	private final String headerName;

	public RequestIdGenerator(String header) {
		super();
		this.headerName = header;
	}

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) {
		String uid = UUID.randomUUID().toString();
		MDC.put("RequestId", uid);
		String header = httpServletResponse.getHeader(headerName);
		if (Strings.isNullOrEmpty(header)) {
			httpServletResponse.setHeader(headerName, uid);
		}
		return true;
	}
}
