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
package au.com.cybersearch2.e4;

import org.eclipse.e4.core.di.InjectionException;

/**
 * InjectionFactory
 * @author Andrew Bowley
 * 24 May 2016
 */
public interface InjectionFactory
{
    /**
     * Obtain an instance of the specified class and inject it with the context.
     * @param clazz The class to be instantiated
     * @return an instance of the specified class
     * @throws InjectionException if an exception occurred while performing this operation
     */
    <T> T make(Class<T> clazz) throws InjectionException;

}
