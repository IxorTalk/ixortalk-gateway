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
package com.ixortalk.gateway.web;

import java.util.List;

public class LandingPageDTO {

    private String title;
    private String logo;
    private String welcomeText;
    private String noModulesText;
    private String copyright;
    private List<Module> modules;

    public LandingPageDTO(String title, String logo, String welcomeText, String noModulesText, String copyright, List<Module> modules) {
        this.title = title;
        this.logo = logo;
        this.welcomeText = welcomeText;
        this.noModulesText = noModulesText;
        this.copyright = copyright;
        this.modules = modules;
    }

    public String getTitle() {
        return title;
    }

    public String getLogo() {
        return logo;
    }

    public String getWelcomeText() {
        return welcomeText;
    }

    public String getNoModulesText() {
        return noModulesText;
    }

    public String getCopyright() {
        return copyright;
    }

    public List<Module> getModules() {
        return modules;
    }

    public static class Module {
        private String name;
        private String url;
        private String image;
        private String description;

        public Module(String name, String url, String image, String description) {
            this.name = name;
            this.url = url;
            this.image = image;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public String getImage() {
            return image;
        }

        public String getDescription() {
            return description;
        }
    }
}
