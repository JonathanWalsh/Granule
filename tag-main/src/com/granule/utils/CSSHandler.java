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

/**
 * User: Dario Wunsch
 * Date: 14.07.2010
 * Time: 22:08:04
 */

import com.granule.CompressorSettings;
import com.granule.ExternalFragment;
import com.granule.FragmentDescriptor;
import com.granule.IRequestProxy;
import com.granule.JSCompileException;
import com.granule.logging.Logger;
import com.granule.logging.LoggerFactory;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CSSHandler {
    private static final String stringLiteralRegex = "(\"(?:\\.|[^\\\"])*\"|'(?:\\.|[^\\'])*')";
    private static final String urlRegex = String.format(
            "(?:url\\(\\s*(?!\\s*\\'\\/)(?!\\s*\\\"\\/)(?!\\s*\\/)(%s|[^)]*)\\s*\\))", stringLiteralRegex);
    private static final String importRegex = String.format(
            "(?:@import\\s+(%s|%s))", urlRegex, stringLiteralRegex);

    private static final Pattern regex = Pattern.compile(String.format(
            "(%s)|(%s)|%s", importRegex, urlRegex, stringLiteralRegex));

    private class ReplaceInfo {
        boolean isImport = false;
        int begin;
        int end;
        String text;
    }


    public void parse(String line, List<ReplaceInfo> replaces, int start) {
        Matcher m = regex.matcher(line);

        while (m.find()) {
            try {
                if (m.group(3) != null) {
                    ReplaceInfo replace = new ReplaceInfo();
                    replace.isImport = true;
                    replace.text = cleanQuotesFromMatchString(m.group(3));
                    replace.begin = line.substring(0, m.start(3)).lastIndexOf("@import") + start;
                    replace.end = line.substring(m.end(3)).indexOf(";") + m.end(3) + 1 + start;
                    replaces.add(replace);
                }
                if (m.group(5) != null) {
                    ReplaceInfo replace = new ReplaceInfo();
                    replace.isImport = true;
                    replace.text = cleanQuotesFromMatchString(m.group(5));
                    replace.begin = line.substring(0, m.start(5)).lastIndexOf("@import") + start;
                    replace.end = line.substring(m.end(5)).indexOf(";") + m.end(5) + 1 + start;
                    replaces.add(replace);
                }
                if (m.group(7) != null) {
                    ReplaceInfo replace = new ReplaceInfo();
                    replace.text = cleanQuotesFromMatchString(m.group(7));
                    replace.begin = m.start(7) + start;
                    replace.end = m.end(7) + start;
                    if (!PathUtils.isWebAddress(replace.text))
                        replaces.add(replace);
                }
            } catch (IndexOutOfBoundsException e) { /* eat move on */ }
        }
    }

    private String cleanQuotesFromMatchString(String match) {
        if (match.charAt(0) == '\'' || match.charAt(0) == '"')
            return match.substring(1, match.length() - 1);
        return match;
    }

    public String handle(FragmentDescriptor fd, IRequestProxy request, CompressorSettings settings,
                         List<FragmentDescriptor> deps) throws JSCompileException {
        try {
            if (fd instanceof ExternalFragment && settings.isIgnoreMissedFiles() && 
                    !(new File(request.getRealPath(((ExternalFragment) fd).getFilePath()))).exists()) {
                logger.warn(MessageFormat.format("File {0} not found, ignored", ((ExternalFragment) fd).getFilePath()));
                return "";
            }
            String css = fd.getContentText(request);
            String newPath = (fd instanceof ExternalFragment) ? ((ExternalFragment) fd).getFolderPath() : "";
            final List<ReplaceInfo> replaces = new ArrayList<ReplaceInfo>();
            String[] lines = css.split("\n");
            int startLine = 0;
            for (String line : lines) {
                parse(line, replaces, startLine);
                startLine += line.length() + 1;
            }
            if (replaces.size() == 0)
                return css;
            else {
                int start = 0;
                StringBuilder sb = new StringBuilder();
                for (ReplaceInfo replaceInfo : replaces) {
                    sb.append(css.substring(start, replaceInfo.begin));
                    if (!replaceInfo.isImport)
                        sb.append(PathUtils.clean(newPath + replaceInfo.text));
                    else {
                        boolean cyclicLink = false;
                        String cssPath = PathUtils.clean(((newPath.length() > 0 && newPath.charAt(0) != '/') ? "/" :
                                "") + newPath + replaceInfo.text);
                        for (FragmentDescriptor dep : deps)
                            if (dep instanceof ExternalFragment) {
                                ExternalFragment ef = (ExternalFragment) dep;
                                if (ef.getFilePath() != null && ef.getFilePath().equals(cssPath)) {
                                    cyclicLink = true;
                                    break;
                                }
                            }
                        if (cyclicLink) {
                            logger.error("Found cyclic link!!!!");
                            start = replaceInfo.end;
                            continue;
                        }
                        boolean addFile = true;
                        if (settings.isIgnoreMissedFiles() && !(new File(request.getRealPath(cssPath))).exists()) {
                            logger.warn(MessageFormat.format("File {0} not found, ignored", cssPath));
                            addFile = false;
                        }
                        if (addFile) {
                            String filename;
                            if (replaceInfo.text.startsWith("/"))
                                filename = replaceInfo.text;
                            else filename = cssPath;
                            FragmentDescriptor imp = new ExternalFragment(filename);
                            deps.add(imp);
                            sb.append(handle(imp, request, settings, deps));
                        }
                    }
                    start = replaceInfo.end;
                }
                sb.append(css.substring(start));
                return sb.toString();
            }
        } catch (Exception e) {
            throw new JSCompileException(e);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(CSSHandler.class);
}
