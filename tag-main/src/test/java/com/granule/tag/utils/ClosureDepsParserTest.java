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
package com.granule.tag.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;

import com.granule.calcdeps.ClosureDepsParser;
import com.granule.calcdeps.DependencyInfo;

public class ClosureDepsParserTest extends TestCase {
 
	public void testSearchDependencies() {
		String s = "goog.require('goog.dom');\n" + "    goog.require(    'goog.date');\n"
				+ "    goog.require('goog.i18n.DateTimeSymbols'  );\n" + "    goog.require(\"goog.ui.DatePicker\");";
		checkDependencies(s, new String[] {}, new String[] { "goog.dom", "goog.date", "goog.i18n.DateTimeSymbols",
				"goog.ui.DatePicker" });
		s = "   goog.provide(  'goog.date'  );";
		checkDependencies(s, new String[] { "goog.date" }, new String[] {});
		s = "goog.provide(  'goog.date'  ); \n goog.require(\"goog.i18n.DateTimeSymbols\"  ); \n\n\n mega();\n var a='ffff';";
		checkDependencies(s, new String[] { "goog.date" }, new String[] { "goog.i18n.DateTimeSymbols" });
	}

	private void checkDependencies(String s, String[] provides, String[] requires) {
		ClosureDepsParser ccd = new ClosureDepsParser();
		DependencyInfo dep = new DependencyInfo(null);
		try {
			ccd.searchDependencies(new BufferedReader(new StringReader(s)), dep);
			assert (checkDependencyInfo(dep, provides, requires));
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	private boolean checkDependencyInfo(DependencyInfo dep, String[] provides, String[] requires) {
		if (provides.length != dep.getProvides().size())
			return false;
		if (requires.length != dep.getRequires().size())
			return false;
		List<String> lst = dep.getProvides();
		for (int i = 0; i < lst.size(); i++)
			if (!lst.get(i).equals(provides[i]))
				return false;
		lst = dep.getRequires();
		for (int i = 0; i < lst.size(); i++)
			if (!lst.get(i).equals(requires[i]))
				return false;
		return true;
	}

}
