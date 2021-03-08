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

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.ixortalk.gateway.security.IxorTalkProperties;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

public class GatewayIntegrationTest extends AbstractIntegrationTest {

    private static final String EXPECTED_RESPONSE = "expectedResponse";

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(65433);

    @Inject
    private WebApplicationContext context;

    @Inject
    private IxorTalkProperties ixorTalkProperties;

    private MockMvc mockMvc;

    @Before
    public void before() {
        mockMvc = webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    public void permitAll() throws Exception {
        stubFor(WireMock.get(urlEqualTo("/permit-all-module")).willReturn(
                aResponse()
                        .withBody(EXPECTED_RESPONSE)
                        .withStatus(HttpStatus.SC_OK)));

        mockMvc.perform(get("/permit-all-module")).andExpect(status().isOk()).andExpect(content().string(EXPECTED_RESPONSE));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    public void hasAnyRole_userHasRequiredRole() throws Exception {
        stubFor(WireMock.get(urlEqualTo("/has-any-role-module")).willReturn(
                aResponse()
                        .withBody(EXPECTED_RESPONSE)
                        .withStatus(HttpStatus.SC_OK)));

        mockMvc.perform(get("/has-any-role-module")).andExpect(status().isOk()).andExpect(content().string(EXPECTED_RESPONSE));
    }

    @Test
    @WithMockUser
    public void hasAnyRole_userDoesNotHaveRequiredRole() throws Exception {
        stubFor(WireMock.get(urlEqualTo("/has-any-role-module")).willReturn(
                aResponse()
                        .withBody(EXPECTED_RESPONSE)
                        .withStatus(HttpStatus.SC_OK)));


        mockMvc.perform(get("/has-any-role-module")).andExpect(status().isForbidden());
    }

    @Test
    public void csrfDisabled() throws Exception {
        stubFor(post(urlEqualTo("/csrf-disabled-module")).willReturn(
                aResponse()
                        .withStatus(SC_CREATED)));

        mockMvc.perform(MockMvcRequestBuilders.post("/csrf-disabled-module")).andExpect(status().isCreated());
    }

    @Test
    public void permitAllPaths_permitted() throws Exception {
        mockMvc.perform(get("/permitAll.html")).andExpect(status().isOk());
    }

    @Test
    public void permitAllPaths_notPermitted() throws Exception {
        mockMvc.perform(get("/notPermittedToAll.html")).andExpect(status().isFound()).andExpect(header().string("Location", endsWith("/login")));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    public void getLandingPage_accessToModule() throws Exception {
        mockMvc.perform(get("/landing-page.html")).andExpect(status().isOk()).andExpect(content().string(containsString("/assetmgmt-ui/index.html")));
    }

    @Test
    @WithMockUser
    public void getLandingPage_noAccessToModule() throws Exception {
        mockMvc.perform(get("/landing-page.html")).andExpect(status().isOk()).andExpect(content().string(containsString(ixorTalkProperties.getGateway().getNoModulesText())));
    }

    @Test
    public void routeToIndexStripsPath() throws Exception {
        String body = "Body for index.html on the root path";
        stubFor(WireMock.get(urlEqualTo("/index.html"))
                .willReturn(
                        aResponse()
                                .withStatus(HttpServletResponse.SC_OK)
                                .withBody(body)));

        mockMvc.perform(get("/route-to-index-path/whatever/long/path")).andExpect(status().isOk()).andExpect(content().string(body));
    }

    @Test
    public void csrf_noCookies() throws Exception {
        stubFor(post(urlEqualTo("/api")).willReturn(ok(EXPECTED_RESPONSE)));

        mockMvc.perform(MockMvcRequestBuilders.post("/api")).andExpect(status().isOk()).andExpect(content().string(EXPECTED_RESPONSE));
    }

    @Test
    public void csrf_cookies() throws Exception {
        stubFor(post(urlEqualTo("/api")).willReturn(ok("not expected to receive this")));

        mockMvc.perform(MockMvcRequestBuilders.post("/api").cookie(new Cookie("JSESSIONID", "invalid"))).andExpect(status().isForbidden());
    }
}
