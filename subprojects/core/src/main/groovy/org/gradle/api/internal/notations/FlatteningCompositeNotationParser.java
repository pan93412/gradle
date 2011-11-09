/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.notations;

import org.gradle.api.GradleException;
import org.gradle.util.GUtil;

import java.util.Collection;
import java.util.LinkedList;

/**
 * To drive consistency in our approach to notations.
 * Deals with:
 * 1. short-circuiting if notation is instance of the resulting type
 * 2. flattening collections and arrays
 * See also NotationParserBuilder
 *
 * by Szczepan Faber, created at: 11/8/11
 */
public class FlatteningCompositeNotationParser<T> {
    //TODO SF - after completing the DependencyHandler make this class a proper notation parser and separate composite from flattener

    private final Collection<NotationParser<T>> delegates;

    public FlatteningCompositeNotationParser(Collection<NotationParser<T>> delegates) {
        this.delegates = delegates;
    }

    public Collection<T> parseNotation(Object notation) {
        Collection<T> out = new LinkedList<T>();
        Collection notations = GUtil.collectionize(notation);
        for (Object n : notations) {
            out.add(parseSingleNotation(n));
        }
        return out;
    }

    public T parseSingleNotation(Object notation) {
        for (NotationParser<T> delegate : delegates) {
            if (delegate.canParse(notation)) {
                return delegate.parseNotation(notation);
            }
        }

        throw new RuntimeException("Don't know how to parse: " + notation);
    }

    public static class InvalidNotationFormat extends GradleException {
        public InvalidNotationFormat(String message) {
            super(message);
        }

        public InvalidNotationFormat(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class InvalidNotationType extends GradleException {
        public InvalidNotationType(String message) {
            super(message);
        }
    }
}
