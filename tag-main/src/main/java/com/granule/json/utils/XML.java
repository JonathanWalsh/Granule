/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package com.granule.json.utils;

import com.granule.json.JSONArray;
import com.granule.json.JSONObject;
import com.granule.json.utils.internal.JSONSAXHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used for conversion of JSON -> XML.
 */


/**
 * This class is a static helper for various ways of converting an XML document/InputStream 
 * into a JSON stream or String and vice-versa.
 * 
 * For example, the XML document:<br>
 */
public class XML {
    /**
     * Logger init.
     */
    private static String  className              = "org.apache.commons.json.xml.transform.XML";
    private static Logger logger                  = Logger.getLogger(className,null);

    /**
     * Stylesheet for just doing indention.
     */
    private static final String styleSheet= " <xsl:stylesheet version=\"1.0\"                                   \n" +
                                            "     xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">           \n" +
                                            "   <xsl:output method=\"xml\"/>                                    \n" +
                                            "   <xsl:param name=\"indent-increment\" select=\"'   '\" />        \n" +
                                            "   <xsl:template match=\"*\">                                      \n" +
                                            "      <xsl:param name=\"indent\" select=\"'&#xA;'\"/>              \n" +
                                            "      <xsl:value-of select=\"$indent\"/>                           \n" +
                                            "      <xsl:copy>                                                   \n" +
                                            "        <xsl:copy-of select=\"@*\" />                              \n" +
                                            "        <xsl:apply-templates>                                      \n" +
                                            "          <xsl:with-param name=\"indent\"                          \n" +
                                            "               select=\"concat($indent, $indent-increment)\"/>     \n" +
                                            "        </xsl:apply-templates>                                     \n" +
                                            "        <xsl:if test=\"*\">                                        \n" +
                                            "          <xsl:value-of select=\"$indent\"/>                       \n" +
                                            "        </xsl:if>                                                  \n" +
                                            "      </xsl:copy>                                                  \n" +
                                            "   </xsl:template>                                                 \n" +
                                            "   <xsl:template match=\"comment()|processing-instruction()\">     \n" +
                                            "      <xsl:param name=\"indent\" select=\"'&#xA;'\"/>              \n" +
                                            "      <xsl:value-of select=\"$indent\"/>                           \n" +
                                            "      <xsl:copy>                                                   \n" +
                                            "        <xsl:copy-of select=\"@*\" />                              \n" +
                                            "        <xsl:apply-templates>                                      \n" +
                                            "          <xsl:with-param name=\"indent\"                          \n" +
                                            "               select=\"concat($indent, $indent-increment)\"/>     \n" +
                                            "        </xsl:apply-templates>                                     \n" +
                                            "        <xsl:if test=\"*\">                                        \n" +
                                            "          <xsl:value-of select=\"$indent\"/>                       \n" +
                                            "        </xsl:if>                                                  \n" +
                                            "      </xsl:copy>                                                  \n" +
                                            "   </xsl:template>                                                 \n" +
                                            "   <xsl:template match=\"text()[normalize-space(.)='']\"/>         \n" +
                                            " </xsl:stylesheet>                                                 \n" ;

    /**
     * Method to do the transform from an XML input stream to a JSON stream.
     * Neither input nor output streams are closed.  Closure is left up to the caller.  Same as calling toJson(inStream, outStream, false);  (Default is compact form)
     *
     * @param XMLStream The XML stream to convert to JSON
     * @param JSONStream The stream to write out JSON to.  The contents written to this stream are always in UTF-8 format.
     * 
     * @throws SAXException Thrown is a parse error occurs.
     * @throws IOException Thrown if an IO error occurs.
     */
    public static void toJson(InputStream XMLStream, OutputStream JSONStream) throws SAXException, IOException {
        if (logger.isLoggable(Level.FINER)) {
            logger.entering(className, "toJson(InputStream, OutputStream)");
        }
        toJson(XMLStream,JSONStream,false);    

        if (logger.isLoggable(Level.FINER)) {
            logger.entering(className, "toJson(InputStream, OutputStream)");
        }
    }

