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

import java.util.List;

import javax.servlet.ServletContext;

import com.granule.CachedBundle;
import com.granule.CompressorSettings;
import com.granule.FragmentDescriptor;
import com.granule.IRequestProxy;
import com.granule.JSCompileException;

/**
 * User: Dario Wünsch
 * Date: 23.07.2010
 * Time: 3:30:43
 */
public interface TagCache {
    public String compressAndStore(IRequestProxy request, CompressorSettings settings, List<FragmentDescriptor> fragmentDescriptors, boolean isJs, String options) throws JSCompileException;

    public CachedBundle getCompiledBundle(IRequestProxy request, CompressorSettings settings, String id) throws JSCompileException;

    public void initForStandalone(String rootPath, CompressorSettings settings);

    public void initWeb(ServletContext context, CompressorSettings settings);
}
