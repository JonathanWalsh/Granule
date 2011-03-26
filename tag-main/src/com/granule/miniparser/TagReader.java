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
package com.granule.miniparser;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Dario Wunsch
 * Date: 22.12.10
 * Time: 2:09
 */
public class TagReader {
    private List<Tag> tags = new ArrayList<Tag>();

    private String text;

    private static final int BUFFER_SIZE = 1024;

    public TagReader(FileReader reader) throws IOException {
        String text;
        if (reader == null) text = "";
        else {
            try {
                int charsRead;
                final char[] copyBuffer = new char[BUFFER_SIZE];
                final StringBuilder sb = new StringBuilder();
                while ((charsRead = reader.read(copyBuffer, 0, BUFFER_SIZE)) != -1)
                    sb.append(copyBuffer, 0, charsRead);
                text = sb.toString();
            } finally {
                reader.close();
            }
        }
        processText(text);
    }

    public TagReader(String text) {
        processText(text);
    }

    private void processText(String text) {
        this.text = text;
        MiniParser p = new MiniParser(text);
        p.parse(this);
    }

    public String getDebugString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            sb.append("Tag #").append(i).append("\n").append(tags.get(i).getDebugString(text)).append("\n");
        }
        return sb.toString();
    }

    public String getDebugString(String tagName) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Tag t : tags) {
            if (t.getName() != null && t.getName().equalsIgnoreCase(tagName))
                sb.append("Tag #").append(i++).append("\n").append(t.getDebugString(text)).append("\n");
        }
        return sb.toString();
    }

    public List<Tag> getTags() {
        return tags;
    }

    public List<Tag> getTags(String tagName) {
        List<Tag> result = new ArrayList<Tag>();
        if (tagName == null)
            return result;
        for (Tag t : tags)
            if (t.getName().equalsIgnoreCase(tagName))
                result.add(t);
        return result;
    }

    public String getText() {
        return text;
    }

    public void parseAttributes(Tag t) {
        if (t.getName().equalsIgnoreCase("%")) {
            JspDirectiveParser p = new JspDirectiveParser(t, text);
            p.parse();
        }
    }
}
