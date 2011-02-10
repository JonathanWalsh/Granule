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
package com.granule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * User: Dario Wünsch Date: 22.06.2010 Time: 22:20:28
 */
public abstract class FragmentDescriptor implements Cloneable {

	/**
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public String getContentText(IRequestProxy request) throws IOException {
		StringBuilder fragment = new StringBuilder();
		BufferedReader fileHandle = new BufferedReader(getContent(request));
		char[] buf = new char[4096];
		int i;
		while ((i = fileHandle.read(buf)) != -1) {
			fragment.append(buf, 0, i);
		}
		fileHandle.close();
		return fragment.toString();
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public abstract Reader getContent(IRequestProxy request) throws IOException;

	@Override
	public FragmentDescriptor clone() throws CloneNotSupportedException {
		return null;
	}
}
