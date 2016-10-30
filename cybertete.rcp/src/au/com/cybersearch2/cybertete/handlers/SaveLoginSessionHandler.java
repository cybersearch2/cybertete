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

import java.security.ProviderException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UIEventTopic;

import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.service.LoginData;
import au.com.cybersearch2.dialogs.SyncErrorDialog;
import au.com.cybersearch2.e4.JobScheduler;

/**
 * SaveLoginSessionHandler
 * Persists current login session configuration
 * @author Andrew Bowley
 * 20 Feb 2016
 */
public class SaveLoginSessionHandler
{
    public static final String DATA_STORAGE_MESSAGE = "Configuration backup to disk failed";

    /** Logger */
    Logger logger;

    /** Container holding information required to log in */
    @Inject
    LoginData loginData;
    @Inject
    SyncErrorDialog errorDialog;
    @Inject 
    JobScheduler jobScheduler;

    /**
     * postConstruct()
     * @param loggerProvider ILoggerProvider implementation
     */
    @PostConstruct
    public void postConstruct(ILoggerProvider loggerProvider)
    {
        logger = loggerProvider.getClassLogger(SaveLoginSessionHandler.class);
    }

    /**
     * Handle save login session event
     * @param user Login user JID
     */
    @Inject @Optional
    void saveLoginSessionHandler(final @UIEventTopic(CyberteteEvents.SAVE_LOGIN_SESSION) String user)
    {
        jobScheduler.schedule("Save login configuration for " + user, new Runnable() {

            @Override
            public void run()
            {
                logger.info("Saving configuration for " + user);
                persistCurrentLoginSession(user);
             }
        });
    }

    /**
     * Save login session of given user
     * @param user User JID
    */
    protected boolean persistCurrentLoginSession(String user)
    {
        try
        {
            loginData.persist(user);
            return true;
        }
        catch(ProviderException e)
        {
            logger.error(DATA_STORAGE_MESSAGE, e.getCause());
            errorDialog.showError("Configuration Error", DATA_STORAGE_MESSAGE);
            return false;
        }
     }
}
