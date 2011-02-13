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
 * User: Dario Wünsch
 * Date: 09.12.2010
 * Time: 7:00:51
 */
public class Attribute {
    private com.granule.miniparser.Attribute attribute;

    Attribute(com.granule.miniparser.Attribute attribute) {
        this.attribute = attribute;
    }

    public int getBegin() {
        return attribute.getBegin();
    }

    public int getEnd() {
        return attribute.getEnd();
    }
}
