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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * User: Dario Wunsch Date: 20.06.2010 Time: 6:38:03
 */
@SuppressWarnings("serial")
public class CompressTag extends BodyTagSupport {

    public static String COMPREST_TAG_CONTENT = CompressTag.class.getName() + "Content";
    public static String COMPREST_TAG_JS = CompressTag.class.getName() + "js";
    public static String COMPREST_TAG_CSS = CompressTag.class.getName() + "css";

    private String method = null;
    private String id = null;
    private String options = null;
    private String basepath = null;
    
    private static final String NOT_PROCESS_PARAMETER = "granule";

    public int doAfterBody() throws JspTagException {
        HttpServletRequest httpRequest = (HttpServletRequest) pageContext.getRequest();
        BodyContent bc = getBodyContent();
        String oldBody = bc.getString();
        bc.clearBody();
        if (httpRequest.getParameter(NOT_PROCESS_PARAMETER) != null) {
            boolean b = CompressorSettings.getBoolean(httpRequest.getParameter(NOT_PROCESS_PARAMETER), false);
            if (!b)
                httpRequest.getSession().setAttribute(NOT_PROCESS_PARAMETER, true);
            else httpRequest.getSession().removeAttribute(NOT_PROCESS_PARAMETER);
        }
        if (httpRequest.getSession().getAttribute(NOT_PROCESS_PARAMETER)!=null) {
            try {
                getPreviousOut().print(oldBody);
            } catch (IOException e) {
                throw new JspTagException(e);
            }
            return SKIP_BODY;
        }
        try {
            CompressTagHandler compressor = new CompressTagHandler(id, method, options, basepath);
            RealRequestProxy runtimeRequest = new RealRequestProxy(httpRequest);
			String newBody = compressor.handleTag(runtimeRequest, runtimeRequest, oldBody);
            getPreviousOut().print(newBody);
        } catch (Exception e) {
            throw new JspTagException(e);
        }
        return SKIP_BODY;
    }

    public static void addContent(IRequestProxy request, String id, String content) {
        getContent(request, id).append(content);
    }

    public static void addJSContent(IRequestProxy request, String content) {
        addContent(request, COMPREST_TAG_JS, content);
    }

    public static void addCSSContent(IRequestProxy request, String content) {
        addContent(request, COMPREST_TAG_CSS, content);
    }

    public static StringBuilder getJSContent(IRequestProxy request) {
        return getContent(request, COMPREST_TAG_JS);
    }

    public static StringBuilder getCSSContent(IRequestProxy request) {
        return getContent(request, COMPREST_TAG_CSS);
    }

    @SuppressWarnings("unchecked")
    public static StringBuilder getContent(IRequestProxy request, String id) {
        Map<String, StringBuilder> list = (Map<String, StringBuilder>) request.getAttribute(COMPREST_TAG_CONTENT);
        StringBuilder sb;
        if (list == null) {
            list = new HashMap<String, StringBuilder>();
            sb = new StringBuilder();
            list.put(id, sb);
            request.setAttribute(COMPREST_TAG_CONTENT, list);
        } else {
            sb = list.get(id);
            if (sb == null) {
                sb = new StringBuilder();
                list.put(id, sb);
            }
        }
        return sb;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setBasepath(String basepath) {
        this.basepath = basepath;
    }
}
