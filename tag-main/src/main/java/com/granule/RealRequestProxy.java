/*
 * Copyright 2010 Granule Inc.
 * Copyright 2013 Blue Lotus Software, LLC.
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

import com.granule.logging.Logger;
import com.granule.logging.LoggerFactory;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Dario Wunsch
 * @author John Yeary <jyeary@bluelotussoftware.com>
 * @version 1.1
 */
public class RealRequestProxy implements IRequestProxy {

    private final HttpServletRequest request;
    private static final Logger logger = LoggerFactory.getLogger(RealRequestProxy.class);

    public RealRequestProxy(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public String getRealPath(String path) {
        if (path != null && !path.startsWith("/")) {
            path = "/" + path;
        }
        File f = null;
        try {
            f = new File(request.getServletContext().getResource(path).toURI());
        } catch (MalformedURLException ex) {
            logger.error("The URL for the path is malformed.", ex);
        } catch (URISyntaxException ex) {
            logger.error("The URI has a syntax issue.", ex);
        }
        return f != null ? f.getAbsolutePath() : null;
    }

    @Override
    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object o) {
        request.setAttribute(name, o);
    }

    @Override
    public String getServletPath() {
        return request.getServletPath();
    }

    @Override
    public String getBasePath() {
        return request.getContextPath();
    }

    @Override
    public String getContextPath() {
        return request.getContextPath();
    }
}
