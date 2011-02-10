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
package com.granule.logging;

public class LoggerFactory {
	private static Logger defaultLogger =null;

	public static Logger getLogger(final String name) {
        if (defaultLogger == null) defaultLogger = determineDefaultLogger(name);
        return defaultLogger;
	}

	public static Logger getLogger(final Class<?> loggedClass) {
		return getLogger(loggedClass.getName());
	}

	private static Logger determineDefaultLogger(String name) {
        if (isClassAvailable("org.slf4j.impl.StaticLoggerBinder")) {
			if (isClassAvailable("org.slf4j.impl.JDK14LoggerFactory")) return new JavaLogger(name);
			if (isClassAvailable("org.slf4j.impl.Log4jLoggerFactory")) return new Log4JLogger(name);
			if (!isClassAvailable("org.slf4j.impl.JCLLoggerFactory")) return new SLF4JLogger(name);
			// fall through to next check if SLF4J is configured to use JCL
		}
		if (isClassAvailable("org.apache.commons.logging.Log")) {
            String logClassName = "";
            try {
                logClassName = Class.forName("org.apache.commons.logging.LogFactory").getMethod("getLog", String.class).invoke(null, "test").
                        getClass().getName();
            } catch (Exception e) { //
            }

            if (logClassName.equals("org.apache.commons.logging.impl.Jdk14Logger")) return new JavaLogger(name);
			if (logClassName.equals("org.apache.commons.logging.impl.Log4JLogger")) return new Log4JLogger(name);
			return new JCLLogger(name);
		}
		if (isClassAvailable("org.apache.log4j.Logger")) return new Log4JLogger(name);
		return new JavaLogger(name);
	}

	private static boolean isClassAvailable(final String className) {
		try {
			Class.forName(className);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

    public static Logger getDefaultLogger() {
        return defaultLogger;
    }

    public static void setDefaultLogger(Logger defaultLogger) {
        LoggerFactory.defaultLogger = defaultLogger;
    }
}
