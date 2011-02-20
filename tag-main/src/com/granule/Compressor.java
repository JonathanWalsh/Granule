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

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.Result;
import com.granule.utils.CSSHandler;
import com.granule.utils.PathUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: Dario Wunsch
 * Date: 07.06.2010
 * Time: 4:40:08
 */
public class Compressor {

    public static String compile(List<FragmentDescriptor> scripts, IRequestProxy request,
                                 CompressorSettings settings) throws JSCompileException {
        Compiler compiler = new Compiler();

        CompilerOptions options = new CompilerOptions();
        options.markAsCompiled = true;
        if (settings.getLocale() != null)
            options.locale = settings.getLocale();
        options.prettyPrint = settings.isFormatPrettyPrint();
        options.printInputDelimiter = settings.isFormatPrintInputDelimiter();
        if (settings.getOptimization() != null) {
            if (settings.getOptimization().equalsIgnoreCase(CompressorSettings.ADVANCED_OPTIMIZATIONS_VALUE))
                CompilationLevel.ADVANCED_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
            else if (settings.getOptimization().equalsIgnoreCase(CompressorSettings.WHITESPACE_ONLY_VALUE))
                CompilationLevel.WHITESPACE_ONLY.setOptionsForCompilationLevel(options);
            else CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
        }

        List<JSSourceFile> sources = new ArrayList<JSSourceFile>();
        for (FragmentDescriptor sd : scripts) {
            JSSourceFile input;
            if (sd instanceof ExternalFragment)
                input = JSSourceFile.fromFile(request.getRealPath(((ExternalFragment) sd).getFilePath()));
            else
                input = JSSourceFile.fromCode("fragment" + Integer.toString(scripts.indexOf(sd)),
                        ((InternalFragment) sd).getText());
            sources.add(input);
        }

        Result res = compiler.compile(new JSSourceFile[]{}, sources.toArray(new JSSourceFile[]{}), options);
        if (!res.success)
            throw new JSCompileException();
        String licenses = "";
        try {
            licenses = getLicenses(scripts, settings, request);
        } catch (IOException e) {
            throw new JSCompileException(e);
        }

        // The compiler is responsible for generating the compiled code; it is not
        // accessible via the Result.
        if (settings.getOutputWrapper() == null)
            return licenses + compiler.toSource();
        else
            return licenses + settings.getOutputWrapper().replace(settings.getOutputWrapperMarker(),
                    compiler.toSource());
    }

    public static String unify(List<FragmentDescriptor> fragments, IRequestProxy request) throws JSCompileException {
        StringBuilder sb = new StringBuilder();
        for (FragmentDescriptor sd : fragments) {
            try {
                sb.append(sd.getContentText(request));
                if (sd instanceof ExternalFragment) sb.append("\n");
            } catch (IOException e) {
                throw new JSCompileException(e);
            }
        }
        return sb.toString();
    }

    public static String minifyJs(List<FragmentDescriptor> scripts, CompressorSettings settings,
                                  IRequestProxy request) throws JSCompileException {
        String result = unify(scripts, request);
        try {
            StringWriter sw = new StringWriter();
            JSFastMin min = new JSFastMin();
            min.minimize(new StringReader(result), sw);
            result = getLicenses(scripts, settings, request) + sw.toString().trim();
        } catch (Exception e) {
            throw new JSCompileException(e);
        }
        return result;
    }

    public static String unifyCss(List<FragmentDescriptor> fragments, List<FragmentDescriptor> deps,
                                  CompressorSettings settings, IRequestProxy request) throws JSCompileException {
        StringBuilder sb = new StringBuilder();
        for (FragmentDescriptor sd : fragments) {
            try {
                String relativePath = (sd instanceof ExternalFragment)?((ExternalFragment)sd).getFolderPath():null;
                if (relativePath == null || relativePath.trim().equals(""))
                    sb.append(sd.getContentText(request));
                else
                    sb.append((new CSSHandler()).handle(sd, request, settings, deps));
            } catch (IOException e) {
                throw new JSCompileException(e);
            }
        }
        return sb.toString();
    }

    public static String minifyCss(List<FragmentDescriptor> fragments, List<FragmentDescriptor> deps,
                                   CompressorSettings settings, IRequestProxy request) throws JSCompileException {
        String result = unifyCss(fragments, deps, settings, request);
        CSSFastMin cssMin = new CSSFastMin();
        return cssMin.minimize(result);
    }

    private static String getLicenses(List<FragmentDescriptor> fragments, CompressorSettings settings,
                                      IRequestProxy request) throws IOException {
        Set<String> licenses = new HashSet<String>();
        for (FragmentDescriptor fd : fragments) {
            if (fd instanceof ExternalFragment && checkFile(((ExternalFragment) fd).getFilePath(), settings))
                licenses.add(getLicense(request.getRealPath(((ExternalFragment) fd).getFilePath())));
        }
        StringBuilder sb = new StringBuilder();
        for (String s : licenses)
            sb.append(s).append("\n");
        return sb.toString();
    }

    private static boolean checkFile(String filename, CompressorSettings settings) {
        List<String> files = settings.getKeepFirstCommentPathes();
        for (String file : files) {
            file = PathUtils.clean(file);
            if (filename.startsWith("/") && !file.startsWith("/"))
                file = "/" + file;
            if (matches(file, filename)) return true;
        }
        return false;
    }

    private static boolean matches(String pattern, String text) {
        // add sentinel so don't need to worry about *'s at end of pattern
        text += '\0';
        pattern += '\0';

        int n = pattern.length();

        boolean[] states = new boolean[n + 1];
        boolean[] old = new boolean[n + 1];
        old[0] = true;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            states = new boolean[n + 1];       // initialized to false
            for (int j = 0; j < n; j++) {
                char p = pattern.charAt(j);

                // hack to handle *'s that match 0 characters
                if (old[j] && (p == '*')) old[j + 1] = true;

                if (old[j] && (p == c)) states[j + 1] = true;
                if (old[j] && (p == '?')) states[j + 1] = true;
                if (old[j] && (p == '*')) states[j] = true;
                if (old[j] && (p == '*')) states[j + 1] = true;
            }
            old = states;
        }
        return states[n];
    }

    private static String getLicense(String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        //state==0 - beginning of file, state==1 - handling set of oneline comments,
        // state==2 - start of handling multi-
        //string comment, state==3 - end of multi-string comment reached
        int state = 0;
        BufferedReader fileHandle = new BufferedReader(new InputStreamReader(new FileInputStream(filename),
                "UTF-8"));
        try {
            String line;
            while ((line = fileHandle.readLine()) != null) {
                if (state == 0) {
                    if (line.trim().length() == 0) {// do nothing
                    } else if (line.trim().startsWith("//")) {
                        state = 1;
                        sb.append(line).append("\n");
                    } else if (line.trim().startsWith("/*")) {
                        if (line.contains("*/")) {
                            state = 3;
                            sb.append(line.substring(0, line.indexOf("*/") + "*/".length()));
                            break;
                        } else {
                            state = 2;
                            sb.append(line).append("\n");
                        }
                    } else break;
                } else if (state == 1) {
                    if (line.trim().startsWith("//"))
                        sb.append(line).append("\n");
                    else break;
                } else if (state == 2) {
                    if (line.contains("*/")) {
                        state = 3;
                        sb.append(line.substring(0, line.indexOf("*/") + "*/".length()));
                        break;
                    } else {
                        state = 2;
                        sb.append(line).append("\n");
                    }
                }
            }
        } finally {
            fileHandle.close();
        }
        if (state == 2) return "";
        else return sb.toString();
    }
}
