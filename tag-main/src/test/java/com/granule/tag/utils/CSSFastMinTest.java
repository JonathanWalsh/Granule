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

import com.granule.CSSFastMin;
import junit.framework.TestCase;

public class CSSFastMinTest extends TestCase {
 
  public void testBasic() {
	testMin("","");
	testMin("\n","");
	testMin("\r\n","");
	testMin("*{\r\nmargin:0;\r\n}","*{margin:0;}");
  }

  public void testSpaces() {
	testMin("*{\r\nmargin:0;  \r\n}","*{margin:0;}");
	testMin("*{ border:1px dashedlime;}\r\nhtml {\rheight: 100%;\r\n}\r\n","*{border:1px dashedlime;}html {height:100%;}");
  }
  
  public void testBreaks() {
      testMin("*{ border:1px\ndashedlime;}\r\nhtml {\rheight: 100%;\r\n}\r\n","*{border:1px dashedlime;}html {height:100%;}");
  }

  public void testComment() {
	testMin("/*  Comment */","");
	testMin("/*  Comment **/a{b:c;}","a{b:c;}");
	testMin("/**//**/","");
	testMin("/*/**/","");
	testMin("*{ margin:0; /* \r\n* Comment */}\r\nhtml {\rheight:/*  Comment */100%;\r\n}\r\n","*{margin:0;}html {height:100%;}");
  }
  
  public void testWithReqular() {
	  testWithRegular("*{margin:0;/*  Comment */}\r\nhtml{\rheight:/*  Comment */100%;\r\n}\r\n");
	  testWithRegular("/*\r\n* j \r\n*/\r\n//\r\n/* L\r\n ---- \\*/\r\n* html .u {h:1%;}");
  }
  
  public void testJqueryComment() {
	  testMin("/*!\r\n"
 +"\r\n * jQuery JavaScript Library v1.4.2\r\n"
 +"\r\n * http://jquery.com/\r\n"
 +"\r\n *\r\n"
 +"\r\n * Copyright 2010, John Resig\r\n"
 +"\r\n * Dual licensed under the MIT or GPL Version 2 licenses.\r\n"
 +"\r\n * http://jquery.org/license\r\n"
 +"\r\n *\r\n"
 +"\r\n * Includes Sizzle.js\r\n"
 +"\r\n * http://sizzlejs.com/\r\n"
 +"\r\n * Copyright 2010, The Dojo Foundation\r\n"
 +"\r\n * Released under the MIT, BSD, and GPL Licenses.\r\n"
 +"\r\n *\r\n"
 +"\r\n * Date: Sat Feb 13 22:33:48 2010 -0500\r\n"
 +"\r\n */\r\n"
 +"\r\n{ h: 1%; }","{h:1%;}");
  }
  
  private void testWithRegular(String css) {
	testMin(css,iBloomCompareTest(css));  
  }
  
  public void testMin(String in, String out) {
	  CSSFastMin minimizer=new CSSFastMin();
	  String minimized = minimizer.minimize(in);
	  //System.out.println("####"+minimized);
	  assertEquals(minimized, out); 
  }
  
  public static String iBloomCompareTest(String in) {
		String result = in.replaceAll("/\\*[^*]*\\*+([^/][^*]*\\*+)*/", "");
		result = result.replaceAll("(\r\n)|(\r)|(\n)|(\t)|(  +)", "");
		return result.trim();
  }

}
