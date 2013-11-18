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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.granule.utils.Utf8Properties;

/**
 * User: Dario Wunsch Date: 22.06.2010 Time: 22:09:21
 */
public class CompressorSettings {
    private String jsCompressMethod = JSFASTMIN_VALUE;
    private String cssCompressMethod = CSSFASTMIN_VALUE;
    private String cache = MEMORY_VALUE;
    private boolean checkTimestamps = true;
    private boolean ignoreMissedFiles = true;
    private boolean formatPrettyPrint = false;
    private boolean formatPrintInputDelimiter = false;
    private String locale = null;
    private String optimization = SIMPLE_OPTIMIZATIONS_VALUE;
    private boolean handleJavascript = true;
    private boolean handleCss = true;
    private boolean cleanJsDuplicates = true;
    private boolean cleanCssDuplicates = true;
    private List<String> closurePathes = new ArrayList<String>();
    private List<String> keepFirstCommentPathes = new ArrayList<String>();
    private String outputWrapper = null;
    private String cacheFileLocation = null;
    private String tagName = DEFAULT_TAG_NAME;
    private String contextRoot = null;
	private String basePath = null;
	private boolean gzipOutput = true;

    public static final String NONE_VALUE = "none";
    public static final String CLOSURE_COMPILER_VALUE = "closure-compiler";
    public static final String JSFASTMIN_VALUE = "jsfastmin";
    public static final String AUTO_VALUE = "auto";
    public static final String COMBINE_VALUE = "combine";
    public static final String CSSFASTMIN_VALUE = "cssfastmin";
    public static final String CACHE_KEY = "cache";
    public static final String MEMORY_VALUE = "memory";
    public static final String DISK_CACHE_VALUE = "disk";
    public static final String DISK_CACHE_VALUE_ADD2= "file";
    public static final String IGNORE_VALUE = "ignore";
    public static final String ALL_VALUE = "all";
    public static final String JAVASCRIPT_VALUE = "javascript";
    public static final String CSS_VALUE = "css";
    public static final String TAG_PROCESS_KEY = "tag.process";
    public static final String TAG_NAME_KEY = "tag.name";
    public static final String JS_COMPRESS_METHOD_KEY = "tag.method.javascript";
    public static final String TAG_METHOD_CSS_KEY = "tag.method.css";
    public static final String TAG_JS_CLEANDUPICATES_KEY = "tag.js.cleandupicates";
    public static final String TAG_CSS_CLEANDUPLICATES_KEY = "tag.css.cleanduplicates";
    public static final String CLOSURE_COMPILER_LOCALE_KEY = "closure-compiler.locale";
    public static final String CLOSURE_ADD_PATH_KEY = "closure-compiler.add-path";
    public static final String KEEP_FIRST_COMMENT_PATH_KEY = "keepfirstcommentpath";
    public static final String IGNORE_MISSED_FILES_KEY = "ignorenotfoundfiles";
    public static final String CACHE_FILE_LOCATION_KEY = "cache.file.location";
    public static final String CLOSURE_COMPILER_COMPILATION_LEVEL_KEY = "closure-compiler.compilation_level";
    public static final String CLOSURE_COMPILER_FORMATTING_PRETTY_PRINT_KEY = "closure-compiler.formatting.pretty_print";
    public static final String CLOSURE_COMPILER_FORMATTING_PRINT_INPUT_DELIMITER_KEY = "closure-compiler.formatting.print_input_delimiter";
    public static final String CLOSURE_COMPILER_OUTPUT_WRAPPER_KEY = "closure-compiler.output_wrapper";
    public static final String COMPRESS_METHOD_TIMESTAMP_CHECK_KEY = "timestampcheck";
    public static final String DEFAULT_OUTPUT_WRAPPER_MARKER = "%output%";
    public static final String SIMPLE_OPTIMIZATIONS_VALUE = "SIMPLE_OPTIMIZATIONS";
    public static final String WHITESPACE_ONLY_VALUE = "WHITESPACE_ONLY";
    public static final String ADVANCED_OPTIMIZATIONS_VALUE = "ADVANCED_OPTIMIZATIONS";
    public static final String DEFAULT_TAG_NAME = "g:compress";
    public static final String CONTEXTROOT_KEY = "contextroot";
    public static final String BASEPATH_KEY = "basepath";
    public static final String GZIP_OUTPUT_KEY = "gzip.output";

    public String getJsCompressMethod() {
        return jsCompressMethod;
    }

    public String getCache() {
        return cache;
    }

    public boolean isCheckTimestamps() {
        return checkTimestamps;
    }

    public boolean isIgnoreMissedFiles() {
        return ignoreMissedFiles;
    }

    public boolean isFormatPrettyPrint() {
        return formatPrettyPrint;
    }

    public boolean isFormatPrintInputDelimiter() {
        return formatPrintInputDelimiter;
    }

