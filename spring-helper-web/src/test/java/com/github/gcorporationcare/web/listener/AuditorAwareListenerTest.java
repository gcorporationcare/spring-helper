package com.github.gcorporationcare.web.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;

class AuditorAwareListenerTest {

	class AccessRight implements GrantedAuthority {
		private static final long serialVersionUID = 1L;

		@Override
		public String getAuthority() {
			return "test";
		}
	}

	@AllArgsConstructor
	class User implements UserDetails {
		private static final long serialVersionUID = 1L;
		String name;
		List<AccessRight> rights;

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return rights;
		}

		@Override
		public String getPassword() {
			return name;
		}

		@Override
		public String getUsername() {
			return name;
		}

		@Override
		public boolean isAccountNonExpired() {
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return true;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}
	}

	@Test
	void testGetAuthenticatedUser() {
		assertNull(SecuredUserAuditor.getAuthenticatedUser());
		final String username = "a given user";
		User user = new User(username, Arrays.asList(new AccessRight[] { new AccessRight() }));
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(
				new UsernamePasswordAuthenticationToken(user, user.getUsername(), user.getAuthorities()));
		SecurityContextHolder.setContext(context);
		assertEquals(username, ((UserDetails) SecuredUserAuditor.getAuthenticatedUser()).getUsername());
		context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getUsername()));
		assertNull(SecuredUserAuditor.getAuthenticatedUser());
		context.setAuthentication(
				new UsernamePasswordAuthenticationToken(username, user.getUsername(), user.getAuthorities()));
		assertNull(SecuredUserAuditor.getAuthenticatedUser());
	}

	@Test
	void testGetUsernameOfAuthenticatedUser() {
		assertNotNull(SecuredUserAuditor.getUsernameOfAuthenticatedUser());
		final String username = "another user";
		User user = new User(username, Arrays.asList(new AccessRight[] { new AccessRight() }));
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(
				new UsernamePasswordAuthenticationToken(user, user.getUsername(), user.getAuthorities()));
		SecurityContextHolder.setContext(context);
		assertEquals(username, SecuredUserAuditor.getUsernameOfAuthenticatedUser());
	}
}
