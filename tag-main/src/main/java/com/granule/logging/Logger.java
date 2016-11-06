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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Defines the interface for handling log messages.
 * Four <i><a name="LoggingLevel">logging levels</a></i> are defined in this interface.
 * The logging level is specified only by the use of different method names, there is no class or type defining the levels.
 * This makes the code required to wrap other logging frameworks much simpler and more efficient.
 * The four logging levels are:
 * <ul class="SmallVerticalMargin">
 * <li>{@link #error(String) ERROR}
 * <li>{@link #warn(String) WARN}
 * <li>{@link #info(String) INFO}
 * <li>{@link #debug(String) DEBUG}
 * </ul>
 * <br>
 * IMPLEMENTATION NOTE: Ideally the <code>java.util.logging.Logger</code> class could have been used as a basis for logging, even if used to define a wrapper
 * around other logging frameworks.
 * This would have avoided the need to define yet another logging interface, but because <code>java.util.logging.Logger</code> is implemented very poorly,
 * it is quite tricky to extend it as a wrapper.
 * Other logging wrapper frameworks such as <a target="_blank" href="http://www.slf4j.org/">SLF4J</a> or
 * <a target="_blank" href="http://jakarta.apache.org/commons/logging/">Jakarta Commons Logging</a> provide good logging interfaces, but to avoid
 * introducing dependencies it was decided to create this new interface.
 */
public abstract class Logger {
    /**
     * Logs a message at the ERROR level.
     *
     * @param message the message to log.
     */
    public abstract void error(String message);

    /**
     * Logs a message at the WARN level.
     *
     * @param message the message to log.
     */
    public abstract void warn(String message);

    /**
     * Logs a message at the INFO level.
     *
     * @param message the message to log.
     */
    public abstract void info(String message);

    /**
     * Logs a message at the DEBUG level.
     *
     * @param message the message to log.
     */
    public abstract void debug(String message);

    /**
     * Indicates whether logging is enabled at the ERROR level.
     *
     * @return <code>true</code> if logging is enabled at the ERROR level, otherwise <code>false</code>.
     */
    public abstract boolean isErrorEnabled();

    /**
     * Indicates whether logging is enabled at the WARN level.
     *
     * @return <code>true</code> if logging is enabled at the WARN level, otherwise <code>false</code>.
     */
    public abstract boolean isWarnEnabled();

    /**
     * Indicates whether logging is enabled at the INFO level.
     *
     * @return <code>true</code> if logging is enabled at the INFO level, otherwise <code>false</code>.
     */
    public abstract boolean isInfoEnabled();

    /**
     * Indicates whether logging is enabled at the DEBUG level.
     *
     * @return <code>true</code> if logging is enabled at the DEBUG level, otherwise <code>false</code>.
     */
    public abstract boolean isDebugEnabled();

    public void error(String message, Throwable e) {
        error(message + "\n" + getStackTrace(e));
    }

    public void warn(String message, Throwable e) {
        warn(message + "\n" + getStackTrace(e));
    }

    public void info(String message, Throwable e) {
        info(message + "\n" + getStackTrace(e));
    }

    public void debug(String message, Throwable e) {
        debug(message + "\n" + getStackTrace(e));
    }

    private String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }
}