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
import static org.fest.assertions.api.Assertions.assertThat;

import javax.net.ssl.SSLContext;

import au.com.cybersearch2.cybertete.model.Presence;
import au.com.cybersearch2.cybertete.model.internal.MultiGroupContactsTree;

import org.junit.Test;

import au.com.cybersearch2.cybertete.agents.LocalRoster;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.service.CommsStateListener;
import au.com.cybersearch2.cybertete.model.service.LoginController;
import au.com.cybersearch2.cybertete.model.service.NetworkListener;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.service.ChainHostnameVerifier;
import au.com.cybersearch2.cybertete.service.ConnectionNotifier;
import au.com.cybersearch2.cybertete.service.SessionOwner;

/**
 * SmackChatServiceTest
 * @author Andrew Bowley
 * 4 Apr 2016
 */
public class SmackChatServiceTest
{
    static final String TEST_PASSWORD = "secret";
    static final String TEST_USERNAME = "donald";
    static final String TEST_JID = "mickymouse@disney.com";

    class TestEnsemble
    {
        public ChainHostnameVerifier chainHostnameVerifier;
        public ConnectionNotifier connectionNotifier;
        public SmackConnectionListener connectionListener;
        public SmackChatService underTest;
        public XmppConnectionFactory xmppConnectionFactory;
        public SmackChatResponder chatResponder;
        public MultiGroupContactsTree multiGroupContactsTree;
        public SessionOwner sessionOwner;

        public TestEnsemble()
        {
            connectionNotifier = mock(ConnectionNotifier.class);
            chainHostnameVerifier = mock(ChainHostnameVerifier.class);
            xmppConnectionFactory = mock(XmppConnectionFactory.class);
            chatResponder = mock(SmackChatResponder.class);
            multiGroupContactsTree = mock(MultiGroupContactsTree.class);
            sessionOwner = mock(SessionOwner.class);
            
            // Add global objects to Chat service
            underTest = new SmackChatService();
            underTest.hostnameVerifier = chainHostnameVerifier;
            underTest.connectionNotifier = connectionNotifier;
            underTest.xmppConnectionFactory = xmppConnectionFactory;
            underTest.chatResponder = chatResponder;
            underTest.contactsTree = multiGroupContactsTree;
            underTest.sessionOwner = sessionOwner;
        }
        
    }

    @Test
    public void test_startSession()
    {
        TestEnsemble testEnsemble = new TestEnsemble();
        LoginController loginController =  mock(LoginController.class);
        when(loginController.login(isA(SmackLoginTask.class))).thenReturn(true);
        assertThat(testEnsemble.underTest.startSession(loginController )).isTrue();
        verify(testEnsemble.chainHostnameVerifier).clearCertificates();
      }

    @Test
    public void test_startSession_login_fail()
    {
        TestEnsemble testEnsemble = new TestEnsemble();
         LoginController loginController =  mock(LoginController.class);
        when(loginController.login(isA(SmackLoginTask.class))).thenReturn(false);
        assertThat(testEnsemble.underTest.startSession(loginController )).isFalse();
        verify(testEnsemble.chainHostnameVerifier).clearCertificates();
    }
    
    @Test
    public void test_connect()
    {
        XmppConnectionFactory xmppConnectionFactory = mock(XmppConnectionFactory.class);
        XmppConnection xmppConnection = mock(XmppConnection.class);
        SSLContext sslContext = mock(SSLContext.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        TestEnsemble testEnsemble = new TestEnsemble();
        testEnsemble.underTest.xmppConnectionFactory = xmppConnectionFactory;
        when(xmppConnectionFactory.getConnection(sessionDetails, sslContext)).thenReturn(xmppConnection);
        SmackConnectionListener connectionListener = testEnsemble.underTest.connect(sessionDetails, sslContext);
        assertThat(connectionListener.getXmppConnection()).isEqualTo(xmppConnection);
    }
    
    @Test
    public void test_add_listeners()
    {
        TestEnsemble testEnsemble = new TestEnsemble();
        CommsStateListener commsStateListener = mock(CommsStateListener.class);
        testEnsemble.underTest.addChatConnectionListener(commsStateListener);
        verify(testEnsemble.connectionNotifier).add(commsStateListener);
        NetworkListener networkListener = mock(NetworkListener.class);
        testEnsemble.underTest.addNetworkListener(networkListener);
        verify(testEnsemble.connectionNotifier).add(networkListener);
    }

    @Test
    public void test_sessionExists()
    {
        ContactEntry participant = mock(ContactEntry.class);
        TestEnsemble testEnsemble = new TestEnsemble();
        SmackChatResponder chatResponder = mock(SmackChatResponder.class);
        testEnsemble.underTest.chatResponder = chatResponder;
        when(testEnsemble.underTest.chatResponder.chatExists(participant)).thenReturn(true);
        assertThat(testEnsemble.underTest.chatExists(participant)).isTrue();
    }

    @Test
    public void test_sessionExists_not()
    {
        ContactEntry participant = mock(ContactEntry.class);
        TestEnsemble testEnsemble = new TestEnsemble();
        SmackChatResponder chatResponder = mock(SmackChatResponder.class);
        testEnsemble.underTest.chatResponder = chatResponder;
        when(testEnsemble.underTest.chatResponder.chatExists(participant)).thenReturn(false);
        assertThat(testEnsemble.underTest.chatExists(participant)).isFalse();
    }

    @Test
    public void test_sessionExists_chat_connection_null()
    {
        ContactEntry participant = mock(ContactEntry.class);
        TestEnsemble testEnsemble = new TestEnsemble();
        assertThat(testEnsemble.underTest.chatExists(participant)).isFalse();
    }
    
    @Test
    public void test_close()
    {
        TestEnsemble testEnsemble = new TestEnsemble();
        XmppConnection xmppConnection = mock(XmppConnection.class);
        testEnsemble.underTest.xmppConnection = xmppConnection;
         testEnsemble.underTest.close();
        verify(testEnsemble.chainHostnameVerifier).clearCertificates();
        verify(xmppConnection).disconnect();
    }

    @Test
    public void test_close_null()
    {
        TestEnsemble testEnsemble = new TestEnsemble();
        testEnsemble.underTest.close();
        verify(testEnsemble.chainHostnameVerifier).clearCertificates();
    }
    
    @Test
    public void test_sendSmackPresence()
    {
        TestEnsemble testEnsemble = new TestEnsemble();
        XmppConnection xmppConnection = mock(XmppConnection.class);
        testEnsemble.underTest.xmppConnection = xmppConnection;
        testEnsemble.underTest.sendPresence(Presence.online);
        verify(xmppConnection).sendPresence(Presence.online);
        verify(testEnsemble.underTest.xmppConnectionFactory).setSendPresence(true);
    }
    
    @Test
    public void test_setSmackPresence_null_connection()
    {
        TestEnsemble testEnsemble = new TestEnsemble();
        testEnsemble.underTest.sendPresence(Presence.online);
        verify(testEnsemble.underTest.xmppConnectionFactory).setSendPresence(true);
    }
    
    @Test
    public void test_startChatConnection()
    {
        TestEnsemble testEnsemble = new TestEnsemble();
        XmppConnection xmppConnection = mock(XmppConnection.class);
        testEnsemble.underTest.xmppConnection = xmppConnection;
        LocalRoster roster = mock(LocalRoster.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        testEnsemble.underTest.startChatConnection(roster , sessionDetails);
        verify(xmppConnection).addChatListener(isA(SmackChatResponder.class));
        verify(testEnsemble.sessionOwner).update("mickymouse", TEST_JID);
    }
}
