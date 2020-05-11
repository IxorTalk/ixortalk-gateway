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

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;

import static com.ixortalk.test.util.Randomizer.nextString;
import static com.jayway.restassured.RestAssured.given;
import static javax.servlet.http.HttpServletResponse.SC_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.FOUND;

public class ApplicationTests extends AbstractIntegrationTest {

    public static final String AUTH_FORWARD_PARAM_VALUE = nextString("authForwardParam");
    public static final String OTHER_AUTH_FORWARD_PARAM_VALUE = nextString("otherAuthForwardParam");
    public static final String NOT_TO_FORWARD = nextString("notToForward");

    public static final String CONFIGURED_AUTH_FORWARD_NAMESPACE = "test_ixortalk_com";
    public static final String CONFIGURED_AUTH_FORWARD_PARAM = "authForwardParam";
    public static final String CONFIGURED_OTHER_AUTH_FORWARD_PARAM = "otherAuthForwardParam";

    public static final String OTHER_PARAM = "otherParam";

    @Value("${security.oauth2.client.userAuthorizationUri}")
    private String authorizeUri;

    @Inject
    private TestRestTemplate template;

    @Test
    public void homePageLoads() {
        ResponseEntity<String> response = template.getForEntity("http://localhost:" + port + "/", String.class);

        assertThat(response.getStatusCode()).isEqualTo(FOUND);
        assertThat(response.getHeaders().getFirst(LOCATION)).isEqualTo("http://localhost:" + port + "/landing-page.html");
    }

    @Test
    public void explicitIndexHtmlRequest() {
        ResponseEntity<String> response = template.getForEntity("http://localhost:" + port + "/index.html", String.class);

        assertThat(response.getStatusCode()).isEqualTo(FOUND);
        assertThat(response.getHeaders().getFirst(LOCATION)).isEqualTo("http://localhost:" + port + "/landing-page.html");
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

        assertThat(response.getHeaders().getFirst(LOCATION))
                .describedAs("Wrong location: " + response.getHeaders().getFirst(LOCATION))
                .startsWith("http://localhost:" + port + authorizeUri);
    }

    @Test
    public void additionalAuthorizationParams() throws URISyntaxException {

        String login =
                given()
                        .filter(this.sessionFilter)
                        .when()
                        .param(CONFIGURED_AUTH_FORWARD_PARAM, AUTH_FORWARD_PARAM_VALUE)
                        .param(CONFIGURED_OTHER_AUTH_FORWARD_PARAM, OTHER_AUTH_FORWARD_PARAM_VALUE)
                        .param(OTHER_PARAM, NOT_TO_FORWARD)
                        .get("/secured.html")
                        .then()
                        .statusCode(SC_FOUND)
                        .header(LOCATION, "http://localhost:" + port + "/login")
                        .extract().header(LOCATION);

        String authorizationUri =
                given()
                        .filter(this.sessionFilter)
                        .when()
                        .get(login)
                        .then()
                        .statusCode(SC_FOUND)
                        .extract().header(LOCATION);

        assertThat(new URI(authorizationUri))
                .hasParameter(CONFIGURED_AUTH_FORWARD_NAMESPACE + "_" + CONFIGURED_AUTH_FORWARD_PARAM, AUTH_FORWARD_PARAM_VALUE)
                .hasParameter(CONFIGURED_AUTH_FORWARD_NAMESPACE + "_" + CONFIGURED_OTHER_AUTH_FORWARD_PARAM, OTHER_AUTH_FORWARD_PARAM_VALUE)
                .hasNoParameter(OTHER_PARAM);
    }

    @Test
    public void additionalAuthorizationParams_NotPresent() throws URISyntaxException {

        String login =
                given()
                        .filter(this.sessionFilter)
                        .when()
                        .param(CONFIGURED_OTHER_AUTH_FORWARD_PARAM, OTHER_AUTH_FORWARD_PARAM_VALUE)
                        .param(OTHER_PARAM, NOT_TO_FORWARD)
                        .get("/secured.html")
                        .then()
                        .statusCode(SC_FOUND)
                        .header(LOCATION, "http://localhost:" + port + "/login")
                        .extract().header(LOCATION);

        String authorizationUri =
                given()
                        .filter(this.sessionFilter)
                        .when()
                        .get(login)
                        .then()
                        .statusCode(SC_FOUND)
                        .extract().header(LOCATION);

        assertThat(new URI(authorizationUri))
                .hasNoParameter(CONFIGURED_AUTH_FORWARD_NAMESPACE + "_" + CONFIGURED_AUTH_FORWARD_PARAM)
                .hasParameter(CONFIGURED_AUTH_FORWARD_NAMESPACE + "_" + CONFIGURED_OTHER_AUTH_FORWARD_PARAM, OTHER_AUTH_FORWARD_PARAM_VALUE)
                .hasNoParameter(OTHER_PARAM);
    }
}
