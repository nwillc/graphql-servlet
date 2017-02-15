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


import com.github.nwillc.undertow.util.JsonMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.util.stream.Collectors.joining;

public class GraphQLServlet extends HttpServlet implements JsonMapper {
    static final String GRAPHQL_PATH = "graphql";
    private static final String QUERY = "query";
    private static final String ERRORS = "errors";
    private static final String DATA = "data";
    private static final String VARIABLES = "variables";

    @SuppressWarnings("unchecked")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String body = req.getReader().lines().collect(joining(System.lineSeparator()));
        Map<String, Object> payload;
        try {
            payload = getMapper().readValue(body, Map.class);
        } catch (IOException e) {
            resp.sendError(HTTP_BAD_REQUEST, "Could not parse request body as GraphQL map.");
            return;
        }
        Map<String, Object> variables = (Map<String, Object>) payload.get(VARIABLES);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put(DATA, payload);

        resp.setContentType("application/json");
        try (PrintWriter writer = resp.getWriter()) {
            getMapper().writeValue(writer, result);
        }
    }

}
