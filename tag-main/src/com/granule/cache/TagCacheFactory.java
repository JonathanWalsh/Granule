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
package com.granule.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletContext;

import com.granule.CompressorSettings;
import com.granule.JSCompileException;
import com.granule.utils.Utf8Properties;

/**
 * User: Dario Wünsch
 * Date: 23.07.2010
 * Time: 3:34:11
 */
public class TagCacheFactory {

    private static TagCacheImpl instance = null;

    public static TagCache getInstance() throws JSCompileException {
        if (instance==null)
           throw new JSCompileException("Granule cache was loaded not on the server start-up. Recommended to add the parameter <load-on-startup>1<load-on-startup> into servlet configuration in web.xml.");
        return instance;
    }

    public static void init(ServletContext context) throws IOException {
        CompressorSettings settings = initInstance(context.getRealPath("/"));
        instance.init(context, settings);
    }

    private static CompressorSettings initInstance(String rootPath) throws IOException {
        CompressorSettings settings = getCompressorSettings(rootPath);
        if (settings.getCache().equals(CompressorSettings.DISK_VALUE))
            instance = FileCache.getInstance();
        else instance = MemoryCache.getInstance();
        return settings;
    }

    public static void init(String rootPath) throws IOException {
        initInstance(rootPath);
        instance.basicInit(rootPath, getCompressorSettings(rootPath));
    }

    protected static final Utf8Properties settings = new Utf8Properties();

    protected static boolean settingsLoaded = false;

    public static CompressorSettings getCompressorSettings(String rootPath) throws IOException {
        synchronized (settings) {
            if (!settingsLoaded) {
                loadSettings(rootPath, null, false);
            }
        }
        return new CompressorSettings(settings);
    }

    public static CompressorSettings getProductionCompressorSettings(String rootPath, HashMap<String, String> addition) throws IOException {
        synchronized (settings) {
            if (!settingsLoaded) {
                loadSettings(rootPath, addition, true);
            }
        }
        return new CompressorSettings(settings);
    }

    private static void loadSettings(String rootPath, HashMap<String, String> addition, boolean productionSettings) throws IOException {
        String realPath = rootPath + "/WEB-INF/granule.properties";
        String debugPath = rootPath + "/WEB-INF/granule.debug.properties";
        if (new File(realPath).exists())
            settings.load(new FileInputStream(realPath));
        if (!productionSettings && new File(debugPath).exists())
            settings.load(new FileInputStream(debugPath));
        if (addition!=null) {
            settings.putAll(addition);
        }
        settingsLoaded = true;
    }

}
