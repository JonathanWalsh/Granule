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
 * Date: 22.12.10
 * Time: 2:09
 */
public class Attribute {
    private String name;
    private String value;
    private int begin;
    private int end;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public String getDebugString(String text) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nName=").append(name).append("\nValue=").append(value).
                append("\nBegin=").append(begin).append(" end=").append(end).
                append("\nText=").append(text.substring(begin, end));
        return sb.toString();
    }
}
