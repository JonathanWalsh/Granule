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

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.granule.cache.TagCache;
import com.granule.cache.TagCacheFactory;
import com.granule.calcdeps.CalcDeps;
import com.granule.logging.Logger;
import com.granule.logging.LoggerFactory;
import com.granule.parser.Attribute;
import com.granule.parser.Attributes;
import com.granule.parser.Element;
import com.granule.parser.TagReader;
import com.granule.parser.Tags;
import com.granule.utils.OptionsHandler;
import com.granule.utils.PathUtils;

/**
 * User: Dario Wunsch Date: 05.07.2010 Time: 3:43:47
 */
public class CompressTagHandler {

    @SuppressWarnings("unused")
    private String id = null;
    private String method = null;
    private String options = null;
    private String basepath = null;
    
    private static final String JS_DUPLICATES = "granule_js_duplicates";
    private static final String CSS_DUPLICATES = "granule_css_duplicates";
  
    public CompressTagHandler(String id, String method, String options, String basepath) {
        this.id = id;
        this.method = method;
        this.options = options;
        this.basepath = basepath;
    }

    public String handleTag(IRequestProxy requestProxy, IRequestProxy runtimRequest, String oldBody) throws JSCompileException {
        String newBody = oldBody;
        try {
            CompressorSettings settings = TagCacheFactory.getCompressorSettings(requestProxy.getRealPath("/"));
            String bp = basepath == null ? settings.getBasePath() : basepath;
            // JavaScript processing
            String opts = null;
            if (method != null)
                opts = CompressorSettings.JS_COMPRESS_METHOD_KEY + "=" + method + "\n";
            if (options != null) {
                String s = (new OptionsHandler()).handle(options, method == null ? settings.getJsCompressMethod() :
                        method);
                if (opts != null)
                    opts += s;
                else opts = s;
            }
            if (opts != null) {
                settings.setOptions(opts);
            }

            if (settings.isHandleJavascript()) {
            	List<Element> scripts = getScripts(newBody);
                if (scripts.size() > 0) {
                    //newBody=addDynamicJSContent(request,newBody,scripts);
                    int correction = 0;
                    List<FragmentDescriptor> fragmentDescriptors = new ArrayList<FragmentDescriptor>();
                    for (Element e : scripts) {
                        FragmentDescriptor desc = null;
                        boolean addFile = true;
                        Attributes attrs = e.getAttributes();
                        if (attrs.isValueExists("src")) {
                            String src = attrs.getValue("src");
                            if (PathUtils.isWebAddress(src) || !PathUtils.isValidJs(src))
                                throw new JSCompileException("Dynamic or remote scripts can not be combined. src="+src);

                            String path = PathUtils.calcPath
                                    (src, requestProxy, bp);
							File file = new File(requestProxy.getRealPath(path));
							//System.out.println("src="+src+"\npath="+path+"\nrealPath="+requestProxy.getRealPath(path)+"\ncontextPath="+requestProxy.getContextPath());
							if (settings.isIgnoreMissedFiles() && !file.exists()) {
                                addFile = false;
                                logger.warn(MessageFormat.format("File {0} not found, ignored. Real Path={1}",path,requestProxy.getRealPath(path)));
                            }
                            if (addFile) {
                                desc = new ExternalFragment(path);
                            }
                        } else {
                            desc = new InternalFragment(e.getContentAsString());
                        }
                        if (addFile)
                            fragmentDescriptors.add(desc);
                    }
                    if (settings.isCleanJsDuplicates()) {
                        HashSet<String> jsDuplicates = getJsDuplicatesHash(requestProxy);
                        if (jsDuplicates == null) {
                            jsDuplicates = new HashSet<String>();
                            requestProxy.setAttribute(JS_DUPLICATES, jsDuplicates);
                        }
                        try {
                            if (CalcDeps.searchClosureLibrary(fragmentDescriptors, requestProxy) < 0) {
                                int i = 0;
                                while (i < fragmentDescriptors.size()) {
                                    if (fragmentDescriptors.get(i) instanceof ExternalFragment && jsDuplicates
                                            .contains(PathUtils.clean(((ExternalFragment) fragmentDescriptors.get(i))
                                            .getFilePath()))) {
                                        fragmentDescriptors.remove(i);
                                    } else {
                                        if (fragmentDescriptors.get(i) instanceof ExternalFragment)
                                            jsDuplicates.add(PathUtils.clean(((ExternalFragment) fragmentDescriptors
                                                    .get(i)).getFilePath()));
                                        i++;
                                    }
                                }
                            }
                        } catch (IOException e) {
                            throw new JSCompileException(e);
                        }
                    }

                    String bundleId = null;

                    if (fragmentDescriptors.size() > 0) {
                        TagCache tagCache = TagCacheFactory.getInstance();
                        bundleId = tagCache.compressAndStore(requestProxy, settings, fragmentDescriptors, true, opts);
                    }
                    if (runtimRequest!=null) //generate output only in runtime
	                    for (int i = 0; i < scripts.size(); i++) {
	                        Element e = scripts.get(i);
	                        if (i != scripts.size() - 1) {
	                            newBody = newBody.substring(0, e.getBegin() + correction)
	                                    + newBody.substring(e.getEnd() + correction);
	                            correction -= e.getEnd() - e.getBegin();
	                        } else {
	                            if (fragmentDescriptors.size() > 0) {
	                                String newText = "<script type=\"text/javascript\" src=\"" + PathUtils.getContextURL(runtimRequest.getContextPath(),"/combined.js?id="
	                                        + bundleId) + "\"></script>";
	                                newBody = newBody.substring(0, e.getBegin() + correction) + newText
	                                        + newBody.substring(e.getEnd() + correction);
	                                correction -= e.getEnd() - e.getBegin();
	                                correction += newBody.length();
	                            } else {
	                                newBody = newBody.substring(0, e.getBegin() + correction)
	                                        + newBody.substring(e.getEnd() + correction);
	                                correction -= e.getEnd() - e.getBegin();
	                            }
	                        }
	                    }
                }
            } else
                newBody = oldBody;

            // CSS processing
            if (settings.isHandleCss()) {
                int start = 0;
                StringBuilder sb = new StringBuilder();
                List<Element> styles = getStyles(newBody);
                if (styles.size() > 0) {
                    //newBody=addDynamicCSSContent(request, newBody, styles);
                    for (Element style : styles) {
                        String before = newBody.substring(start, style.getBegin());
                        sb.append(processChunk(before, requestProxy, runtimRequest, settings));
                        if (style.isContentExists()
                                && settings.getCssCompressMethod()
                                .equalsIgnoreCase(CompressorSettings.CSSFASTMIN_VALUE)) {
                            sb.append(newBody.substring(style.getBegin(), style.getContentBegin()));
                            String s = newBody.substring(style.getContentBegin(), style.getContentEnd());
                            CSSFastMin min = new CSSFastMin();
                            s = min.minimize(s);
                            sb.append(s);
                            sb.append(newBody.substring(style.getContentEnd(), style.getEnd()));
                            start = style.getEnd();
                        } else {
                            sb.append(newBody.substring(style.getBegin(), style.getEnd()));
                        }
                    }
                }
                sb.append(processChunk(newBody.substring(start), requestProxy, runtimRequest, settings));
                newBody = sb.toString();
            }

            return newBody;
        } catch (IOException e) {
            throw new JSCompileException(e);
        }
    }

