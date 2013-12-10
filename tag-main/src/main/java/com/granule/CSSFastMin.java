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

/**
 * 
 * @author Jonathan Walsh 
 *
 */
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public class CSSFastMin {
	private static final int EOF = -1;

	// States
	private static final int REGULAR = 0;
	private static final int SLASH = 1;
	private static final int STARTED_COMMENT = 2;
	private static final int STAR_IN_COMMENT = 3;
	private static final int OK_SKIP_SPACE = 4;

	public void minimize(final Reader in, final Writer out) throws IOException {
		int state = OK_SKIP_SPACE;
        int outCommentState=OK_SKIP_SPACE;
		int c;
		while ((c = in.read()) != EOF) {
		    
		    if (c=='\n'||c=='\r')
			  c=' ';
		    else if (c < ' ') {
				continue;
			}

			switch (state) {
			case OK_SKIP_SPACE:
				if (c ==' ') {
					continue;
				}
			case REGULAR:
				if (c == '/') {
					state = SLASH;
					continue;
				} else if (c==' ' || c=='{' || c==',' || c==';' || c==':' || c=='}') {
					state = OK_SKIP_SPACE;
					outCommentState=OK_SKIP_SPACE;
				}
				else {
					state = REGULAR;
					outCommentState=REGULAR;
				}
				out.write(c);
				break;

			case STAR_IN_COMMENT:
				if (c == '/')
					state = outCommentState;
				else if (c == '*')
                    state = STAR_IN_COMMENT;
				else	
				    state=STARTED_COMMENT;
				continue;

			case SLASH:
				if (c == '*') {
					state = STARTED_COMMENT;
					continue;
				} else {
					out.write('/');
					out.write(c);
					state = outCommentState;
				}
				break;

			case STARTED_COMMENT:
				if (c == '*')
					state = STAR_IN_COMMENT;
				continue;

			}
		}
		out.flush();
	}

	public String minimize(String s) {
		Reader in = new StringReader(s);
		Writer out = new StringWriter(s.length());
		try {
			minimize(in, out);
		} catch (IOException e) {
			// Having IO exception when reading String would be strange
			throw new RuntimeException(e);
		}
		return out.toString();
	}
}
