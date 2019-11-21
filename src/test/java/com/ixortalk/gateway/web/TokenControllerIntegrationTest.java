package com.ixortalk.gateway.web;

import com.ixortalk.gateway.AbstractIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

public class TokenControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String CSRF_TOKEN = "org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN";

    @Inject
    private WebApplicationContext context;

    @MockBean
    private OAuth2ClientContext oAuth2ClientContext;

    private MockMvc mockMvc;

    private CsrfToken csrfToken;

    @Before
    public void before() {
        mockMvc = webAppContextSetup(context).apply(springSecurity()).build();
        csrfToken = new HttpSessionCsrfTokenRepository().generateToken(new MockHttpServletRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void refreshAccessToken() throws Exception {
        mockMvc
                .perform(
                        post("/token/refresh-access-token")
                                .sessionAttr(CSRF_TOKEN, csrfToken)
                                .param(csrfToken.getParameterName(), csrfToken.getToken()))
                .andExpect(status().isFound())
                .andExpect(header().string(LOCATION, endsWith("/login")));

        verify(oAuth2ClientContext).setAccessToken(null);
    }
}