    public String getLocale() {
        return locale;
    }

    public String getOptimization() {
        return optimization;
    }

    public String getOutputWrapper() {
        return outputWrapper;
    }

    public String getOutputWrapperMarker() {
        return DEFAULT_OUTPUT_WRAPPER_MARKER;
    }

    public String getTagName() {
        return tagName;
    }   

    /**
     * @return Returns the gzipOutput.
     */
    public boolean isGzipOutput() {
        return gzipOutput;
    }

    /**
     * @param gzipOutput The gzipOutput to set.
     */
    public void setGzipOutput(boolean gzipOutput) {
        this.gzipOutput = gzipOutput;
    }

    public void load(Utf8Properties props) throws IOException {
        String readed;
        String mode = ALL_VALUE;
        
        if (props.containsKey(TAG_PROCESS_KEY)) {
            readed = props.getProperty(TAG_PROCESS_KEY);
            if (ALL_VALUE.equalsIgnoreCase(readed) || JAVASCRIPT_VALUE.equalsIgnoreCase(readed)
                    || CSS_VALUE.equalsIgnoreCase(readed) || IGNORE_VALUE.equalsIgnoreCase(readed))
                mode = readed;
            if (ALL_VALUE.equalsIgnoreCase(mode)) {
                handleJavascript = true;
                handleCss = true;
            } else if (JAVASCRIPT_VALUE.equalsIgnoreCase(mode)) {
                handleJavascript = true;
                handleCss = false;
            } else if (CSS_VALUE.equalsIgnoreCase(mode)) {
                handleJavascript = false;
                handleCss = true;
            } else {
                handleJavascript = false;
                handleCss = false;
            }
        }

        if (props.containsKey(TAG_METHOD_CSS_KEY)) {
            readed = props.getProperty(TAG_METHOD_CSS_KEY);
            if (COMBINE_VALUE.equalsIgnoreCase(readed) || CSSFASTMIN_VALUE.equalsIgnoreCase(readed))
                cssCompressMethod = readed;
        }

        if (props.containsKey(COMPRESS_METHOD_TIMESTAMP_CHECK_KEY))
            checkTimestamps = getBoolean(props.getProperty(COMPRESS_METHOD_TIMESTAMP_CHECK_KEY), checkTimestamps);

        if (props.containsKey(JS_COMPRESS_METHOD_KEY)) {
            readed = props.getProperty(JS_COMPRESS_METHOD_KEY);
            if (COMBINE_VALUE.equalsIgnoreCase(readed)
                    || CLOSURE_COMPILER_VALUE.equalsIgnoreCase(readed)
                    || JSFASTMIN_VALUE.equalsIgnoreCase(readed)
                    || AUTO_VALUE.equalsIgnoreCase(readed))
                jsCompressMethod = readed;
        }

        if (props.containsKey(CACHE_KEY)) {
            readed = props.getProperty(CACHE_KEY);
            if (NONE_VALUE.equalsIgnoreCase(readed)
                    || MEMORY_VALUE.equalsIgnoreCase(readed)
                    || DISK_CACHE_VALUE.equalsIgnoreCase(readed)
                    || DISK_CACHE_VALUE_ADD2.equalsIgnoreCase(readed))
                if (DISK_CACHE_VALUE_ADD2.equalsIgnoreCase(readed))
                	readed=DISK_CACHE_VALUE;
            	cache = readed;
        }

        if (props.containsKey(CLOSURE_COMPILER_FORMATTING_PRETTY_PRINT_KEY))
            formatPrettyPrint = getBoolean(props.getProperty(CLOSURE_COMPILER_FORMATTING_PRETTY_PRINT_KEY),
                    formatPrettyPrint);

        if (props.containsKey(CLOSURE_COMPILER_FORMATTING_PRINT_INPUT_DELIMITER_KEY))
            formatPrintInputDelimiter = getBoolean(props
                    .getProperty(CLOSURE_COMPILER_FORMATTING_PRINT_INPUT_DELIMITER_KEY), formatPrintInputDelimiter);

        if (props.containsKey(CLOSURE_COMPILER_LOCALE_KEY))
            locale = props.getProperty(CLOSURE_COMPILER_LOCALE_KEY);

        if (props.containsKey(CLOSURE_COMPILER_COMPILATION_LEVEL_KEY)) {
            readed = props.getProperty(CLOSURE_COMPILER_COMPILATION_LEVEL_KEY);
            if (SIMPLE_OPTIMIZATIONS_VALUE.equalsIgnoreCase(readed)
                    || WHITESPACE_ONLY_VALUE.equalsIgnoreCase(readed)
                    || ADVANCED_OPTIMIZATIONS_VALUE.equalsIgnoreCase(readed))
                optimization = readed;
        }
        
        if (props.containsKey(CLOSURE_COMPILER_OUTPUT_WRAPPER_KEY))
            outputWrapper = props.getProperty(CLOSURE_COMPILER_OUTPUT_WRAPPER_KEY);

        if (props.containsKey(TAG_CSS_CLEANDUPLICATES_KEY))
            cleanCssDuplicates = getBoolean(props.getProperty(TAG_CSS_CLEANDUPLICATES_KEY), cleanCssDuplicates);

        if (props.containsKey(TAG_JS_CLEANDUPICATES_KEY))
            cleanJsDuplicates = getBoolean(props.getProperty(TAG_JS_CLEANDUPICATES_KEY), cleanJsDuplicates);

        readFileListProperty(props, CLOSURE_ADD_PATH_KEY, closurePathes);
        
        readFileListProperty(props, KEEP_FIRST_COMMENT_PATH_KEY, keepFirstCommentPathes);

        if (props.containsKey(IGNORE_MISSED_FILES_KEY))
            ignoreMissedFiles = getBoolean(props.getProperty(IGNORE_MISSED_FILES_KEY), ignoreMissedFiles);
        
        if (props.containsKey(CACHE_FILE_LOCATION_KEY))
            cacheFileLocation = props.getProperty(CACHE_FILE_LOCATION_KEY);
        
        if (props.containsKey(TAG_NAME_KEY))
            tagName = props.getProperty(TAG_NAME_KEY);
        
        if (props.containsKey(CONTEXTROOT_KEY))
        	contextRoot = props.getProperty(CONTEXTROOT_KEY);
        
        if(props.contains(GZIP_OUTPUT_KEY)){
            gzipOutput = getBoolean(props.getProperty(GZIP_OUTPUT_KEY), gzipOutput);
        }
    }

