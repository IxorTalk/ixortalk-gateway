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