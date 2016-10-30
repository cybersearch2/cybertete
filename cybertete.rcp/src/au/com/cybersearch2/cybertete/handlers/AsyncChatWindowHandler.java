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
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;

import au.com.cybersearch2.cybertete.model.ChatPost;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.Presence;
import au.com.cybersearch2.cybertete.model.service.ChatSession;
import au.com.cybersearch2.cybertete.views.ChatSessionView;
import au.com.cybersearch2.cybertete.views.ContactsView;
import au.com.cybersearch2.e4.ModelPart;
import au.com.cybersearch2.e4.ModelPartController;
import au.com.cybersearch2.e4.ModelPartsList;

/**
 * AsyncChatWindowHandler
 * Performs rendering operations for ChahWindowHandler
 * @author Andrew Bowley
 * 21 Feb 2016
 */
@Creatable
public class AsyncChatWindowHandler
{
    @Inject
    ModelPartController<ChatSessionView> chatController;
    @Inject
    ModelPartController<ContactsView> contactsController;
    /** Executes task on main thread */
    @Inject 
    UISynchronize sync;

    /**
     * Start Chat on previously rendered view
     * @param activePart Part to display chat conversation
     * @param chatSession Operations of one participant's Chat session
     * @param body The message content
     */
    public void startChat(
            final ModelPart<ChatSessionView> activePart,
            final ChatSession chatSession,
            final String body)
    {
        sync.asyncExec(new Runnable() {
            ContactEntry from = chatSession.getParticipant(); 
            public void run() 
            {
                // Expand contact in left pane
                // Activate right pane
                // Enable view if default
                // Set view selection to initiate transcript
                // Update contact's presence to "online"
                // If content provided, display it
                expandContactView(from);
                chatController.activatePart(activePart);
                ChatSessionView chatSessionView = activePart.getRenderedArtifact();
                if (from.getWindowId().equals(ChatSessionView.ID))
                    chatSessionView.enable();
                chatSessionView.setChatSession(chatSession);
                chatSessionView.setSelection(from);
                from.setPresence(Presence.online);
                if ((body != null) && !body.isEmpty())
                    chatSessionView.displayMessage(from, body);
            }
        });
    }

    /**
     * Start Chat on to be rendered view
     * @param toActivateId Identity of part to activate
     * @param chatSession The Chat session operator
     * @param body The message content
     */
    public void startChat(
            final String  toActivateId,
            final ChatSession chatSession,
            final String body)
    {
        sync.asyncExec(new Runnable() {
            ContactEntry from = chatSession.getParticipant(); 
            public void run() 
            {
                // Expand contact in left pane
                // Update contact's presence to "online"
                // Add part to view list if new part
                // Activate right pane
                // Get object created by part
                // Set view selection to initiate transcript
                // If content provided, display it
                expandContactView(from);
                from.setPresence(Presence.online);
                // Part stack must be updated before calling part service to activate view
                // Note: This operation has side effects which require main thread context
                ModelPart<ChatSessionView> activePart = chatController.activatePart(toActivateId);
                if ((activePart != null) && activePart.isRendered())
                {
                    ChatSessionView chatSessionView = activePart.getRenderedArtifact();
                    chatSessionView.setChatSession(chatSession);
                    chatSessionView.setSelection(from);
                    if ((body != null) && !body.isEmpty())
                        chatSessionView.displayMessage(from, body);
                }
             }
        });
    }
 
    /**
     * Add ChatSessionView to given part stack
     * @param partList List of ChatSessionView parts
     * @param activePart ChatSessionView part to add 
     */
    public void addToStack(final ModelPartsList<ChatSessionView> partList, final ModelPart<ChatSessionView> activePart)
    {
        sync.asyncExec(new Runnable() {
            
            public void run() 
            {
                partList.addToStack(activePart.getPart());
            }
         });
    }

    /**
     * Clear Chat view window and disable message editor
     * @param modelPart The view to clear
     */
    public void clearChatView(final ModelPart<ChatSessionView> modelPart)
    {
        sync.asyncExec(new Runnable() {
            
            public void run() 
            {
                ChatSessionView chatView = modelPart.getRenderedArtifact();
                chatView.clear();
                chatView.disable();
           }
        });
    }

    /**
     * Hide Chat view
     * @param modelPart The part owning the view to hide
     */
    public void hideChatView(final ModelPart<ChatSessionView> modelPart)
    {
        sync.asyncExec(new Runnable() {
            
            public void run() 
            {
                chatController.hidePart(modelPart);
            }
        });
    }

    /**
     * Handler for messages
     * @param chatPost Message to display in Chat View
     */
    @Inject @Optional
    void messageHandler(@UIEventTopic(CyberteteEvents.POST_CHAT) ChatPost chatPost)
    {
        String windowId = chatPost.getFrom().getWindowId();
        if (windowId != null)  
        {   // Fetch existing view part
            ModelPart<ChatSessionView> chatSessionViewPart = chatController.findPart(windowId);
            if (chatSessionViewPart.isRendered())
                chatSessionViewPart.getRenderedArtifact().displayMessage(chatPost.getFrom(), chatPost.getBody());
        }
    }
    
    /**
     * Expand contact in left pane. Must run in main thread.
     * @param from Contact to expand
     */
    protected void expandContactView(ContactEntry from)
    {
        ModelPart<ContactsView> contactsViewPart = contactsController.activatePart(ContactsView.ID);
        if ((contactsViewPart != null) && contactsViewPart.isRendered())
            contactsViewPart.getRenderedArtifact().setSelection(from);
    }

}
