package com.github.gcorporationcare.web.listener;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.github.gcorporationcare.data.listener.CurrentUserAuditor;

/**
 * Listener for filling creators/editors of entities
 */
public class SecuredUserAuditor implements CurrentUserAuditor {

	/**
	 * Get the user's name of current authenticated user
	 * 
	 * @return a String representing the name of the current authenticated user or
	 *         N/A if none
	 */
	public static String getUsernameOfAuthenticatedUser() {
		UserDetails securedUser = (UserDetails) getAuthenticatedUser();
		return (securedUser != null) ? securedUser.getUsername() : "N/A";
	}

	/**
	 * Get the principal of current authenticated user
	 * 
	 * @return the UserDetails of the current authenticated user (null if none)
	 */
	public static Object getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()
				|| !UserDetails.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
			return null;
		}
		return authentication.getPrincipal();
	}

	@Override
	public String username() {
		return SecuredUserAuditor.getUsernameOfAuthenticatedUser();
	}
}
