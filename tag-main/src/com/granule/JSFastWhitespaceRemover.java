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
import java.io.StringWriter;
import java.io.Writer;

/**
 * Simple and Fast JS Compresser. It removes whitespace and comments from source code in safest way is possible.
 * Ignores IE conditional comments, 
 *
 * * @author Jonathan Walsh 
 */
public class JSFastWhitespaceRemover {
	
	public void compress(final Reader in, final Writer out) throws IOException {
		ParseState state = ParseState.TEXT_OK_SKIP_SPACE;
		ParseState stateText = ParseState.TEXT_OK_SKIP_SPACE;
		int c;
		char prevLex = ' ';
		int commentLength = 0;
		int quote = -1;
		while ((c = in.read()) != -1) {

			if (c == '\r' || c == '\n')
				c = '\n';
			else if (c < ' ') {
				continue;
			}

			switch (state) {
			case QUOTE:
				out.write(c);
				if (quote == c) {
					state = ParseState.TEXT_OK_SKIP_SPACE;
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
					state = ParseState.SLASH;
					if (prevLex == '(' || prevLex == ',' || prevLex == '=' || prevLex == ':' || prevLex == '['
							|| prevLex == '!' || prevLex == '&' || prevLex == '|' || prevLex == '?' || prevLex == '{'
							|| prevLex == '}' || prevLex == ';' || prevLex == '\n')
						state = ParseState.MAY_REGULAR_EXPR;
					break;
				} else if (c == ' ' || c == '{' || c == ',' || c == ';' || c == ':' || c=='=' || 
						   c == '(' || c == '[' || c == '!' || c == '&' || c == '|' || c=='?'
						   ) 
					state = ParseState.TEXT_OK_SKIP_SPACE;
				else if (c == '\n')
					state = ParseState.TEXT_BREAK;
				else if (c == '\'' || c == '"') {
					state = ParseState.QUOTE;
					quote = c;
				} else
					state = ParseState.TEXT;
				out.write(c);
				stateText=state;
				break;

			case STAR_IN_COMMENT:
				if (c == '/')
					state = stateText;
				else if (c == '*')
					state = ParseState.STAR_IN_COMMENT;
				else
					state = ParseState.STARTED_COMMENT;
				break;

			case MAY_REGULAR_EXPR:
				if (c == '*') {
					state = ParseState.STARTED_COMMENT;
					commentLength = 0;
				} else if (c == '/') {
					state = ParseState.LINE_COMMENT;
				} else if (c == '\n') {
					state = ParseState.TEXT;
					out.write(c);
				} else {
					state = ParseState.REGULAR_EXPR;
					out.write('/');
					out.write(c);
				}
				break;

			case REGULAR_EXPR:
				if (c == '\n') {
					state = ParseState.TEXT;
				}
				out.write(c);
				break;

			case SLASH:
				if (c == '*') {
					state = ParseState.STARTED_COMMENT;
					commentLength = 0;
					break;
				} else if (c == '/') {
					state = ParseState.LINE_COMMENT;
					break;
				} else {
					out.write('/');
					out.write(c);
					state = ParseState.TEXT;
				}
				break;

			case STARTED_COMMENT:
				if (c == '*')
					state = ParseState.STAR_IN_COMMENT;
				else if (commentLength == 0 && c == '@') {
					out.write('/');
					out.write('*');
					out.write(c);
					state = ParseState.CONDITIONAL_COMMENT;
				}
				commentLength++;
				break;

			case CONDITIONAL_COMMENT:
				if (c == '*')
					state = ParseState.CLOSING_STAR_IN_COND_COMMENT;
				else
					state = ParseState.CONDITIONAL_COMMENT;
				out.write(c);
				break;

			case CLOSING_STAR_IN_COND_COMMENT:
				if (c == '/')
					state = stateText;
				else if (c == '*')
					state = ParseState.CLOSING_STAR_IN_COND_COMMENT;
				else
					state = ParseState.CONDITIONAL_COMMENT;
				out.write(c);
				break;

			case LINE_COMMENT:
				if (c == '\n')
					state= ParseState.TEXT_OK_SKIP_SPACE;
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
			compress(in, out);
		} catch (IOException e) {
			// Having IO exception when reading String would be strange
			throw new RuntimeException(e);
		}
		return out.toString();
	}
}

//States
enum ParseState {
TEXT,
TEXT_OK_SKIP_SPACE,
SLASH,
STARTED_COMMENT,
TAR_IN_COMMENT,
EXT_OK_SKIP_SPACE,
LINE_COMMENT,
TEXT_BREAK,
STAR_IN_COMMENT,
CONDITIONAL_COMMENT,
CLOSING_STAR_IN_COND_COMMENT,
QUOTE,
MAY_REGULAR_EXPR,
REGULAR_EXPR
}
