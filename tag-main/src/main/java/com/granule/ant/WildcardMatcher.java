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

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

public class WildcardMatcher {
    public static boolean match(String filename, String wildcardMatcher) {
        if (filename == null && wildcardMatcher == null) {
            return true;
        }
        if (filename == null || wildcardMatcher == null) {
            return false;
        }
        filename = convertCase(filename.replace('\\','/'));
        wildcardMatcher = convertCase(wildcardMatcher.replace('\\', '/'));
        String[] wcs = splitOnTokens(wildcardMatcher);
        boolean anyChars = false;
        int textIdx = 0;
        int wcsIdx = 0;
        Stack<int[]> backtrack = new Stack<int[]>();

        do {
            if (backtrack.size() > 0) {
                int[] array = backtrack.pop();
                wcsIdx = array[0];
                textIdx = array[1];
                anyChars = true;
            }

            while (wcsIdx < wcs.length) {

                if (wcs[wcsIdx].equals("?")) {
                    textIdx++;
                    anyChars = false;
                }
                else if (wcs[wcsIdx].equals("*")) {
                    anyChars = true;
                    if (wcsIdx == wcs.length - 1) {
                        textIdx = filename.length();
                    }
                }
                else {
                    if (anyChars) {
                        textIdx = filename.indexOf(wcs[wcsIdx], textIdx);
                        if (textIdx == -1) {
                            break;
                        }
                        int repeat = filename.indexOf(wcs[wcsIdx], textIdx + 1);
                        if (repeat >= 0) {
                            backtrack.push(new int[]{wcsIdx, repeat});
                        }
                    }
                    else {
                        if (!filename.startsWith(wcs[wcsIdx], textIdx)) {
                            break;
                        }
                    }
                    textIdx += wcs[wcsIdx].length();
                    anyChars = false;
                }
                wcsIdx++;
            }
            if (wcsIdx == wcs.length && textIdx == filename.length()) {
                return true;
            }
        }
        while (backtrack.size() > 0);

        return false;
    }

    static String[] splitOnTokens(String text) {
        if (text.indexOf("?") == -1 && text.indexOf("*") == -1) {
            return new String[]{text};
        }
        char[] array = text.toCharArray();
        ArrayList<String> list = new ArrayList<String>();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            if (array[i] == '?' || array[i] == '*') {
                if (buffer.length() != 0) {
                    list.add(buffer.toString());
                    buffer.setLength(0);
                }
                if (array[i] == '?') {
                    list.add("?");
                }
                else if (list.size() == 0 ||
                        (i > 0 && !list.get(list.size() - 1).equals("*"))) {
                    list.add("*");
                }
            }
            else {
                buffer.append(array[i]);
            }
        }
        if (buffer.length() != 0) {
            list.add(buffer.toString());
        }
        return list.toArray(new String[list.size()]);
    }

    private static String convertCase(String str) {
        if (str == null) {
            return null;
        }
        return File.separatorChar=='/' ? str : str.toLowerCase();
    }
}
