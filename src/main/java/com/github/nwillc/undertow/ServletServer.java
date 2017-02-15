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
        String address = (String) options.valueOf(CLI.address.name());

        final DeploymentInfo servletBuilder = deployment()
                .setClassLoader(ServletServer.class.getClassLoader())
                .setContextPath(APP)
                .setDeploymentName(ServletServer.class.getSimpleName() + ".war")
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
