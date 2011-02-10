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
package com.granule.miniparser;

import java.util.List;

/**
 * User: Dario Wünsch
 * Date: 22.12.10
 * Time: 2:10
 */
public class Scanner extends BaseScanner {
    private String lowerText;

    Scanner(String text) {
        this.text = text;
        this.lowerText = text == null ? "" : text.toLowerCase();
        currentPosition = 0;
    }

    public boolean hasSymbols() {
        return currentPosition < text.length();
    }

    public void skipText(List<String> finishTags) {
        int tempPosition = currentPosition;
        while (tempPosition < text.length()) {
            int index = text.indexOf("<", tempPosition);
            if (index == -1) {
                tempPosition = text.length();
                break;
            }
            else if (text.startsWith("</", index)) {
                int tp = index + "</".length();
                while (tp < text.length())
                    if (Utils.isLetter(text.charAt(tp))) tp++;
                    else break;
                String name = text.substring(index + "</".length(), tp).toLowerCase();
                if (finishTags.contains(name)) {
                    tempPosition = index;
                    break;
                }
                else tempPosition = tp;
            }
            else if (text.startsWith("<!", index) || text.startsWith("<%", index) || isTagStart(text, index + 1)) {
                tempPosition = index;
                break;
            }
            else tempPosition = index + 1;
        }
        currentPosition = tempPosition;
    }

    public static boolean isTagStart(String text, int pos) {
        boolean result = false;
        while (pos < text.length()) {
            if (Character.isWhitespace(text.charAt(pos)) || text.charAt(pos) == '>' || text.charAt(pos) == '/')
                return result;
            else if (Utils.isLetter(text.charAt(pos))) {
                result = true;
                pos++;
            }
            else return false;
        }
        return result;
    }

    public void processHtmlComment() {
        int index = text.indexOf("-->", currentPosition);
        currentPosition = index < 0 ? text.length() : index + "-->".length();
    }

    public void processHtmlDirective() {
        int index = text.indexOf(">", currentPosition);
        currentPosition = index < 0 ? text.length() : index + ">".length();
    }

    public void processJspTag() {
        int index = text.indexOf("%>", currentPosition);
        currentPosition = index < 0 ? text.length() : index + "%>".length();
    }

    public void skipTagName() {
        while (currentPosition < text.length() && Utils.isLetter(text.charAt(currentPosition)))
            currentPosition++;
    }

    public String getNotQuotedAttribute() {
        int pos = currentPosition;
        int lastChar = text.length();
        int index = text.indexOf(">", currentPosition);
        if (index > 0) lastChar = index;
        index = text.indexOf("/>", currentPosition);
        if (index > 0 && index < lastChar) lastChar = index;
        for (int i = pos; i < lastChar; i++)
            if (Character.isWhitespace(text.charAt(i))) {
                lastChar = i;
                break;
            }
        currentPosition = lastChar;
        return text.substring(pos, currentPosition);
    }

    public int acceptStyleOrScript(String tagName) {
        int index = lowerText.indexOf("</" + tagName.toLowerCase(), currentPosition);
        if (index < 0) index = text.length();
        currentPosition = index;
        return index;
    }
}
