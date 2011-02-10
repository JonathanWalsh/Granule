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

package com.granule.json;

import java.io.Reader;
import java.io.StringReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.granule.json.internal.Parser;

/**
 * Extension of the basic JSONObject.  This class allows control of the serialization order of attributes.  
 * The order in which items are put into the instance controls the order in which they are serialized out.  For example, the
 * last item put is the last item serialized.  
 * <BR><BR>
 * JSON-able values are: null, and instances of String, Boolean, Number, JSONObject and JSONArray.
 * <BR><BR>
 * Instances of this class are not thread-safe.
 */
public class OrderedJSONObject extends JSONObject {

    private static final long serialVersionUID = -3269263069889337299L;
    private ArrayList order                    = null;

    /**
     * Create a new instance of this class. 
     */
    public OrderedJSONObject() {
        super();
        this.order = new ArrayList();
    }

    /**
     * Create a new instance of this class from the provided JSON object string.
     * Note:  This is the same as calling new OrderedJSONObject(str, false).
     * @param str The String of JSON to parse
     * @throws JSONException Thrown when the string passed is null, or malformed JSON.. 
     */
    public OrderedJSONObject(String str) throws JSONException {
        super();
        this.order = new ArrayList();
        StringReader reader = new StringReader(str);
        (new Parser(reader)).parse(true, this);
    }

    /**
     * Create a new instance of this class from the provided JSON object string.
     * @param str The String of JSON to parse
     * @param strict Boolean flag indicating if strict mode should be used.  Strict mode means comments and unquoted strings are not allowed.
     * @throws JSONException Thrown when the string passed is null, or malformed JSON.. 
     */
    public OrderedJSONObject(String str, boolean strict) throws JSONException {
        super();
        this.order = new ArrayList();
        StringReader reader = new StringReader(str);
        (new Parser(reader, strict)).parse(true, this);
    }

    /**
     * Create a new instance of this class from the data provided from the reader.  The reader content must be a JSON object string.
     * Note:  The reader will not be closed, that is left to the caller.
     * Note:  This is the same as calling new OrderedJSONObject(rdr, false).
     * @param rdr The reader from which to read the JSON to parse
     * @throws JSONException Thrown when the string passed is null, or malformed JSON.. 
     */
    public OrderedJSONObject(Reader rdr) throws JSONException {
        super();
        this.order = new ArrayList();
        (new Parser(rdr)).parse(true, this);
    }

    /**
     * Create a new instance of this class from the data provided from the reader.  The reader content must be a JSON object string.
     * Note:  The reader will not be closed, that is left to the caller.
     * @param rdr The reader from which to read the JSON to parse
     * @param strict Boolean flag indicating if strict mode should be used.  Strict mode means comments and unquoted strings are not allowed.
     * @throws JSONException Thrown when the string passed is null, or malformed JSON.. 
     */
    public OrderedJSONObject(Reader rdr, boolean strict) throws JSONException {
        super();
        this.order = new ArrayList();
        (new Parser(rdr, strict)).parse(true, this);
    }

    /**
     * Create a new instance of this class from the data provided from the input stream.  The stream content must be a JSON object string.
     * Note:  The input stream content is assumed to be UTF-8 encoded.
     * Note:  The InputStream will not be closed, that is left to the caller.
     * Note:  This is the same as calling new OrderedJSONObject(is, false).
     * @param is The InputStream from which to read the JSON to parse
     * @throws JSONException Thrown when the string passed is null, or malformed JSON.. 
     */
    public OrderedJSONObject (InputStream is) throws JSONException {
        super();
        this.order = new ArrayList();
        InputStreamReader isr = null;
        if (is != null) {
            try {
                isr = new InputStreamReader(is, "UTF-8");
            } catch (Exception ex) {
                isr = new InputStreamReader(is);
            }
        } else {
            throw new JSONException("Inputstream cannot be null");
        }
        (new Parser(isr)).parse(true, this);
    }

