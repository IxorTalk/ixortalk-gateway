/**
 *
 *  2016 (c) IxorTalk CVBA
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of IxorTalk CVBA
 *
 * The intellectual and technical concepts contained
 * herein are proprietary to IxorTalk CVBA
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 *
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from IxorTalk CVBA.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.
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