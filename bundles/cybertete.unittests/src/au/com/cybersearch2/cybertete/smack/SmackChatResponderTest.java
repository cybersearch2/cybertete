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
package au.com.cybersearch2.cybertete.smack;

import static org.mockito.Mockito.*;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactGroup;
import au.com.cybersearch2.cybertete.model.ContactsTree;
import au.com.cybersearch2.cybertete.model.internal.ContactEntryList;
import au.com.cybersearch2.cybertete.model.internal.MultiGroupContactsTree;
import au.com.cybersearch2.cybertete.model.service.ChatListener;
import au.com.cybersearch2.cybertete.service.ChatContactListener;
import au.com.cybersearch2.cybertete.service.SessionOwner;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * SmackChatResponderTest
 * @author Andrew Bowley
 * 12 Apr 2016
 */
public class SmackChatResponderTest
{
    static final String TEST_JID = "mickymouse@disney.com";
    static final String TEST_MESSAGE = "Hello world";

    @Test
    public void test_chatExists()
    {
        SmackChatResponder underTest = new SmackChatResponder();
        underTest.postConstruct();
        ContactEntry participant = mock(ContactEntry.class);
        assertThat(underTest.chatExists(participant)).isFalse();
        Chat chat = mock(Chat.class);
        underTest.chatMap.put(chat , participant);
        assertThat(underTest.chatExists(participant)).isTrue();
    }
    
    @Test
    public void test_close()
    {
        SmackChatResponder underTest = new SmackChatResponder();
        underTest.postConstruct();
        ContactEntry participant = mock(ContactEntry.class);
        Chat chat = mock(Chat.class);
        underTest.chatMap.put(chat , participant);
        underTest.close();
        verify(chat).close();
        assertThat(underTest.chatExists(participant)).isFalse();
    }

    @Test
    public void test_chatCreated_locally()
    {
        ChatListener chatListener = mock(ChatListener.class);
        ContactEntry user = mock(ContactEntry.class);
        SessionOwner sessionOwner = mock(SessionOwner.class);
        when(sessionOwner.getContact()).thenReturn(user);
        ContactsTree contactsTree = mock(MultiGroupContactsTree.class);
        SmackChatResponder underTest = new SmackChatResponder();
        underTest.postConstruct();
        underTest.contactsTree = contactsTree;
        underTest.sessionOwner = sessionOwner;
        underTest.chatListener = chatListener;
        ContactEntry participant = mock(ContactEntry.class);
        ContactEntryList participantList = mock(ContactEntryList.class);
        when(contactsTree.getContactEntryList(TEST_JID)).thenReturn(participantList);
        Chat chat = mock(Chat.class);
        underTest.onStartChat(chat, participant, null);
        verify(chat).addMessageListener(underTest);
        underTest.chatCreated(chat, true);
        assertThat(underTest.chatExists(participant)).isTrue();
        ArgumentCaptor<SmackChatSession> chatSessionCaptor = ArgumentCaptor.forClass(SmackChatSession.class);
        verify(chatListener).onStartChat(chatSessionCaptor.capture(), eq(participant), any(String.class));
        SmackChatSession smackChatSession = chatSessionCaptor.getValue();
        assertThat(smackChatSession.chat).isEqualTo(chat);
        assertThat(smackChatSession.getParticipant()).isEqualTo(participant);
        assertThat(smackChatSession.getSessionOwner()).isEqualTo(user);
    }

