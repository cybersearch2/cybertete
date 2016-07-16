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

import static org.mockito.Mockito.*;
import static org.fest.assertions.api.Assertions.assertThat;

import java.net.ConnectException;

import javax.xml.ws.WebServiceException;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.window.Window;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.controls.CustomDialog;
import au.com.cybersearch2.cybertete.dialogs.AddContactControls;
import au.com.cybersearch2.cybertete.dialogs.DialogFactory;
import au.com.cybersearch2.cybertete.model.ConnectionError;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactGroup;
import au.com.cybersearch2.cybertete.model.RosterAgent;
import au.com.cybersearch2.cybertete.service.XmppConnectionException;
import au.com.cybersearch2.dialogs.SyncErrorDialog;
import au.com.cybersearch2.e4.JobScheduler;

/**
 * AddContactHandlerTest
 * @author Andrew Bowley
 * 20 Apr 2016
 */
public class AddContactHandlerTest
{
    class TestTarget
    {
        public RosterAgent rosterAgent;
        public ContactGroup group;
        public ContactEntry contactEntry;
        public JobScheduler jobScheduler;
        public SyncErrorDialog errorDialog;
        public Logger logger;
        public AddContactHandler underTest;
    }
 
    static final String TEST_JID = "mickymouse@disney.com";
    static final String GROUP_NAME = "MyGroup";

    @Test
    public void test_addContact()
    {
        TestTarget testTarget = getTestTarget();
        ContactEntry entry  = mock(ContactEntry.class);
        when(testTarget.rosterAgent.addContact(entry)).thenReturn(true);
        testTarget.underTest.addContact(entry);
    }
    
    @Test
    public void test_addContact_already_exists()
    {
        TestTarget testTarget = getTestTarget();
        ContactEntry entry  = mock(ContactEntry.class);
        when(entry.getUser()).thenReturn(TEST_JID);
        when(testTarget.rosterAgent.addContact(entry)).thenReturn(false);
        testTarget.underTest.addContact(entry);
        verify(testTarget.errorDialog).showError("Already exists", "Contact \"" + TEST_JID + "\" already exists in roster");
    }
    
    @Test
    public void test_addContact_XmppException()
    {
        TestTarget testTarget = getTestTarget();
        ContactEntry entry  = mock(ContactEntry.class);
        when(entry.getUser()).thenReturn(TEST_JID);
        Throwable networkFailException = new ConnectException("Host unavailable");
        XmppConnectionException connectionException = 
                new XmppConnectionException("Network down", networkFailException, ConnectionError.connectionRefused, "google.com", 5222);
        when(testTarget.rosterAgent.addContact(entry)).thenThrow(connectionException);
        testTarget.underTest.addContact(entry);
        verify(testTarget.errorDialog).showError(connectionException.getMessage(), connectionException.getDetails());
        verify(testTarget.logger).error(networkFailException, "Network down");
    }
    
    @Test
    public void test_addContact_WebServiceException()
    {
        TestTarget testTarget = getTestTarget();
        ContactEntry entry  = mock(ContactEntry.class);
        when(entry.getUser()).thenReturn(TEST_JID);
        WebServiceException serviceException = 
                new WebServiceException("Packet error");
        when(testTarget.rosterAgent.addContact(entry)).thenThrow(serviceException);
        testTarget.underTest.addContact(entry);
        verify(testTarget.errorDialog).showError("Error", "Packet error");
        verify(testTarget.logger).error(serviceException, "Error adding " + entry.getUser() + " to roster");
    }
 
    @Test
    public void test_execute()
    {
        TestTarget testTarget = getTestTarget();
        when(testTarget.group.getName()).thenReturn(GROUP_NAME);
        testTarget.underTest.contactEntry = null;
        doExecute(testTarget, testTarget.group);
    }
    
    @Test
    public void test_execute_contact_selected()
    {
        ContactGroup group = mock(ContactGroup.class);
        when(group.getName()).thenReturn(GROUP_NAME);
        TestTarget testTarget = getTestTarget();
        when(testTarget.contactEntry.getParent()).thenReturn(group);
        testTarget.underTest.group = null;
        doExecute(testTarget, group);
    }
 
    @Test
    public void test_execute_paranoid()
    {
        TestTarget testTarget = getTestTarget();
        testTarget.underTest.contactEntry = null;
        testTarget.underTest.group = null;
        testTarget.underTest.execute(getDialogFactory(true));
        verify(testTarget.errorDialog).showError("Error", "No group selected");
   }
    
    void doExecute(TestTarget testTarget, ContactGroup group)
    {
        when(testTarget.rosterAgent.addContact(isA(ContactEntry.class))).thenReturn(true);
        testTarget.underTest.execute(getDialogFactory(true));
        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(testTarget.jobScheduler).schedule(eq("Add Contact Job"), taskCaptor.capture());
        taskCaptor.getValue().run();
        ArgumentCaptor<ContactEntry> contactCaptor = ArgumentCaptor.forClass(ContactEntry.class);
        verify(testTarget.rosterAgent).addContact(contactCaptor.capture());
        ContactEntry contactEntry = contactCaptor.getValue();
        assertThat(contactEntry.getName()).isEqualTo("Nick");
        assertThat(contactEntry.getUser()).isEqualTo(TEST_JID);
        assertThat(contactEntry.getParent()).isEqualTo(group);
        // Test for contactEntry.addToSelf() not called
        verify(group, times(0)).getItems();
        
        
    }
    
    TestTarget getTestTarget()
    {
        TestTarget testTarget = new TestTarget();
        AddContactHandler underTest = new AddContactHandler();
        testTarget.underTest = underTest;
        underTest.contactEntry = mock(ContactEntry.class);
        underTest.errorDialog = mock(SyncErrorDialog.class);
        underTest.group = mock(ContactGroup.class);
        underTest.jobScheduler = mock(JobScheduler.class);
        underTest.rosterAgent = mock(RosterAgent.class);
        underTest.logger = mock(Logger.class);
        testTarget.contactEntry = underTest.contactEntry;
        testTarget.errorDialog = underTest.errorDialog;
        testTarget.group = underTest.group;
        testTarget.jobScheduler = underTest.jobScheduler;
        testTarget.rosterAgent = underTest.rosterAgent;
        testTarget.logger = underTest.logger;
        
        return testTarget;
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    DialogFactory getDialogFactory(boolean doAddContact)
    {
        DialogFactory dialogFactory = mock(DialogFactory.class);
        CustomDialog addContactDialog = mock(CustomDialog.class);
        when(dialogFactory.addContactDialogInstance("Add Contact to " + GROUP_NAME)).thenReturn(addContactDialog);
        when(addContactDialog.syncOpen(isA(UISynchronize.class))).thenReturn(doAddContact ? Window.OK : Window.CANCEL);
        final AddContactControls addContactControls = mock(AddContactControls.class);
        when(addContactControls.getNickname()).thenReturn("Nick");
        when(addContactControls.getJid()).thenReturn(TEST_JID);
        when(addContactDialog.getCustomControls()).thenReturn(addContactControls);
        return dialogFactory;
    }
}