    /**
     * Method to do the transform from an XML input stream to a JSON stream.
     * Neither input nor output streams are closed.  Closure is left up to the caller.
     *
     * @param XMLStream The XML stream to convert to JSON
     * @param JSONStream The stream to write out JSON to.  The contents written to this stream are always in UTF-8 format.
     * @param verbose Flag to denote whether or not to render the JSON text in verbose (indented easy to read), or compact (not so easy to read, but smaller), format.
     *
     * @throws SAXException Thrown if a parse error occurs.
     * @throws IOException Thrown if an IO error occurs.
     */
    public static void toJson(InputStream XMLStream, OutputStream JSONStream, boolean verbose) throws SAXException, IOException {
        if (logger.isLoggable(Level.FINER)) {
            logger.entering(className, "toJson(InputStream, OutputStream)");
        }

        if (XMLStream == null) {
            throw new NullPointerException("XMLStream cannot be null");
        } else if (JSONStream == null) {
            throw new NullPointerException("JSONStream cannot be null");
        } else {

            if (logger.isLoggable(Level.FINEST)) {
                logger.logp(Level.FINEST, className, "transform", "Fetching a SAX parser for use with JSONSAXHandler");
            }

            try {
                /**
                 * Get a parser.
                 */
                SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setNamespaceAware(true);
                SAXParser sParser = factory.newSAXParser();
                XMLReader parser = sParser.getXMLReader();
                JSONSAXHandler jsonHandler = new JSONSAXHandler(JSONStream, verbose);
                parser.setContentHandler(jsonHandler);
                parser.setErrorHandler(jsonHandler);
                InputSource source = new InputSource(new BufferedInputStream(XMLStream));

                if (logger.isLoggable(Level.FINEST)) {
                    logger.logp(Level.FINEST, className, "transform", "Parsing the XML content to JSON");
                }

                /** 
                 * Parse it.
                 */
                source.setEncoding("UTF-8");
                parser.parse(source);                 
                jsonHandler.flushBuffer();
            } catch (javax.xml.parsers.ParserConfigurationException pce) {
                throw new SAXException("Could not get a parser: " + pce.toString());
            }
        }

        if (logger.isLoggable(Level.FINER)) {
            logger.exiting(className, "toJson(InputStream, OutputStream)");
        }
    }

    /**
     * Method to take an input stream to an XML document and return a String of the JSON format.  
     * Note that the xmlStream is not closed when read is complete.  This is left up to the caller, who may wish to do more with it.  
     * This is the same as toJson(xmlStream,false)
     *
     * @param xmlStream The InputStream to an XML document to transform to JSON.
     * @return A string of the JSON representation of the XML file
     * 
     * @throws SAXException Thrown if an error occurs during parse.
     * @throws IOException Thrown if an IOError occurs.
     */
    public static String toJson(InputStream xmlStream) throws SAXException, IOException {
        return toJson(xmlStream,false);
    }


