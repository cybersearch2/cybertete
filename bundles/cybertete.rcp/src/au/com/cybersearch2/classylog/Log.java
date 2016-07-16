/**
    Copyright (C) 2014  www.cybersearch2.com.au

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/> */
package au.com.cybersearch2.classylog;

import java.util.logging.Level;

/**
 * Log
 * Provides a Java util logging interface similar to that of Android log.
 * This is to ease replacement of Java logging with Android logging if desired. See android.util.Log
 * @author Andrew Bowley
 * 11/06/2014
 */
public interface Log
{
    /**
     * Send a VERBOSE log message. Level = FINEST.
     * @param msg The message you would like logged.
     */
    void verbose(String msg);

    /**
     * Send a VERBOSE log message and log the exception. Level = FINEST.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    void verbose(String msg, Throwable tr);

    /**
     * Send a DEBUG log message. Level = FINE.
     * @param msg The message you would like logged.
     */
    void debug(String msg);

    /**
     * Send a DEBUG log message and log the exception. Level = FINE.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    void debug(String msg, Throwable tr);

    /**
     * Send an INFO log message. Level = INFO. Level = INFO.
     * @param msg The message you would like logged.
     */
    void info(String msg);

    /**
     * Send a INFO log message and log the exception. Level = INFO.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    void info(String msg, Throwable tr);

    /**
     * Send a WARN log message. Level = WARNING.
     * @param msg The message you would like logged.
     */
    void warn(String msg);

    /**
     * Send a #WARN log message and log the exception. Level = WARNING.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    void warn(String msg, Throwable tr);
    
    /**
     * Send an ERROR log message. Level = SEVERE.
     * @param msg The message you would like logged.
     */
    void error(String msg);

    /**
     * Send an ERROR log message and log the exception. Level = SEVERE.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    void error(String msg, Throwable tr);
    
    /**
     * Checks to see whether or not a log for the specified tag is loggable at the specified level.
     * 
     * NOTE IF USING Android Log implementation:    
     * Log.isLoggable() will throw an exception if the length of the tag is greater than
     * 23 characters, so trim it if necessary to avoid the exception.
     *
     * @param level The level to check.
     * @return Whether or not that this is allowed to be logged.
     */
    boolean isLoggable(Level level);
    
    /**
     * Set logging level. 
     * NOTE IF USING Android Log implementation, this function is not supported natively by Android.
     */
    void setLevel(Level level);
}
