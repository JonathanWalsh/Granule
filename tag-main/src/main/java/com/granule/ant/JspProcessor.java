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
package com.granule.ant;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import com.granule.CompressTagHandler;
import com.granule.CompressorSettings;
import com.granule.IRequestProxy;
import com.granule.JSCompileException;
import com.granule.SimpleRequestProxy;
import com.granule.cache.TagCacheFactory;
import com.granule.logging.Logger;
import com.granule.logging.LoggerFactory;
import com.granule.parser.Attributes;
import com.granule.parser.Element;
import com.granule.parser.TagReader;
import com.granule.utils.PathUtils;

/**
 * User: Dario Wunsch
 * Date: 19.09.2010
 * Time: 4:11:30
 */
public class JspProcessor {

    public static final int MAX_ERROR_COUNT = 10;

    public int generateCache(List<String> files, String rootPath, String outputPath) throws IOException {
        rootPath=PathUtils.clean(rootPath);
    	java.util.logging.Logger.getLogger("com.google.javascript.jscomp").setLevel(Level.WARNING);
        java.util.logging.Logger.getLogger("com.granule").setLevel(Level.INFO);
        int errorCount = 0;
        if (rootPath.endsWith("."))
            rootPath = rootPath.substring(0, rootPath.lastIndexOf("."));
        HashMap<String, String> additions = new HashMap<String, String>();
        additions.put(CompressorSettings.CACHE_KEY, CompressorSettings.DISK_CACHE_VALUE);
        if (outputPath != null) {
            additions.put(CompressorSettings.CACHE_FILE_LOCATION_KEY, outputPath);
        }
        TagCacheFactory.getProductionCompressorSettings(rootPath, additions);
        TagCacheFactory.init(rootPath);
        for (String f : files) {
           
        	errorCount += processFile(f, rootPath);
            if (errorCount >= MAX_ERROR_COUNT)
                break;
        }
        return errorCount;
    }

    private int processFile(String filename, String webAppRootPath) {
    	filename=PathUtils.clean(filename);
    	int errorCount = 0;
        CompressorSettings settings = null;
        try {
            settings = TagCacheFactory.getCompressorSettings(webAppRootPath);
        } catch (IOException e) {
            logger.error("Could not load settings", e);
            errorCount++;
            return errorCount;
        }
        String servletName = settings.getContextRoot();
        if (servletName!=null&&!servletName.startsWith("/"))
            servletName = "/" + servletName;
        IRequestProxy request = new SimpleRequestProxy(webAppRootPath, servletName);
     
        System.out.println("Processing file "+filename);
        
        String folderPath = PathUtils.getFolderPath(filename);
		String relpath = PathUtils.getRelpath(folderPath,webAppRootPath);
		errorCount = processFile(filename, webAppRootPath, request, settings, relpath);
        
        return errorCount;
    }

    private int processFile(String filename, String webAppRootPath, IRequestProxy request, CompressorSettings settings,
                            String pathAddition) {
    	//System.out.println("pathAddition "+pathAddition);
    	int errorCount = 0;
        TagReader source = null;
        try {
            source = new TagReader(new FileReader(filename));
        } catch (IOException e) {
            logger.error("Could not open file", e);
            errorCount++;
            return errorCount;
        }
        
        List<Element> els = source.getAllElements();
        for (Element el : els) {
            if (el.getName().equals(settings.getTagName())) {
                try {
                    String body = el.getContentAsString();
                    Attributes attrs = source.parseAttributes(el);
                    String id = attrs.getValue("id");
                    String method = attrs.getValue("method");
                    String options = attrs.getValue("options");
                    String basepath = attrs.getValue("basepath");
                    if (body == null) body = "";
                    if (body.contains("<%--"))
                        body = removeJspComments(body);
                    if (body.contains("<%")) {
                        logger.warn("Dynamic content found, tag skipped in file " + filename);
                    } else {
                        CompressTagHandler tagHandler = new CompressTagHandler(id, method, options, basepath);
                        tagHandler.handleTag(request, null, body);
                    }
                } catch (JSCompileException e) {
                    errorCount++;
                    logger.error("Tag process exception:", e);
                }
            } else if (el.getName().equals("%")) {
                Attributes attrs = source.parseAttributes(el);
                if (attrs != null && attrs.isValueExists("include") && attrs.isValueExists("file")) {
                    String file = attrs.getValue("file");
                    String pa = "";
                    if (!file.trim().startsWith("/") && file.indexOf("/")>=0)
                        pa = file.substring(0, file.lastIndexOf("/")+1);
                    file =  webAppRootPath + "/" + PathUtils.calcPath(
                            (pathAddition.equals("")||file.startsWith("/")?"":(pathAddition+"/"))+file, request,"");
                    errorCount += processFile(file, webAppRootPath, request, settings, pathAddition+pa);
                }
            }
        }
        return errorCount;
    }

    private static String removeJspComments(String str) {
        if (str == null) return "";
        StringBuilder sb = new StringBuilder();
        int start = 0;
        while (start < str.length()) {
            int startIndex = str.indexOf("<%--", start);
            if (startIndex < 0)
                break;
            else {
                int endIndex = str.indexOf("--%>", startIndex + 1);
                if (endIndex < 0) break;
                else {
                    sb.append(str.substring(start, startIndex));
                    start = endIndex + "--%>".length();
                }
            }
        }
        if (start < str.length())
            sb.append(str.substring(start, str.length()));
        return sb.toString();
    }

    private static final Logger logger = LoggerFactory.getLogger(JspProcessor.class);
}
