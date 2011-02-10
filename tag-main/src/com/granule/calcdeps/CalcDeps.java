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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import com.granule.AbsolutePathFragment;
import com.granule.ExternalFragment;
import com.granule.FragmentDescriptor;
import com.granule.IRequestProxy;
import com.granule.JSCompileException;
import com.granule.utils.PathUtils;

/**
 * User: Dario Wünsch Date: 24.06.2010 Time: 3:18:19
 */
public class CalcDeps {

    private static final String NAMESPACE_LIST = "closure_namespaces";

    public List<FragmentDescriptor> calcDeps(List<FragmentDescriptor> list, IRequestProxy request,
                                             List<String> pathes)
            throws JSCompileException {
        try {
            int baseFileIndex = searchClosureLibrary(list, request);
            if (baseFileIndex == -1) //bypass if there is no need to calculate dependencies
                return list;
            
            List<FragmentDescriptor> inputFragments = new ArrayList<FragmentDescriptor>();
            for (FragmentDescriptor sd : list)
                inputFragments.add(sd.clone());
            
            //Put base descriptor on first place
            FragmentDescriptor baseDescriptor = inputFragments.get(baseFileIndex);
            inputFragments.remove(baseFileIndex);
            inputFragments.add(0, baseDescriptor);
            
            calcRealPathes(inputFragments, request);
            
            //Calculate the set of all might needed js scripts for compiling
            String jsPath = (new File(request.getRealPath(((ExternalFragment) baseDescriptor).getFilePath()))).getParentFile()
                    .getParentFile().getAbsolutePath();
            List<String> closureScripts = expandDirectories(Arrays.asList(jsPath));
            List<String> realPathes = new ArrayList<String>();
            for (String path : pathes) {
                path = request.getRealPath(path);
                realPathes.add(path);
            }
            List<String> addPathes = expandDirectories(realPathes);
            closureScripts.addAll(addPathes);
            List<FragmentDescriptor> dependableFragments = new ArrayList<FragmentDescriptor>();
            for (String s : closureScripts)
                dependableFragments.add(new AbsolutePathFragment(PathUtils.clean(s)));
            
            //Calculate only what is needed
            List<FragmentDescriptor> depsResult = calculateDependencies(inputFragments, dependableFragments, request);
            
            calcRelativePathes(depsResult, request);
            
            return depsResult;
        } catch (Exception e) {
            throw new JSCompileException(e);
        }
    }

    private void calcRealPathes(List<FragmentDescriptor> list, IRequestProxy request) {
        for (int i = 0; i < list.size(); i++) {
            FragmentDescriptor fd = list.get(i);
            if (fd instanceof ExternalFragment) {
                String filename = ((ExternalFragment) fd).getFilePath();
                filename = PathUtils.clean(request.getRealPath(filename));
                AbsolutePathFragment newFragment = new AbsolutePathFragment(filename);
                list.remove(i);
                list.add(i, newFragment);
            }
        }
    }

    private void calcRelativePathes(List<FragmentDescriptor> list, IRequestProxy request) {
        String root = request.getRealPath("./");
        if (root.replace('\\','/').endsWith("/."))
           root = root.substring(0, root.length()-2);
        root = PathUtils.clean(root);
        for (int i = 0; i < list.size(); i++) {
            FragmentDescriptor fd = list.get(i);
            if (fd instanceof AbsolutePathFragment) {
                String filename = ((AbsolutePathFragment) fd).getFilePath();
                filename = PathUtils.getRelpath(filename, root);
                ExternalFragment newFragment = new ExternalFragment(filename);
                list.remove(i);
                list.add(i, newFragment);
            }
        }
    }

	public static int searchClosureLibrary(List<FragmentDescriptor> list, IRequestProxy request) throws IOException {
		for (FragmentDescriptor sd : list) {
			if (sd instanceof ExternalFragment && ((ExternalFragment) sd).getFilePath().endsWith("base.js")) {
				BufferedReader f = new BufferedReader(sd.getContent(request));
				try {
					boolean isBase = false;
					String line;
					while ((line = f.readLine()) != null)
						if (line.startsWith("var goog = goog || {};")) {
							isBase = true;
							break;
						}

					if (isBase)
						return list.indexOf(sd);
				} finally {
					f.close();
				}
			}
		}
		return -1;
	}

	private List<DependencyInfo> buildDependenciesFromFiles(List<FragmentDescriptor> scripts, IRequestProxy request) throws IOException {
		List<DependencyInfo> result = new ArrayList<DependencyInfo>();
		Set<String> filenames = new HashSet<String>();
		ClosureDepsParser ccd = new ClosureDepsParser();
		for (FragmentDescriptor script : scripts) {
			if (script instanceof AbsolutePathFragment
					&& filenames.contains(((AbsolutePathFragment) script).getFilePath()))
				continue;
			BufferedReader fileHandle = new BufferedReader(script.getContent(request));
			try {
				DependencyInfo dep = new DependencyInfo(script);
				ccd.searchDependencies(fileHandle, dep);
				result.add(dep);
			} finally {
				fileHandle.close();
			}
		}
		return result;
	}

