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
