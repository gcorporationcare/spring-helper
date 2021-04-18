package com.github.gcorporationcare.data.listener;

public interface CurrentUserAuditor {
	/**
	 * Get the name of current user
	 * 
	 * @return the name of the user or a null value
	 */
	String username();
}
