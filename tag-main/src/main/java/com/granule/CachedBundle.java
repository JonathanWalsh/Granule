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
package com.granule;

import com.granule.calcdeps.CalcDeps;
import com.granule.json.JSONArray;
import com.granule.json.JSONException;
import com.granule.json.JSONObject;
import com.granule.logging.Logger;
import com.granule.logging.LoggerFactory;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * User: Dario Wunsch
 * Date: 23.06.2010
 * Time: 2:00:08
 */
public class CachedBundle {
    private long modifyDate = 0;
    private List<FragmentDescriptor> fragments;
    private List<FragmentDescriptor> dependentFragments = new ArrayList<FragmentDescriptor>();
    private byte[] bundleValue;
    private String mimeType = JAVASCRIPT_MIME;
    private String options = null;
    private static final String JAVASCRIPT_MIME = "application/x-javascript";
    private static final String CSS_MIME = "text/css";
    private String hash;

    private static final long ZIP_ERROR_COMPENSATION = 10*1000;//10 seconds 
   
    public byte[] getBundleValue() {
        return bundleValue;
    }

    public void setFragments(List<FragmentDescriptor> fragments) {
        this.fragments = fragments;
    }

    public List<FragmentDescriptor> getFragments() {
        return fragments;
    }

    public long calcModifyDate(IRequestProxy request) {
        return calcModifyDate(fragments, dependentFragments, request);
    }

    protected static long calcModifyDate(List<FragmentDescriptor> fragments, List<FragmentDescriptor> deps,
                                      IRequestProxy request) {
        long d = 0;
        if (fragments != null)
            for (FragmentDescriptor fd : fragments) {
                if (fd instanceof ExternalFragment) {
                    File f = new File(request.getRealPath(((ExternalFragment) fd).getFilePath()));
                    if (f.lastModified() > d) d = f.lastModified();
                }
            }
        if (deps != null)
            for (FragmentDescriptor fd : deps) {
                if (fd instanceof ExternalFragment) {
                    File f = new File(request.getRealPath(((ExternalFragment) fd).getFilePath()));
                    if (f.lastModified() > d) d = f.lastModified();
                }
            }
        return d;
    }

    public void compileScript(CompressorSettings settings, IRequestProxy request) throws JSCompileException {
        logger.debug("Start compile javascript");
        mimeType = JAVASCRIPT_MIME;
        CalcDeps cd = new CalcDeps();
        String text = "";
        List<FragmentDescriptor> list = cd.calcDeps(fragments, request, settings.getClosurePathes());
        boolean isGoogleClosurePresent = !list.equals(fragments);
        if (isGoogleClosurePresent)
            list.add(0, new InternalFragment("CLOSURE_NO_DEPS=true;\n"));
        if (settings.getJsCompressMethod().equals(CompressorSettings.CLOSURE_COMPILER_VALUE))
            text = Compressor.compile(list, request, settings);
        else if (settings.getJsCompressMethod().equals(CompressorSettings.JSFASTMIN_VALUE))
            text = Compressor.minifyJs(list, settings, request);
        else if (settings.getJsCompressMethod().equalsIgnoreCase(CompressorSettings.AUTO_VALUE)) {
            if (isGoogleClosurePresent) text = Compressor.compile(list, request, settings);
            else text = Compressor.minifyJs(list, settings, request);
        } else text = Compressor.unify(list, request);
        if (isGoogleClosurePresent)
            list.remove(0);
        dependentFragments.clear();
        HashSet<String> hash = new HashSet<String>();
        for (FragmentDescriptor fd : fragments)
            if (fd instanceof ExternalFragment) hash.add(((ExternalFragment) fd).getFilePath());
        for (FragmentDescriptor dep : list)
            if (dep instanceof ExternalFragment && !hash.contains("/" + ((ExternalFragment) dep).getFilePath()))
                dependentFragments.add(dep);
        try {
            bundleValue = gzip(text);
            this.hash = DigestUtils.md5Hex(bundleValue);
        } catch (IOException e) {
            throw new JSCompileException(e);
        }

        this.modifyDate=calcModifyDate(request);
    }

    public void compile(CompressorSettings settings, IRequestProxy request) throws JSCompileException {
        dependentFragments.clear();
        if (mimeType != null && mimeType.equals(JAVASCRIPT_MIME))
            compileScript(settings, request);
        else compileCss(settings, request);
    }

