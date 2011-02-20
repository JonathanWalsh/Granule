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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * User: Dario Wunsch
 * Date: 10.01.11
 * Time: 2:11
 */
public class InternalFragment extends FragmentDescriptor {
    private final String text;

    public InternalFragment(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String toString() {
        return text;
    }

    @Override
    public String getContentText(IRequestProxy request) throws IOException {
        return text;
    }

    public FragmentDescriptor clone() throws CloneNotSupportedException {
        return new InternalFragment(text);
    }

	@Override
	public Reader getContent(IRequestProxy request) throws IOException {
		return new StringReader(text);
	}
}
