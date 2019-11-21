package com.ixortalk.gateway.web;

import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import javax.inject.Inject;

@Controller
public class TokenController {

    @Inject
    private OAuth2ClientContext oAuth2ClientContext;

    @PostMapping("/token/refresh-access-token")
    public String refreshAccessToken() {
        oAuth2ClientContext.setAccessToken(null);
        return "redirect:/login";
    }
}
