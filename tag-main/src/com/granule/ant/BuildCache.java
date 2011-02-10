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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.granule.utils.PathUtils;

/**
 * User: Dario Wünsch
 * Date: 25.09.2010
 * Time: 0:57:20
 */
public class BuildCache {
    public static void main(String[] args) {
        BuildCacheOptions options = new BuildCacheOptions();
        CmdLineParser parser = new CmdLineParser(options);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java -jar granule.jar [options...] arguments...");
            parser.printUsage(System.err);
            return;
        }
        String rootdir = options.getRootpath();
        if (rootdir == null) rootdir = ".";
        File f = new File(rootdir);
        rootdir = f.getAbsolutePath();
        if (!f.exists()) {
            System.err.println("Root web-app path not found " + rootdir);
            System.exit(5);
        }
        String outputpath = options.getOutputpath();
        final List<String> includes = options.getInclude();
        final List<String> excludes = options.getExclude();
        final String r = rootdir;

        List<String> files = listFiles(new File(rootdir), new FilenameFilter() {
            public boolean accept(File dir, String name) {
                boolean result = false;
                for (String i : includes)
                    if (WildcardMatcher.match(PathUtils.getRelpath(dir.getAbsolutePath() + "/" + name, r), i)) {
                        result = true;
                        break;
                    }
                if (!result) return result;
                for (String e : excludes)
                    if (WildcardMatcher.match(PathUtils.getRelpath(dir.getAbsolutePath() + "/" + name, r), e))
                        return false;
                return result;
            }
        });
        JspProcessor jspProcessor = new JspProcessor();
        int errorCount = 0;
        try {
            errorCount = jspProcessor.generateCache(files, rootdir, outputpath);
        } catch (IOException e) {
            e.printStackTrace();
            errorCount++;
        }
        if (errorCount>0)
            System.out.println("Build cache errors!!!"+errorCount);
    }

    private static List<String> listFiles(File directory, FilenameFilter filter) {
        ArrayList<String> files = new ArrayList<String>();
        File[] entries = directory.listFiles();
        if (entries != null) {
            for (File entry : entries) {
                if (filter == null || filter.accept(directory, entry.getName())) {
                    files.add(entry.getAbsolutePath());
                }

                if (entry.isDirectory()) {
                    files.addAll(listFiles(entry, filter));
                }
            }
        }
        return files;
    }
}
