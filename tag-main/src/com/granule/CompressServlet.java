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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.granule.cache.TagCacheFactory;
import com.granule.logging.Logger;
import com.granule.logging.LoggerFactory;

/**
 * User: Dario Wünsch
 * Date: 21.06.2010
 * Time: 2:04:08
 */
public class CompressServlet extends HttpServlet {
    private static final String ID_PARAMETER = "id";

    private static final String VERSION_KEY = "version";

    private static String version = "unknown";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String id = request.getParameter(ID_PARAMETER);
        (new CompressorHandler()).handle(request, response, id);
    }

    @Override
    public void init() throws ServletException {
        try {
            TagCacheFactory.init(getServletConfig().getServletContext());
        } catch (IOException e) {
            throw new ServletException(e);
        }
        java.util.logging.Logger.getLogger("com.google.javascript.jscomp").setLevel(Level.WARNING);
        loadVersion();
        logger.info(MessageFormat.format("Granule {0} Started", version));
    }

    private void loadVersion() {
        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("/com/granule/config.properties"));
            if (props.containsKey(VERSION_KEY))
                version = props.getProperty(VERSION_KEY);
        } catch (IOException e) {
            logger.warn("Can not load config.properties", e);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(CompressServlet.class);
}

