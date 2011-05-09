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

import com.granule.CachedBundle;
import com.granule.CompressorSettings;
import com.granule.FragmentDescriptor;
import com.granule.IRequestProxy;
import com.granule.JSCompileException;
import com.granule.json.JSONException;
import com.granule.logging.Logger;
import com.granule.logging.LoggerFactory;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * User: Dario Wunsch
 * Date: 12.09.2010
 * Time: 4:04:53
 */
public class MemoryCache extends TagCacheImpl {
    protected static TagCacheImpl instance = new MemoryCache();

    private MemoryCache() {
    	super();
    }

    /*
    * Singleton instance
    */
    public static TagCacheImpl getInstance() {
        return instance;
    }

    public String compressAndStore(IRequestProxy request, CompressorSettings settings,
                                   List<FragmentDescriptor> fragmentDescriptors, boolean isJs, String options) throws JSCompileException {

        String signature = generateSignature(settings, fragmentDescriptors, options, isJs);

        // Try to identify if it was already compiled
        String id;
        if (settings.getCache().equalsIgnoreCase("none"))
            id = null;
        else
            synchronized (this) {
                id = signatureToId.get(signature);
            }

        // If it wasn't then compress it
        if (id == null) {
            CachedBundle cs = new CachedBundle();
            cs.setFragments(fragmentDescriptors);
            cs.setOptions(options);
            if (isJs)
                cs.compileScript(settings, request);
            else
                cs.compileCss(settings, request);
            synchronized (this) {
                id = generateId(signature);
                signatureToId.put(signature, id);
                bundles.put(id, cs);
                try {
                    logger.debug(cs.getJSONString(id));
                } catch (JSONException e) {
                    //
                }
            }
        }
        return id;
    }

    public CachedBundle getCompiledBundle(IRequestProxy request, CompressorSettings settings, String id)
            throws JSCompileException {
        CachedBundle bundle = null;
        synchronized (this) {
            bundle = bundles.get(id);
        }
        if (bundle != null && settings.isCheckTimestamps()) {
            synchronized (bundle) {
                if (bundle.isChanged(request)) {
                    if (bundle.getOptions() != null)
                        settings.setOptions(bundle.getOptions());
                    bundle.compile(settings, request);
                }
            }
        }

        return bundle;
    }

    public void initForStandalone(String rootPath, CompressorSettings settings) {
        // no actions
    }

    public void initWeb(ServletContext context, CompressorSettings settings) {
        // no actions
    }

    private static final Logger logger = LoggerFactory.getLogger(MemoryCache.class);
}
