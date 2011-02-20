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
package com.granule.parser;

/**
 * User: Dario Wunsch
 * Date: 09.12.2010
 * Time: 7:01:04
 */
public class Element {
    /*package*/
    com.granule.miniparser.Tag element;

    Element(com.granule.miniparser.Tag element) {
        this.element = element;
    }

    public String getName() {
        return element.getName();
    }

    public String getContentAsString() {
        return element.getContent().toString();
    }

    public Attributes getAttributes() {
        return new Attributes(element.getAttributes());
    }

    public int getBegin() {
        return element.getBegin();
    }

    public int getEnd() {
        return element.getEnd();
    }

    public int getContentBegin() {
        return element.getContentBegin();
    }

    public int getContentEnd() {
        return element.getContentEnd();
    }

    public boolean isContentExists() {
        return element.getContent() != null;
    }
}
