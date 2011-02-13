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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

/**
 * This class extends the Properties class to provide the ability to load
 * properties file using UTF-8 encoding
 * 
 * @see java.util.Properties
 */
public class Utf8Properties extends Properties {

	private static final long serialVersionUID = 1L;
	public static final String ENCODING = "UTF-8";
	private static final String COMMENT = "#!";

	/**
	 * Possible Separator between key and value of a property in a property file
	 */
	private static final String keyValueSeparators = "=:\r\n\f";

	public Utf8Properties() {
		this(null);
	}

	public Utf8Properties(Properties defaults) {
		this.defaults = defaults;
	}

	public synchronized void load(InputStream inStream) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(inStream,
				ENCODING));
		String line = in.readLine();

		while (line != null) {
			line = removeWhiteSpaces(line);
			if (!line.equals("") && COMMENT.indexOf(line.charAt(0)) == -1) {
				String property = line;
				// Reads the whole property if it is on multiple lines
				while (continueLine(line)) {
					property = property.substring(0, property.length() - 1);
					line = in.readLine();
					property += line;
				}

				if (!property.equals("")) {
					int endOfKey = 0;
					while (endOfKey < property.length()
							&& (keyValueSeparators.indexOf(property
									.charAt(endOfKey)) == -1)) {
						endOfKey++;
					}
					String key = property.substring(0, endOfKey).trim();
					String value = property.substring(endOfKey + 1,
							property.length()).trim();
					put(key, value);
				}
			}
			line = in.readLine();
		}
	}

	public static String removeWhiteSpaces(String line) {
		int index = 0;
		while (index < line.length()
				&& keyValueSeparators.indexOf(line.charAt(index)) != -1) {
			index++;
		}
		return line.substring(index, line.length());
	}

	private boolean continueLine(String line) {
		if (line != null && !line.equals("")) {
			return line.charAt(line.length() - 1) == '\\';
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public void store(OutputStream out, String header) throws IOException {
		BufferedWriter output;
		output = new BufferedWriter(new OutputStreamWriter(out, ENCODING));
		if (header != null) {
			output.write("#" + header);
			output.newLine();
		}
		output.write("#" + new Date());
		output.newLine();
		synchronized (this) {
			Enumeration e = keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String val = (String) get(key);

				output.write(key + "=" + val);
				output.newLine();
			}
		}
		output.flush();
	}
}
