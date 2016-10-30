/**
    Copyright (C) 2016  www.cybersearch2.com.au

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
package au.com.cybersearch2.cybertete;

/**
 * CyberteteException
 * Unchecked exception for reporting exceptions thrown from the bottom of a call stack.
 * @author Andrew Bowley
 * 19 Feb 2016
 */
public class CyberteteException extends RuntimeException
{
    private static final long serialVersionUID = 7766636354403665647L;

    /**
     * Create CyberteteException object containing only a message
     * @param message
     */
    public CyberteteException(String message)
    {
        super(message);
    }

    /**
     * Create CyberteteException object containing message and a cause
     * @param message
     * @param cause
     */
    public CyberteteException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
