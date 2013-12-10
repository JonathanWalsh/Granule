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

import com.granule.parser.Attributes;
import com.granule.parser.Element;
import com.granule.parser.Tags;
import com.granule.parser.TagReader;
import junit.framework.TestCase;

import java.util.List;

/**
 * User: Dario Wunsch
 * Date: 11.12.10
 * Time: 3:02
 */
public class ParserTest extends TestCase {

    public void testBasic() {
        String text = "";
        TagReader s = new TagReader(text);
        assertEquals(s.getAllElements().size(), 0);

        text = "  \t\t\n\n\t\n";
        s = new TagReader(text);
        assertEquals(s.getAllElements().size(), 0);
    }

    public void testHtmlComments() {
        String text = "  <!-- html comment <script>aaa</script> -->";
        TagReader s = new TagReader(text);
        assertEquals(s.getAllElements(Tags.SCRIPT).size(), 0);
    }

    public void testAttributes() {
        String text = "<p style=\"aa aaa\" align=righT class='tt'>";
        TagReader s = new TagReader(text);
        Element el = s.getAllElements().get(0);
        assertEquals(el.getAttributes().getValue("StYle"), "aa aaa");
        assertEquals(el.getAttributes().getValue("ALIGN"), "righT");
        assertNotSame(el.getAttributes().getValue("ALIGN"), "right");
        assertEquals(el.getAttributes().getValue("class"), "tt");
        assertEquals(el.getAttributes().getValue("id"), null);

        text = "<LINK title=style href=\"style.css\" type=text/css rel=stylesheet> ";
        s = new TagReader(text);
        assertEquals(s.getAllElements().get(0).getAttributes().getValue("type"), "text/css");

        text = "<script tt=\"<script src='a.js'></script>\">dddddddddd</script>";
        s = new TagReader(text);
        assertEquals(s.getAllElements(Tags.SCRIPT).get(0).getAttributes().getValue("tt"), "<script src='a.js'></script>");
    }

    public void testCdata() {
        String text = "<p>\n" +
                "   var a = '<LINK title=style href=\"/veralab/srk_files/srkstyle.css\" type=text/css rel=stylesheet>';\n" +
                "   alert(a.substring(5,10));\n" +
                "</p>";
        TagReader s = new TagReader(text);
        assertEquals(s.getAllElements(Tags.LINK).size(),1);

        text = "<script type=\"text/javascript\">\n" +
                "   var a = '<LINK title=style href=\"/veralab/srk_files/srkstyle.css\" type=text/css rel=stylesheet>';\n" +
                "   alert(a.substring(5,10));\n" +
                "</script>";
        s = new TagReader(text);
        assertEquals(s.getAllElements(Tags.LINK).size(), 0);

        text = "<script>a='</script>';b=c;</script>";
        s = new TagReader(text);
        assertEquals(s.getAllElements(Tags.SCRIPT).get(0).getContentAsString(), "a='");
    }

    public void testConditionalComment() {
        String text = "<LINK rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"/css/menu1.css\">\n" +
                "<!--[If IE]>\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" MEDIA=\"screen\" href=\"/css/ie/menu.css\">\n" +
                "<![endif]-->"+
                "<link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" HREF=\"/css/menu2.css\">\n";
        TagReader s = new TagReader(text);
        List<Element> lst = s.getAllElements(Tags.LINK);
        assertEquals(lst.size(), 2);
        assertEquals(lst.get(0).getAttributes().getValue("href"), "/css/menu1.css");
        assertEquals(lst.get(1).getAttributes().getValue("href"), "/css/menu2.css");
    }
    
    public void testInclude() {
    	testInclude("<%@ include file='ff.inc'%>",0);
        testInclude(" \t!<%@include file='ff.inc'%>",0);
        testInclude("<%--<%@include file='ff.inc'%>--%><%@include file='ff.inc'%>",1);
    }

	private void testInclude(String text,int tagPosition) {
		TagReader s;
		Element includeTag;
		Attributes includeAttributes;
		s = new TagReader(text);
        includeTag = s.getAllElements().get(tagPosition);
        includeAttributes = s.parseAttributes(includeTag);
        assertEquals(includeAttributes.isValueExists("include"), true);
        assertEquals(includeAttributes.getValue("file"), "ff.inc");
	}

