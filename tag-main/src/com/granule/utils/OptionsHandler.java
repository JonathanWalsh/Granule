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

import java.util.ArrayList;
import java.util.List;

public class OptionsHandler {
    private static final String PARAMETERS_DIVIDER = "--";

    private class Pair {
        int start;
        int end;

        private Pair(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    private boolean isInQuote(List<Pair> quotes, int position) {
        for (Pair p : quotes)
            if (position >= p.start && position <= p.end)
                return true;
            else if (position < p.start)
                return false;
        return false;
    }

    public String handle(String s, String method) {
        List<Pair> quotes = new ArrayList<Pair>();
        int start = 0;
        while (start < s.length()) {
            if (s.charAt(start) == '\"' || s.charAt(start) == '\'') {
                char c = s.charAt(start) == '\"' ? '\"' : '\'';
                int endPosition = s.indexOf(c, start + 1);
                if (endPosition < 0) endPosition = s.length() - 1;
                Pair p = new Pair(start, endPosition);
                quotes.add(p);
                start = endPosition + 1;
            } else start++;
        }
        List<Integer> prefixes = new ArrayList<Integer>();
        start = 0;
        while (start < s.length()) {
            int pos = s.indexOf(PARAMETERS_DIVIDER, start);
            if (pos >= 0 && !isInQuote(quotes, pos))
                prefixes.add(pos);
            if (pos >= 0)
                start = pos + 1;
            else start = s.length();
        }

        if (s.length() > 0)
            prefixes.add(s.length());
        StringBuilder sb = new StringBuilder();
        if (prefixes.size() == 1)
            sb.append(method).append(".").append(s).append("\n");
        for (int i = 0; i < prefixes.size() - 1; i++) {
            int p1 = prefixes.get(i);
            int p2 = prefixes.get(i + 1);
            String p = s.substring(p1, p2);
            if (p.startsWith(PARAMETERS_DIVIDER))
                p = method + "." + p.substring(p.indexOf(PARAMETERS_DIVIDER) + PARAMETERS_DIVIDER.length());
            if (p.indexOf(" ") > 0)
                p = p.substring(0, p.indexOf(" ")) + "=" + p.substring(p.indexOf(" ") + 1);
            sb.append(p).append("\n");
        }
        return sb.toString();
    }
}
