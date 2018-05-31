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
        private Map<String,Module> modules;
        private String title;
        private String copyright;
        private String logo;
        private String welcomeText;


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
}