    public void compileCss(CompressorSettings settings, IRequestProxy request) throws JSCompileException {
        logger.debug("Start compile css");
        mimeType = CSS_MIME;
        String text = "";
        if (settings.getCssCompressMethod().equals(CompressorSettings.CSSFASTMIN_VALUE))
            text = Compressor.minifyCss(fragments, dependentFragments, settings, request);
        else
            text = Compressor.unifyCss(fragments, dependentFragments, settings, request);

        try {
            bundleValue = gzip(text);
            hash = DigestUtils.md5Hex(bundleValue);
        } catch (IOException e) {
            throw new JSCompileException(e);
        }
        this.modifyDate=calcModifyDate(request);
    }

    private static byte[] gzip(String text) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gzipStream = new GZIPOutputStream(bos);
        gzipStream.write(text.getBytes("UTF-8"));
        gzipStream.close();
        return bos.toByteArray();
    }

    public void getUncompressedScript(OutputStream out) throws IOException {
        final int BLOCKSIZE = 8192;
        byte[] buffer = new byte[BLOCKSIZE];
        GZIPInputStream gzis = new GZIPInputStream(new ByteArrayInputStream(bundleValue));
        for (int length; (length = gzis.read(buffer, 0, BLOCKSIZE)) != -1;)
            out.write(buffer, 0, length);
        gzis.close();
    }

    public boolean isChanged(IRequestProxy request) {
        long d = calcModifyDate(fragments, dependentFragments, request);
        return d > modifyDate+ZIP_ERROR_COMPENSATION;
    }

    public String getMimeType() {
        return mimeType;
    }

    public List<FragmentDescriptor> getDependentFragments() {
        return dependentFragments;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getJSONString(String id) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("mime-type", mimeType);
        if (options != null)
            obj.put("options", options);
        if (fragments.size() > 0) {
            JSONArray array = new JSONArray();
            for (FragmentDescriptor fd : fragments) {
                JSONObject o = new JSONObject();
                o.put("type", fd instanceof ExternalFragment ? "file" : "script");
                if (fd instanceof ExternalFragment) o.put("file", ((ExternalFragment) fd).getFilePath());
                else o.put("text", ((InternalFragment) fd).getText());
                array.put(o);
            }
            obj.put("fragments", array);
        }
        if (dependentFragments != null && dependentFragments.size() > 0) {
            JSONArray array = new JSONArray();
            for (FragmentDescriptor fd : dependentFragments) {
                JSONObject o = new JSONObject();
                o.put("file", fd.toString());
                array.put(o);
            }
            obj.put("dependency", array);
        }
        return obj.toString() + "\n";
    }

    public void loadFromJSON(JSONObject obj, String cacheFolder) throws JSONException, IOException {
        mimeType = obj.getString("mime-type");
        if (obj.has("options"))
            options = obj.getString("options");
        else options = null;
        fragments = new ArrayList<FragmentDescriptor>();
        if (obj.has("fragments")) {
            JSONArray array = obj.getJSONArray("fragments");
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = (JSONObject) array.get(i);
                FragmentDescriptor fd;
                if (o.getString("type").equals("file")) {
                    fd = new ExternalFragment(o.getString("file"));
                } else {
                    fd = new InternalFragment(o.getString("text"));
                }
                fragments.add(fd);
            }
        }
        dependentFragments.clear();
        if (obj.has("dependency")) {
            JSONArray array = obj.getJSONArray("dependency");
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = (JSONObject) array.get(i);
                FragmentDescriptor fd = new ExternalFragment(o.getString("file"));
                dependentFragments.add(fd);
            }
        }

        String filename = cacheFolder + "/" + obj.getString("id") + ".gzip." + (isScript() ? "js" : "css");
        File file = new File(filename);
        this.modifyDate = file.lastModified();
        InputStream is = new FileInputStream(file);
        try {
            long length = file.length();
            bundleValue = new byte[(int) length];
            int offset = 0;
            int numRead = 0;
            while (offset < bundleValue.length
                    && (numRead = is.read(bundleValue, offset, bundleValue.length - offset)) >= 0) {
                offset += numRead;
            }
            // Ensure all the bytes have been read in
            if (offset < bundleValue.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }
        } finally {
            is.close();
        }
    }

    public boolean isScript() {
        return mimeType != null && mimeType.equalsIgnoreCase(JAVASCRIPT_MIME);
    }

    public long getModifyDate() {
        return modifyDate;
    }
    public String getHash(){
        return hash;
    }

    public void setModifyDate(long modifyDate) {
        this.modifyDate=modifyDate;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(CachedBundle.class);
}
