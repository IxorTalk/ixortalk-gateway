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

import org.springframework.security.web.RedirectStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class RedirectStrategyDecorator implements RedirectStrategy {
    private final String redirectUriParamName;
    private final RedirectStrategy redirectStrategy;

    public RedirectStrategyDecorator(String redirectUriParamName, RedirectStrategy redirectStrategy) {
        this.redirectUriParamName = redirectUriParamName;
        this.redirectStrategy = redirectStrategy;
    }

    @Override
    public void sendRedirect(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String url) throws IOException {
        if (!Objects.isNull(httpServletRequest.getParameter(redirectUriParamName)) && !httpServletRequest.getParameter(redirectUriParamName).isEmpty())
            redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, url + "?" + redirectUriParamName + "=" + httpServletRequest.getParameter(redirectUriParamName));
        else
            redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, url);
    }
}
