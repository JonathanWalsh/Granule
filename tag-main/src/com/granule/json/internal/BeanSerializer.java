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

package com.granule.json.internal;

import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Method;
import java.lang.NoSuchMethodException;

import com.granule.json.JSONArray;
import com.granule.json.JSONArtifact;
import com.granule.json.JSONException;
import com.granule.json.JSONObject;

/**
 * Private class to introspect a JavaBean and convert it to its respective JSON type.
 */
public class BeanSerializer {

    /** 
     * This method inspects a bean and converts it to its corrisponding JSON. 
     * This function expects non-basic types (no String, Number, etc).
     * @param obj The Object to inspect.
     * @param includeSuperclass Boolean indicating if superclass properties should be included in the output JSON.                       
     * @return An instance of the JSONArtifact that best represents the data in this JavaBean
     * @throws IllegalArgumentException Thrown if input type is a String, Number, Boolean, etc.
     * @throws com.granule.json.JSONException Thrown if a JSON conversion error occurs.
     */
    public static JSONArtifact toJson(Object obj, boolean includeSuperclass) throws IllegalArgumentException, JSONException {
        JSONArtifact ja = null;

        if (obj != null) {
            Class clazz = obj.getClass();
            if (String.class  == clazz) {
                throw new IllegalArgumentException("Class was String type, not a Javabean.");
            } else if (Boolean.class == clazz) {
                throw new IllegalArgumentException("Class was Boolean type, not a Javabean.");
            } else if (Number.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException("Class was Number type, not a Javabean.");
            } else if (JSONObject.class.isAssignableFrom(clazz)) {
                ja = (JSONObject)obj;
            } else if (JSONArray.class.isAssignableFrom(clazz)) {
                ja = (JSONArray)obj;
            } else if (Map.class.isAssignableFrom(clazz)) {
                ja = new JSONObject((Map)obj);
            } else if (Collection.class.isAssignableFrom(clazz)) {
                ja = new JSONArray((Collection)obj);
            } else if (clazz.isArray()) {
                ja = new JSONArray((Object[])obj);
            } 
            else {
                //TODO:  Bean introspection time.
                ja = introspectBean(obj,includeSuperclass, new ArrayList());
            }
        }
        return ja;
    }

