/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-present IxorTalk CVBA
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.ixortalk.gateway.security;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Thibaud Leprêtre
 *
 * This filter is required to run the authserver behind our gateway
 * See:
 * 		- https://github.com/spring-cloud/spring-cloud-security/issues/94
 * 		- https://github.com/spring-projects/spring-security-oauth/issues/671
 */
class DynamicOauth2ClientContextFilter extends OAuth2ClientContextFilter {
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	protected void redirectUser(UserRedirectRequiredException e, HttpServletRequest request,
								HttpServletResponse response) throws IOException {
		String redirectUri = e.getRedirectUri();
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(redirectUri);
		Map<String, String> requestParams = e.getRequestParams();
		for (Map.Entry<String, String> param : requestParams.entrySet()) {
			builder.queryParam(param.getKey(), param.getValue());
		}

		if (e.getStateKey() != null) {
			builder.queryParam("state", e.getStateKey());
		}

		this.redirectStrategy.sendRedirect(request, response, builder.build().encode().toUriString());
	}

	@Override
	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}
}