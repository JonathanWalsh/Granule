Description
===========
Granule is an optimization solution for Java-based web applications (JSP, JSF, Grails). It combines and compresses JavaScript and CSS files into less granuled packages, increasing speed and saving bandwidth.

Granule can automatically choose minimization algorithms by content from simple whitespace removal algorithms to advanced methods as Google Closure Compiler. It helps integrate Google Closure Library by automatically calculating dependencies between JS files and providing caching or pre-compiling capabilities. The library includes JSP tag and ant task so all packages can be precompiled.

The library organizes work with large sets of web files. It has two modes: development and production, configuration of those can be tuned separately up to turning off any effect of the library at all. 

The library is released under business friendly Apache 2.0 Open Source License. 
For JDK1.5 use Granule Closure Compiler.

List of features
----------------
  * Calculate dependencies using google package/namespace system
  * Merges and compresses JS and CSS when running in production. 
  * Uses uncompressed originals when running in development.
  * Generates packages on demand in production
  * Support fast JS/CSS compressions along with compilation methods from Google Closure Compiler
  * Supports multiple combinations of JS/CSS
  * Puts the bundles in javascripts/cache and stylesheets/cache
  * Several types of cache, memory and file.
  * Automatically regenerates the bundle if you modify an included file.
  * Proxy-friendly gzip support
  * JSP, JSF, Grails integration
  * Bundle precompiling
  * Multiple loggers support
  * Preserve License Headers
