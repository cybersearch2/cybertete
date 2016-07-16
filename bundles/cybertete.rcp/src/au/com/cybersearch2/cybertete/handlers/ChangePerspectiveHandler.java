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
package au.com.cybersearch2.cybertete.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.core.services.log.Logger;

import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.Presence;
import au.com.cybersearch2.e4.ApplicationModel;

/**
 * ChangePerspectiveHandler
 * Toggles perspective between Default and Offline. The latter change causes secondary offline posts.
 * Requests to change perspective before the application model is loaded will be ignored.
 * Such occurrences will be logged to help with debugging.
 * @author Andrew Bowley
 * 26 Nov 2015
 */
public class ChangePerspectiveHandler
{
    /** ID of Default perspective */
    public static final String DEFAULT_PERSPECTIVE = "au.com.cybersearch2.cybertete.perspective.default";
    /** ID of Offline perspective */
    public static final String OFFLINE_PERSPECTIVE = "au.com.cybersearch2.cybertete.perspective.offline";

    /** Logger */
    Logger logger;
    
    /** Application model artifacts - only available when @ProcessAdditions E4 Lifecycle stage reached */
    @Inject
    ApplicationModel applicationModel;
    /** Event broker service */
    @Inject
    IEventBroker eventBroker;

    /**
     * postConstruct
     * @param loggerProvider Logger factory
     */
    @PostConstruct
    void postConstruct(ILoggerProvider loggerProvider)
    {
        logger = loggerProvider.getClassLogger(ChangePerspectiveHandler.class);
    }

    /**
     * Handle switch to Default perspective
     * @param message Reason for switch
     */
    @Inject @Optional
    void onDefaultHandler(@UIEventTopic(CyberteteEvents.PERSPECTIVE_DEFAULT) String message)
    {
        if (!applicationModel.switchPerspective(DEFAULT_PERSPECTIVE))
            logger.error("Unable to change to Default Persective with message \"" + message + "\""); 
    }
    
    /**
     * Handle switch to Offline perspective
     * @param message Reason for switch
     */
    @Inject @Optional
    void onOfflineHandler(@UIEventTopic(CyberteteEvents.PERSPECTIVE_OFFLINE) String message)
    {
        if (!applicationModel.switchPerspective(OFFLINE_PERSPECTIVE))
            logger.error("Unable to change to Offline Persective with message \"" + message + "\""); 
        // Post communications offline 
        eventBroker.post(CyberteteEvents.COMMS_OFFLINE, message);
        // Post presence offline
        eventBroker.post(CyberteteEvents.PRESENCE, Presence.offline);
    }
   
}
