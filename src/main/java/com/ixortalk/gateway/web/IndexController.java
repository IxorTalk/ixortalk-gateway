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

import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.ixortalk.gateway.security.IxorTalkProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Controller
public class IndexController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @Inject
    private IxorTalkProperties ixorTalkProperties;

    @RequestMapping({"/index.html","/"})
    public String index(Model model, HttpServletRequest request) {
        model.addAttribute(
                "indexDTO",
                new IndexDTO(
                        ixorTalkProperties.getGateway().getTitle(),
                        ixorTalkProperties.getGateway().getLogo(),
                        ixorTalkProperties.getGateway().getWelcomeText(),
                        ixorTalkProperties.getGateway().getNoModulesText(),
                        ixorTalkProperties.getGateway().getCopyright(),
                        ixorTalkProperties.getGateway().getModules()
                                .entrySet()
                                .stream()
                                .filter(moduleEntry -> showForRole(moduleEntry.getKey(), request))
                                .map(Map.Entry::getValue)
                                .map(module -> new IndexDTO.Module(
                                        module.getName(),
                                        module.getUrl(),
                                        module.getImage(),
                                        module.getDescription()))
                                .collect(toList())));
        return "index";
    }

    private boolean showForRole(String module, HttpServletRequest request) {
        IxorTalkProperties.Roles roles = ixorTalkProperties.getRoleMatchers().get(module);
        if (roles == null) {
            LOGGER.error("No role-matcher configured for landing page module '" + module + "'");
            throw new AccessDeniedException("Could not construct landing page");
        }
        return stream(roles.hasAnyRoleNames()).anyMatch(role -> request.isUserInRole(role));
    }

}