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

import com.granule.JSFastMin;
import junit.framework.TestCase;

public class JSFastMinTest extends TestCase {

	public void testBasic() {
		testMin("", "");
		testMin("f(){ };", "f(){};");
	}

	public void testBreak() {
		testMin("\r\n", "");
		testMin("f(){\r\n };\r\n", "f(){};");
		testMin("f(){/**/\r\n };\r\n", "f(){};");
		testMin("a()\r\nb()", "a()\nb()");
		testMin("a() b()", "a() b()");
	}

	public void testComment() {
		testMin("g(	a// r\n//v \n);\n", "g(a);");
		testMin("f(){//a ()\n	// d\n}\r\ng(	a// r\n//v \n);\n", "f(){}\ng(a);");
		testMin("/*  Comment */", "");
		testMin("/*  Comment **/f(){ };", "f(){};");
		testMin("/**//**/", "");
		testMin("/*/**/", "");
		testMin("//d", "");
		testMin("//d\n//c", "");
		testMin("f(){/* \r\n* Comment */}\r\ng(a/*  Comment */);\n", "f(){}\ng(a);");
	}

	public void testConditionalComment() {
		testMin("f(){/*@ \r\n* Comment */}\r\ng(a/*  Comment */);\n", "f(){/*@ \n\n* Comment */}\ng(a);");
	}

	public void testQuotes() {
		testMin("f(){a='  '};", "f(){a='  '};");
		testMin("f(){a=\"  \"};", "f(){a=\"  \"};");
		testMin("f(){a=' // '};", "f(){a=' // '};");
		testMin("f(){a=' \"\" '};", "f(){a=' \"\" '};");
		testMin("f(){a=' /**/ '};", "f(){a=' /**/ '};");
		testMin("\"*/*\"", "\"*/*\"");
		testMin("{d: \"**\" }{c: \"**\" }", "{d:\"**\"}{c:\"**\"}");
	}

	public void testRegularExpr() {
		String regexpr = "var chunker=/((?:\\((?:\\([^()]+\\)|[^()]+)+\\)|\\[(?:\\[[^[\\]]*\\]|['\"][^'\"]*['\"]|[^[\\]'\"]+)+\\]|\\\\.|[^ >+~,(\\[\\\\]+)+|[>+~])(\\s*,\\s*)?((?:.|\\r|\\n)*)/g,";
		testMin(regexpr + "\ndone=0,\n// Here", regexpr + "\ndone=0,");

		regexpr = "var chunker = /^[a-zA-Z0-9\\-_.!~*\'()]*$/;";
		testMin(regexpr + "\ndone=0,\n// Here", "var chunker =/^[a-zA-Z0-9\\-_.!~*\'()]*$/;" + "\ndone=0,");

	}

	public void testJqueryComment() {
		testMin("/*!\r\n" + "\r\n * jQuery JavaScript Library v1.4.2\r\n" + "\r\n * http://jquery.com/\r\n"
				+ "\r\n *\r\n" + "\r\n * http://jquery.org/license\r\n" + "\r\n *\r\n"
				+ "\r\n * Includes Sizzle.js\r\n" + "\r\n * http://sizzlejs.com/\r\n" + "\r\n *\r\n"
				+ "\r\n * Date: Sat Feb 13 22:33:48 2010 -0500\r\n" + "\r\n */\r\n" + "\r\nf(){ }//d\n//d\n;",
				"f(){};");
	}

	public void testMin(String in, String out) {
		JSFastMin minimizer = new JSFastMin();
		String minimized = minimizer.minimize(in);
		assertEquals(minimized, out);
	}
}
