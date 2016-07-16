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

import au.com.cybersearch2.cybertete.model.ChatAgent;
import au.com.cybersearch2.cybertete.model.ChatContacts;
import au.com.cybersearch2.cybertete.model.RosterAgent;

/**
 * ChatService
 * Provides services to establish and monitor Chat sessions
 * @author Andrew Bowley
 * 23 Oct 2015
 */
public interface ChatService extends ChatContacts, ChatAgent, RosterAgent
{
    /**
     * Start session
     */
    boolean startSession(LoginController loginController);

    /**
     * Add chat connection listener
     * @param commsStateListener CommsStateListener implementation
     */
    void addChatConnectionListener(CommsStateListener commsStateListener);
    
    /**
     * Add network listener
     * @param networkListener NetworkListener implementation
     */
    void addNetworkListener(NetworkListener networkListener);

    /**
     * Shutdown
     */
    void close();

}
