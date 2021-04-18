package com.github.gcorporationcare.data.listener;

public class AnonymousCurrentUserAuditor implements CurrentUserAuditor {
	@Override
	public String username() {
		return "N/A";
	}
}
