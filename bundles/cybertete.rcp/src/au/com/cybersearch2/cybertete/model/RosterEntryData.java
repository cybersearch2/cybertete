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
package au.com.cybersearch2.cybertete.model;

/**
 * RosterEntryData
 * Information required by Cybertete from a remote roster entry record
 * @author Andrew Bowley
 * 24 Feb 2016
 */
public interface RosterEntryData
{

    /**
     * Returns friendly name of user
     * @return name
     */
    String getName();
    /**
     * Returns user JID 
     * @return user JID
     */
    String getUser();

}