    @Test
    public void test_chatCreated_remotely()
    {
        ChatListener chatListener = mock(ChatListener.class);
        String groupName = "MyGroup";
        ContactGroup group = mock(ContactGroup.class);
        when(group.getName()).thenReturn(groupName);
        ContactEntry user = mock(ContactEntry.class);
        when(user.getParent()).thenReturn(group);
        SessionOwner sessionOwner = mock(SessionOwner.class);
        when(sessionOwner.getContact()).thenReturn(user);
        ContactsTree contactsTree = mock(MultiGroupContactsTree.class);
        SmackChatResponder underTest = new SmackChatResponder();
        underTest.postConstruct();
        underTest.contactsTree = contactsTree;
        underTest.sessionOwner = sessionOwner;
        underTest.chatListener = chatListener;
        ContactEntry participant = mock(ContactEntry.class);
        ContactEntryList contactEntryList = mock(ContactEntryList.class);
        when(contactEntryList.getEntryByGroup(groupName)).thenReturn(participant);
        when(contactsTree.getContactEntryList(TEST_JID)).thenReturn(contactEntryList);
        Chat chat = mock(Chat.class);
        when(chat.getParticipant()).thenReturn(TEST_JID);
        underTest.chatCreated(chat, false);
        ArgumentCaptor<ChatMessageListener> listenerCaptor = ArgumentCaptor.forClass(ChatMessageListener.class);
        verify(chat).addMessageListener(listenerCaptor.capture());
        ChatMessageListener messageListener = listenerCaptor.getValue();
        Message message = new Message();
        message.setBody(TEST_MESSAGE);
        messageListener.processMessage(chat, message);
        assertThat(underTest.chatExists(participant)).isTrue();
        ArgumentCaptor<SmackChatSession> chatSessionCaptor = ArgumentCaptor.forClass(SmackChatSession.class);
        verify(chatListener).onStartChat(chatSessionCaptor.capture(), eq(participant), eq(TEST_MESSAGE));
        SmackChatSession smackChatSession = chatSessionCaptor.getValue();
        assertThat(smackChatSession.chat).isEqualTo(chat);
        assertThat(smackChatSession.getParticipant()).isEqualTo(participant);
        assertThat(smackChatSession.getSessionOwner()).isEqualTo(user);
    }
    
    @Test
    public void test_onStartChat_skip_cases()
    {
        ContactsTree contactsTree = mock(MultiGroupContactsTree.class);
        SmackChatResponder underTest = new SmackChatResponder();
        underTest.postConstruct();
        underTest.contactsTree = contactsTree;
        Chat chat = mock(Chat.class);
        when(chat.getParticipant()).thenReturn(TEST_JID);
        // Contact not found in roster
        underTest.onStartChat(chat, null);
        assertThat(underTest.chatMap.size()).isEqualTo(0);
        ContactEntry participant = mock(ContactEntry.class);
        String groupName = "MyGroup";
        ContactGroup group = mock(ContactGroup.class);
        when(group.getName()).thenReturn(groupName);
        ContactEntry user = mock(ContactEntry.class);
        when(user.getParent()).thenReturn(group);
        SessionOwner sessionOwner = mock(SessionOwner.class);
        when(sessionOwner.getContact()).thenReturn(user);
        underTest.sessionOwner = sessionOwner;
        // Chat exists
        underTest.chatMap.put(chat , participant);
        ContactEntryList contactEntryList = mock(ContactEntryList.class);
        when(contactEntryList.getEntryByGroup(groupName)).thenReturn(participant);
        when(contactsTree.getContactEntryList(TEST_JID)).thenReturn(contactEntryList);
        underTest.onStartChat(chat, null);
        assertThat(underTest.chatMap.size()).isEqualTo(1);
        verify(chat, times(0)).addMessageListener(underTest);
    }
    
    @Test
    public void test_processMessage()
    {
        ContactEntry participant = mock(ContactEntry.class);
        SmackChatResponder underTest = new SmackChatResponder();
        underTest.postConstruct();
        underTest.chatContactListener = mock(ChatContactListener.class);
        Chat chat = mock(Chat.class);
        underTest.chatMap.put(chat , participant);
        Message message = new Message();
        message.setBody(TEST_MESSAGE);
        underTest.processMessage(chat, message);
        verify(underTest.chatContactListener).onMessageReceived(participant, TEST_MESSAGE);
    }

    @Test
    public void test_processMessage_no_body()
    {
        ContactEntry participant = mock(ContactEntry.class);
        SmackChatResponder underTest = new SmackChatResponder();
        underTest.postConstruct();
        underTest.chatContactListener = mock(ChatContactListener.class);
        Chat chat = mock(Chat.class);
        underTest.chatMap.put(chat , participant);
        Message message = new Message();
        underTest.processMessage(chat, message);
        verify(underTest.chatContactListener, times(0)).onMessageReceived(eq(participant), any(String.class));
    }

    @Test
    public void test_processMessage_no_contact()
    {
        ContactEntry participant = mock(ContactEntry.class);
        SmackChatResponder underTest = new SmackChatResponder();
        underTest.postConstruct();
        underTest.chatContactListener = mock(ChatContactListener.class);
        Chat chat = mock(Chat.class);
        Message message = new Message();
        message.setBody(TEST_MESSAGE);
        underTest.processMessage(chat, message);
        verify(underTest.chatContactListener, times(0)).onMessageReceived(participant, TEST_MESSAGE);
    }
}