    public void testComplexOne() {
        String text = "<script src=\"js/closure/goog/base.js\"></script>\n" +
                "        <script>\n" +
                "          goog.require('goog.events');\n" +
                "          goog.require('goog.fx');\n" +
                "          goog.require('goog.fx.dom');\n" +
                "        </script>\n" +
                "        <link rel=\"stylesheet\" href=\"js/closure/goog/demos/css/demo.css\">\n" +
                "<!-- comment <script>aaa</script>  -->\n" +
                "        <style>\n" +
                "          #test1 {\n" +
                "            position: absolute;\n" +
                "            left: 150px;\n" +
                "            top: 100px;\n" +
                "            width: 20px;\n" +
                "            height: 20px;\n" +
                "            background-color: rgb(0,0,0);\n" +
                "          }\n" +
                "\n" +
                "          button {\n" +
                "            font: normal 10px arial;\n" +
                "            width: 125px;\n" +
                "          }\n" +
                "        </style>";
        TagReader s = new TagReader(text);
        assertEquals(s.getAllElements(Tags.SCRIPT).size(), 2);
        Element firstScript = s.getAllElements(Tags.SCRIPT).get(0);
        Element secondScript = s.getAllElements(Tags.SCRIPT).get(1);
        assertEquals(firstScript.isContentExists(), true);
        assertEquals(secondScript.isContentExists(), true);
        assertEquals(firstScript.getContentAsString(), "");
        assertEquals(secondScript.getContentAsString(), "\n" +
                "          goog.require('goog.events');\n" +
                "          goog.require('goog.fx');\n" +
                "          goog.require('goog.fx.dom');\n" + "        ");
        assertEquals(firstScript.getAttributes().isValueExists("src"), true);
        assertEquals(secondScript.getAttributes().isValueExists("src"), false);
        assertEquals(firstScript.getAttributes().getValue("src"), "js/closure/goog/base.js");

        Element link = s.getAllElements(Tags.LINK).get(0);
        assertEquals(link.getAttributes().getValue("REL"), "stylesheet");
        assertEquals(link.getAttributes().get("ReL").getBegin(), text.indexOf("rel="));
        assertEquals(link.getAttributes().get("ReL").getEnd(), text.indexOf("stylesheet") + "stylesheet\"".length());

        Element style = s.getAllElements(Tags.STYLE).get(0);
        assertEquals(style.getBegin(), text.indexOf("<style>"));
        assertEquals(style.getEnd(), text.indexOf("</style>") + "</style>".length());
        assertEquals(style.getContentBegin(), text.indexOf("<style>") + "<style>".length());
        assertEquals(style.getContentEnd(), text.indexOf("</style>"));
    }

    public void testComplexTwo() {
        String text = "<%@ taglib uri=\"/WEB-INF/giraffe.tld\" prefix=\"g\" %>\n" +
                "<%@ page contentType=\"text/html;charset=UTF-8\" language=\"java\" %>\n" +
                "<%@ include file=\"includes/test1.inc\"%>\n" +
                "  <script type=\"text/javascript\">\n" +
                "     CLOSURE_BASE_PATH=\"<%=request.getContextPath()%>/js/closure/goog/\";\n" +
                "  </script>\n" +
                "  <g:compress method=\"closure-compiler\">\n" +
                "  <script src=\"../js/closure/goog/base.js\"></script></g:compress>";
        TagReader s = new TagReader(text);
        List<Element> all = s.getAllElements();
        int jspTags = 0;
        int gCompressPos = 0;
        for (Element e : all)
            if (e.getName() != null && e.getName().equalsIgnoreCase("%"))
                jspTags++;
            else if (e.getName() != null && e.getName().equals("g:compress")) gCompressPos = all.indexOf(e);
        assertEquals(jspTags, 3);
        Element pageTag = all.get(1);
        Attributes pageAttributes = s.parseAttributes(pageTag);
        assertEquals(pageAttributes.isValueExists("include"), false);
        assertEquals(pageAttributes.isValueExists("file"), false);

        Element includeTag = all.get(2);
        Attributes includeAttributes = s.parseAttributes(includeTag);
        assertEquals(includeAttributes.isValueExists("include"), true);
        assertEquals(includeAttributes.isValueExists("file"), true);
        assertEquals(includeTag.getBegin(), text.indexOf("<%@ include"));
        assertEquals(includeTag.getEnd(), text.indexOf("<%@ include") + "<%@ include file=\"includes/test1.inc\"%>".length());

        Element gCompress = all.get(gCompressPos);
        Attributes gCompressAttrs = s.parseAttributes(gCompress);
        assertEquals(gCompressAttrs.isValueExists("dsdsd"), false);
        assertEquals(gCompressAttrs.isValueExists("method"), true);
        assertEquals(gCompressAttrs.get("method").getBegin(), text.indexOf("method="));
        assertEquals(gCompressAttrs.get("method").getEnd(), text.indexOf("method=") + "method=\"closure-compiler\"".length());
        assertEquals(gCompress.getBegin(), text.indexOf("<g:compress"));
        assertEquals(gCompress.getEnd(), text.indexOf("</g:compress>") + "</g:compress>".length());
    }

   
}
