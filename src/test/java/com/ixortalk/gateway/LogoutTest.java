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
package com.ixortalk.gateway;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {
		"ixortalk.logout.redirect-uri-param-name: redirect_uri",
		"ixortalk.logout.default-redirect-uri: /uaa/signout"
})
public class LogoutTest extends AbstractIntegrationTest {

	@Value("${security.oauth2.client.userAuthorizationUri}")
	private String authorizeUri;

	@Inject
	private TestRestTemplate template;

	@Value("${ixortalk.logout.redirect-uri-param-name}")
	private String REDIRECT_URI_PARAM_NAME;
	@Value("${ixortalk.logout.default-redirect-uri}")
	private String DEFAULT_REDIRECT_URI;

	@Test
	public void withoutRedirect() {
		ResponseEntity<String> response = template.getForEntity("http://localhost:" + port + "/logout", String.class);

		assertThat(response.getHeaders().getLocation().getPath()).isEqualTo(DEFAULT_REDIRECT_URI);
		assertThat(response.getHeaders().getLocation().getQuery()).isNull();
	}

	@Test
	public void withRedirect() {
		String REQUEST_PARAMS = REDIRECT_URI_PARAM_NAME + "=" + "foo://bar.com";

		ResponseEntity<String> response = template.getForEntity("http://localhost:" + port + "/logout?" + REQUEST_PARAMS, String.class);

		assertThat(response.getHeaders().getLocation().getPath()).isEqualTo(DEFAULT_REDIRECT_URI);
		assertThat(response.getHeaders().getLocation().getQuery()).isEqualTo(REQUEST_PARAMS);
	}

	@Test
	public void withEmptyRedirect() {
		ResponseEntity<String> response = template.getForEntity("http://localhost:" + port + "/logout?" + REDIRECT_URI_PARAM_NAME + "=", String.class);

		assertThat(response.getHeaders().getLocation().getPath()).isEqualTo(DEFAULT_REDIRECT_URI);
		assertThat(response.getHeaders().getLocation().getQuery()).isNull();
	}
}
