package com.github.gcorporationcare.data.listener;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

import lombok.Setter;

public class AuditorAwareListener implements AuditorAware<String> {

	@Setter
	CurrentUserAuditor currentUserAuditor;

	@Override
	public Optional<String> getCurrentAuditor() {
		return Optional.of(currentUserAuditor.username());
	}
}