    /**
     * Internal method for introspecting a bean and converting it to a JSONAble type.
     * @param obj The Object to inspect.
     * @param includeSuperclass Boolean indicating if superclass properties should be included in the output JSON.                       
     * @param parsedObjects An array list of objects traversed to try and avoid loops in graphs
     * @throws com.granule.json.JSONException Thrown if a JSON conversion error occurs.
     */
    private static JSONArtifact introspectBean(Object obj, boolean includeSuperclass, ArrayList parsedObjects) throws JSONException {
        JSONObject ja = null;
        boolean found = false; 
        for (int i = 0; i < parsedObjects.size(); i++) {
            // Check and try to avoid graphs by parsing the same 
            // object multiple times, which may indicate a cycle.
            Object possibleObj = parsedObjects.get(i);
            if (possibleObj != null && obj == possibleObj) {
                found = true;
                break;
            }
        }

        if (!found) {
            parsedObjects.add(obj);
            ja = new JSONObject();

            Class clazz = obj.getClass();

            ja.put("_type", "JavaClass");
            ja.put("_classname", clazz.getName());

            // Fetch all the methods, based on including superclass or not.
            Method[] methods = null;
            if (includeSuperclass) {
                methods = clazz.getMethods();
            } else {
                methods = clazz.getDeclaredMethods();
            }

            if (methods != null && methods.length > 0) {
                for (int i = 0; i < methods.length; i++) {
                    Method m = methods[i];
                    // Include all superclass methods if requested, 
                    // or only those that are part of the actual declaring class.
                    String mName = m.getName();
                    Class[] types = m.getParameterTypes();

                    // Getter, so we can assume this accesses a field.
                    if (mName.startsWith("get") && mName.length() > 3 && (types == null || types.length == 0)) {
                        String attr = mName.substring(3, mName.length());
                        attr = Character.toLowerCase(attr.charAt(0)) + attr.substring(1, attr.length());
                        try {
                            Object val = m.invoke(obj, null);
                            if (val == null) {
                                ja.put(attr, (Object)null);
                            } else {
                                Class vClazz = val.getClass();
                                if (String.class == vClazz) {
                                    ja.put(attr, val);
                                } else if (Boolean.class == vClazz) {
                                    ja.put(attr, val);
                                } else if (Class.class == vClazz) {
                                    ja.put(attr, ((Class)val).getName());
                                } else if (Number.class.isAssignableFrom(vClazz)) {
                                    ja.put(attr, val);
                                } else if (JSONObject.class.isAssignableFrom(vClazz)) {
                                    ja.put(attr, val);
                                } else if (JSONArray.class.isAssignableFrom(vClazz)) {
                                    ja.put(attr, val);
                                } else if (Map.class.isAssignableFrom(vClazz)) {
                                    ja.put(attr, new JSONObject((Map)val));
                                } else if (Collection.class.isAssignableFrom(vClazz)) {
                                    ja.put(attr, new JSONArray((Collection)obj));
                                } else {
                                    if (val != obj) {
                                        // Try to avoid processing references to itself.
                                        ja.put(attr, introspectBean(val, includeSuperclass, parsedObjects));
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            ja.put(attr, (Object)null);
                        }
                    }
                }
            }
        }
        return ja;
    }

    /**
     * Method to try to convert a JSONObject back into its class representation.
     * @param jo The JSONObject to try to convert back to a class.
     * @throws NullPointerException Thrown if jo is null.
     * @throws com.granule.json.JSONException Thrown if the JSON cannot be converted to a java class.
     * @return An instance of a Java Object that corrisponds to the type in _classname
     */
    public static Object fromJson(JSONObject jo) throws NullPointerException, JSONException {
        Object obj = null;
        if (jo == null) {
            throw new NullPointerException("Input JSONObject cannot be null");
        } else {
            if (jo.get("_classname") != null && "JavaClass".equals(jo.get("_type"))) {
                // Okay, we can try to process this back to a class.
                try {
                    String cName = (String)jo.get("_classname");
                    Class clazz = Class.forName(cName);
                    if (clazz != null) {
                        Method[] methods = clazz.getMethods();

                        obj = clazz.newInstance();
                        Iterator keys = jo.keys();
                        if (keys != null) {
                            while (keys.hasNext()) {
                                String key = (String)keys.next();
                                // Ignore our specially named attributes.
                                if(key != null && !key.equals("_classname") && !key.equals("_type")){
                                    Method m = null;

                                    String setter = "set" + Character.toUpperCase(key.charAt(0)) + key.substring(1, key.length());
                                    Object val = jo.get(key);

                                    if (val != null) {
                                        Class vClazz = val.getClass();

                                        // Try to locate the best matching method.
                                        if (String.class == vClazz) {
                                            // Handle locating a String method.
                                            for (int i = 0; i < methods.length; i++) {
                                                Method tM = methods[i];
                                                if(tM.getName().equals(setter)){
                                                    Class[] mParms = tM.getParameterTypes();
                                                    if (mParms != null && mParms.length == 1) {
                                                        // Possible method, lets check the type.
                                                        Class c = mParms[0];
                                                        if (c == vClazz) {
                                                            // We have a String match, stop here.
                                                            m = tM;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        } else if (Boolean.class == vClazz) {
                                            // Handle locating a boolean method.
                                            for (int i = 0; i < methods.length; i++) {
                                                Method tM = methods[i];
                                                if(tM.getName().equals(setter)){
                                                    Class[] mParms = tM.getParameterTypes();
                                                    if (mParms != null && mParms.length == 1) {
                                                        // Possible method, lets check the type.
                                                        Class c = mParms[0];
                                                        if (c == vClazz || Boolean.TYPE == c) {
                                                            // We have a boolean match, stop here.
                                                            m = tM;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        } else if (Number.class.isAssignableFrom(vClazz)) {
                                            // Handle locating the best-matching number method.
                                            if(Double.class.isAssignableFrom(vClazz)){
                                                for (int i = 0; i < methods.length; i++) {
                                                    Method tM = methods[i];
                                                    if(tM.getName().equals(setter)){
                                                        Class[] mParms = tM.getParameterTypes();
                                                        if (mParms != null && mParms.length == 1) {
                                                            // Possible method, lets check the type.
                                                            Class c = mParms[0];
                                                            if (c == Double.class || Double.TYPE == c) {
                                                                // We have a double match, stop here.
                                                                m = tM;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                                if(m == null){
                                                    // Look for a float assigner and if found, set the value to a Float
                                                    // type.
                                                    for (int i = 0; i < methods.length; i++) {
                                                        Method tM = methods[i];
                                                        if(tM.getName().equals(setter)){
                                                            Class[] mParms = tM.getParameterTypes();
                                                            if (mParms != null && mParms.length == 1) {
                                                                // Possible method, lets check the type.
                                                                Class c = mParms[0];
                                                                if (c == Float.class || Float.TYPE == c) {
                                                                    // We have a Float match, stop here.
                                                                    m = tM;
                                                                    val = new Float(((Number)val).floatValue());
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (Float.class.isAssignableFrom(vClazz)) {
                                                for (int i = 0; i < methods.length; i++) {
                                                    Method tM = methods[i];
                                                    if(tM.getName().equals(setter)){
                                                        Class[] mParms = tM.getParameterTypes();
                                                        if (mParms != null && mParms.length == 1) {
                                                            // Possible method, lets check the type.
                                                            Class c = mParms[0];
                                                            if (c == Float.class || Float.TYPE == c) {
                                                                // We have a Float match, stop here.
                                                                m = tM;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                                if(m == null){
                                                    // Look for a double assigner and if found, set the value to a Float
                                                    // type.
                                                    for (int i = 0; i < methods.length; i++) {
                                                        Method tM = methods[i];
                                                        if(tM.getName().equals(setter)){
                                                            Class[] mParms = tM.getParameterTypes();
                                                            if (mParms != null && mParms.length == 1) {
                                                                // Possible method, lets check the type.
                                                                Class c = mParms[0];
                                                                if (c == Double.class || Double.TYPE == c) {
                                                                    // We have a Double match, stop here.
                                                                    m = tM;
                                                                    val = new Double(((Number)val).doubleValue());
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (Long.class.isAssignableFrom(vClazz)){
                                                for (int i = 0; i < methods.length; i++) {
                                                    Method tM = methods[i];
                                                    if(tM.getName().equals(setter)){
                                                        Class[] mParms = tM.getParameterTypes();
                                                        if (mParms != null && mParms.length == 1) {
                                                            // Possible method, lets check the type.
                                                            Class c = mParms[0];
                                                            if (c == Long.class || Long.TYPE == c) {
                                                                // We have a Long match, stop here.
                                                                m = tM;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                                if(m == null){
                                                    // Look for a integer assigner and if found, set the value to a int
                                                    // type.
                                                    for (int i = 0; i < methods.length; i++) {
                                                        Method tM = methods[i];
                                                        if(tM.getName().equals(setter)){
                                                            Class[] mParms = tM.getParameterTypes();
                                                            if (mParms != null && mParms.length == 1) {
                                                                // Possible method, lets check the type.
                                                                Class c = mParms[0];
                                                                if (c == Integer.class || Integer.TYPE == c) {
                                                                    // We have an int match, stop here.
                                                                    m = tM;
                                                                    val = new Integer(((Number)val).intValue());
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                if(m == null){
                                                    // Look for a short assigner and if found, set the value to a int
                                                    // type.
                                                    for (int i = 0; i < methods.length; i++) {
                                                        Method tM = methods[i];
                                                        if(tM.getName().equals(setter)){
                                                            Class[] mParms = tM.getParameterTypes();
                                                            if (mParms != null && mParms.length == 1) {
                                                                // Possible method, lets check the type.
                                                                Class c = mParms[0];
                                                                if (c == Short.class || Short.TYPE == c) {
                                                                    // We have a short match, stop here.
                                                                    m = tM;
                                                                    val = new Short(((Number)val).shortValue());
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (Integer.class.isAssignableFrom(vClazz)){
                                                for (int i = 0; i < methods.length; i++) {
                                                    Method tM = methods[i];
                                                    if(tM.getName().equals(setter)){
                                                        Class[] mParms = tM.getParameterTypes();
                                                        if (mParms != null && mParms.length == 1) {
                                                            // Possible method, lets check the type.
                                                            Class c = mParms[0];
                                                            if (c == Integer.class || Integer.TYPE == c) {
                                                                // We have an int match, stop here.
                                                                m = tM;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                                if(m == null){
                                                    // Look for a Long assigner and if found, set the value to a int
                                                    // type.
                                                    for (int i = 0; i < methods.length; i++) {
                                                        Method tM = methods[i];
                                                        if(tM.getName().equals(setter)){
                                                            Class[] mParms = tM.getParameterTypes();
                                                            if (mParms != null && mParms.length == 1) {
                                                                // Possible method, lets check the type.
                                                                Class c = mParms[0];
                                                                if (c == Long.class || Long.TYPE == c) {
                                                                    // We have a long match, stop here.
                                                                    m = tM;
                                                                    val = new Long(((Number)val).longValue());
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                if(m == null){
                                                    // Look for a short assigner and if found, set the value to a int
                                                    // type.
                                                    for (int i = 0; i < methods.length; i++) {
                                                        Method tM = methods[i];
                                                        if(tM.getName().equals(setter)){
                                                            Class[] mParms = tM.getParameterTypes();
                                                            if (mParms != null && mParms.length == 1) {
                                                                // Possible method, lets check the type.
                                                                Class c = mParms[0];
                                                                if (c == Short.class || Short.TYPE == c) {
                                                                    // We have a short match, stop here.
                                                                    m = tM;
                                                                    val = new Short(((Number)val).shortValue());
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (Short.class.isAssignableFrom(vClazz)){
                                                for (int i = 0; i < methods.length; i++) {
                                                    Method tM = methods[i];
                                                    if(tM.getName().equals(setter)){
                                                        Class[] mParms = tM.getParameterTypes();
                                                        if (mParms != null && mParms.length == 1) {
                                                            // Possible method, lets check the type.
                                                            Class c = mParms[0];
                                                            if (c == Short.class || Short.TYPE == c) {
                                                                // We have a short match, stop here.
                                                                m = tM;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                                if(m == null){
                                                    // Look for a integer assigner and if found, set the value to a int
                                                    // type.
                                                    for (int i = 0; i < methods.length; i++) {
                                                        Method tM = methods[i];
                                                        if(tM.getName().equals(setter)){
                                                            Class[] mParms = tM.getParameterTypes();
                                                            if (mParms != null && mParms.length == 1) {
                                                                // Possible method, lets check the type.
                                                                Class c = mParms[0];
                                                                if (c == Integer.class || Integer.TYPE == c) {
                                                                    // We have a int match, stop here.
                                                                    m = tM;
                                                                    val = new Integer(((Number)val).intValue());
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                if(m == null){
                                                    // Look for a Long assigner and if found, set the value to a int
                                                    // type.
                                                    for (int i = 0; i < methods.length; i++) {
                                                        Method tM = methods[i];
                                                        if(tM.getName().equals(setter)){
                                                            Class[] mParms = tM.getParameterTypes();
                                                            if (mParms != null && mParms.length == 1) {
                                                                // Possible method, lets check the type.
                                                                Class c = mParms[0];
                                                                if (c == Long.class || Long.TYPE == c) {
                                                                    // We have a long match, stop here.
                                                                    m = tM;
                                                                    val = new Long(((Number)val).longValue());
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } else if (JSONArray.class.isAssignableFrom(vClazz)) {
                                            // Handle determining a collection type to set, which means 
                                            // we need to find a Collection setter.
                                            for (int i = 0; i < methods.length; i++) {
                                                Method tM = methods[i];
                                                if(tM.getName().equals(setter)){
                                                    Class[] mParms = tM.getParameterTypes();
                                                    if (mParms != null && mParms.length == 1) {
                                                        // Possible method, lets check the type.
                                                        Class c = mParms[0];
                                                        if (List.class.isAssignableFrom(c)) {
                                                            // We have a Collections match, so we'll use it.
                                                            m = tM;
                                                            if(c != JSONArray.class){
                                                                // Convert it.  Whee.
                                                                List list = (List)c.newInstance();

                                                                JSONArray array = (JSONArray)val;
                                                                for (int j = 0; j < array.length(); j++) {
                                                                    // Convert each type as needed.
                                                                    Object aVal = array.get(j);
                                                                    if(aVal != null){
                                                                        Class aVClazz = aVal.getClass();
                                                                        if(Number.class.isAssignableFrom(aVClazz) ||
                                                                            Boolean.class.isAssignableFrom(aVClazz) ||
                                                                            String.class.isAssignableFrom(aVClazz)) {
                                                                            list.add(aVal);
                                                                        } else if (JSONObject.class.isAssignableFrom(aVClazz)) {
                                                                            list.add(fromJson((JSONObject)aVal));
                                                                        } else if (JSONObject.class.isAssignableFrom(aVClazz)) {
                                                                            // Not sure what to do here!
                                                                        }
                                                                    } else {
                                                                        list.add(null);
                                                                    }
                                                                }
                                                                val = list;
                                                            }
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        } else if (JSONObject.class.isAssignableFrom(vClazz)) {
                                            // Handle determining a map type to set, if there is one.
                                            JSONObject jObj = (JSONObject)val;
                                            Class vC = val.getClass();
                                            if(jObj.get("_classname") != null && "JavaClass".equals(jObj.get("_type"))){
                                                val = fromJson(jObj);
                                                vC = val.getClass();
                                            }

                                            // Handle locating a boolean method.
                                            for (int i = 0; i < methods.length; i++) {
                                                Method tM = methods[i];
                                                if(tM.getName().equals(setter)){
                                                    Class[] mParms = tM.getParameterTypes();
                                                    if (mParms != null && mParms.length == 1) {
                                                        // Possible method, lets check the type.
                                                        Class c = mParms[0];
                                                        if (c.isAssignableFrom(vC)) {
                                                            // We have setter for the conversion.
                                                            m = tM;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            // Dunno?
                                            throw new JSONException("Unknown type: [" + vClazz.getName() + "]");
                                        }
                                    } else {
                                        try {
                                            m = clazz.getMethod(setter, null);
                                        } catch (NoSuchMethodException nmex){
                                            // Ignore, no setter.
                                        }
                                    }
                                    if (m != null) {
                                        m.invoke(obj, new Object[] { val });
                                    }
                                }
                            }
                        }
                    } else {
                        throw new JSONException("Could not locate class: [" + cName + "]");
                    }
                } catch (Exception ex) {
                    if (ex instanceof JSONException) {
                        throw (JSONException)ex;
                    } else {
                        JSONException jex = new JSONException("Error in converting JSON to Java Class");
                        jex.initCause(ex);
                        throw jex;
                    }
                }
            } else {
                throw new JSONException("Provided JSONObject does not contain attributes '_classname' or '_type'");
            }
        }
        return obj;
    }
}

