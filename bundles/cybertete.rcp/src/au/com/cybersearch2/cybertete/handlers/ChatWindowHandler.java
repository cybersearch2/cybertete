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
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import au.com.cybersearch2.cybertete.model.ApplicationState;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.service.ChatSession;
import au.com.cybersearch2.cybertete.model.service.ChatListener;
import au.com.cybersearch2.cybertete.views.ChatSessionView;
import au.com.cybersearch2.e4.ModelPart;
import au.com.cybersearch2.e4.ModelPartController;
import au.com.cybersearch2.e4.ModelPartsList;

/**
 * ChatWindowHandler
 * Updates Contacts and Chat Sessions displayed when a new Chat session is started or all Chat sessions are closed.
 * The logic to handle start of chat is complicated by the fact one Chat session view is always placed in the right half
 * of the default view to maintain a constant 50% split with the Contacts view on the left. Additional Chat session views
 * are stacked and can be deleted.
 * @author Andrew Bowley
 * 1 Nov 2015
 */
public class ChatWindowHandler implements ChatListener
{
    /** Flag set true when UIEvents.UILifeCycle.APP_STARTUP_COMPLETE event posted */
    boolean isApplicationStarted;
 
    /** Chat window handler which runs on main thread */
    @Inject
    AsyncChatWindowHandler asyncHandler;
    /** Applies Application services to model parts */
    @Inject
    ModelPartController<ChatSessionView> chatController;

    /**
     * postConstruct
     * @param eventBroker Event broker service
     */
    @PostConstruct
    void postConstruct(IEventBroker eventBroker)
    {
        // The chat window will not be ready to handle events untile application startup is complete
        eventBroker.subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, new EventHandler(){

            @Override
            public void handleEvent(Event event)
            {
                onApplicationStartupComplete();
            }});
    }

    /**
     * onStartChat
     * @see au.com.cybersearch2.cybertete.model.service.ChatListener#onStartChat(au.com.cybersearch2.cybertete.model.service.ChatSession, au.com.cybersearch2.cybertete.model.ContactEntry, java.lang.String)
     */
    @Override
    public void onStartChat(final ChatSession chatSession, final ContactEntry from,
            final String body)
    {
        // Assumes this event will not occur unless application startup is completed. 
        // Remote chat initiation is inhibited by delaying transmission of a presence packet until startup.
        
        // Part to display chat conversation
        ModelPart<ChatSessionView> chatSessionViewPart = null;
        // List of Chat Session Views on left. Should be at lease one in list.
        ModelPartsList<ChatSessionView> partList = chatController.getPartList(ChatSessionView.STACK_ID);
        // Window ID uniquely identifies the Chat Session View assigned to to contact
        String windowId = from.getWindowId();
        // If no ID set and only unrendered window in list, 
        // use default window ID so it can be fetched from Part service
        if ((windowId == null) && (partList.isNewList()))
        {   
             windowId = ChatSessionView.ID;
             from.setWindowId(windowId);
        }
        if (windowId != null)  // Fetch existing view part
            chatSessionViewPart = chatController.findPart(windowId);
        boolean isNewChatView = chatSessionViewPart == null;
        if (isNewChatView)
        {
            // Create window because previous one closed or new one required
            if (windowId == null)
            {
                // Not default window, so append contact id for uniqueness
                windowId = ChatSessionView.ID + from.getId();
                from.setWindowId(windowId);
            }
            chatSessionViewPart = chatController.createPart(windowId, ChatSessionView.class);       
            chatSessionViewPart.setCloseable(true);
        }
        chatSessionViewPart.setLabel(from.getName());
        if (isNewChatView)
            asyncHandler.addToStack(partList, chatSessionViewPart);
        else
        {   // Expect to get View from Part, but fall back to start if part is unrendered
            if (chatSessionViewPart.isRendered())
            {
                asyncHandler.startChat(chatSessionViewPart, chatSession, body);
                return;
            }
        }
        asyncHandler.startChat(windowId, chatSession, body);
     }

    /**
     * Close all Chat Views. The default Chat View is only cleared to preserve application window layout.
     */
    public void closeAllSessions()
    {
        ModelPartsList<ChatSessionView> partList = chatController.getPartList(ChatSessionView.STACK_ID);
            for (ModelPart<ChatSessionView> modelPart: partList.getParts())
            {
                if (modelPart.getRenderedArtifact() != null)
                {
                    if (!modelPart.isCloseable())
                        // Do not hide default window, just clear transcript
                        asyncHandler.clearChatView(modelPart);
                    else 
                        asyncHandler.hideChatView(modelPart);
                }
            }
    }

    /**
     * @see au.com.cybersearch2.cybertete.model.service.ChatListener#onSessionEnd(au.com.cybersearch2.cybertete.model.service.ChatSession, java.lang.String)
     */
    @Override
    public void onSessionEnd(ChatSession chatSession, String message)
    {
        chatSession.getParticipant().setWindowId(null);
    }

    /**
     * Handler for logout. Close all Chat Views 
     * @param nextState Next application state post logout
     */
    @Inject @Optional
    void onLogoutHandler(@UIEventTopic(CyberteteEvents.LOGOUT) ApplicationState nextState)
    {
        if (isApplicationStarted)
            closeAllSessions();
    }

    /**
     * Handler for pause
     * @param info Message to display in Chat View to explain pause
     */
    @Inject @Optional
    void sessionPauseHandler(@UIEventTopic(CyberteteEvents.SESSION_PAUSE) String info)
    {
        handleSessionEvent(CyberteteEvents.SESSION_PAUSE, info);
    }
    
    /**
     * Handler for resume
     * @param info Message to display in Chat View to record resume
     */
    @Inject @Optional
    void sessionResumeHandler(@UIEventTopic(CyberteteEvents.SESSION_RESUME) String info)
    {
        handleSessionEvent(CyberteteEvents.SESSION_RESUME, info);
    }

    /**
     * Handle application startup complete.
     */
    void onApplicationStartupComplete()
    {
        // Set startup complete flag
        isApplicationStarted = true;
    }

    /**
     * Handle session event. Assumes called in main thread.
     * @param type SESSION_RESUME or SESSION_PAUSE
     * @param info Message to show in Chat window
     */
    void handleSessionEvent(String type, String info)
    {
        if (isApplicationStarted)
        {   // TODO - Check for null
            for (ModelPart<ChatSessionView> modelPart: chatController.getPartList(ChatSessionView.STACK_ID).getParts())
            {   // Each chat window to be enabled/disabled according to event type and specified message displayed
                if (modelPart.isRendered())
                {
                    ChatSessionView chatSessionView = modelPart.getRenderedArtifact();
                    boolean viewChanged = 
                        type.equals(CyberteteEvents.SESSION_PAUSE) ?
                        chatSessionView.disable() :
                        chatSessionView.enable();
                    if (viewChanged)
                        chatSessionView.displayMessage(info);
                }
            }
        }
    }

}
