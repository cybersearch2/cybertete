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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import au.com.cybersearch2.cybertete.model.ApplicationState;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.service.ChatSession;
import au.com.cybersearch2.cybertete.views.ChatSessionView;
import au.com.cybersearch2.e4.ApplicationModel;
import au.com.cybersearch2.e4.ModelPart;
import au.com.cybersearch2.e4.ModelPartController;
import au.com.cybersearch2.e4.ModelPartsList;

/**
 * ChatWindowHandlerTest
 * @author Andrew Bowley
 * 21 Feb 2016
 */
public class ChatWindowHandlerTest
{
    private static final String NICKNAME = "Micky";
    //private static final String CHAT_SESSION_VIEW_URI = "bundleclass://au.com.cybersearch2.cybertete/au.com.cybersearch2.cybertete.views.ChatSessionView";
    private static final String INFO = "Chat Service unavailable";

    @Test
    public void test_postConstruct()
    {
        ChatWindowHandler underTest = new ChatWindowHandler();
        ApplicationModel applicationModel = mock(ApplicationModel.class);
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.postConstruct(eventBroker);
        ArgumentCaptor<EventHandler> handlerCaptor = ArgumentCaptor.forClass(EventHandler.class);
        verify(eventBroker).subscribe(eq(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE), handlerCaptor.capture());
        Event event = mock (Event.class);
        handlerCaptor.getValue().handleEvent(event);
        assertThat(underTest.isApplicationStarted).isTrue();
        // Check handleSessionEvent() not called
        verify(applicationModel, times(0)).getModelService();
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void test_onStartChat_first_time()
    {
        // void onStartChat(ChatSession chatSession, ContactEntry from, String body)
        // First time from.windowId == null and only one unrendered Chat Session View in stack
        AsyncChatWindowHandler asyncHandler = mock(AsyncChatWindowHandler.class);
        ModelPartController<ChatSessionView> chatController = mock(ModelPartController.class);
        ChatSession chatSession = mock(ChatSession.class);
        ContactEntry from = mock(ContactEntry.class);
        when(from.getName()).thenReturn(NICKNAME);
        ModelPart<ChatSessionView> chatSessionViewPart = mock(ModelPart.class);
        when(chatSessionViewPart.isRendered()).thenReturn(false);
        String body = "";
        ChatWindowHandler chatWindowHandler = new ChatWindowHandler();
        chatWindowHandler.asyncHandler = asyncHandler;
        chatWindowHandler.chatController = chatController;
        ModelPartsList<ChatSessionView> partList = mock(ModelPartsList.class);
        when(partList.isNewList()).thenReturn(true);
        when(chatController.getPartList(ChatSessionView.STACK_ID)).thenReturn(partList);
        when(chatController.findPart(ChatSessionView.ID)).thenReturn(chatSessionViewPart);
        chatWindowHandler.onStartChat(chatSession, from, body);
        verify(chatSessionViewPart).setLabel(NICKNAME);
        verify(asyncHandler).startChat(ChatSessionView.ID, chatSession, body);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void test_onStartChat_second_time()
    {
        // void onStartChat(ChatSession chatSession, ContactEntry from, String body)
        // Second time from.windowId == ChatSessionView.ID and default, rendered Chat Session View in stack
        AsyncChatWindowHandler asyncHandler = mock(AsyncChatWindowHandler.class);
        ModelPartController<ChatSessionView> chatController = mock(ModelPartController.class);
        ChatSession chatSession = mock(ChatSession.class);
        ContactEntry from = mock(ContactEntry.class);
        when(from.getName()).thenReturn(NICKNAME);
        // Different here from first time
        when(from.getWindowId()).thenReturn(ChatSessionView.ID);
        ModelPart<ChatSessionView> chatSessionViewPart = mock(ModelPart.class);
        // Different here from first time
        when(chatSessionViewPart.isRendered()).thenReturn(true);
        String body = "";
        ChatWindowHandler chatWindowHandler = new ChatWindowHandler();
        chatWindowHandler.asyncHandler = asyncHandler;
        chatWindowHandler.chatController = chatController;
        ModelPartsList<ChatSessionView> partList = mock(ModelPartsList.class);
        when(partList.isNewList()).thenReturn(false);
        when(chatController.getPartList(ChatSessionView.STACK_ID)).thenReturn(partList);
        when(chatController.findPart(ChatSessionView.ID)).thenReturn(chatSessionViewPart);
        chatWindowHandler.onStartChat(chatSession, from, body);
        verify(chatSessionViewPart).setLabel(NICKNAME);
        verify(asyncHandler).startChat(chatSessionViewPart, chatSession, body);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void test_onStartChat_first_time_new_view()
    {
        // void onStartChat(ChatSession chatSession, ContactEntry from, String body)
        // First time from.windowId == null and default Chat Session View in stack
        AsyncChatWindowHandler asyncHandler = mock(AsyncChatWindowHandler.class);
        ModelPartController<ChatSessionView> chatController = mock(ModelPartController.class);
        ChatSession chatSession = mock(ChatSession.class);
        ContactEntry from = mock(ContactEntry.class);
        when(from.getId()).thenReturn(1);
        when(from.getName()).thenReturn(NICKNAME);
        ModelPart<ChatSessionView> defaultChatSessionViewPart = mock(ModelPart.class);
        when(defaultChatSessionViewPart.isRendered()).thenReturn(true);
        String chatViewId = ChatSessionView.ID + "1";
        ModelPart<ChatSessionView> chatSessionViewPart = mock(ModelPart.class);
        when(chatController.createPart(chatViewId, ChatSessionView.class)).thenReturn(chatSessionViewPart);
        String body = "";
        ChatWindowHandler chatWindowHandler = new ChatWindowHandler();
        chatWindowHandler.asyncHandler = asyncHandler;
        chatWindowHandler.chatController = chatController;
        ModelPartsList<ChatSessionView> partList = mock(ModelPartsList.class);
        when(partList.isNewList()).thenReturn(false);
        when(chatController.getPartList(ChatSessionView.STACK_ID)).thenReturn(partList);
        chatWindowHandler.onStartChat(chatSession, from, body);
        verify(from).setWindowId(chatViewId);
        verify(chatSessionViewPart).setLabel(NICKNAME);
        verify(chatSessionViewPart).setCloseable(true);
        verify(asyncHandler).addToStack(partList, chatSessionViewPart);
        verify(asyncHandler).startChat(chatViewId, chatSession, body);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void test_onStartChat_second_time_new_view()
    {
        // void onStartChat(ChatSession chatSession, ContactEntry from, String body)
        // Second time from.windowId == ChatSessionView.ID + "1" and 2 Chat Session Views in stack
        AsyncChatWindowHandler asyncHandler = mock(AsyncChatWindowHandler.class);
        ModelPartController<ChatSessionView> chatController = mock(ModelPartController.class);
        ChatSession chatSession = mock(ChatSession.class);
        ContactEntry from = mock(ContactEntry.class);
        when(from.getId()).thenReturn(1);
        when(from.getName()).thenReturn(NICKNAME);
        ModelPart<ChatSessionView> defaultChatSessionViewPart = mock(ModelPart.class);
        when(defaultChatSessionViewPart.isRendered()).thenReturn(true);
        String chatViewId = ChatSessionView.ID + "1";
        when(from.getWindowId()).thenReturn(chatViewId);
        ModelPart<ChatSessionView> chatSessionViewPart = mock(ModelPart.class);
        when(chatSessionViewPart.isRendered()).thenReturn(true);
        when(chatController.findPart(chatViewId)).thenReturn(chatSessionViewPart);
        String body = "";
        ChatWindowHandler chatWindowHandler = new ChatWindowHandler();
        chatWindowHandler.asyncHandler = asyncHandler;
        chatWindowHandler.chatController = chatController;
        ModelPartsList<ChatSessionView> partList = mock(ModelPartsList.class);
        when(partList.isNewList()).thenReturn(false);
        when(chatController.getPartList(ChatSessionView.STACK_ID)).thenReturn(partList);
        chatWindowHandler.onStartChat(chatSession, from, body);
        verify(chatSessionViewPart).setLabel(NICKNAME);
        verify(asyncHandler, times(0)).addToStack(partList, chatSessionViewPart);
        verify(asyncHandler).startChat(chatSessionViewPart, chatSession, body);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void test_onStartChat_reopen_closed_view()
    {
        // void onStartChat(ChatSession chatSession, ContactEntry from, String body)
        // First time from.windowId == ChatSessionView.ID + "1" and default Chat Session View in stack
        AsyncChatWindowHandler asyncHandler = mock(AsyncChatWindowHandler.class);
        ModelPartController<ChatSessionView> chatController = mock(ModelPartController.class);
        ChatSession chatSession = mock(ChatSession.class);
        ContactEntry from = mock(ContactEntry.class);
        ModelPart<ChatSessionView> defaultChatSessionViewPart = mock(ModelPart.class);
        when(defaultChatSessionViewPart.isRendered()).thenReturn(true);
        String chatViewId = ChatSessionView.ID + "1";
        when(from.getWindowId()).thenReturn(chatViewId);
        when(from.getId()).thenReturn(1);
        when(from.getName()).thenReturn(NICKNAME);
        ModelPart<ChatSessionView> chatSessionViewPart = mock(ModelPart.class);
        when(chatController.createPart(chatViewId, ChatSessionView.class)).thenReturn(chatSessionViewPart);
        String body = "";
        ChatWindowHandler chatWindowHandler = new ChatWindowHandler();
        chatWindowHandler.asyncHandler = asyncHandler;
        chatWindowHandler.chatController = chatController;
        ModelPartsList<ChatSessionView> partList = mock(ModelPartsList.class);
        when(partList.isNewList()).thenReturn(false);
        when(chatController.getPartList(ChatSessionView.STACK_ID)).thenReturn(partList);
        chatWindowHandler.onStartChat(chatSession,from, body);
        verify(from, times(0)).setWindowId(chatViewId);
        verify(chatSessionViewPart).setLabel(NICKNAME);
        verify(chatSessionViewPart).setCloseable(true);
        verify(asyncHandler).addToStack(partList, chatSessionViewPart);
        verify(asyncHandler).startChat(chatViewId, chatSession, body);
    }
    
    @Test
    public void test_closeAllSessions()
    {
        doCloseAllSessions(false);
    }
    
    @Test
    public void test_onLogoutHandler()
    {
        doCloseAllSessions(true);
    }

    @Test
    public void test_sessionPauseHandler()
    {
        doHandleSessionEvent(CyberteteEvents.SESSION_PAUSE, true);
        doHandleSessionEvent(CyberteteEvents.SESSION_PAUSE, false);
    }
    
    @Test
    public void test_sessionResumeHandler()
    {
        doHandleSessionEvent(CyberteteEvents.SESSION_RESUME, true);
        doHandleSessionEvent(CyberteteEvents.SESSION_RESUME, false);
    }
 
    @Test
    public void test_handleSessionEvent_not_started()
    {
        ChatWindowHandler chatWindowHandler = new ChatWindowHandler();
        chatWindowHandler.handleSessionEvent(CyberteteEvents.SESSION_PAUSE, "info");
    }
    
    @Test
    public void test_onLogoutHandler_not_started()
    {
        ChatWindowHandler chatWindowHandler = new ChatWindowHandler();
        chatWindowHandler.onLogoutHandler(ApplicationState.shutdown);
    }
 
    @Test
    public void test_onSessionEnd()
    {
        ChatWindowHandler chatWindowHandler = new ChatWindowHandler();
        ChatSession chatSession = mock(ChatSession.class);
        ContactEntry contact = mock(ContactEntry.class);
        when(chatSession.getParticipant()).thenReturn(contact );
        chatWindowHandler.onSessionEnd(chatSession, "Sign off");
        verify(contact).setWindowId(null);
    }
    
    @SuppressWarnings("unchecked")
    void doCloseAllSessions(boolean isLogout)
    {
        AsyncChatWindowHandler asyncHandler = mock(AsyncChatWindowHandler.class);
        ModelPartController<ChatSessionView> chatController = mock(ModelPartController.class);
        ChatWindowHandler chatWindowHandler = new ChatWindowHandler();
        chatWindowHandler.isApplicationStarted = true;
        chatWindowHandler.asyncHandler = asyncHandler;
        chatWindowHandler.chatController = chatController;
        ModelPart<ChatSessionView> defaultChatSessionViewPart = mock(ModelPart.class);
        when(defaultChatSessionViewPart.getRenderedArtifact()).thenReturn(mock(ChatSessionView.class));
        ModelPart<ChatSessionView> chatSessionViewPart = mock(ModelPart.class);
        when(chatSessionViewPart.getRenderedArtifact()).thenReturn(mock(ChatSessionView.class));
        when(chatSessionViewPart.isCloseable()).thenReturn(true);
        ModelPartsList<ChatSessionView> partList = mock(ModelPartsList.class);
        when(chatController.getPartList(ChatSessionView.STACK_ID)).thenReturn(partList);
        List<ModelPart<ChatSessionView>> parts = new ArrayList<ModelPart<ChatSessionView>>(2);
        parts.add(defaultChatSessionViewPart);
        parts.add(chatSessionViewPart);
        when(partList.getParts()).thenReturn(parts );
        if (isLogout)
            chatWindowHandler.closeAllSessions();
        else
            chatWindowHandler.onLogoutHandler(ApplicationState.shutdown);
        verify(asyncHandler).clearChatView(defaultChatSessionViewPart);
        verify(asyncHandler).hideChatView(chatSessionViewPart);
    }
    
    @SuppressWarnings("unchecked")
    void doHandleSessionEvent(String type, boolean viewFlag)
    {
        ChatWindowHandler chatWindowHandler = new ChatWindowHandler();
        chatWindowHandler.isApplicationStarted = true;
        AsyncChatWindowHandler asyncHandler = mock(AsyncChatWindowHandler.class);
        ModelPartController<ChatSessionView> chatController = mock(ModelPartController.class);
        chatWindowHandler.asyncHandler = asyncHandler;
        chatWindowHandler.chatController = chatController;
        ModelPart<ChatSessionView> defaultChatSessionViewPart = mock(ModelPart.class);
        when(defaultChatSessionViewPart.isRendered()).thenReturn(true);
        ChatSessionView defaultChatSessionView = mock(ChatSessionView.class);
        when(defaultChatSessionViewPart.getRenderedArtifact()).thenReturn(defaultChatSessionView);
        when(defaultChatSessionView.disable()).thenReturn(viewFlag);
        when(defaultChatSessionView.enable()).thenReturn(viewFlag);
        ModelPart<ChatSessionView> chatSessionViewPart = mock(ModelPart.class);
        when(chatSessionViewPart.isRendered()).thenReturn(true);
        ChatSessionView chatSessionView = mock(ChatSessionView.class);
        when(chatSessionViewPart.getRenderedArtifact()).thenReturn(chatSessionView);
        when(chatSessionView.disable()).thenReturn(viewFlag);
        when(chatSessionView.enable()).thenReturn(viewFlag);
        ModelPartsList<ChatSessionView> partList = mock(ModelPartsList.class);
        when(chatController.getPartList(ChatSessionView.STACK_ID)).thenReturn(partList);
        List<ModelPart<ChatSessionView>> parts = new ArrayList<ModelPart<ChatSessionView>>(2);
        parts.add(defaultChatSessionViewPart);
        parts.add(chatSessionViewPart);
        when(partList.getParts()).thenReturn(parts );
        if (type.equals(CyberteteEvents.SESSION_RESUME))
        {
            chatWindowHandler.sessionResumeHandler(INFO);
            verify(defaultChatSessionView).enable();
            verify(chatSessionView).enable();
        }
        else
        {
            chatWindowHandler.sessionPauseHandler(INFO);
            verify(defaultChatSessionView).disable();
            verify(chatSessionView).disable();
        }
        if (viewFlag)
        {
            verify(defaultChatSessionView).displayMessage(INFO);
            verify(chatSessionView).displayMessage(INFO);
        }
        else
        {
            verify(defaultChatSessionView, times(0)).displayMessage(INFO);
            verify(chatSessionView, times(0)).displayMessage(INFO);
        }
    }
}
