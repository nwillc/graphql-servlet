/*
 * Copyright (c) 2017, nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 */

package com.github.nwillc.undertow;

import com.github.nwillc.undertow.CliOptions.CLI;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.pmw.tinylog.Logger;

import javax.servlet.ServletException;
import java.io.IOException;

import static io.undertow.servlet.Servlets.*;

public class ServletServer {

    private final static String APP = "/graphql";

    public static void main(final String[] args) throws ServletException, IOException {
        final OptionParser parser = CliOptions.getOptions();

        final OptionSet options = parser.parse(args);

        if (options.has(CLI.help.name())) {
            parser.printHelpOn(System.out);
            System.exit(0);
        }

        Integer port = (Integer) options.valueOf(CLI.port.name());
        Logger.info("Configuring port: " + port);

        String address = (String) options.valueOf(CLI.address.name());
        Logger.info("Configuring address: " + address);

        final DeploymentInfo servletBuilder = deployment()
                .setClassLoader(ServletServer.class.getClassLoader())
                .setContextPath(APP)
                .setDeploymentName(APP)
                .addServlets(
                        servlet(GraphQLServlet.class.getSimpleName(), GraphQLServlet.class)
                                .addMapping("/*"));
        final DeploymentManager manager = defaultContainer().addDeployment(servletBuilder);
        manager.deploy();

        final HttpHandler servletHandler = manager.start();
        final PathHandler path = Handlers.path(Handlers.redirect(APP))
                .addPrefixPath(APP, servletHandler);
        final Undertow server = Undertow.builder()
                .addHttpListener(port, address)
                .setHandler(path)
                .build();
        server.start();
    }
}