    private void readFileListProperty(Utf8Properties props, String settingName, List<String> list) throws IOException {
        if (props.containsKey(settingName)) {
            list.clear();
            String s = props.getProperty(settingName);
            StreamTokenizer st = new StreamTokenizer(new StringReader(s.replaceAll("\\\\", "\\\\\\\\")));
            st.resetSyntax();
            st.wordChars('\u0000', '\uFFFF');
            st.whitespaceChars(',', ',');
            st.quoteChar('\"');
            st.quoteChar('\'');
            st.eolIsSignificant(false);
            while (st.nextToken() != StreamTokenizer.TT_EOF)
                list.add(st.sval);
        }
    }

    public void load(InputStream in) throws IOException {
        BufferedInputStream is = new BufferedInputStream(in);
        Utf8Properties props = new Utf8Properties();
        props.load(is);
        load(props);
    }

    public CompressorSettings(InputStream in) throws IOException {
        load(in);
    }

    public CompressorSettings(Utf8Properties props) throws IOException {
        load(props);
    }

    public void setJsCompressMethod(String newMethod) {
        jsCompressMethod = newMethod;
        if (jsCompressMethod == null)
            jsCompressMethod = COMBINE_VALUE;
        else
            jsCompressMethod = jsCompressMethod.toLowerCase();
        if (!jsCompressMethod.equalsIgnoreCase(COMBINE_VALUE)
                && !jsCompressMethod.equalsIgnoreCase(CLOSURE_COMPILER_VALUE)
                && !jsCompressMethod.equalsIgnoreCase(JSFASTMIN_VALUE))
            jsCompressMethod = COMBINE_VALUE;
    }

    public boolean isHandleJavascript() {
        return handleJavascript;
    }

    public boolean isHandleCss() {
        return handleCss;
    }

    public String getCssCompressMethod() {
        return cssCompressMethod;
    }

    public boolean isCleanJsDuplicates() {
        return cleanJsDuplicates;
    }

    public boolean isCleanCssDuplicates() {
        return cleanCssDuplicates;
    }

    public List<String> getClosurePathes() {
        return closurePathes;
    }

    public List<String> getKeepFirstCommentPathes() {
        return keepFirstCommentPathes;
    }

    public String getCacheFileLocation() {
        return cacheFileLocation;
    }
 
    public String getContextRoot() {
		return contextRoot;
	}

	public String getBasePath() {
		return basePath;
	}

    
    public static boolean getBoolean(String str, boolean defaultvalue) {
        boolean result = "yes".equalsIgnoreCase(str)
                || "on".equalsIgnoreCase(str)
                || "true".equalsIgnoreCase(str)
                || "1".equalsIgnoreCase(str);
        if (result)
            return true;
        result = "no".equalsIgnoreCase(str)
                || "off".equalsIgnoreCase(str)
                || "false".equalsIgnoreCase(str)
                || "0".equalsIgnoreCase(str);
        if (result)
            return false;
        return defaultvalue;
    }

    public void setOptions(String options) throws JSCompileException {
        if (options != null) {
            try {
                ByteArrayInputStream is = new ByteArrayInputStream(options.getBytes("UTF-8"));
                load(is);
            } catch (IOException e) {
                throw new JSCompileException(e);
            }
        }
    }
}
