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

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

public class GatewaySecurityIntegrationTest extends AbstractIntegrationTest {

    private static final String EXPECTED_RESPONSE = "expectedResponse";

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(65433);

    @Inject
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void before() {
        mockMvc = webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    public void permitAll() throws Exception {
        stubFor(get(urlEqualTo("/permit-all-module")).willReturn(
                aResponse()
                        .withBody(EXPECTED_RESPONSE)
                        .withStatus(SC_OK)));

        mockMvc.perform(MockMvcRequestBuilders.get("/permit-all-module")).andExpect(status().isOk()).andExpect(content().string(EXPECTED_RESPONSE));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    public void hasAnyRole_userHasRequiredRole() throws Exception {
        stubFor(get(urlEqualTo("/has-any-role-module")).willReturn(
                aResponse()
                        .withBody(EXPECTED_RESPONSE)
                        .withStatus(SC_OK)));

        mockMvc.perform(MockMvcRequestBuilders.get("/has-any-role-module")).andExpect(status().isOk()).andExpect(content().string(EXPECTED_RESPONSE));
    }

    @Test
    @WithMockUser(roles = { "USER" })
    public void hasAnyRole_userDoesNotHaveRequiredRole() throws Exception {
        stubFor(get(urlEqualTo("/has-any-role-module")).willReturn(
                aResponse()
                        .withBody(EXPECTED_RESPONSE)
                        .withStatus(SC_OK)));


        mockMvc.perform(MockMvcRequestBuilders.get("/has-any-role-module")).andExpect(status().isForbidden());
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
        mockMvc.perform(MockMvcRequestBuilders.get("/permitAll.html")).andExpect(status().isOk());
    }

    @Test
    public void permitAllPaths_notPermitted() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/notPermittedToAll.html")).andExpect(status().isFound()).andExpect(header().string("Location", endsWith("/login")));
    }

}
