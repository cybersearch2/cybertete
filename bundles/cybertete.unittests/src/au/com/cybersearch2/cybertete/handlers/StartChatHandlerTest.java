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

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.cybertete.model.ChatAgent;
import au.com.cybersearch2.cybertete.model.ChatContacts;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.Presence;
import au.com.cybersearch2.cybertete.service.CommunicationsState;
import au.com.cybersearch2.dialogs.SyncErrorDialog;
import au.com.cybersearch2.e4.JobScheduler;

/**
 * StartChatHandlerTest
 * @author Andrew Bowley
 * 28 Apr 2016
 */
public class StartChatHandlerTest
{
    @Test
    public void test_execute()
    {
        StartChatHandler underTest = new StartChatHandler();
        ChatAgent chatAgent = mock(ChatAgent.class);
        underTest.chatAgent = chatAgent;
        ContactEntry selected = mock(ContactEntry.class);
        JobScheduler jobScheduler = mock(JobScheduler.class);
        underTest.jobScheduler = jobScheduler;
        underTest.execute(selected);
        ArgumentCaptor<Runnable> jobCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(jobScheduler).schedule(eq("Start chat"), jobCaptor.capture());
        jobCaptor.getValue().run();
        verify(chatAgent).startChat(selected);
    }

    @Test
    public void test_execute_no_selection()
    {
        StartChatHandler underTest = new StartChatHandler();
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        underTest.errorDialog = errorDialog;
        underTest.execute(null);
        verify(errorDialog).showError("Error", "No contact selected");
    }
    
    @Test
    public void test_canExecute()
    {
        StartChatHandler underTest = new StartChatHandler();
        ContactEntry selected = mock(ContactEntry.class);
        when(selected.getUser()).thenReturn("Micky@disney.com");
        when(selected.getPresence()).thenReturn(Presence.online);
        CommunicationsState communicationsState = mock(CommunicationsState.class);
        when(communicationsState.isOnline()).thenReturn(true);
        ChatContacts chatContacts = mock(ChatContacts.class);
        when(chatContacts.chatExists(selected)).thenReturn(false);
        assertThat(underTest.canExecute(selected, communicationsState, chatContacts)).isTrue();
        
    }
    
    @Test
    public void test_canExecute_no_selection()
    {
        StartChatHandler underTest = new StartChatHandler();
        CommunicationsState communicationsState = mock(CommunicationsState.class);
        ChatContacts chatContacts = mock(ChatContacts.class);
        assertThat(underTest.canExecute(null, communicationsState, chatContacts)).isFalse();
        
    }

    @Test
    public void test_canExecute_new_selection()
    {
        StartChatHandler underTest = new StartChatHandler();
        CommunicationsState communicationsState = mock(CommunicationsState.class);
        ChatContacts chatContacts = mock(ChatContacts.class);
        ContactEntry selected = mock(ContactEntry.class);
        when(selected.getUser()).thenReturn("");
       assertThat(underTest.canExecute(selected, communicationsState, chatContacts)).isFalse();
        
    }

    @Test
    public void test_canExecute_offline()
    {
        StartChatHandler underTest = new StartChatHandler();
        ContactEntry selected = mock(ContactEntry.class);
        when(selected.getUser()).thenReturn("Micky@disney.com");
        when(selected.getPresence()).thenReturn(Presence.online);
        CommunicationsState communicationsState = mock(CommunicationsState.class);
        when(communicationsState.isOnline()).thenReturn(false);
        ChatContacts chatContacts = mock(ChatContacts.class);
        assertThat(underTest.canExecute(selected, communicationsState, chatContacts)).isFalse();
        
    }
    
    @Test
    public void test_canExecute_contact_already_chatting()
    {
        StartChatHandler underTest = new StartChatHandler();
        ContactEntry selected = mock(ContactEntry.class);
        when(selected.getUser()).thenReturn("Micky@disney.com");
        when(selected.getPresence()).thenReturn(Presence.online);
        CommunicationsState communicationsState = mock(CommunicationsState.class);
        when(communicationsState.isOnline()).thenReturn(true);
        ChatContacts chatContacts = mock(ChatContacts.class);
        when(chatContacts.chatExists(selected)).thenReturn(true);
        assertThat(underTest.canExecute(selected, communicationsState, chatContacts)).isFalse();
        
    }
    
}