    private List<String> expandDirectories(List<String> refs) {
        List<String> result = new ArrayList<String>();
        for (String ref : refs) {
            if (isDirectory(ref)) {
                String[] files = (new File(ref)).list();
                List<String> tempList = new ArrayList<String>();
                for (String file : files)
                    tempList.add(ref + File.separator + file);
                List<String> fs = expandDirectories(tempList);
                result.addAll(fs);
            } else if (isJsFile(ref))
                result.add(ref);
        }

        return result;
    }

    private HashMap<String, DependencyInfo> buildDependencyHashFromDependencies(List<DependencyInfo> deps)
            throws Exception {
        HashMap<String, DependencyInfo> depHash = new HashMap<String, DependencyInfo>();
        for (DependencyInfo dep : deps) {
            for (String provide : dep.getProvides()) {
                if (depHash.containsKey(provide)) {
                    String filename = ((AbsolutePathFragment) depHash.get(provide).script).getFilePath();
                    if (!filename.equals(((AbsolutePathFragment) dep.script).getFilePath()))
                        throw new Exception(MessageFormat.format("Duplicate provide ({0}) in ({1}, {2})", provide,
                                filename, ((AbsolutePathFragment) dep.script).getFilePath()));
                }
                depHash.put(provide, dep);
            }
        }
        return depHash;
    }

    private boolean isDirectory(String ref) {
        return (new File(ref)).isDirectory();
    }

    private boolean isJsFile(String ref) {
        return ref.endsWith(".js");
    }

    private List<FragmentDescriptor> calculateDependencies(List<FragmentDescriptor> inputs,
                                                           List<FragmentDescriptor> paths, IRequestProxy request) throws Exception {
        List<FragmentDescriptor> tempList = new ArrayList<FragmentDescriptor>();
        tempList.addAll(paths);
        tempList.addAll(inputs);
        List<DependencyInfo> deps = buildDependenciesFromFiles(tempList,request);
        HashMap<String, DependencyInfo> searchHash = buildDependencyHashFromDependencies(deps);
        List<FragmentDescriptor> resultList = new ArrayList<FragmentDescriptor>();
        List<FragmentDescriptor> seenList = new ArrayList<FragmentDescriptor>();
        for (FragmentDescriptor script : inputs) {
            seenList.add(script);
            BufferedReader fileHandle=new BufferedReader(script.getContent(request));
            try {
                String line;
                while ((line = fileHandle.readLine()) != null) {
                    Matcher m = ClosureDepsParser.reqRegex.matcher(line);
                    if (m.lookingAt()) {
                        String require = m.group(1);
                        resolveDependencies(require, searchHash, resultList, seenList);
                    }
                }
            } finally {
                fileHandle.close();
            }
            resultList.add(script);
        }

        // add calculated namespaces to request parameter
//        HashSet<String> namespaces;
//        if (request.getAttribute(NAMESPACE_LIST) != null)
//            namespaces = getNamespaces(request);
//        else namespaces = new HashSet<String>();
//        HashMap<FragmentDescriptor, DependencyInfo> fileDependency = new HashMap<FragmentDescriptor,
//                DependencyInfo>();
//        for (DependencyInfo dep : deps)
//            fileDependency.put(dep.script, dep);
//        for (FragmentDescriptor fd : resultList) {
//            DependencyInfo dep = fileDependency.get(fd);
//            if (dep != null) {
//                List<String> provides = dep.getProvides();
//                for (String s : provides)
//                    namespaces.add(s);
//            }
//        }
//        request.setAttribute(NAMESPACE_LIST, namespaces);

        return resultList;
    }

    @SuppressWarnings("unchecked")
    public static HashSet<String> getNamespaces(IRequestProxy request) {
        return (HashSet<String>) request.getAttribute(NAMESPACE_LIST);
    }

    private void resolveDependencies(String require, HashMap<String, DependencyInfo> searchHash,
                                     List<FragmentDescriptor> resultList, List<FragmentDescriptor> seenList) throws
            Exception {
        if (!searchHash.containsKey(require))
            throw new Exception(MessageFormat.format("Missing provider for ({0})", require));
        DependencyInfo dep = searchHash.get(require);
        if (!seenList.contains(dep.script)) {
            seenList.add(dep.script);
            for (String subRequire : dep.getRequires())
                resolveDependencies(subRequire, searchHash, resultList, seenList);
            resultList.add(dep.script);
        }
    }
}
