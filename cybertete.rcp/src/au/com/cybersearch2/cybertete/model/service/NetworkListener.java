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

import au.com.cybersearch2.cybertete.security.SslSessionData;

/**
 * NetworkListener
 * Receives events associated with connecting to a network 
 * @author Andrew Bowley
 * 17 Nov 2015
 */
public interface NetworkListener
{

    /**
     * Network disconnected or connection being establishedd
     * @param message Reason for network unavailable
     */
    void onUnavailable(String message);
    
    /**
     * Network connected but insecure
     * @param hostName Host address
     */
    void onConnected(String hostName);
    
    /**
     * Network connected and secure - always preceded by onConnected()
     * sslSessionData Server certificates and cipher suite
     */
    void onSecured(SslSessionData sslSessionData);
    
    /**
     * User credentials verified
     */
    void onAuthenticated();
}
