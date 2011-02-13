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

import javax.servlet.http.HttpServletRequest;

/**
 * User: Dario Wünsch
 * Date: 20.07.2010
 * Time: 2:35:48
 */
public class RealRequestProxy implements IRequestProxy {
    private HttpServletRequest request;

    public RealRequestProxy(HttpServletRequest request) {
        this.request = request;
    }

    public String getRealPath(String path) {
        return request.getSession().getServletContext().getRealPath(path);
    }

    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    public void setAttribute(String name, Object o) {
        request.setAttribute(name, o);
    }

    public String getServletPath() {
        return request.getServletPath();
    }

    public String getContextPath() {
        return request.getContextPath();
    }
}
