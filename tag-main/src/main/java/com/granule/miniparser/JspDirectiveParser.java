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

public class JspDirectiveParser {
    private Tag tag;
    private String text;

    public JspDirectiveParser(Tag tag, String text) {
        this.tag = tag;
        this.text = text.substring(tag.getBegin(), tag.getEnd());
    }

    public void parse() {
        if (text.startsWith("<%@")) {
             Scanner scanner = new Scanner(text);
            scanner.acceptString("<%@");
            scanner.skipWhitespace();

            while (scanner.getCurrentPosition() < text.length() && Character.isLetter(text.charAt(scanner.getCurrentPosition()))) {
                int pos = scanner.getCurrentPosition();
                Attribute a = new Attribute();
                a.setBegin(pos + tag.getBegin());
                scanner.acceptAttributeName();
                a.setEnd(scanner.getCurrentPosition() + tag.getBegin());
                a.setName(text.substring(a.getBegin() - tag.getBegin(), a.getEnd() - tag.getBegin()));
                scanner.skipWhitespace();
                if (scanner.getCurrentPosition() < text.length() && text.charAt(scanner.getCurrentPosition()) == '=') {
                    scanner.acceptString("=");
                    scanner.skipWhitespace();
                    if (scanner.getCurrentPosition() < text.length()) {
                        String value = "";
                        if (text.charAt(scanner.getCurrentPosition()) == '"' || text.charAt(scanner.getCurrentPosition()) == '\'')
                            value = scanner.getQuotedAttribute(text.charAt(scanner.getCurrentPosition()));
                        a.setValue(value);
                        a.setEnd(scanner.getCurrentPosition() + tag.getBegin());
                    }
                }
                tag.addAttribute(a);
                scanner.skipWhitespace();
            }
        }
    }

    private class Scanner extends BaseScanner {
        Scanner(String text) {
            this.text = text;
            currentPosition = 0;
        }
    }
}
