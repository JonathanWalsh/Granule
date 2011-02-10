/*
 * Copyright 2010 Granule Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.granule;

import com.granule.cache.TagCacheFactory;
import com.granule.calcdeps.CalcDeps;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;

/**
 * User: Dario Wunsch
 * Date: 17.01.11
 * Time: 2:47
 */
public class GranuleHelper {
    public static CompressorSettings getSettings(HttpServletRequest request) throws IOException {
        return TagCacheFactory.getCompressorSettings(new RealRequestProxy(request).getRealPath("/"));
    }

    public static boolean isNamespaceProvided(String namespace, HttpServletRequest request) {
        HashSet<String> namespaces = CalcDeps.getNamespaces(new RealRequestProxy(request));
        return namespaces != null && namespaces.contains(namespace);
    }
}
