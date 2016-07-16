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
import javax.inject.Named;
import javax.xml.ws.WebServiceException;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.window.Window;

import au.com.cybersearch2.controls.CustomDialog;
import au.com.cybersearch2.cybertete.dialogs.AddContactControls;
import au.com.cybersearch2.cybertete.dialogs.DialogFactory;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactGroup;
import au.com.cybersearch2.cybertete.model.RosterAgent;
import au.com.cybersearch2.cybertete.service.CommunicationsState;
import au.com.cybersearch2.cybertete.service.XmppConnectionException;
import au.com.cybersearch2.dialogs.SyncErrorDialog;
import au.com.cybersearch2.e4.JobScheduler;

/**
 * AddContactHandler
 * Adds a contact to the roster. The group is automatically taken from currently selected item in the contacts view.
 * @author Andrew Bowley
 * 20 Apr 2016
 */
public class AddContactHandler 
{
    /** Logger */
    Logger logger;
 
    /** Roster agent provides add contact function */
    @Inject
    RosterAgent rosterAgent;
    /** Currently selected contact group. May be null. */
    @Inject @Optional
    @Named(IServiceConstants.ACTIVE_SELECTION)
    ContactGroup group;
    /** Currently selected contact entry. May be null. */
    @Inject @Optional
    @Named(IServiceConstants.ACTIVE_SELECTION)
    ContactEntry contactEntry;
    /** Creates and schedules a job given a job name and task to perform */
    @Inject 
    JobScheduler jobScheduler;
    /** Syncs with task run in UI thread */
    @Inject
    UISynchronize sync;
    /** Displays error dialog */
    @Inject
    SyncErrorDialog errorDialog;

    /**
     * postConstruct
     * @param loggerProvider Logger factory
     */
    @PostConstruct
    void postConstruct(ILoggerProvider loggerProvider) 
    {
        logger = loggerProvider.getClassLogger(AddContactHandler.class);
    }

    /**
     * Execute
     * @param dialogFactory Creates instances of application-specific dialogs
     */
	@Execute
	public void execute(final DialogFactory dialogFactory)
	{
	    // Paranoid check
        if ((group == null) && (contactEntry == null))
        {
            errorDialog.showError("Error", "No group selected");
            return;
        }
        // If contact entry selected, then get it's parent to obtain group
        if (group == null)
            group = (ContactGroup)contactEntry.getParent();
	    jobScheduler.schedule("Add Contact Job", new Runnable(){

            @Override
            public void run()
            {
                CustomDialog<AddContactControls> addContactDialog = 
                        dialogFactory.addContactDialogInstance("Add Contact to " + group.getName());
                AddContactControls addContactControls = addContactDialog.getCustomControls();
                if (addContactDialog.syncOpen(sync) == Window.OK) 
                {
                    ContactEntry entry = 
                        new ContactEntry(addContactControls.getNickname(), 
                                         addContactControls.getJid(), 
                                         group);
                    // Don't call entry.addSelfToParent() as this entry will be discarded.
                    // Another contact entry object will be created when the local roster is updated.
                    addContact(entry);
                }
            }});
	}

    /**
	 * canExecute
	 * @param communicationsState Provides application with online status
	 * @return flag set true if add contact is possible
	 */
    @CanExecute
    public boolean canExecute(CommunicationsState communicationsState)
    {
        return communicationsState.isOnline() &&
                !((group == null) && (contactEntry == null));
    }

    /**
     * Add contact using supplied contact entry
     * @param entry New contact entry (addSelfToParent() NEVER to be invoked). 
     */
    void addContact(ContactEntry entry)
    {
        try
        {
            if (!rosterAgent.addContact(entry))
               errorDialog.showError("Already exists", "Contact \"" + entry.getUser() + "\" already exists in roster");
        }
        catch (final XmppConnectionException e)
        {
            logger.error(e.getCause(), e.getMessage());
            errorDialog.showError(e.getMessage(), e.getDetails());
        }
        catch (final WebServiceException e)
        {
            logger.error(e, "Error adding " + entry.getUser() + " to roster");
            errorDialog.showError("Error", e.getMessage());
        }
    }
}
