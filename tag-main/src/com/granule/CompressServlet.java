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
import com.granule.logging.Logger;
import com.granule.logging.LoggerFactory;
import com.granule.utils.PathUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * User: Dario Wunsch
 * Date: 21.06.2010
 * Time: 2:04:08
 */
public class CompressServlet extends HttpServlet {

    private static final long serialVersionUID = -2526640346318371192L;

    private static final String CLOSURE_COMPILER_PACKAGE_NAME = "com.google.javascript.jscomp";

    private static final String ID_PARAMETER = "id";

    private static final String VERSION_KEY = "version";

    private static String version = "unknown";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        process(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws IOException,
            ServletException {
        String id = request.getParameter(ID_PARAMETER);
        (new CompressorHandler()).handle(request, response, id);
    }

    @Override
    public void init() throws ServletException {
        java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger(CLOSURE_COMPILER_PACKAGE_NAME);
        Filter logFilter = new CompilerLogFilter();
        rootLogger.setFilter(logFilter);
        rootLogger = java.util.logging.Logger.getLogger(CLOSURE_COMPILER_PACKAGE_NAME + ".PhaseOptimizer");
        rootLogger.setFilter(logFilter);

        try {
            TagCacheFactory.init(getServletConfig().getServletContext());
            loadVersion();
        } catch (IOException e) {
            throw new ServletException(e);
        }

    
        logger.info(MessageFormat.format("Granule {0} Started", version));
    }
    
	private void loadVersion() {
		Properties props = new Properties();
		try {
			InputStream resourceAsStream = PathUtils.getResourceAsStream(this
					.getClass(), "/com/granule/config.properties");
			try {
				if (resourceAsStream == null)
					logger
							.warn("Can not find /com/granule/config.properties resource");
				else
					props.load(resourceAsStream);
			} finally {
				if (resourceAsStream != null)
					resourceAsStream.close();
			}
			if (props.containsKey(VERSION_KEY))
				version = props.getProperty(VERSION_KEY);
		} catch (IOException e) {
			logger.warn("Can not load config.properties", e);
		}
	}

    public class CompilerLogFilter implements Filter {
        public boolean isLoggable(LogRecord lr) {
            return !(lr.getSourceClassName().startsWith(CLOSURE_COMPILER_PACKAGE_NAME) && lr.getLevel().intValue() <
                    Level.WARNING.intValue());
        }
    }


    private static final Logger logger = LoggerFactory.getLogger(CompressServlet.class);
}

