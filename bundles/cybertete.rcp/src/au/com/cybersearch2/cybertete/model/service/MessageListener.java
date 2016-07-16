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
 * MessageListener
 * Receives notification of chat messages received
 * @author Andrew Bowley
 * 23 Oct 2015
 */
public interface MessageListener
{
    /**
     * Callback on message received
     * @param from JID of message sender
     * @param body Message text
     */
    void onMessageReceived(ContactEntry from, String body);
    /**
     * Callback on session termination
      * @param message Reason for termination
     */
    void onSessionEnd(String message);
}
