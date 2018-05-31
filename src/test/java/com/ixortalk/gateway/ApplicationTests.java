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
package com.ixortalk.gateway;

import javax.inject.Inject;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.OK;

public class ApplicationTests extends AbstractIntegrationTest {

	@Value("${security.oauth2.client.userAuthorizationUri}")
	private String authorizeUri;

	@Inject
	private TestRestTemplate template;

	@Test
	public void homePageLoads() {
		ResponseEntity<String> response = template.getForEntity("http://localhost:" + port + "/", String.class);

		assertThat(response.getStatusCode()).describedAs(response.toString()).isEqualTo(OK);
	}

	@Test
	public void userEndpointProtected() {
		ResponseEntity<String> response = template.getForEntity("http://localhost:" + port + "/user", String.class);

		assertThat(response.getStatusCode()).isEqualTo(FOUND);
	}

	@Test
	public void resourceEndpointProtected() {
		ResponseEntity<String> response = template.getForEntity("http://localhost:" + port + "/resource", String.class);

		assertThat(response.getStatusCode()).isEqualTo(FOUND);
	}

	@Test
	public void loginRedirects() {
		ResponseEntity<String> response = template.getForEntity("http://localhost:" + port + "/login", String.class);

		assertThat(response.getStatusCode()).isEqualTo(FOUND);

		String location = response.getHeaders().getFirst("Location");
        // authorizeUri has become a true URI and not a URL (auth behind zuul proxy pattern)
        assertThat(location).describedAs("Wrong location: " + location).startsWith("http://localhost:" + port + authorizeUri);
	}

}
