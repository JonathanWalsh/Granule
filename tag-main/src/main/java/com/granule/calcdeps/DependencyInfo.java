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
package com.granule.calcdeps;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.granule.FragmentDescriptor;

public class DependencyInfo {
    FragmentDescriptor script;
    private List<String> provides = new ArrayList<String>();
    private List<String> requires = new ArrayList<String>();

    public DependencyInfo(FragmentDescriptor script) {
        this.script = script;
    }

    public String toString() {
        StringBuilder prov = new StringBuilder();
        for (int i = 0; i < provides.size(); i++)
            prov.append(i != 0 ? "," : "").append(provides.get(i));
        StringBuilder req = new StringBuilder();
        for (int i = 0; i < requires.size(); i++)
            req.append(i != 0 ? "," : "").append(requires.get(i));
        return MessageFormat.format("{0} Provides: {1} Requires: {2}", script==null?"":script.toString(), prov.toString(), req.toString());
    }

    public List<String> getProvides() {
        return provides;
    }

    public List<String> getRequires() {
        return requires;
    }
}