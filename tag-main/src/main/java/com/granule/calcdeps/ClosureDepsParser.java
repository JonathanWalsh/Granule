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
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Dario Wunsch Date: 22.07.2010 Time: 0:18:19
 */
public class ClosureDepsParser {
	public static final Pattern reqRegex = Pattern.compile("\\s*goog\\.require\\s*\\(\\s*[\'\"]([^\\)]+)[\'\"]\\s*\\)");
	public static final Pattern provRegex = Pattern
			.compile("\\s*goog\\.provide\\s*\\(\\s*[\\'\\\"]([^\\)]+)[\\'\\\"]\\s*\\)");

	public void searchDependencies(BufferedReader in, DependencyInfo dep) throws IOException {
		String line;
		while ((line = in.readLine()) != null) {
			if (line.indexOf("goog.require") != -1) {
				Matcher m = reqRegex.matcher(line);
				if (m.lookingAt())
					dep.getRequires().add(m.group(1));
			}
			if (line.indexOf("goog.provide") != -1) {
				Matcher m = provRegex.matcher(line);
				if (m.lookingAt())
					dep.getProvides().add(m.group(1));
			}
		}
	}
}
