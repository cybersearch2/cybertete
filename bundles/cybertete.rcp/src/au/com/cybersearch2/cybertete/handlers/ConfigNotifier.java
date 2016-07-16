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
package au.com.cybersearch2.cybertete.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;

import au.com.cybersearch2.cybertete.model.CyberteteEvents;

/**
 * ConfigNotifier
 * Post events for login configuration updates
 * @author Andrew Bowley
 * 29 Apr 2016
 */
@Creatable
public class ConfigNotifier
{
    /** Event broker service */
    @Inject
    IEventBroker eventBroker;

    /**
     * Apply changes to login configuration
     * @param loginConfigEnsemble Login configuration, completion callback and job status
     */
    public void applyChanges(LoginConfigEnsemble loginConfigEnsemble)
    {
        eventBroker.post(CyberteteEvents.UPDATE_LOGIN_CONFIG, loginConfigEnsemble);
    }
 
    /**
     * Save login configuration
     * @param user User JID identifying the configuration
     */
    public void saveLoginConfig(String user)
    {   
        // Fire at the save login session handler.
        eventBroker.post(CyberteteEvents.SAVE_LOGIN_SESSION, user);
    }

    /**
     * Load single signon (Kerberos) configuration
     * @see au.com.cybersearch2.cybertete.dialogs.LoginControlsBase#loadKerberosConfig()
     */
    public void loadKerberosConfig(LoadKerberosConfigEvent configEvent)
    {
        eventBroker.post(CyberteteEvents.LOAD_KERBEROS_CONFIG, configEvent);
    }

    /**
     * Save last user configuration
     */
    public void saveLastUser(String user)
    {
        eventBroker.post(CyberteteEvents.LAST_USER_CONFIG, user);
    }

}
