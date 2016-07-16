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

/**
 * CommsStateListener
 * Receives notifications of communications state changes and tracks online status
 * @author Andrew Bowley
 * 15 Nov 2015
 */
public interface CommsStateListener
{
    /** Message used to indicate communications down from normal shutdown */
    public static final String CONNECTION_CLOSED = "Connection closed";
   
    /**
     * Communications up for Chat traffic
     * @param hostDomain Host address
     * @param user User's JID
     */
    void onCommsUp(String hostDomain, String user);
    
    /**
     * Communications down for normal shutdown or error
     * @param hostDomain Host address
     */
    void onCommsDown(String hostDomain);
    
    /**
     * Communications recovery initiated
     * @param hostDomain Host address
     */
    void onEstablishComms(String hostDomain);
    
    /**
     * Returns flag set true if communications is online (last state change was "onCommsUp")
     * @return boolean
     */
    boolean isOnline();
}
