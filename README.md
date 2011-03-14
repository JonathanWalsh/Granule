Overview
=======
Granule is an optimization solution for Java-based web applications (JSP, JSF, Grails). It combines and compresses JavaScript and CSS files into less granulated packages, increasing speed and saving bandwidth.

The granule solution includes:
- JSP Tag library. You just need put the tag around your StyleSheets and JavaScripts to compress and combine them.
- Ant task, to include pre-compressing in your build scripts. 


![Example](https://sites.google.com/site/granuletag/_/rsrc/1297244554577/home/demojsphtml.png)

Granule can automatically choose minimization algorithms by content from simple whitespace removal algorithms to advanced methods as Google Closure Compiler. It helps integrate Google Closure Library by automatically calculating dependencies between JS files and providing caching or pre-compiling capabilities. 

The library organizes work with large sets of web files. It has two modes: development and production, configuration of those can be tuned separately up to turning off any effect of the library at all. 

The library is released under business friendly Apache 2.0 Open Source License. 
For JDK1.5 use Granule Closure Compiler.


List of features
===========
  *  Combine and compresses JS and CSS using different methods: on fly or in build process, CSS and JS fast compression or more sophisticated Google Closure compression, or just simple file combining. 
  *  No-lock in solution. The tag just put around existing scripts. The tag can be turn on/off on the different levels: page and application.
  *  Debug and production modes.
  *  Calculate dependencies using Closure Library package/namespace system.
  *  Can automatically choose optimization methods.
  *  Multiple combinations of JS/CSS even with different compression methods on one page.
  *  Support JSP includes.
  *  Several types of cache, memory and file.
  *  Automatically regenerates the bundle if you modify an included file.
  *  Proxy-friendly GZip support.
  *  Rewrites relative URLs in your CSS files.
  *  JSP, JSF, Grails integration.
  *  Multiple loggers support (SLF4J, Log4J, Apache Logger)
  *  Can be setup to preserve license headers of JS libraries.
  * JDK1.5 and higher even for Google Closure Compiler.

## Installation Granule Tag Library ##
1. Download the binary distribution of Granule Tag Library by following this URL: http://code.google.com/p/granule/downloads/list (granuleNNN.zip) and unpack the compressed file.

2. Copy granuleNNN.jar in the distribution’s ‘lib’ directory to your web applications WEB-NF\lib directory.

3. To use the granule compress tag, you must include taglib directive {{{<%@ taglib uri="http://granule.com/tags" prefix="g" %> }}}at the top of each JSP that uses this library.

4. Copy the {{{<servlet> and <servlet-mapping>}}} declarations from web.xml (look below) from compressed file into your /WEB-INF/web.xml 
{{{  
  <servlet>
        <servlet-name>CompressServlet</servlet-name>
        <servlet-class>com.granule.CompressServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>CompressServlet</servlet-name>
        <url-pattern>/combined.js</url-pattern>
    </servlet-mapping>
    <servlet-mapping>
        <servlet-name>CompressServlet</servlet-name>
        <url-pattern>/combined.css</url-pattern>
    </servlet-mapping>
}}}

5. Put <g:compress> tags around the lists of script decorations (JS or CSS). For example -
{{{
<g:compress>
  <link rel="stylesheet" type="text/css" href="css/dp.css"/>
  <link rel="stylesheet" type="text/css" href="css/demo.css"/>    
</g:compress>
...
<div id="datepicker"></div>
<g:compress>
  <script type="text/javascript" src="common.js"/>
  <script type="text/javascript" src="closure/goog/base.js"/>
  <script>
       goog.require('goog.dom');
       goog.require('goog.date');
       goog.require('goog.ui.DatePicker');
  </script>
  <script type="text/javascript">
      var dp = new goog.ui.DatePicker();
      dp.render(document.getElementById('datepicker'));
  </script>
</g:compress>
...
}}}

6.  Done. Run your web application and check output html source. It should convert CSS and JS declarations similar to this.
{{{
<link rel="stylesheet" type="text/css" 
      href="/combined.css?id=cc4c21b0"/>    

<script src="/combined.js?id=4658acf30"/>
}}}