    /*
	private String addDynamicJSContent(IRequestProxy request, String content, List<Element> scripts) {
		StringBuilder sb=CompressTag.getJSContent(request);
		if (sb.length() > 0) {
			content=content+sb;
			scripts.clear();
			scripts.addAll(getScripts(content));
			sb.setLength(0);//content should be used once here
		}
		return content;
	}*/

    private List<Element> getScripts(String text) {
        TagReader source = new TagReader(text);
        return source.getAllElements(Tags.SCRIPT);
    }

    /*
     private String addDynamicCSSContent(IRequestProxy request, String content, List<Element> styles) {
         StringBuilder sb=CompressTag.getJSContent(request);
         if (sb.length() > 0) {
             content=content+sb;
             styles.clear();
             styles.addAll(getStyles(content));
             sb.setLength(0);//content should be used once here
         }
         return content;
     }*/

    private List<Element> getStyles(String text) {
        TagReader source = new TagReader(text);
        return source.getAllElements(Tags.STYLE);
    }

    private class MediaInfo {
        public List<FragmentDescriptor> fragments = new ArrayList<FragmentDescriptor>();
        public List<Integer> indexes = new ArrayList<Integer>();
        public String bundleId = null;
    }

    private class LinkInfo {
        public int index;
        public String scriptId = null;
    }

