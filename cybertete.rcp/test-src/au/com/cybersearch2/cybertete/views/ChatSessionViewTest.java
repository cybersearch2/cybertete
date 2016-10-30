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
package au.com.cybersearch2.cybertete.views;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.xml.ws.WebServiceException;

import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.jivesoftware.smack.SmackException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.service.ChatSession;
import au.com.cybersearch2.dialogs.SyncErrorDialog;
import au.com.cybersearch2.e4.JobScheduler;

/**
 * ChatSessionViewTest
 * @author Andrew Bowley
 * 6 Mar 2016
 */
public class ChatSessionViewTest
{
   private static final String TEST_BODY = "This is a test message";
   private static final String TEST_JID = "mickymouse@disney.com";
   private static final String TEST_JID2 = "aliz@google.com";

   @Test
   public void test_postConstruct()
   {
       ChatSessionView chatSessionView = new ChatSessionView();
       ChatSessionControl chatSessionControl = mock(ChatSessionControl.class);
       chatSessionView.chatSessionControl = chatSessionControl;
       ArgumentCaptor<KeyAdapter> keyAdapterCaptor = ArgumentCaptor.forClass(KeyAdapter.class);
       ILoggerProvider loggerProvider = mock(ILoggerProvider.class); 
       chatSessionView.postConstruct(loggerProvider);
       verify(chatSessionControl).setKeyListener(keyAdapterCaptor.capture());
       ContactEntry from = mock(ContactEntry.class);
       when(from.getUser()).thenReturn(TEST_JID);
       ContactEntry contact = mock(ContactEntry.class);
       when(contact.getUser()).thenReturn(TEST_JID2);
       chatSessionView.contact = contact;
       KeyAdapter keyAdapter = keyAdapterCaptor.getValue();
       KeyEvent event = mock(KeyEvent.class);
       event.character = SWT.CR;
       event.doit = true;
       JobScheduler jobScheduler = mock(JobScheduler.class);
       chatSessionView.jobScheduler =  jobScheduler;
       ArgumentCaptor<Runnable> sendChatMessageTaskCaptor = ArgumentCaptor.forClass(Runnable.class);
       ChatSession chatSession = mock(ChatSession.class);
       when(chatSession.getSessionOwner()).thenReturn(from);
       chatSessionView.chatSession = chatSession;
       when(chatSessionControl.getText()).thenReturn(TEST_BODY);
       keyAdapter.keyPressed(event);
       assertThat(event.doit).isEqualTo(false);
       verify(jobScheduler).schedule(eq("Send chat message"), sendChatMessageTaskCaptor.capture());
       sendChatMessageTaskCaptor.getValue().run();
       verify(chatSession).sendMessage(TEST_BODY);
       verify(chatSessionControl).displayMessage("<mickymouse>  " + TEST_BODY);
       WebServiceException webServiceException = new WebServiceException(new SmackException.NotLoggedInException());
       doThrow(webServiceException)
       .when(chatSession).sendMessage(TEST_BODY);
       Logger logger = mock(Logger.class);
       chatSessionView.logger = logger;
       SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
       chatSessionView.errorDialog = errorDialog;
       sendChatMessageTaskCaptor.getValue().run();
       verify(logger).error(webServiceException.getCause(), webServiceException.getMessage());
       errorDialog.showError("Communications Error", webServiceException.getMessage());
   }
   
   @Test
   public void test_preDestroy()
   {
       ChatSessionView chatSessionView = new ChatSessionView();
       ContactEntry contact = mock(ContactEntry.class);
       chatSessionView.contact = contact;
       chatSessionView.preDestroy();
   }
   
   @Test 
   public void test_onFocus()
   {
       ChatSessionView chatSessionView = new ChatSessionView();
       ChatSessionControl chatSessionControl = mock(ChatSessionControl.class);
       chatSessionView.chatSessionControl = chatSessionControl;
       chatSessionView.onFocus();
       verify(chatSessionControl).onFocus();
   }
     
   @Test 
   public void test_setSelection()
   {
       ChatSessionView chatSessionView = new ChatSessionView();
       ContactEntry contact = mock(ContactEntry.class);
       when(contact.getUser()).thenReturn(TEST_JID2);
       chatSessionView.setSelection(contact);
       assertThat(chatSessionView.contact).isEqualTo(contact);
   }
      
   @Test
   public void test_displayMessage_no_body()
   {
       ChatSessionView chatSessionView = new ChatSessionView();
       ContactEntry contact = mock(ContactEntry.class);
       chatSessionView.displayMessage(contact, "");
       verify(contact, times(0)).getUser();
   }
      
   @Test
   public void test_clear()
   {
       ChatSessionView chatSessionView = new ChatSessionView();
       ChatSessionControl chatSessionControl = mock(ChatSessionControl.class);
       chatSessionView.chatSessionControl = chatSessionControl;
       chatSessionView.clear();
       verify(chatSessionControl).clear();
    }
      
    @Test
    public void test_enable()
    {
        ChatSessionView chatSessionView = new ChatSessionView();
        ChatSessionControl chatSessionControl = mock(ChatSessionControl.class);
        chatSessionView.chatSessionControl = chatSessionControl;
        when(chatSessionControl.setEnabled(true)).thenReturn(true);
        when(chatSessionControl.setEnabled(false)).thenReturn(true);
        assertThat(chatSessionView.enable()).isTrue();
        assertThat(chatSessionView.disable()).isTrue();
        when(chatSessionControl.setEnabled(true)).thenReturn(false);
        when(chatSessionControl.setEnabled(false)).thenReturn(false);
        assertThat(chatSessionView.enable()).isFalse();
        assertThat(chatSessionView.disable()).isFalse();
     }
}
