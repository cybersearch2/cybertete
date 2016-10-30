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

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;

import au.com.cybersearch2.cybertete.model.ChatAgent;
import au.com.cybersearch2.cybertete.model.ChatContacts;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.service.CommunicationsState;
import au.com.cybersearch2.dialogs.SyncErrorDialog;
import au.com.cybersearch2.e4.JobScheduler;

/**
 * StartChatHandler
 * Employs ChatAgent to start a chat with the currently selected contact
 * @author Andrew Bowley
 * 28 Apr 2016
 */
public class StartChatHandler 
{
    /** Chat agent */
    @Inject
    ChatAgent chatAgent;
    /** Job scheduler */
    @Inject 
    JobScheduler jobScheduler;
    /** Error dialog */
    @Inject
    SyncErrorDialog errorDialog;

    /**
     * execute
     * @param selected Currently selected contact or null if none selected
     */
    @Execute
	public void execute(final @Optional @Named(IServiceConstants.ACTIVE_SELECTION) ContactEntry selected) 
	{
        if (selected != null)
             jobScheduler.schedule("Start chat", new Runnable(){

                @Override
                public void run()
                {
                    chatAgent.startChat(selected);
                }
            });
        else
            errorDialog.showError("Error", "No contact selected");
	}

    /**
     * canExecute
     * @param selected Currently selected contact or null if none selected
     * @param communicationsState Receives Chat Service notifications and dispatches events on state transitions
     * @param chatContacts Tracks contacts currently engaged in chats
     * @return flag set true if a contact is selected and able to initiate a chat
     */
    @CanExecute
    public boolean canExecute(
            @Optional @Named(IServiceConstants.ACTIVE_SELECTION) ContactEntry selected, 
            CommunicationsState communicationsState, 
            ChatContacts chatContacts)
    {
        boolean hasSelection = selected != null && !selected.getUser().isEmpty();
        return hasSelection && 
                communicationsState.isOnline() && 
                selected.getPresence().isAvailable() && 
                !chatContacts.chatExists(selected);
    }
}