    private String processChunk(String chunk, IRequestProxy requestProxy, IRequestProxy runtimeRequest, CompressorSettings settings)
            throws JSCompileException {
        TagReader source = new TagReader(chunk);
        String bp = basepath == null ? settings.getBasePath() : basepath;
        List<Element> links = source.getAllElements(Tags.LINK);
        if (links.size() > 0) {
            HashSet<String> cssDuplicates = new HashSet<String>();
            List<Integer> eliminatedStyles = new ArrayList<Integer>();
            if (settings.isCleanCssDuplicates()) {
                cssDuplicates = getCssDuplicatesHash(requestProxy);
                if (cssDuplicates == null) {
                    cssDuplicates = new HashSet<String>();
                    requestProxy.setAttribute(CSS_DUPLICATES, cssDuplicates);
                }
            }
            HashMap<String, MediaInfo> mediae = new HashMap<String, MediaInfo>();
            for (int i = 0; i < links.size(); i++) {
                Element link = links.get(i);
                Attributes attrs = link.getAttributes();
                if (attrs.isValueExists("rel") && attrs.getValue("rel").equalsIgnoreCase("stylesheet")
                        && attrs.isValueExists("href")) {
                    String media = "";
                    if (attrs.isValueExists("media"))
                        media = attrs.getValue("media");
                    MediaInfo md = mediae.get(media);
                    if (md == null) {
                        md = new MediaInfo();
                        mediae.put(media, md);
                    }
                    String href = attrs.getValue("href");
                    if (PathUtils.isWebAddress(href) || !PathUtils.isValidCss(href))
                        throw new JSCompileException("Dynamic or remote stylesheets can not be combined. href="+href);
                    String path = PathUtils.calcPath(href, requestProxy, bp);
					if (settings.isCleanCssDuplicates()
                            && cssDuplicates.contains(media + path)) {
                        eliminatedStyles.add(i);
                    } else if (settings.isIgnoreMissedFiles() && !(new File(requestProxy.getRealPath(path))).exists()) {
                        eliminatedStyles.add(i);
                        logger.warn(MessageFormat.format("File {0} not found, ignored. Real Path={1}",path,requestProxy.getRealPath(path)));
                    } else {
                        FragmentDescriptor bd = new ExternalFragment(path);
                        md.fragments.add(bd);
                        md.indexes.add(i);
                        if (settings.isCleanCssDuplicates())
                            cssDuplicates.add(media + path);
                    }
                }
            }

            if (mediae.keySet().size() == 0)
                return chunk;
            for (String media : mediae.keySet()) {
                if (mediae.get(media).fragments.size() > 0) {
                    TagCache tagCache = TagCacheFactory.getInstance();
                    mediae.get(media).bundleId = tagCache.compressAndStore(requestProxy, settings,
                            mediae.get(media).fragments, false, null);
                }
            }
            List<LinkInfo> lst = new ArrayList<LinkInfo>();
            for (String media : mediae.keySet()) {
                if (mediae.get(media).fragments.size() > 0) {
                    for (int i = 0; i < mediae.get(media).fragments.size(); i++) {
                        LinkInfo ld = new LinkInfo();
                        ld.index = mediae.get(media).indexes.get(i);
                        if (i == mediae.get(media).fragments.size() - 1)
                            ld.scriptId = mediae.get(media).bundleId;
                        lst.add(ld);
                    }
                }
            }
            for (int i : eliminatedStyles) {
                LinkInfo ld = new LinkInfo();
                ld.index = i;
                lst.add(ld);
            }

            Collections.sort(lst, new Comparator<LinkInfo>() {
                public int compare(LinkInfo o1, LinkInfo o2) {
                    if (o1.index == o2.index)
                        return 0;
                    else if (o1.index < o2.index)
                        return -1;
                    else
                        return 1;
                }
            });
       
			StringBuilder sb = new StringBuilder();
			int start = 0;
			if (runtimeRequest != null)
				for (LinkInfo ld : lst) {
					int p = ld.index;
					sb.append(chunk.substring(start, links.get(p).getBegin()));
					if (ld.scriptId != null) {
						Attribute a = links.get(p).getAttributes().get("href");
						sb.append(chunk.substring(links.get(p).getBegin(), a
								.getBegin()));
						sb.append("href=\"");
						sb.append(PathUtils.getContextURL(runtimeRequest
								.getContextPath(), "/combined.css?id="));
						sb.append(ld.scriptId);
						sb.append("\" ");
						sb.append(chunk.substring(a.getEnd(), links.get(p)
								.getEnd()));
					}
					start = links.get(p).getEnd();
				}
			sb.append(chunk.substring(start));
            return sb.toString();
        } else
            return chunk;
    }

    @SuppressWarnings("unchecked")
    private HashSet<String> getJsDuplicatesHash(IRequestProxy request) {
        return (HashSet<String>) request.getAttribute(JS_DUPLICATES);
    }

    @SuppressWarnings("unchecked")
    private HashSet<String> getCssDuplicatesHash(IRequestProxy request) {
        return (HashSet<String>) request.getAttribute(CSS_DUPLICATES);
    }
    
    private static final Logger logger = LoggerFactory.getLogger(CompressTagHandler.class);

}