    /**
     * Method to take an input stream to an XML document and return a String of the JSON format.  Note that the xmlStream is not closed when read is complete.  This is left up to the caller, who may wish to do more with it.
     * @param xmlStream The InputStream to an XML document to transform to JSON.
     * @param verbose Boolean flag denoting whther or not to write the JSON in verbose (formatted), or compact form (no whitespace)
     * @return A string of the JSON representation of the XML file
     * 
     * @throws SAXException Thrown if an error occurs during parse.
     * @throws IOException Thrown if an IOError occurs.
     */                                                                    
    public static String toJson(InputStream xmlStream, boolean verbose)  throws SAXException, IOException {
        if (logger.isLoggable(Level.FINER)) {
            logger.exiting(className, "toJson(InputStream, boolean)");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String result              = null;

        try {
            toJson(xmlStream,baos,verbose);
            result = baos.toString("UTF-8");
            baos.close();
        } catch (UnsupportedEncodingException uec) {
            IOException iox = new IOException(uec.toString());
            iox.initCause(uec);
            throw iox;
        }

        if (logger.isLoggable(Level.FINER)) {
            logger.exiting(className, "toJson(InputStream, boolean)");
        }

        return result;
    }


    /**
     * Method to take an XML file and return a String of the JSON format.  
     * 
     * @param xmlFile The XML file to transform to JSON.
     * @param verbose Boolean flag denoting whther or not to write the JSON in verbose (formatted), or compact form (no whitespace)
     * @return A string of the JSON representation of the XML file
     * 
     * @throws SAXException Thrown if an error occurs during parse.
     * @throws IOException Thrown if an IOError occurs.
     */
    public static String toJson(File xmlFile, boolean verbose) throws SAXException, IOException {
        if (logger.isLoggable(Level.FINER)) {
            logger.exiting(className, "toJson(InputStream, boolean)");
        }

        FileInputStream fis        = new FileInputStream(xmlFile);
        String result              = null;

        result = toJson(fis,verbose);
        fis.close();

        if (logger.isLoggable(Level.FINER)) {
            logger.exiting(className, "toJson(InputStream, boolean)");
        }

        return result;
    }

    /**
     * Method to take an XML file and return a String of the JSON format.  
     * This is the same as toJson(xmlStream,false)
     *
     * @param xmlFile The XML file to convert to JSON.
     * @return A string of the JSON representation of the XML file
     * 
     * @throws SAXException Thrown if an error occurs during parse.
     * @throws IOException Thrown if an IOError occurs.
     */
    public static String toJson(File xmlFile) throws SAXException, IOException {
        return toJson(xmlFile,false);
    }

    /**
     * Method to do the transform from an JSON input stream to a XML stream.
     * Neither input nor output streams are closed.  Closure is left up to the caller.  Same as calling toXml(inStream, outStream, false);  (Default is compact form)
     *
     * @param JSONStream The JSON stream to convert to XML
     * @param XMLStream The stream to write out XML to.  The contents written to this stream are always in UTF-8 format.
     * 
     * @throws IOException Thrown if an IO error occurs.
     */
    public static void toXml(InputStream JSONStream, OutputStream XMLStream)
    throws IOException
    {
        if (logger.isLoggable(Level.FINER)) {
            logger.entering(className, "toXml(InputStream, OutputStream)");
        }
        toXml(JSONStream,XMLStream,false);    

        if (logger.isLoggable(Level.FINER)) {
            logger.entering(className, "toXml(InputStream, OutputStream)");
        }
    }

    /**
     * Method to do the transform from an JSON input stream to a XML stream.
     * Neither input nor output streams are closed.  Closure is left up to the caller.
     *
     * @param JSONStream The XML stream to convert to JSON
     * @param XMLStream The stream to write out JSON to.  The contents written to this stream are always in UTF-8 format.
     * @param verbose Flag to denote whether or not to render the XML text in verbose (indented easy to read), or compact (not so easy to read, but smaller), format.
     *
     * @throws IOException Thrown if an IO error occurs.
     */
    public static void toXml(InputStream JSONStream, OutputStream XMLStream, boolean verbose)
    throws IOException
    {
        if (logger.isLoggable(Level.FINER)) {
            logger.entering(className, "toXml(InputStream, OutputStream)");
        }

        if (XMLStream == null) {
            throw new NullPointerException("XMLStream cannot be null");
        } else if (JSONStream == null) {
            throw new NullPointerException("JSONStream cannot be null");
        } else {

            if (logger.isLoggable(Level.FINEST)) {
                logger.logp(Level.FINEST, className, "transform", "Parsing the JSON and a DOM builder.");
            }

            try {
                //Get the JSON from the stream.
                JSONObject jObject = new JSONObject(JSONStream);

                //Create a new document

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbf.newDocumentBuilder();
                Document doc = dBuilder.newDocument();

                if (logger.isLoggable(Level.FINEST)) {
                    logger.logp(Level.FINEST, className, "transform", "Parsing the JSON content to XML");
                }

                convertJSONObject(doc, doc.getDocumentElement(), jObject, "jsonObject");

                //Serialize it.
                TransformerFactory tfactory = TransformerFactory.newInstance();
                Transformer serializer  = null;
                if (verbose) {
                    serializer = tfactory.newTransformer(new StreamSource( new StringReader(styleSheet) ));;
                } else {
                    serializer = tfactory.newTransformer();
                }
                Properties oprops = new Properties();
                oprops.put(OutputKeys.METHOD, "xml");
                oprops.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
                oprops.put(OutputKeys.VERSION, "1.0");
                oprops.put(OutputKeys.INDENT, "true");
                serializer.setOutputProperties(oprops);
                serializer.transform(new DOMSource(doc), new StreamResult(XMLStream));

            } catch (Exception ex) {
                IOException iox = new IOException("Problem during conversion");
                iox.initCause(ex);
                throw iox;
            }
        }

        if (logger.isLoggable(Level.FINER)) {
            logger.exiting(className, "toXml(InputStream, OutputStream)");
        }
    }

    /**
     * Method to take an input stream to an JSON document and return a String of the XML format.  
     * Note that the JSONStream is not closed when read is complete.  This is left up to the caller, who may wish to do more with it.  
     * This is the same as toXml(JSONStream,false)
     *
     * @param JSONStream The InputStream to an JSON document to transform to XML.
     * @return A string of the JSON representation of the XML file
     * 
     * @throws IOException Thrown if an IOError occurs.
     */
    public static String toXml(InputStream JSONStream)
    throws IOException
    {
        return toXml(JSONStream,false);
    }


    /**
     * Method to take an input stream to an JSON document and return a String of the XML format.  Note that the JSONStream is not closed when read is complete.  This is left up to the caller, who may wish to do more with it.
     * @param xmlStream The InputStream to an JSON document to transform to XML.
     * @param verbose Boolean flag denoting whther or not to write the XML in verbose (formatted), or compact form (no whitespace)
     * @return A string of the JSON representation of the XML file
     * 
     * @throws IOException Thrown if an IOError occurs.
     */
    public static String toXml(InputStream JSONStream, boolean verbose)
    throws IOException
    {
        if (logger.isLoggable(Level.FINER)) {
            logger.exiting(className, "toXml(InputStream, boolean)");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String result              = null;

        try {
            toXml(JSONStream,baos,verbose);
            result = baos.toString("UTF-8");
            baos.close();
        } catch (UnsupportedEncodingException uec) {
            IOException iox = new IOException(uec.toString());
            iox.initCause(uec);
            throw iox;
        }

        if (logger.isLoggable(Level.FINER)) {
            logger.exiting(className, "toXml(InputStream, boolean)");
        }

        return result;
    }


    /**
     * Method to take a JSON file and return a String of the XML format.  
     * 
     * @param xmlFile The JSON file to transform to XML.
     * @param verbose Boolean flag denoting whther or not to write the XML in verbose (formatted), or compact form (no whitespace)
     * @return A string of the XML representation of the JSON file
     * 
     * @throws IOException Thrown if an IOError occurs.
     */
    public static String toXml(File jsonFile, boolean verbose)
    throws IOException
    {
        if (logger.isLoggable(Level.FINER)) {
            logger.exiting(className, "toXml(InputStream, boolean)");
        }

        FileInputStream fis        = new FileInputStream(jsonFile);
        String result              = null;

        result = toXml(fis,verbose);
        fis.close();

        if (logger.isLoggable(Level.FINER)) {
            logger.exiting(className, "toXml(InputStream, boolean)");
        }

        return result;
    }

    /**
     * Method to take an JSON file and return a String of the XML format.  
     * This is the same as toXml(jsonStream,false)
     *
     * @param jsonFile The XML file to convert to XML.
     * @return A string of the XML representation of the JSON file
     * 
     * @throws IOException Thrown if an IOError occurs.
     */
    public static String toXml(File jsonFile)
    throws IOException
    {
        return toXml(jsonFile,false);
    }

    private static void convertJSONObject(Document doc, Element parent, JSONObject jObject, String tagName) {
        Set attributes    = jObject.keySet();
        Iterator attrsItr = attributes.iterator();

        Element element   = doc.createElement(removeProblemCharacters(tagName));
        if (parent != null) {
            parent.appendChild(element);
        } else {
            doc.appendChild(element);
        }

        while (attrsItr.hasNext()) {
            String attr = (String) attrsItr.next();
            Object obj = jObject.opt(attr);

            if (obj instanceof Number) {
                element.setAttribute(attr, obj.toString());
            } else if (obj instanceof Boolean) {
                element.setAttribute(attr, obj.toString());
            } else if (obj instanceof String) {
                element.setAttribute(attr, escapeEntityCharacters(obj.toString()));
            } else if (obj == null) {
                element.setAttribute(attr, "");
            } else if (obj instanceof JSONObject) {
                convertJSONObject(doc, element, (JSONObject)obj, attr);
            } else if (obj instanceof JSONArray) {
                convertJSONArray(doc, element, (JSONArray)obj, attr);
            }
        }
    }

    private static void convertJSONArray(Document doc, Element parent, JSONArray jArray, String tagName) {
        tagName = removeProblemCharacters(tagName);
        for (int i = 0; i < jArray.size(); i++) {
            Element element   = doc.createElement(tagName);
            if (parent != null) {
                parent.appendChild(element);
            } else {
                doc.appendChild(element);
            }

            Object obj = jArray.get(i);

            if (obj instanceof Number) {
                Node tNode = doc.createTextNode(obj.toString());
                element.appendChild(tNode);
            } else if (obj instanceof Boolean) {
                Node tNode = doc.createTextNode(obj.toString());
                element.appendChild(tNode);
            } else if (obj instanceof String) {
                Node tNode = doc.createTextNode(escapeEntityCharacters(obj.toString()));
                element.appendChild(tNode);
            } else if (obj instanceof JSONObject) {
                convertJSONObject(doc, element, (JSONObject)obj, "jsonObject");
            } else if (obj instanceof JSONArray) {
                convertJSONArray(doc, element, (JSONArray)obj, "jsonArray");
            }
        }
    }

    /**
     * Simple method to escape any special characters in the string into proper XML formatted
     * characters.
     * @param str The string to convert.
     */
    private static String escapeEntityCharacters(String str) {
        String retVal = null;
        if (str != null) {
            StringBuffer strBuf = new StringBuffer("");
            for (int i = 0; i < str.length(); i++) {
                char character = str.charAt(i);

                switch (character) {
                    case '&':
                        {
                            strBuf.append("&amp;");
                            break;
                        }
                    case '>':
                        {
                            strBuf.append("&gt;");
                            break;
                        }
                    case '<':
                        {
                            strBuf.append("&lt;");
                            break;
                        }
                    case '\"':
                        {
                            strBuf.append("&quot;");
                            break;
                        }
                    case '\'':
                        {
                            strBuf.append("&apos;");
                            break;
                        }
                    default:
                        {
                            strBuf.append(character);
                        }
                }
            }
            retVal = strBuf.toString();
        }
        return retVal;
    }

    /**
     * Simple method to escape any special characters in the string into proper XML formatted
     * characters.
     * @param str The string to convert.
     */
    private static String removeProblemCharacters(String str) {
        String retVal = null;
        if (str != null) {
            StringBuffer strBuf = new StringBuffer("");
            for (int i = 0; i < str.length(); i++) {
                char character = str.charAt(i);

                switch (character) {
                    case '&':
                    case '>':
                    case '<':
                    case '\"':
                    case '\'':
                    case ':':
                    case ';':
                    case '%':
                    case ' ':
                    case '{':
                    case '}':
                    case '(':
                    case ')':
                    case '[':
                    case ']':
                    case '\\':
                    case '/':
                    case '|':
                    case '#':
                    case '*':
                    case '^':
                    case '!':
                        {
                            strBuf.append("_");
                            break;
                        }
                    default:
                        {
                            strBuf.append(character);
                        }
                }
            }
            retVal = strBuf.toString();
        }
        return retVal;
    }
}
