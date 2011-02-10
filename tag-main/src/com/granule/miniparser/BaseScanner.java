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

/**
 * User: Dario Wünsch
 * Date: 27.12.10
 * Time: 13:20
 */
public class BaseScanner {
    protected String text;

    protected int currentPosition = 0;

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void skipWhitespace() {
        while (currentPosition < text.length() && Character.isWhitespace(text.charAt(currentPosition)))
            currentPosition++;
    }

    public void acceptAttributeName() {
        if (currentPosition < text.length() && Character.isLetter(text.charAt(currentPosition)))
            currentPosition++;
        else return;
        while (currentPosition < text.length() && Character.isLetterOrDigit(text.charAt(currentPosition)))
            currentPosition++;
    }

    public String getQuotedAttribute(char symbol) {
        if (currentPosition < text.length() && text.charAt(currentPosition) == symbol)
            currentPosition++;
        else return "";
        int pos = currentPosition;
        int index = text.indexOf(symbol, currentPosition);
        if (index < 0) {
            currentPosition = text.length();
            return text.substring(pos);
        }
        else {
            currentPosition = index + 1;
            return text.substring(pos, currentPosition - 1);
        }
    }

    public boolean acceptString(String str) {
        if (currentPosition < text.length() && text.startsWith(str, currentPosition)) {
            currentPosition += str.length();
            return true;
        }
        return false;
    }
}
