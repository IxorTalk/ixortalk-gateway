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
package com.ixortalk.gateway.zuul.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.eclipse.jetty.http.HttpHeader;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UrlPathHelper;

import javax.inject.Inject;

import static org.springframework.util.StringUtils.hasText;

/**
 * ZuulFilter that sets a correct X-Forwarded-Port and X-Forwarded-Prefix header
 * so that redirection from the gateway to the login page
 * will work regardless of hostnam / port.
 *
 * Only works with Jetty so make sure that Jetty is activated in
 * the pom.xml of the authserver component
 *
 *  Without this
 *
 *  - Tomcat can redirect using the proper port but uses the wrong hostname (localhost)
 *  - Jetty can redirect using the proper hostname but uses the wrong port (80)
 *
 *  More info: https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.23
 */
@Component
public class XForwardedHeadersPreFilter extends ZuulFilter
{
    private static final String XFHOST_HEADER = HttpHeader.X_FORWARDED_HOST.toString();
    private static final String XFFOR_HEADER = HttpHeader.X_FORWARDED_FOR.toString();
    private static final String XFPROTO_HEADER = HttpHeader.X_FORWARDED_PROTO.toString();
    private static final String XFPORT_HEADER = "X-Forwarded-Port";
    private static final String XFPREFIX_HEADER = "X-Forwarded-Prefix";

    private static final String[] XF_HEADERS = { XFHOST_HEADER, XFFOR_HEADER, XFPROTO_HEADER, XFPORT_HEADER, XFPREFIX_HEADER };

    private ProxyRequestHelper helper = new ProxyRequestHelper();

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Inject
    private RouteLocator routeLocator;

    @Override
    public int filterOrder()
    {
        return 4;
    }

    @Override
    public String filterType()
    {
        return "pre";
    }

    @Override
    public boolean shouldFilter()
    {
        return true;
    }

    @Override
    public Object run()
    {
        RequestContext ctx = RequestContext.getCurrentContext();
        int port = ctx.getRequest().getServerPort();
        String clientIp = ctx.getRequest().getRemoteAddr();
        String scheme = ctx.getRequest().getScheme();

        ctx.addZuulRequestHeader(XFHOST_HEADER, getHostHeaderValue(ctx, scheme, port));
        ctx.addZuulRequestHeader(XFFOR_HEADER, clientIp);
        ctx.addZuulRequestHeader(XFPROTO_HEADER, scheme);
        ctx.addZuulRequestHeader(XFPORT_HEADER, String.valueOf(port));
        addXForwardedPrefix(ctx);

        //This will make sure that the original headers are not passed down the chain
        helper.addIgnoredHeaders(XF_HEADERS);

        return null;
    }

    public void addXForwardedPrefix(RequestContext ctx) {
        final String requestURI = this.urlPathHelper.getPathWithinApplication(ctx.getRequest());
        Route route = this.routeLocator.getMatchingRoute(requestURI);

        if (hasText(route.getPrefix()) && !route.getLocation().endsWith(route.getPrefix() + "/")) {
            String existingPrefix = ctx.getRequest().getHeader(XFPREFIX_HEADER);
            StringBuilder newPrefixBuilder = new StringBuilder();
            if (StringUtils.hasLength(existingPrefix)) {
                if (existingPrefix.endsWith("/")
                        && route.getPrefix().startsWith("/")) {
                    newPrefixBuilder.append(existingPrefix, 0,
                            existingPrefix.length() - 1);
                }
                else {
                    newPrefixBuilder.append(existingPrefix);
                }
            }
            newPrefixBuilder.append(route.getPrefix());
            ctx.addZuulRequestHeader(XFPREFIX_HEADER, newPrefixBuilder.toString());
        }
    }

    private String getHostHeaderValue(RequestContext ctx,
                                      String scheme,
                                      int port)
    {
        String host = ctx.getRequest().getServerName();
        if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
            host = String.format("%s:%d", host, port);
        }
        return host;
    }
}