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

import java.io.File;
import java.util.HashMap;

/**
 * User: Dario Wünsch
 * Date: 20.07.2010
 * Time: 2:59:14
 */
public class SimpleRequestProxy implements IRequestProxy {
    private HashMap<String, Object> attributes = new HashMap<String, Object>();

    private String basePath = "";
    private String servletPath = "";

    public SimpleRequestProxy(String basePath) {
        this(basePath, null);
    }

    public SimpleRequestProxy(String basePath, String servletPath) {
        if (basePath.length() > 0 && basePath.charAt(basePath.length() - 1) != '/' && basePath.charAt(basePath.length() - 1) != '\\')
            basePath = basePath + File.separator;
        this.basePath = basePath;
        if (servletPath != null)
            this.servletPath = servletPath;
    }

    public String getRealPath(String path) {
        return basePath + path;
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void setAttribute(String name, Object o) {
        attributes.put(name, o);
    }

    public String getServletPath() {
        return servletPath;
    }

    public String getContextPath() {
        return "/test";
    }
}
