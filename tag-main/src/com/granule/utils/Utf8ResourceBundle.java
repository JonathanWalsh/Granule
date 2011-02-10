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
package com.granule.utils;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public abstract class Utf8ResourceBundle {

	public static final ResourceBundle getBundle(String baseName) {
		ResourceBundle bundle = ResourceBundle.getBundle(baseName);
		return createUtf8PropertyResourceBundle(bundle);
	}

	public static final ResourceBundle getBundle(String baseName, Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
		return createUtf8PropertyResourceBundle(bundle);
	}

	public static ResourceBundle getBundle(String baseName, Locale locale, ClassLoader loader) {
		ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
		return createUtf8PropertyResourceBundle(bundle);
	}

	private static ResourceBundle createUtf8PropertyResourceBundle(ResourceBundle bundle) {
		if (!(bundle instanceof PropertyResourceBundle))
			return bundle;

		return new Utf8PropertyResourceBundle((PropertyResourceBundle) bundle);
	}

	private static class Utf8PropertyResourceBundle extends ResourceBundle {
		PropertyResourceBundle bundle;

		private Utf8PropertyResourceBundle(PropertyResourceBundle bundle) {
			this.bundle = bundle;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ResourceBundle#getKeys()
		 */
		public Enumeration<String> getKeys() {
			return bundle.getKeys();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
		 */
		protected Object handleGetObject(String key) {
			String value = (String) bundle.handleGetObject(key);
			try {
				return new String(value.getBytes("ISO-8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// Shouldn't fail - but should we still add logging message?
				return null;
			}
		}

	}
}