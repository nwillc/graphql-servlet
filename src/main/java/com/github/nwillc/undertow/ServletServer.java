package com.github.nwillc.undertow;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;

import javax.servlet.ServletException;

import static io.undertow.servlet.Servlets.*;

public class ServletServer {

    private final static String APP = "/graphql";

    public static void main(final String[] args) throws ServletException {
        DeploymentInfo servletBuilder = deployment()
                .setClassLoader(ServletServer.class.getClassLoader())
                .setContextPath(APP)
                .setDeploymentName(ServletServer.class.getSimpleName() + ".war")
                .addServlets(
                        servlet(GraphQLServlet.class.getSimpleName(), GraphQLServlet.class)
                                .addMapping("/*"));
        DeploymentManager manager = defaultContainer().addDeployment(servletBuilder);
        manager.deploy();

        HttpHandler servletHandler = manager.start();
        PathHandler path = Handlers.path(Handlers.redirect(APP))
                .addPrefixPath(APP, servletHandler);
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(path)
                .build();
        server.start();
    }
}
