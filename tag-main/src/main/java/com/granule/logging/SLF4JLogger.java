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

public class SLF4JLogger extends Logger {
    private org.slf4j.Logger logger;

    public SLF4JLogger(String name) {
        logger = org.slf4j.LoggerFactory.getLogger(name);
    }

    public void error(final String message) {
        logger.error(message);
    }

    public void warn(final String message) {
        logger.warn(message);
    }

    public void info(final String message) {
        logger.info(message);
    }

    public void debug(final String message) {
        logger.debug(message);
    }

    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }
}

