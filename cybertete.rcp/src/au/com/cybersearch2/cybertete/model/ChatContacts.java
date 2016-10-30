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
 * ChatContacts
 * Relates Chats to participants. 
 * Used by handler to start chats to disable chat menu option for a participant already engaged in chatting.
 * @author Andrew Bowley
 * 11 Apr 2016
 */
public interface ChatContacts
{

    /**
     * Returns flag to indicate if chat is in progress for specified user
     * @param participant ContactEntry object of remote user
     * @return boolean
     */
    boolean chatExists(ContactEntry participant);
 
}
