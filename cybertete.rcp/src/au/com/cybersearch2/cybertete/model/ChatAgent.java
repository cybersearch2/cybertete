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
 * ChatAgent
 * Interacts with remote Chat server on behalf of the user
 * @author Andrew Bowley
 * 11 Apr 2016
 */
public interface ChatAgent
{
    /**
     * Start Chat session for specified participant
     * @param participant Participant's contact entry
     */
    void startChat(ContactEntry participant);
    
    /**
     * Notify change presence of currently logged in user
     * @param presence Model presence
     */
    void sendPresence(Presence presence); 

}
