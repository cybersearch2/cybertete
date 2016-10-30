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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.Presence;
import au.com.cybersearch2.cybertete.model.service.ChatSession;
import au.com.cybersearch2.cybertete.views.ChatSessionView;
import au.com.cybersearch2.cybertete.views.ContactsView;
import au.com.cybersearch2.e4.ModelPart;
import au.com.cybersearch2.e4.ModelPartController;

/**
 * AsyncChatWindowHandlerTest
 * @author Andrew Bowley
 * 14 Mar 2016
 */
public class AsyncChatWindowHandlerTest
{
    final static String TEST_MESSAGE = "Hello world!";
    
    @SuppressWarnings("unchecked")
    @Test
    public void test_startChat_previously_rendered()
    {
        AsyncChatWindowHandler handlerUnderTest = new  AsyncChatWindowHandler();
        ContactsView contactsView = mock(ContactsView.class);
        MPart contactsViewPart = mock(MPart.class);
        when(contactsViewPart.getObject()).thenReturn(contactsView);
        ModelPartController<ContactsView> contactsController = mock(ModelPartController.class);
        ModelPart<ContactsView> contactsModelPart = new ModelPart<ContactsView>(contactsViewPart);
        when(contactsController.activatePart(ContactsView.ID)).thenReturn(contactsModelPart);
        handlerUnderTest.contactsController = contactsController;
        ModelPartController<ChatSessionView> chatController = mock(ModelPartController.class);
        handlerUnderTest.chatController = chatController;
        ChatSessionView chatSessionView = mock(ChatSessionView.class);
        MPart activePart = mock(MPart.class);
        when(activePart.getObject()).thenReturn(chatSessionView);
        ModelPart<ChatSessionView> chatModelPart = new ModelPart<ChatSessionView>(activePart);
        UISynchronize sync = mock(UISynchronize.class);
        handlerUnderTest.sync = sync;
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        ContactEntry from = mock(ContactEntry.class);
        when(from.getWindowId()).thenReturn(ChatSessionView.ID);
        ChatSession chatSession = mock(ChatSession.class);
        when(chatSession.getParticipant()).thenReturn(from);
        handlerUnderTest.startChat(chatModelPart, chatSession, TEST_MESSAGE);
        verify(sync).asyncExec(runnableCaptor.capture());
        runnableCaptor.getValue().run();
        verify(chatSessionView).enable();
        verify(chatSessionView).setChatSession(chatSession);
        verify(chatSessionView).setSelection(from);
        verify(from).setPresence(Presence.online);
        verify(chatSessionView).displayMessage(from, TEST_MESSAGE);
        verify(contactsView).setSelection(from);
        verify(chatController).activatePart(chatModelPart);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_startChat_render()
    {
        String  toActivateId = ChatSessionView.ID; 
        AsyncChatWindowHandler handlerUnderTest = new  AsyncChatWindowHandler();
        MPart contactsViewPart = mock(MPart.class);
        ContactsView contactsView = mock(ContactsView.class);
        when(contactsViewPart.getObject()).thenReturn(contactsView);
        ModelPartController<ContactsView> contactsController = mock(ModelPartController.class);
        ModelPart<ContactsView> contactsModelPart = new ModelPart<ContactsView>(contactsViewPart);
        when(contactsController.activatePart(ContactsView.ID)).thenReturn(contactsModelPart);
        handlerUnderTest.contactsController = contactsController;
        ModelPartController<ChatSessionView> chatController = mock(ModelPartController.class);
        handlerUnderTest.chatController = chatController;
        MPart activePart = mock(MPart.class);
        ModelPart<ChatSessionView> chatModelPart = new ModelPart<ChatSessionView>(activePart);
        when(chatController.activatePart(toActivateId)).thenReturn(chatModelPart);
        ChatSessionView chatSessionView = mock(ChatSessionView.class);
        UISynchronize sync = mock(UISynchronize.class);
        handlerUnderTest.sync = sync;
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        ContactEntry from = mock(ContactEntry.class);
        when(from.getWindowId()).thenReturn(ChatSessionView.ID);
        ChatSession chatSession = mock(ChatSession.class);
        when(chatSession.getParticipant()).thenReturn(from);
        when(activePart.getObject()).thenReturn(chatSessionView);
        handlerUnderTest.startChat(toActivateId, chatSession, TEST_MESSAGE);
        verify(sync).asyncExec(runnableCaptor.capture());
        runnableCaptor.getValue().run();
        verify(chatSessionView).setChatSession(chatSession);
        verify(chatSessionView).setSelection(from);
        verify(from).setPresence(Presence.online);
        verify(chatSessionView).displayMessage(from, TEST_MESSAGE);
        verify(contactsView).setSelection(from);
    }
    
    @Test
    public void test_clearChatView()
    {
        AsyncChatWindowHandler handlerUnderTest = new  AsyncChatWindowHandler();
        UISynchronize sync = mock(UISynchronize.class);
        handlerUnderTest.sync = sync;
        ChatSessionView chatView = mock(ChatSessionView.class);
        MPart activePart = mock(MPart.class);
        when(activePart.getObject()).thenReturn(chatView);
        ModelPart<ChatSessionView> chatModelPart = new ModelPart<ChatSessionView>(activePart);
        handlerUnderTest.clearChatView(chatModelPart );
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(sync).asyncExec(runnableCaptor.capture());
        runnableCaptor.getValue().run();
        verify(chatView).clear();
        verify(chatView).disable();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_hideChatView()
    {
        AsyncChatWindowHandler handlerUnderTest = new  AsyncChatWindowHandler();
        ModelPartController<ChatSessionView> chatController = mock(ModelPartController.class);
         handlerUnderTest.chatController = chatController;
        UISynchronize sync = mock(UISynchronize.class);
        handlerUnderTest.sync = sync;
        MPart viewPart= mock(MPart.class);
        ModelPart<ChatSessionView> chatModelPart = new ModelPart<ChatSessionView>(viewPart);
        handlerUnderTest.hideChatView(chatModelPart);
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(sync).asyncExec(runnableCaptor.capture());
        runnableCaptor.getValue().run();
        verify(chatController).hidePart(chatModelPart);
    }
}
