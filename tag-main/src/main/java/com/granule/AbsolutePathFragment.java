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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * User: Dario Wunsch
 * Date: 10.01.11
 * Time: 2:10
 */
public class AbsolutePathFragment extends FragmentDescriptor {
    private final String filePath;

    public AbsolutePathFragment(String filename) {
        this.filePath = filename;
    }

    public String getFilePath() {
        return filePath;
    }

    public String toString() {
        return filePath;
    }

    @Override
    public AbsolutePathFragment clone() throws CloneNotSupportedException {
        return new AbsolutePathFragment(filePath);
    }
    
    @Override
	public Reader getContent(IRequestProxy request) throws IOException {
		return new InputStreamReader(new FileInputStream(filePath), "UTF-8");
	}
}
