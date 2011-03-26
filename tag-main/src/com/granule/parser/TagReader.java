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
package com.granule.parser;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Dario Wunsch
 * Date: 09.12.2010
 * Time: 7:00:27
 */
public class TagReader {
    private com.granule.miniparser.TagReader tagReader;

    public TagReader(FileReader fileReader) throws IOException {
        tagReader = new com.granule.miniparser.TagReader(fileReader);
    }

    public TagReader(String text) {
        tagReader = new com.granule.miniparser.TagReader(text);
    }

    public List<Element> getAllElements() {
        List<Element> result = new ArrayList<Element>();
        for (com.granule.miniparser.Tag tag : tagReader.getTags())
            result.add(new Element(tag));
        return result;
    }

    public List<Element> getAllElements(String name) {
        List<Element> result = new ArrayList<Element>();
        for (com.granule.miniparser.Tag tag : tagReader.getTags(name))
            result.add(new Element(tag));
        return result;
    }

    public Attributes parseAttributes(Element el) {
        tagReader.parseAttributes(el.element);
        return el.getAttributes();
    }
}
