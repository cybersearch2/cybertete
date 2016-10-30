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

import au.com.cybersearch2.cybertete.model.service.ConnectLoginTask;
import au.com.cybersearch2.cybertete.model.service.ServiceThreadManager;
import au.com.cybersearch2.cybertete.service.LoginData;

/**
 * InteractiveLogin
 * Manages login dialogs
 * @author Andrew Bowley
 * 19 Feb 2016
 */
public interface InteractiveLogin
{
    /**
     * Set connection error to know when to clear password field (ie. on authentication error)
     * @param connectionError The ConnectionError value 
     */
    void setConnectionError(ConnectionError connectionError);

    /**
     * Open login dialog and return code upon dismissal
     * @param loginData Container holding information required to log in
     * @return int
     */
    int open(LoginData loginData);
    
    /**
     * Save login configuration and close dialog
     * @param user User JID identifying the configuration
     */
    void save(String user);

    /**
     * Close dialog
     */
    void close();

    /**
     * Flag set true if Advanced button pressed
     * @return boolean
     */
    boolean showAdvanceOptions();
    
    /**
     * Clear controls for advanced options
     */
    void clearAdvanceOptions();

    /**
     * Show progress monitor dialog
     * @param serviceThreadManager Manager to handle unexpected exceptions
     * @param serviceLoginTask  Connects to Chat server, authenticates and loads roster
     */
    void displayProgressDialog(ServiceThreadManager serviceThreadManager, ConnectLoginTask serviceLoginTask);
}
