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

import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import au.com.cybersearch2.cybertete.model.ApplicationState;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.dialogs.SyncQuestionDialog;

/**
 * CloseAtStartupEventHandler
 * Presents user with dialog asking for confirmation quit the application.
 * To get here, the user must cancel at the splash screen login.
 * This handler exists because the first opportunity to close the workbench is when application starts.
  * @author Andrew Bowley
 * 4 Mar 2016
 */
public class CloseAtStartupEventHandler implements EventHandler
{
    @Inject
    IEventBroker eventBroker;
    /** Prompts user for yes/no answer */
    @Inject
    SyncQuestionDialog questionDialog;
    
    /**
     * Handle application startup complete
     * @see org.osgi.service.event.EventHandler#handleEvent(org.osgi.service.event.Event)
     */
    @Override
    public void handleEvent(Event event)
    {
        if (questionDialog.ask("Cybertete", LoginHandler.QUIT_PROMPT))
            // Close the application
            eventBroker.post(CyberteteEvents.SHUTDOWN, ApplicationState.shutdown);
        else 
            // Return to Login dialog 
            eventBroker.post(CyberteteEvents.LOGIN, ApplicationState.running);
    }

}
