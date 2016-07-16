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

import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.Presence;
import au.com.cybersearch2.cybertete.service.CommunicationsState;

/**
 * RunAtStartupEventHandler
 * Completes application startup once Main window is up. Initiates switch to Offline perspective if offline.
 * @author Andrew Bowley
 * 4 Mar 2016
 */
public class RunAtStartupEventHandler implements EventHandler
{
    /** Receives Chat Service notifications and dispatches events on state transitions */
    @Inject
    CommunicationsState communicationsState;
    /** Event broker service */
    @Inject
    IEventBroker eventBroker;

    /**
     * @see org.osgi.service.event.EventHandler#handleEvent(org.osgi.service.event.Event)
     */
    @Override
    public void handleEvent(Event event)
    {
        if (communicationsState.isOnline())
            // Online runs in Default perspective
            eventBroker.post(CyberteteEvents.PRESENCE, Presence.online);
        else
            // Offline runs in Offline perspective, which shows Advanced Login view 
            eventBroker.post(CyberteteEvents.PERSPECTIVE_OFFLINE, "Application startup");
    }

}
