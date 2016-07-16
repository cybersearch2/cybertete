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
package au.com.cybersearch2.cybertete.model.service;

import au.com.cybersearch2.cybertete.model.ContactEntry;

/**
 * ChatSession
 * Operations of one participant's Chat session 
 * @author Andrew Bowley
 * 25 Oct 2015
 */
public interface ChatSession
{
    /**
     * Send message. 
     * @param body   Message body
     */
    void sendMessage(String body);

    /**
     * Close the Chat session
     * @param message Reason for closing the session
     */
    void close(String message);

    /**
     * Returns participant contact entry
     * @return ContactEntry object
     */
    ContactEntry getParticipant();

    /**
     * Returns the logged in user contact item from the roster
     * @return ContactEntry object
     */
    ContactEntry getSessionOwner();

}
