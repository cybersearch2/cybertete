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

import javax.net.ssl.SSLContext;

import au.com.cybersearch2.cybertete.model.ConnectionError;

/**
 * ConnectLoginTask
 * Connects to Chat server, authenticates and loads roster
 * @author Andrew Bowley
 * 28 Oct 2015
 */
public interface ConnectLoginTask 
{
    /**
     * Connect to Chat server, authenticate and load roster 
     * @param sessionDetails Information required for one user, identified by JID, to log in
     * @param sslContext Java SSLContext object or null
     */
    void connectLogin(SessionDetails sessionDetails, SSLContext sslContext);
    /**
     * Returns Cybertete connection error, default is unclassified, but on success is ConnectionError.noError 
     * @return ConnectionError
     */
    ConnectionError getConnectionError();
}
