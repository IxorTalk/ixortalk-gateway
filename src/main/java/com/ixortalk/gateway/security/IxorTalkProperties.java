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

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

@ConfigurationProperties("ixortalk")
public class IxorTalkProperties {

    private Set<String> permitAllPaths = newHashSet();

    private Map<String, Roles> roleMatchers = newHashMap();

    private Gateway gateway;

    private Logout logout = new Logout();

    public Logout getLogout() {
        return logout;
    }

    public Set<String> getPermitAllPaths() {
        return permitAllPaths;
    }

    public void setPermitAllPaths(Set<String> permitAllPaths) {
        this.permitAllPaths = permitAllPaths;
    }

    public Map<String, Roles> getRoleMatchers() {
        return roleMatchers;
    }

    public Gateway getGateway() {
        return gateway;
    }

    public IxorTalkProperties setGateway(Gateway gateway) {
        this.gateway = gateway;
        return this;
    }

    public static class Roles {
        private boolean csrfEnabled = true;
        private boolean permitAll = false;
        private Set<String> hasAnyRole = newHashSet();

        public boolean isCsrfEnabled() {
            return csrfEnabled;
        }

        public void setCsrfEnabled(boolean csrfEnabled) {
            this.csrfEnabled = csrfEnabled;
        }

        public boolean isPermitAll() {
            return permitAll;
        }

        public void setPermitAll(boolean permitAll) {
            this.permitAll = permitAll;
        }

        public Set<String> getHasAnyRole() {
            return hasAnyRole;
        }

        public void setHasAnyRole(Set<String> hasAnyRole) {
            this.hasAnyRole = hasAnyRole;
        }

        public String[] hasAnyRoleNames() {
            return hasAnyRole.stream().toArray(String[]::new);
        }
    }

    public static class Gateway {
        private String redirectIndexTo = "/landing-page.html";
        private Map<String,Module> modules;
        private String title;
        private String copyright;
        private String logo;
        private String welcomeText = "Welcome to the IxorTalk platform";
        private String noModulesText = "<h2><i>You don't seem to have access to any IxorTalk module</i></h2>";

        public String getRedirectIndexTo() {
            return redirectIndexTo;
        }

        public void setRedirectIndexTo(String redirectIndexTo) {
            this.redirectIndexTo = redirectIndexTo;
        }

        public Map<String, Module> getModules() {
            return modules;
        }

        public Gateway setModules(Map<String, Module> modules) {
            this.modules = modules;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public Gateway setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getCopyright() {
            return copyright;
        }

        public Gateway setCopyright(String copyright) {
            this.copyright = copyright;
            return this;
        }

        public String getLogo() {
            return logo;
        }

        public Gateway setLogo(String logo) {
            this.logo = logo;
            return this;
        }

        public String getWelcomeText() {
            return welcomeText;
        }

        public Gateway setWelcomeText(String welcomeText) {
            this.welcomeText = welcomeText;
            return this;
        }

        public String getNoModulesText() {
            return noModulesText;
        }

        public void setNoModulesText(String noModulesText) {
            this.noModulesText = noModulesText;
        }
    }

    public static class Module {
        private String name;
        private String url;
        private String image;
        private String description;

        public String getName() {
            return name;
        }

        public Module setName(String name) {
            this.name = name;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public Module setUrl(String url) {
            this.url = url;
            return this;
        }

        public String getImage() {
            return image;
        }

        public Module setImage(String image) {
            this.image = image;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public Module setDescription(String description) {
            this.description = description;
            return this;
        }
    }

    public static class Logout {

        private String defaultRedirectUri = "/uaa/signout";
        private String redirectUriParamName;

        public void setDefaultRedirectUri(String defaultRedirectUri) {
            this.defaultRedirectUri = defaultRedirectUri;
        }

        public String getDefaultRedirectUri() {
            return defaultRedirectUri;
        }

        public String getRedirectUriParamName() {
            return redirectUriParamName;
        }

        public void setRedirectUriParamName(String redirectUriParamName) {
            this.redirectUriParamName = redirectUriParamName;
        }
    }
}
