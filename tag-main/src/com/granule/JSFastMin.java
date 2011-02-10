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
 *
 */
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/*
 * JS Simple and Fast Minimizator
 * 
 */
public class JSFastMin {
	private static final int EOF = -1;

	// States
	private static final int TEXT = 0;
	private static final int SLASH = 1;
	private static final int STARTED_COMMENT = 2;
	private static final int STAR_IN_COMMENT = 3;
	private static final int TEXT_OK_SKIP_SPACE = 4;
	private static final int LINE_COMMENT = 5;
	private static final int TEXT_BREAK = 6;
	private static final int CONDITIONAL_COMMENT = 7;
	private static final int CLOSING_STAR_IN_COND_COMMENT = 8;
	private static final int QUOTE = 9;
	private static final int MAY_REGULAR_EXPR = 10;
	private static final int REGULAR_EXPR = 11;

	public void minimize(final Reader in, final Writer out) throws IOException {
		int state = TEXT_OK_SKIP_SPACE;
        int stateText = TEXT_OK_SKIP_SPACE;
		int c;
		char prevLex = ' ';
		int commentLength = 0;
		int quote = -1;
		while ((c = in.read()) != EOF) {

			if (c == '\r' || c == '\n')
				c = '\n';
			else if (c < ' ') {
				continue;
			}

			switch (state) {
			case QUOTE:
				out.write(c);
				if (quote == c) {
					state = TEXT_OK_SKIP_SPACE;
				}
				break;

			case TEXT_OK_SKIP_SPACE:
				if (c == ' '|| c=='\n') {
					break;
				} 

			case TEXT_BREAK:
				if (c == '\n') {
					break;
				} 

			case TEXT:
				if (c == '/') {
					state = SLASH;
					if (prevLex == '(' || prevLex == ',' || prevLex == '=' || prevLex == ':' || prevLex == '['
							|| prevLex == '!' || prevLex == '&' || prevLex == '|' || prevLex == '?' || prevLex == '{'
							|| prevLex == '}' || prevLex == ';' || prevLex == '\n')
						state = MAY_REGULAR_EXPR;
					break;
				} else if (c == ' ' || c == '{' || c == ',' || c == ';' || c == ':' || c=='=' || 
						   c == '(' || c == '[' || c == '!' || c == '&' || c == '|' || c=='?'
						   ) 
					state = TEXT_OK_SKIP_SPACE;
				else if (c == '\n')
					state = TEXT_BREAK;
				else if (c == '\'' || c == '"') {
					state = QUOTE;
					quote = c;
				} else
					state = TEXT;
				out.write(c);
				stateText=state;
				break;

			case STAR_IN_COMMENT:
				if (c == '/')
					state = stateText;
				else if (c == '*')
					state = STAR_IN_COMMENT;
				else
					state = STARTED_COMMENT;
				break;

			case MAY_REGULAR_EXPR:
				if (c == '*') {
					state = STARTED_COMMENT;
					commentLength = 0;
				} else if (c == '/') {
					state = LINE_COMMENT;
				} else if (c == '\n') {
					state = TEXT;
					out.write(c);
				} else {
					state = REGULAR_EXPR;
					out.write('/');
					out.write(c);
				}
				break;

			case REGULAR_EXPR:
				if (c == '\n') {
					state = TEXT;
				}
				out.write(c);
				break;

			case SLASH:
				if (c == '*') {
					state = STARTED_COMMENT;
					commentLength = 0;
					break;
				} else if (c == '/') {
					state = LINE_COMMENT;
					break;
				} else {
					out.write('/');
					out.write(c);
					state = TEXT;
				}
				break;

			case STARTED_COMMENT:
				if (c == '*')
					state = STAR_IN_COMMENT;
				else if (commentLength == 0 && c == '@') {
					out.write('/');
					out.write('*');
					out.write(c);
					state = CONDITIONAL_COMMENT;
				}
				commentLength++;
				break;

			case CONDITIONAL_COMMENT:
				if (c == '*')
					state = CLOSING_STAR_IN_COND_COMMENT;
				else
					state = CONDITIONAL_COMMENT;
				out.write(c);
				break;

			case CLOSING_STAR_IN_COND_COMMENT:
				if (c == '/')
					state = stateText;
				else if (c == '*')
					state = CLOSING_STAR_IN_COND_COMMENT;
				else
					state = CONDITIONAL_COMMENT;
				out.write(c);
				break;

			case LINE_COMMENT:
				if (c == '\n')
					state=TEXT_OK_SKIP_SPACE;
				break;

			}
			if (c != ' ')
				prevLex = (char) c;
		}
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
