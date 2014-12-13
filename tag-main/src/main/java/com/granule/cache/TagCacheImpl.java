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

import java.util.HashMap;
import java.util.List;

/**
 * User: Dario Wunsch Date: 23.07.2010 Time: 3:31:12
 */
public abstract class TagCacheImpl implements TagCache {

	// Map script signature to script id
    protected HashMap<String, String> signatureToId = new HashMap<String, String>();

	// Map id to bundle
    protected HashMap<String, CachedBundle> bundles = new HashMap<String, CachedBundle>();

	protected TagCacheImpl() {
	}

	protected String generateId(String hash) {
		String pureId=hash;
		return pureId;
	}
	
	protected String generateSignature(CompressorSettings settings, List<FragmentDescriptor> fragmentDescriptors,
                                       String options, boolean isJs) {
        StringBuilder all = new StringBuilder();
        for (FragmentDescriptor sd : fragmentDescriptors)
            all.append(sd.toString()).append("\n");
        if (isJs) {
            all.append(settings.getJsCompressMethod());
            if (options != null)
                all.append(options);
        } else all.append(settings.getCssCompressMethod());
        String signature = all.toString();
        return signature;
    }
}
