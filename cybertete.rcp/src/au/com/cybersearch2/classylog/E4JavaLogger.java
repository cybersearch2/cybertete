/**
    Copyright (C) 2015  www.cybersearch2.com.au

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

import org.eclipse.e4.core.services.log.Logger;
import java.util.logging.Level;

/**
 * E4JavaLogger
 * Adapts JavaLogger to E4 Logger
 * @author Andrew Bowley
 * 22 Dec 2015
 */
public class E4JavaLogger extends Logger
{
    private JavaLogger logger;
    
    public E4JavaLogger(Class<?> clazz)
    {
        logger = new JavaLogger(clazz);
    }

    /**
     * @see org.eclipse.e4.core.services.log.Logger#isErrorEnabled()
     */
    @Override
    public boolean isErrorEnabled()
    {
        return logger.isLoggable(Level.SEVERE);
    }

    /**
     * @see org.eclipse.e4.core.services.log.Logger#error(java.lang.Throwable, java.lang.String)
     */
    @Override
    public void error(Throwable t, String message)
    {
        logger.error(message, t);
    }

    /**
     * @see org.eclipse.e4.core.services.log.Logger#isWarnEnabled()
     */
    @Override
    public boolean isWarnEnabled()
    {
        return logger.isLoggable(Level.WARNING);
    }

    /**
     * @see org.eclipse.e4.core.services.log.Logger#warn(java.lang.Throwable, java.lang.String)
     */
    @Override
    public void warn(Throwable t, String message)
    {
        logger.warn(message, t);
    }

    /**
     * @see org.eclipse.e4.core.services.log.Logger#isInfoEnabled()
     */
    @Override
    public boolean isInfoEnabled()
    {
        return logger.isLoggable(Level.INFO);
    }

    /**
     * @see org.eclipse.e4.core.services.log.Logger#info(java.lang.Throwable, java.lang.String)
     */
    @Override
    public void info(Throwable t, String message)
    {
        logger.info(message, t);
    }

    /**
     * @see org.eclipse.e4.core.services.log.Logger#isTraceEnabled()
     */
    @Override
    public boolean isTraceEnabled()
    {
        return logger.isLoggable(Level.FINEST);
    }

    /**
     * @see org.eclipse.e4.core.services.log.Logger#trace(java.lang.Throwable, java.lang.String)
     */
    @Override
    public void trace(Throwable t, String message)
    {
        logger.verbose(message, t);
    }

    /**
     * @see org.eclipse.e4.core.services.log.Logger#isDebugEnabled()
     */
    @Override
    public boolean isDebugEnabled()
    {
        return logger.isLoggable(Level.FINE);
    }

    /**
     * @see org.eclipse.e4.core.services.log.Logger#debug(java.lang.Throwable)
     */
    @Override
    public void debug(Throwable t)
    {
        logger.debug("", t);
    }

    /**
     * @see org.eclipse.e4.core.services.log.Logger#debug(java.lang.Throwable, java.lang.String)
     */
    @Override
    public void debug(Throwable t, String message)
    {
        logger.debug(message, t);
    }

}
