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

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * ServiceThreadManager
 * Operations supporting ServiceThread implementation
 * @author Andrew Bowley
 * 2 May 2016
 */
public interface ServiceThreadManager extends UncaughtExceptionHandler
{
    /**
     * Connect to Chat server, authenticate and load roster
     * @param serviceLoginTask The task to perform the login
     */
    void connectLogin(ConnectLoginTask serviceLoginTask);
    
    /**
     * Add network listener to Chat service
     * @param networkListener ChatLoginProgressTask object
     */
    void addNetworkListener(NetworkListener networkListener);
    
    /**
     * Remove network listener from Chat service
     * @param networkListener ChatLoginProgressTask object
     */
    void removeNetworkListener(NetworkListener networkListener);

    /** Handle user cancel */
    void onInterrupt();

}