    /**
     * Create a new instance of this class from the data provided from the input stream.  The stream content must be a JSON object string.
     * Note:  The input stream content is assumed to be UTF-8 encoded.
     * Note:  The InputStream will not be closed, that is left to the caller.
     * @param is The InputStream from which to read the JSON to parse
     * @param strict Boolean flag indicating if strict mode should be used.  Strict mode means comments and unquoted strings are not allowed.
     * @throws JSONException Thrown when the string passed is null, or malformed JSON.. 
     */
    public OrderedJSONObject (InputStream is, boolean strict) throws JSONException {
        super();
        this.order = new ArrayList();
        InputStreamReader isr = null;
        if (is != null) {
            try {
                isr = new InputStreamReader(is, "UTF-8");
            } catch (Exception ex) {
                isr = new InputStreamReader(is);
            }
        } else {
            throw new JSONException("Inputstream cannot be null");
        }
        (new Parser(isr, strict)).parse(true, this);
    }

    /**
     * Create a new instance of this class using the contents of the provided map.  
     * The contents of the map should be values considered JSONable.
     * @param map The map of key/value pairs to insert into this JSON object
     * @throws JSONException Thrown when contents in the map cannot be made JSONable.
     * @throws NullPointerException Thrown if the map is null, or a key in the map is null..
     */
    public OrderedJSONObject(Map map) throws JSONException {
        super();
        this.order = new ArrayList();
        Set set = map.keySet();
        if (set != null) {
            Iterator itr = set.iterator();
            if (itr != null) {
                while (itr.hasNext()) {
                    Object key = itr.next();
                    String sKey = key.toString();
                    this.put(sKey, map.get(key));
                }
            }
        }
    }

    /**
     * Method to put a JSON'able object into the instance.  Note that the order of initial puts controls the order of serialization.  
     * Meaning that the first time an item is put into the object determines is position of serialization.  Subsequent puts with the same
     * key replace the existing entry value and leave serialization position alone.  For moving the position, the object must be removed, 
     * then re-put.
     * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
     */
    public Object put(Object key, Object value) {
        if (null == key) throw new IllegalArgumentException("key must not be null");
        if (!(key instanceof String)) throw new IllegalArgumentException("key must be a String");

        if (!isValidObject(value)) {
            if (value != null) {
                throw new IllegalArgumentException("Invalid type of value.  Type: [" + value.getClass().getName() + "] with value: [" + value.toString() + "]");
            } else {
                throw new IllegalArgumentException("Invalid type of value.");
            }
        }

        /**
         * Only put it in the ordering list if it isn't already present.
         */
        if (!this.containsKey(key)) {
            this.order.add(key);
        }
        return super.put(key, value);
    }

    /**
     * Method to remove an entry from the OrderedJSONObject instance.
     * @see java.util.HashMap#remove(java.lang.Object)
     */
    public Object remove(Object key) {
        Object retVal = null;

        if (null == key) throw new IllegalArgumentException("key must not be null");
        if (this.containsKey(key)) {
            retVal = super.remove(key);

            for (int i = 0; i < this.order.size(); i++) {
                Object obj = this.order.get(i);
                if (obj.equals(key)) {
                    this.order.remove(i);
                    break;
                }
            }
        }
        return retVal;
    }

    /**
     * (non-Javadoc)
     * @see java.util.HashMap#clear()
     */
    public void clear() {
        super.clear();
        this.order.clear();
    }

    /** 
     * Returns a shallow copy of this HashMap instance: the keys and values themselves are not cloned.
     */
    public Object clone() {
        OrderedJSONObject clone = (OrderedJSONObject)super.clone();
        Iterator order = clone.getOrder();
        ArrayList orderList = new ArrayList();
        while (order.hasNext()) {
            orderList.add(order.next());
            clone.order = orderList;
        }
        return clone;
    }

    /**
     * Method to obtain the order in which the items will be serialized.
     * @return An iterator that represents the attribute names in the order that they will be serialized.
     */
    public Iterator getOrder() {
        return this.order.iterator();
    }
}
