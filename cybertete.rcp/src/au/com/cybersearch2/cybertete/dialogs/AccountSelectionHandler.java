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
package au.com.cybersearch2.cybertete.dialogs;

import org.eclipse.swt.widgets.Listener;

import au.com.cybersearch2.cybertete.model.service.ChatAccount;

/**
 * AccountSelectionHandler
 * Interface for JID Combo listener which has to update a number of controls
 * depending on SSO and account details
 * @author Andrew Bowley
 * 29 Nov 2015
 */
public interface AccountSelectionHandler extends Listener
{
    /**
     * Returns flag set true if SSO authentication applies
     * @return boolean
     */
    boolean isGssapi();
    
    /**
     * Return session details for selected JID
     * @return SessionDetails object
     */
    ChatAccount getAccount();
    
    /**
     * Show username with label
     */
    void showUsername();
    
    /**
     * Show host and port with labels
     */
    void showHostPort();
    
    /**
     * Show planSasl checkbox
     */
    void showPlainSasl();
    
    /**
     * Show all single signon fields controlled by this handler
     */
    void hideSingleSignonFields();
    
    /**
     * Hide all single signon fields controlled by this handler
     */
    void showSingleSignonFields();
}
