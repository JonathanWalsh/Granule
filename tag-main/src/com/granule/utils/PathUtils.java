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
package com.granule.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import com.granule.IRequestProxy;

public class PathUtils {
	public static final Pattern compiledSlashPattern = Pattern.compile(Pattern.quote("/"));
	
	public static String subtractContextRoot(String uri, String contextRoot) {

		if (contextRoot == null || contextRoot.length() == 0 || contextRoot.equals("/"))
			return uri;

		String lowerUri = clean(uri.toLowerCase());
		String lowercontextRoot = contextRoot.toLowerCase();
		int contextRootPosition = lowerUri.indexOf(lowercontextRoot);
		if (contextRootPosition == -1)
			return uri;

		int slashPos = lowerUri.indexOf('/', contextRootPosition
				+ contextRoot.length());
		if (slashPos == -1)
			return uri;

		return clean(uri.substring(slashPos));
	}

	public static String clean(String path) {
		String result = path.replace("\\", "/").replace("//", "/").replace(
				"/./", "/").trim();
		return cleanParentFolders(result);
	}

	/**
	 * Removes all .. from the path, requires that all non-existence of
	 * duplicate slashes
	 * 
	 * @param path
	 * @return
	 */
	private static String cleanParentFolders(String path) {
		StringTokenizer tokenizer = new StringTokenizer(path, "./", true);
		ArrayList<String> tokens = new ArrayList<String>();
		int count = 0;
		int dots = 0;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (token.equals(".")) {
				dots++;
			} else {
				if (dots == 2 && token.equals("/")) {		
					count -= 4;
					if (count<0) count=0;
					dots=0;
					continue;
				}
				dots = 0;
			}
			if (count > tokens.size() - 1)
				tokens.add(token);
			else
				tokens.set(count, token);
			count++;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append(tokens.get(i));
		}
		return sb.toString();
	}
	
	

	/*
	 * Calculates relative path from absolutes ones
	 * 
	 * 
	 */
	public static String getRelpath(String target, String base) {
        target = target.replace("\\", "/");
        base = base.replace("\\", "/");
    	
        String[] splitTarget = compiledSlashPattern.split(target, 0);
		String[] splitBase = compiledSlashPattern.split(base, -1);

		StringBuilder common = new StringBuilder();
		int commonIndex = 0;
		for (int i = 0; i < splitTarget.length && i < splitBase.length; i++) {
			if (splitTarget[i].equals(splitBase[i])) {
				common.append(splitTarget[i]).append("/");
				commonIndex++;
			} else
				break;
		}

		if (commonIndex == 0) //support case for windows paths started from drive letter
			return target;

		StringBuilder relative = new StringBuilder();
		if (splitBase.length != commonIndex) {
			int dirsUp = splitBase.length - commonIndex - 1;
		    for (int i = 1; i <= dirsUp; i++)
				relative.append("../");
		}
		if (target.length() > common.length())
			relative.append(target.substring(common.length()));

		return relative.toString();
	}
	
    public static boolean isWebAddress(String path) {
        return path.startsWith("http://") || path.startsWith("https://");
    }

    public static boolean isValidJs(String path) {
        if (path.indexOf("?") != -1)
            path = path.substring(0, path.indexOf("?"));
        return path.toLowerCase().endsWith(".js");
    }

    public static boolean isValidCss(String path) {
        if (path.indexOf("?") != -1)
            path = path.substring(0, path.indexOf("?"));
        return path.toLowerCase().endsWith(".css");
    }

    public static String calcPath(String path, IRequestProxy request, String basepath) {
        int questionPos = path.indexOf('?');
        if (questionPos != -1)
            path = path.substring(0, questionPos);

        if (path.startsWith("/")) {
            path = subtractContextRoot(path, request.getContextPath());
        } else {
            String servletPath = (basepath==null||basepath.length()==0)?request.getServletPath():basepath;
            if (servletPath.indexOf('/') != -1) {
            	int startPos=0;
            	if (servletPath.charAt(0)=='/')
            		startPos=1;
                path = servletPath.substring(startPos, servletPath.lastIndexOf('/') + 1) + path;
            }
        }
        return clean(path);
    }
    
	public static InputStream getResourceAsStream(Class<?> clazz,String fileName) {
		InputStream is=clazz.getResourceAsStream(fileName);
		if (is == null) {
			is=clazz.getClassLoader().getResourceAsStream(fileName);
		}
		if (is == null) {
			is = ClassLoader.getSystemResourceAsStream(fileName);
		}
		if (is == null) {
			is = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(fileName);
		}
		return is;
	}
	
	public static String getRelativeFolderPath(String filePath) {
		String folderPath;
		if (filePath.indexOf("/") == -1 || filePath.lastIndexOf('/') == 0)
			folderPath = "";
		else
			folderPath = filePath.substring(filePath.startsWith("/") ? 1 : 0,
					filePath.lastIndexOf("/"));
		if (folderPath.length() > 0
				&& folderPath.charAt(folderPath.length() - 1) != '/')
			folderPath += "/";
		return folderPath;
	}
	
	public static String getFolderPath(String filePath) {
		String folderPath;
		if (filePath.indexOf("/") == -1 || filePath.lastIndexOf('/') == 0)
			folderPath = "";
		else
			folderPath = filePath.substring(0,filePath.lastIndexOf("/"));
		if (folderPath.length() > 0
				&& folderPath.charAt(folderPath.length() - 1) != '/')
			folderPath += "/";
		return folderPath;
	}
	
	public static String getContextURL(String contextPath, String path) {
    	if (contextPath==null||contextPath.length()==0||contextPath.equals("/")) {
    		return path;
    	}
    	else return contextPath+path;
	}

}

