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
package com.granule.ant;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Option;

/**
 * User: Dario Wunsch
 * Date: 25.09.2010
 * Time: 1:00:19
 */
public class BuildCacheOptions {
    @Option(name = "--rootpath", aliases = "-r", usage = "Description here")
    private String rootpath;

    @Option(name = "--outputpath", aliases = "-o", usage = "Description here")
    private String outputpath;

    @Option(name = "--include", aliases = "-i", usage = "Description here")
    private List<String> include = new ArrayList<String>();

    @Option(name = "--exclude", aliases = "-e", usage = "Description here")
    private List<String> exclude = new ArrayList<String>();

    public String getRootpath() {
        return rootpath;
    }

    public String getOutputpath() {
        return outputpath;
    }

    public List<String> getInclude() {
        return include;
    }

    public List<String> getExclude() {
        return exclude;
    }
}
