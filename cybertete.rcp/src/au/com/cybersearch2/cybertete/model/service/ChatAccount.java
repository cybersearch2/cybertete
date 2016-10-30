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
package au.com.cybersearch2.cybertete.model.service;

/**
 * ChatAccount
 * Details which appear on normal login dialog. This is a read-only interface to SessionDetails
 * @author Andrew Bowley
 * 19 Apr 2016
 */
public interface ChatAccount
{
    /**
     * @return user JID
     */
    String getJid();
    
    /**
     * @return the password
     */
    String getPassword();
    
    /**
     * Returns authentication user ID 
     * @return authcid or null if not used
     */
    String getAuthcid();
    
    /**
     * Returns host
     * @return host or null if not used
     */
    String getHost();
    
    /**
     * @return the port
     */
    int getPort();
 
    /**
     * @return Flag set true if plain SASL authentication allowed
     */
     boolean isPlainSasl();
    
    /**
     * Returns flag set true if single signon (GSSAPI implementation)
     * @return boolean
     */
    boolean isGssapi();
}
