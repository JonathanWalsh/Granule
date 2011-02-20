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

import java.util.HashMap;

/**
 * User: Dario Wunsch
 * Date: 22.12.10
 * Time: 2:09
 */
public class Tag {
    private String name = "";
    private String content = "";
    private int begin;
    private int end;
    private int contentBegin;
    private int contentEnd;

    private HashMap<String, Attribute> attributes = new HashMap<String, Attribute>();

    public String getDebugString(String text) {
        StringBuilder sb = new StringBuilder();
        sb.append("Name=").append(name).append("\ncontent=").append(content).
                append("\nbegin=").append(begin).append(" end=").append(end).
                append("\ncontentBegin=").append(contentBegin).append(" contentEnd=").append(contentEnd).
                append("\nTag all=").append(text.substring(begin, end)).
                append("\nTag value=").append(text.substring(contentBegin, contentEnd));
        sb.append("\nAttributes");
        for (String key : attributes.keySet()) {
            sb.append(attributes.get(key).getDebugString(text)).append("\n");
        }
        return sb.toString();
    }

    public void addAttribute(Attribute a) {
        attributes.put(a.getName().toLowerCase(), a);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getContentBegin() {
        return contentBegin;
    }

    public void setContentBegin(int contentBegin) {
        this.contentBegin = contentBegin;
    }

    public int getContentEnd() {
        return contentEnd;
    }

    public void setContentEnd(int contentEnd) {
        this.contentEnd = contentEnd;
    }

    public HashMap<String, Attribute> getAttributes() {
        return attributes;
    }
}
