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

import java.util.HashMap;

/**
 * User: Dario Wunsch
 * Date: 09.12.2010
 * Time: 7:00:43
 */
public class Attributes {

    private HashMap<String, com.granule.miniparser.Attribute> attributes;

    Attributes(HashMap<String, com.granule.miniparser.Attribute> attributes) {
        this.attributes = attributes;
    }

    public String getValue(String name) {
        if (name!=null) name = name.toLowerCase();
        return attributes.get(name)==null?null:attributes.get(name).getValue();
    }

    public boolean isValueExists(String name) {
        if (name != null) name = name.toLowerCase();
        return attributes.get(name) != null;
    }

    public Attribute get(String name) {
        if (name != null) name = name.toLowerCase();
        return new Attribute(attributes.get(name));
    }
}
