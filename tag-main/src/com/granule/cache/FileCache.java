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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.ServletContext;

import com.granule.CachedBundle;
import com.granule.CompressorSettings;
import com.granule.FragmentDescriptor;
import com.granule.IRequestProxy;
import com.granule.JSCompileException;
import com.granule.json.JSONException;
import com.granule.json.JSONObject;
import com.granule.logging.Logger;
import com.granule.logging.LoggerFactory;
import com.granule.utils.PathUtils;

/**
 * User: Dario Wunsch Date: 12.09.2010 Time: 4:05:16
 */
public class FileCache extends TagCacheImpl {

	/*
	 * Default pathname (it is abstract name reference to context path of web
	 * app) Started from . to mark path as not absolute in the Unix File Systems
	 */
	private static final String DEFAULT_CACHE_FOLDER = "./granulecache";
	private static final String CATALOG_JSON = "catalog.json";

	protected static TagCacheImpl instance = new FileCache();

	private String cacheFolder;
	private BufferedOutputStream catalog;

	private FileCache() {
		super();
	}

	/*
	 * Singleton instance
	 */

	public static TagCacheImpl getInstance() {
		return instance;
	}

	public String compressAndStore(IRequestProxy request,
			CompressorSettings settings,
			List<FragmentDescriptor> fragmentDescriptors, boolean isJs,
			String options) throws JSCompileException {

		logger.debug("FileCache compressAndStore");

		String signature = generateSignature(settings, fragmentDescriptors,
				options, isJs);

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

				saveBundle(id, cs);
			}
		}
		return id;
	}

	private void saveBundle(String id, CachedBundle cs) {
		try {
			String filename = "/" + id + ".gzip."
					+ (cs.isScript() ? "js" : "css");

			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(cacheFolder + filename));
			try {
				bos.write(cs.getBundleValue());
			} finally {
				bos.close();
			}
			//File f = new File(cacheFolder + filename);
			//f.setLastModified(System.currentTimeMillis());// cs.getModifyDate());

			String json = cs.getJSONString(id);
			catalog.write(json.getBytes("UTF-8"));
			catalog.flush();
		} catch (Exception e) {
			logger.error("Could not save bundle:", e);
		}
	}

	public CachedBundle getCompiledBundle(IRequestProxy request,
			CompressorSettings settings, String id) throws JSCompileException {
		logger.debug("FileCache getCompiledBundle");
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
					synchronized (this) {
						saveBundle(id, bundle);
					}
				}
			}
		}
		return bundle;
	}

	private boolean readCache(String catalog, ServletContext context)
			throws JSONException, IOException, JSCompileException {
		BufferedReader reader = new BufferedReader(new FileReader(catalog));
		boolean needRebuildCache;
		try {
			String line;
			needRebuildCache = false;
			while ((line = reader.readLine()) != null) {
				try {
					JSONObject obj = new JSONObject(line);
					String id = obj.getString("id");
					if (bundles.containsKey(id)) {
						logger.warn("Rebuilding from dublicate id");
						needRebuildCache = true;
					}
					CachedBundle cs = new CachedBundle();
					cs.loadFromJSON(obj, cacheFolder);
					CompressorSettings settings = TagCacheFactory
							.getCompressorSettings(context.getRealPath("/"));
					if (cs.getOptions() != null)
						settings.setOptions(cs.getOptions());
					String signature = generateSignature(settings, cs
							.getFragments(), cs.getOptions(), cs.isScript());
					signatureToId.put(signature, id);
					bundles.put(id, cs);
				} catch (Exception e) {
					needRebuildCache = true;
					logger.error("Could not load bundle from catalog:", e);
				}
			}
		} finally {
			reader.close();
		}
		return needRebuildCache;
	}

	private static String expandEnvironmentStrings(String str) {
		StringBuilder sb = new StringBuilder();
		int start = 0;
		while (start < str.length()) {
			int startIndex = str.indexOf('%', start);
			if (startIndex < 0)
				break;
			else {
				int endIndex = str.indexOf('%', startIndex + 1);
				if (endIndex < 0)
					break;
				else {
					sb.append(str.substring(start, startIndex));
					if (startIndex + 1 == endIndex)
						sb.append("%");
					else {
						String varValue = System.getenv(str.substring(
								startIndex + 1, endIndex));
						sb.append(varValue == null ? "" : varValue);
					}
					start = endIndex + 1;
				}
			}
		}
		if (start < str.length())
			sb.append(str.substring(start, str.length()));
		return sb.toString();
	}

	private void rebuildCache(String catalogFilename) throws IOException,
			JSONException {
		BufferedOutputStream cat = new BufferedOutputStream(
				new FileOutputStream(catalogFilename));
		try {
			for (String id : bundles.keySet()) {
				String json = bundles.get(id).getJSONString(id);
				cat.write(json.getBytes("UTF-8"));
			}
		} finally {
			cat.close();
		}
	}

	private int deleteUnusedBundleFiles() {
		File f = new File(cacheFolder);
		String[] files = f.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if ((new File(dir.getAbsolutePath() + "/" + name).isDirectory()))
					return false;
				return (name.endsWith(".js") || name.endsWith(".css"));
			}
		});
		int deleted = 0;
		try {
			for (String s : files) {
				if (!bundles.containsKey(s.substring(0, s.indexOf(".")))) {
					(new File(cacheFolder + "/" + s)).delete();
					deleted++;
				}
			}
		} catch (Exception e) {
			logger.error("Error while deleting files from folder "
					+ cacheFolder, e);
		}
		return deleted;
	}

	public void initForStandalone(String rootPath, CompressorSettings settings) {
		cacheFolder = calculateCacheLocation(settings, rootPath);
		emptyCacheFolder();
		String catalogFilename = getCacheCatalogFilePath();
		openCatalogFile(catalogFilename);
	}

	private void emptyCacheFolder() {
		// Delete all files in cacheFolder
		File[] files = (new File(cacheFolder)).listFiles();
		if (files != null)
			for (File f : files)
				f.delete();
	}

	public void initWeb(ServletContext context, CompressorSettings settings) {
		logger.debug("FileCache init");
		String rootPath = context.getRealPath("/");

		cacheFolder = calculateCacheLocation(settings, rootPath);

		String catalogFilename = getCacheCatalogFilePath();
		if ((new File(catalogFilename)).exists())
			try {
				boolean needRebuildCache = readCache(catalogFilename, context);
				if (needRebuildCache) {
					emptyCacheFolder();
					rebuildCache(catalogFilename);
				} else {
					if (deleteUnusedBundleFiles() > 0)
						rebuildCache(catalogFilename);
				}
			} catch (Exception e) {
				logger.error("Error while reading cache catalog", e);
			}

		openCatalogFile(catalogFilename);
	}

	private void openCatalogFile(String catalogFilename) {
		try {
			File f = new File(cacheFolder);
			if (!f.exists())
				f.mkdirs();
			catalog = new BufferedOutputStream(new FileOutputStream(
					catalogFilename, true));
		} catch (Exception e) {
			logger.error("Error while opening catalog for writing", e);
		}
	}

	private String calculateCacheLocation(CompressorSettings settings,
			String rootPath) {
		String cacheFolder;
		if (settings.getCacheFileLocation() == null)
			cacheFolder = DEFAULT_CACHE_FOLDER;
		else
			cacheFolder = settings.getCacheFileLocation();
		cacheFolder = expandEnvironmentStrings(cacheFolder);
		if (!(new File(cacheFolder).isAbsolute()))
			cacheFolder = rootPath + "/" + cacheFolder;
		cacheFolder = PathUtils.clean(cacheFolder);
		logger.info(MessageFormat.format("Granule FileCache location is {0}",
				cacheFolder));
		return cacheFolder;
	}

	private String getCacheCatalogFilePath() {
		return cacheFolder + "/"+CATALOG_JSON;
	}

	private static final Logger logger = LoggerFactory
			.getLogger(FileCache.class);
}
