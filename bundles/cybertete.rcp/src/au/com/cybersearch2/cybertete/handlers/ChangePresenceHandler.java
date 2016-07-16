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
import javax.xml.ws.WebServiceException;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.jface.window.Window;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import au.com.cybersearch2.controls.CustomDialog;
import au.com.cybersearch2.cybertete.dialogs.DialogFactory;
import au.com.cybersearch2.cybertete.dialogs.PresenceControls;
import au.com.cybersearch2.cybertete.model.ChatAgent;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.Presence;
import au.com.cybersearch2.cybertete.service.SessionOwner;
import au.com.cybersearch2.e4.JobScheduler;

/**
 * Handles user click change presence menu option events which change presence such as going offline
 * ChangePresenceHandler
 * @author Andrew Bowley
 * 22 Apr 2016
 */
public class ChangePresenceHandler 
{
    /** Current presence value */
    Presence presence;
    /** Looger */
    Logger logger;
 
    /** Event broker service */
    @Inject
    IEventBroker eventBroker;
    /** Agent to update presence on remote roster */
    @Inject
    ChatAgent chatAgent;
    /** Contains contact entry of logged in user */
    @Inject
    SessionOwner sessionOwner;
    /** Job scheduler */
    @Inject 
    JobScheduler jobScheduler;
    /** Syncs with task run in UI thread */
    @Inject
    UISynchronize sync;

    /**
     * postConstruct
     * @param loggerProvider Logger factory
     */
    @PostConstruct
    public void postConstruct(ILoggerProvider loggerProvider)
    {
        logger = loggerProvider.getClassLogger(ChangePresenceHandler.class);
        // Current presence is online but notification delayed until application startup is completed
        presence = Presence.online;
        eventBroker.subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, new EventHandler(){

            @Override
            public void handleEvent(Event event)
            {
                notifyPresence();
            }});
    }

    /**
     * execute
     * @param dialogFactory Creates instances of application-specific dialogs
     */
    @Execute
	public void execute(DialogFactory dialogFactory) 
	{
        // Display dialog to let user change presence
        final CustomDialog<PresenceControls> presenceDialog = 
            dialogFactory.presenceDialogInstance(PresenceControls.TITLE);
        jobScheduler.schedule(PresenceControls.TITLE, new Runnable(){

            @Override
            public void run()
            {
        	    if (presenceDialog.syncOpen(sync) == Window.OK)
        	        setPresence(presenceDialog.getCustomControls().getPresence());
            }});
	}

    /**
     * Handle presence event
     * @param newPresence
     */
    @Inject @Optional
    void presenceHandler(@UIEventTopic(CyberteteEvents.PRESENCE) Presence newPresence)
    {
        setPresence(newPresence);
    }
 
    /**
     * Notify change of presence
     */
    public void notifyPresence()
    {
        // Post presence event to status line    
        eventBroker.post(CyberteteEvents.PRESENCE + "/" + presence.toString(), presence.getDisplayText());
        sendPresence();
    }

    /**
     * Send presence update to remote roster
     */
    void sendPresence()
    {
        jobScheduler.schedule("Send presence update", new Runnable(){

            @Override
            public void run()
            {
                try
                {
                    // Notify remote roster
                    chatAgent.sendPresence(presence);
                }
                catch (WebServiceException e)
                {   // This exception is not expected
                    logger.error(e.getCause(), e.getMessage());
                }
            }});
    }
    
    /**
     * Update presence to specified value if different from current
     * @param newPresence Presence
     */
    private void setPresence(Presence newPresence)
    {
        if (newPresence != presence)
        {
            presence = newPresence;
            // Update session owner
            sessionOwner.getContact().setPresence(presence);
            // Notify change
            notifyPresence();
        }
    }
}