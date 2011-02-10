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

import java.util.ArrayList;
import java.util.List;

/**
 * User: Dario W?nsch
 * Date: 22.12.10
 * Time: 2:10
 */
public class Parser {
    private String text;
    private String lowerText;

    public Parser(String text) {
        this.text = text;
        this.lowerText = text == null ? "" : text.toLowerCase();
    }

    public boolean parse(Source s) {
        if (text == null || text.length() == 0)
            return true;
        Scanner scanner = new Scanner(text);
        parseTagContent(s, scanner, new ArrayList<String>());
        return true;
    }

    private void parseTagContent(Source s, Scanner scanner, List<String> finishTags) {
        while (scanner.hasSymbols()) {
            scanner.skipText(finishTags);
            if (scanner.hasSymbols()) {
                if (text.startsWith("<!--", scanner.getCurrentPosition()))
                    processHtmlComment(s, scanner);
                else if (text.startsWith("<!", scanner.getCurrentPosition()))
                    processHtmlDirective(s, scanner);
                else if (text.startsWith("<%", scanner.getCurrentPosition()))
                    processJspTag(s, scanner);
                else if (text.startsWith("</", scanner.getCurrentPosition()))
                    return;
                else processTag(s, scanner, finishTags);
            }
        }
    }

    private void processHtmlComment(Source s, Scanner scanner) {
        Tag t = new Tag();
        t.setName("!--");
        t.setContent("");
        t.setBegin(scanner.getCurrentPosition());
        t.setEnd(scanner.getCurrentPosition());
        scanner.processHtmlComment();
        t.setEnd(scanner.getCurrentPosition());
        s.getTags().add(t);
    }

    private void processHtmlDirective(Source s, Scanner scanner) {
        Tag t = new Tag();
        t.setName("!");
        t.setContent("");
        t.setBegin(scanner.getCurrentPosition());
        t.setEnd(scanner.getCurrentPosition());
        scanner.processHtmlDirective();
        t.setEnd(scanner.getCurrentPosition());
        s.getTags().add(t);
    }

    private void processJspTag(Source s, Scanner scanner) {
        Tag t = new Tag();
        t.setName("%");
        t.setContent("");
        t.setBegin(scanner.getCurrentPosition());
        t.setEnd(scanner.getCurrentPosition());
        scanner.processJspTag();
        t.setEnd(scanner.getCurrentPosition());
        s.getTags().add(t);
    }

    private void processTag(Source s, Scanner scanner, List<String> finishTags) {
        Tag t = new Tag();
        s.getTags().add(t);
        t.setBegin(scanner.getCurrentPosition());
        t.setEnd(scanner.getCurrentPosition());
        scanner.acceptString("<");
        scanner.skipTagName();
        t.setName(text.substring(t.getBegin() + 1, scanner.getCurrentPosition()));
        scanner.skipWhitespace();
        //attributes
        while (scanner.getCurrentPosition() < text.length() && Character.isLetter(text.charAt(scanner.getCurrentPosition()))) {
            int pos = scanner.getCurrentPosition();
            Attribute a = new Attribute();
            a.setBegin(pos);
            scanner.acceptAttributeName();
            a.setEnd(scanner.getCurrentPosition());
            a.setName(text.substring(a.getBegin(), a.getEnd()));
            scanner.skipWhitespace();
            if (scanner.getCurrentPosition() < text.length() && text.charAt(scanner.getCurrentPosition()) == '=') {
                scanner.acceptString("=");
                scanner.skipWhitespace();
                if (scanner.getCurrentPosition() < text.length()) {
                    String value = "";
                    if (text.charAt(scanner.getCurrentPosition()) == '"' || text.charAt(scanner.getCurrentPosition()) == '\'')
                        value = scanner.getQuotedAttribute(text.charAt(scanner.getCurrentPosition()));
                    else value = scanner.getNotQuotedAttribute();
                    a.setValue(value);
                    a.setEnd(scanner.getCurrentPosition());
                }
            }
            t.addAttribute(a);
            scanner.skipWhitespace();
        }
        t.setEnd(scanner.getCurrentPosition());
        if (scanner.getCurrentPosition() < text.length()) {
            if (text.startsWith("/>", scanner.getCurrentPosition())) {
                scanner.acceptString("/>");
                t.setEnd(scanner.getCurrentPosition());
            } else if (text.startsWith(">", scanner.getCurrentPosition())) {
                scanner.acceptString(">");
                t.setContentBegin(scanner.getCurrentPosition());
                t.setEnd(scanner.getCurrentPosition());
                if (t.getName() != null && (t.getName().equalsIgnoreCase("script") || t.getName().equalsIgnoreCase("style"))) {
                    int index = scanner.acceptStyleOrScript(t.getName());
                    t.setContentEnd(index);
                    t.setContent(text.substring(t.getContentBegin(), t.getContentEnd()));
                    scanner.acceptString("</");
                    scanner.skipTagName();
                    scanner.skipWhitespace();
                    scanner.acceptString(">");
                    t.setEnd(scanner.getCurrentPosition());
                } else {
                    List<String> tempList = new ArrayList<String>();
                    tempList.addAll(finishTags);
                    tempList.add(t.getName().toLowerCase());
                    parseTagContent(s, scanner, tempList);
                    t.setContentEnd(scanner.getCurrentPosition());
                    scanner.skipWhitespace();
                    try {
                        t.setContent(text.substring(t.getContentBegin(), t.getContentEnd()));
                    } catch (Exception e) {
                        //
                    }

                    if (lowerText.startsWith("</" + t.getName().toLowerCase(), scanner.getCurrentPosition())) {
                        scanner.acceptString("</");
                        scanner.skipTagName();
                        scanner.skipWhitespace();
                        scanner.acceptString(">");
                        t.setEnd(scanner.getCurrentPosition());
                    }
                }
            }
        }
    }
}
