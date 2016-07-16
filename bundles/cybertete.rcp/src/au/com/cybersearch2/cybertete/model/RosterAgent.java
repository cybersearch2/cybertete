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
 * RosterAgent
 * Performs operations on remoter roster on behalf of user
 * @author Andrew Bowley
 * 11 Apr 2016
 */
public interface RosterAgent
{
    /**
     * Add new contact on remote Chat server. When complete, this roster will be updated via an "add uers" event.
     * @param entry New contact entry
     * @return flag set true if opertion completed or false if contact already exists in remote roster
     * @throws SmackException 
     * @throws XMPPErrorException 
     */
    boolean addContact(ContactEntry entry);

}
