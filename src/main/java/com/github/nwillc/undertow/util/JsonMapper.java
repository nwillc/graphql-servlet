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
 */

package com.github.nwillc.undertow.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.io.IOException;

public interface JsonMapper {
    ThreadLocal<ObjectMapper> mapper = ThreadLocal.withInitial(() ->
            new ObjectMapper().registerModule(new Jdk8Module())
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));

    default ObjectMapper getMapper() {
        return mapper.get();
    }

    default String toJson(Object obj) {
        try {
            return mapper.get().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON generation", e);
        }
    }

    default <T> T fromJson(String json, Class<T> tClass) {
        try {
            return mapper.get().readValue(json, tClass);
        } catch (IOException e) {
            throw new RuntimeException("JSON parsing", e);
        }
    }
}
