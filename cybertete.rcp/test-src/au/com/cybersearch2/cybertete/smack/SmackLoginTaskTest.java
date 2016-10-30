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

import java.io.IOException;

import javax.net.ssl.SSLContext;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.junit.Test;

import au.com.cybersearch2.cybertete.agents.LocalRoster;
import au.com.cybersearch2.cybertete.model.ConnectionError;
import au.com.cybersearch2.cybertete.model.ContactsTree;
import au.com.cybersearch2.cybertete.model.internal.MultiGroupContactsTree;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.service.XmppConnectionException;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * SmackLoginTaskTest
 * @author Andrew Bowley
 * 7 Apr 2016
 */
public class SmackLoginTaskTest
{
    static final String TEST_JID = "mickymouse@disney.com";
    static final String TEST_HOST = "google.talk";

    @Test
    public void test_inner_connectLogin() throws SmackException, IOException, XMPPException
    {
        SmackChatService chatService = mock(SmackChatService.class);
        ContactsTree contactsTree = mock(MultiGroupContactsTree.class);
        SmackLoginTask underTest = new SmackLoginTask(chatService, contactsTree);
        XmppConnection xmppConnection = mock(XmppConnection.class);
        LocalRoster roster = mock(LocalRoster.class);
        when(xmppConnection.connectLogin(contactsTree)).thenReturn(roster );
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        assertThat(underTest.connectLogin(xmppConnection, sessionDetails)).isTrue();
        verify(chatService).startChatConnection(roster, sessionDetails);
        assertThat(underTest.getConnectionError()).isEqualTo( ConnectionError.noError );
    }
    
    @Test
    public void test_connectLogin() throws SmackException, IOException, XMPPException
    {
        SessionDetails sessionDetails = mock(SessionDetails.class);
        SmackConnectionListener connectionListener = mock(SmackConnectionListener.class);
        SmackChatService chatService = mock(SmackChatService.class);
        ContactsTree contactsTree = mock(MultiGroupContactsTree.class);
        SSLContext sslContext = mock(SSLContext.class);
        when(chatService.connect(sessionDetails, sslContext )).thenReturn(connectionListener);
        SmackLoginTask underTest = new SmackLoginTask(chatService, contactsTree);
        XmppConnection xmppConnection = mock(XmppConnection.class);
        when(connectionListener.getXmppConnection()).thenReturn(xmppConnection);
        LocalRoster roster = mock(LocalRoster.class);
        when(xmppConnection.connectLogin(contactsTree)).thenReturn(roster );
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        underTest.connectLogin(sessionDetails, sslContext);
        verify(chatService).startChatConnection(roster, sessionDetails);
        assertThat(underTest.getConnectionError()).isEqualTo( ConnectionError.noError );
        verify(connectionListener).notifyCommsUp();
        verify(xmppConnection).enableAutoReconnect();
    }    
    
    @Test
    public void test_connectLogin_exception() throws SmackException, IOException, XMPPException
    {
        SessionDetails sessionDetails = mock(SessionDetails.class);
        SmackConnectionListener connectionListener = mock(SmackConnectionListener.class);
        SmackChatService chatService = mock(SmackChatService.class);
        ContactsTree contactsTree = mock(MultiGroupContactsTree.class);
        SSLContext sslContext = mock(SSLContext.class);
        when(chatService.connect(sessionDetails, sslContext )).thenReturn(connectionListener);
        SmackLoginTask underTest = new SmackLoginTask(chatService, contactsTree);
        XmppConnection xmppConnection = mock(XmppConnection.class);
        when(xmppConnection.getHostName()).thenReturn(TEST_HOST);
        when(xmppConnection.getPort()).thenReturn(5222);
        when(connectionListener.getXmppConnection()).thenReturn(xmppConnection);
        SmackException exception = new SmackException("Network down");
        when(xmppConnection.connectLogin(contactsTree)).thenThrow(exception);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        try
        {
            underTest.connectLogin(sessionDetails, sslContext);
            failBecauseExceptionWasNotThrown(XmppConnectionException.class);
        }
        catch (XmppConnectionException e)
        {
            assertThat(e.getMessage()).isEqualTo("Connect, log in and load roster failed");
            assertThat(e.getHost()).isEqualTo(TEST_HOST);
            assertThat(e.getPort()).isEqualTo(5222);
            assertThat(e.getConnectionError()).isEqualTo(ConnectionError.classifyException(exception));
        }
        verify(connectionListener, times(0)).notifyCommsUp();
        verify(xmppConnection, times(0)).enableAutoReconnect();
    }

    
    
}
