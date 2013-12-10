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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

public class HttpHeaders {

	private static final String INTERNET_TIME_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";

	public static void setCacheExpireDate(HttpServletResponse response, int seconds) {
		if (response != null) {
			Calendar cal = new GregorianCalendar();
			cal.add(Calendar.SECOND, seconds);
			response.setHeader("Cache-Control", " public, max-age="
					+ seconds); //+ ", must-revalidate"
			response.setHeader("Expires", htmlExpiresDateFormat().format(
					cal.getTime()));
		}
	}

	public static DateFormat htmlExpiresDateFormat() {
		DateFormat httpDateFormat = new SimpleDateFormat(
				INTERNET_TIME_FORMAT, Locale.US);
		httpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return httpDateFormat;
	}
}
