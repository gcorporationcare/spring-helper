package com.gcorp.listener;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

import com.gcorp.common.Utils;

/**
 * Listener for filling creators/editors of entities
 */
public class AuditorAwareListener implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		return Optional.of(Utils.getUsernameOfAuthenticatedUser());
	}
}
