/**
 * Copyright © 2005-2012 Akiban Technologies, Inc.  All rights reserved.
 * 
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * This program may also be available under different license terms.
 * For more information, see www.akiban.com or contact licensing@akiban.com.
 * 
 * Contributors:
 * Akiban Technologies, Inc.
 */

package com.persistit.logging;

import java.util.EnumMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Wraps a Log4J <code>org.apache.log4j.Logger</code> for Persistit logging.
 * Code to enable default logging through Log4J is shown here: <code><pre>
 *    // refer to any appropriate org.apache.log4j.Logger, for example
 *    Logger logger = Logger.getLogger("com.persistit"); //(for example)
 *    Persistit.setPersistitLogger(new Log4JAdapter(logger));
 * </pre></code>
 * 
 * @version 1.1
 */
public class Log4JAdapter implements PersistitLogger {

    private final static EnumMap<PersistitLevel, Level> LEVEL_MAP = new EnumMap<PersistitLevel, Level>(
            PersistitLevel.class);

    static {
        LEVEL_MAP.put(PersistitLevel.NONE, Level.OFF);
        LEVEL_MAP.put(PersistitLevel.TRACE, Level.TRACE);
        LEVEL_MAP.put(PersistitLevel.DEBUG, Level.DEBUG);
        LEVEL_MAP.put(PersistitLevel.INFO, Level.INFO);
        LEVEL_MAP.put(PersistitLevel.WARNING, Level.WARN);
        LEVEL_MAP.put(PersistitLevel.ERROR, Level.ERROR);
    }

    private final Logger _logger;

    /**
     * Constructs a wrapped Log4J Logger.
     * 
     * @param logger
     *            A <code>Logger</code> to which Persistit log messages will be
     *            directed.
     */
    public Log4JAdapter(final Logger logger) {
        _logger = logger;
    }

    /**
     * Overrides <code>isLoggable</code> to allow control by the wrapped
     * <code>Logger</code>.
     * 
     * @param level
     *            The <code>PersistitLevel</code>
     */
    @Override
    public boolean isLoggable(final PersistitLevel level) {
        return _logger.isEnabledFor(LEVEL_MAP.get(level));
    }

    /**
     * Writes a log message generated by Persistit to the wrapped
     * <code>Logger</code>.
     * 
     * @param level
     *            The <code>PersistitLevel</code>
     * @param message
     *            The message to write to the log.
     */
    @Override
    public void log(final PersistitLevel level, final String message) {
        _logger.log(LEVEL_MAP.get(level), message);
    }

    @Override
    public void open() {
        // Nothing to do - the log is created and destroyed by the embedding
        // application
    }

    @Override
    public void close() {
        // Nothing to do - the log is created and destroyed by the embedding
        // application
    }

    @Override
    public void flush() {
        // Nothing to do - log output is managed by the embedding
        // application
    }
}
