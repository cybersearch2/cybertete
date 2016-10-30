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
import java.util.ArrayList;
import java.util.HashSet;

import static org.fest.assertions.api.Assertions.assertThat;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.cybertete.agents.LocalRoster;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactGroup;
import au.com.cybersearch2.cybertete.model.ContactsTree;
import au.com.cybersearch2.cybertete.model.internal.MultiGroupContactsTree;

/**
 * XmppConnectionBaseTest
 * @author Andrew Bowley
 * 6 Apr 2016
 */
public class XmppConnectionBaseTest
{
    public class TestXmppConnectionBase extends XmppConnectionBase
    {
        public ReconnectionManager reconnectionManager;
        public Roster roster;

        public boolean isConnected;
        public String user;

        public TestXmppConnectionBase()
        {
            super(mock(XMPPTCPConnection.class));
        }

        @Override
        protected Roster getRosterInstance()
        {
            roster = mock(Roster.class);
            // Avoid wait for load code
            when(roster.isLoaded()).thenReturn(true);
            when(roster.getGroups()).thenReturn(new ArrayList<RosterGroup>());
            when(roster.getUnfiledEntries()).thenReturn(new HashSet<RosterEntry>());
            return roster;
        }
        
        @Override
        protected ChatManager getChatManagerInstance()
        {
            return mock(ChatManager.class);
        }

        @Override
        protected ReconnectionManager getReconnectionManagerInstance()
        {
            reconnectionManager = mock(ReconnectionManager.class);
            return reconnectionManager;
        }
        
        public void reset()
        {
            reset(true);
        }
        
        public void reset(boolean isConnected)
        {
            this.isConnected = isConnected;
            connection = mock(XMPPTCPConnection.class);
            chatManager = mock(ChatManager.class);
            reconnectionManager = mock(ReconnectionManager.class);
        }

        @Override
        public boolean isConnected()
        {
            return isConnected;
        }

        @Override
        public String getUser()
        {
            return user;
        }


}

    static final String TEST_JID = "mickymouse@disney.com";

    @Test 
    public void test_short_methods() throws SmackException, IOException, XMPPException
    {
        TestXmppConnectionBase underTest = new TestXmppConnectionBase();
        ChatManagerListener listener = mock(ChatManagerListener.class);
        underTest.addChatListener(listener);
        verify(underTest.chatManager).addChatListener(listener);
        underTest.reset();
        ConnectionListener connectionListener = mock(ConnectionListener.class);
        underTest.addListener(connectionListener);
        verify(underTest.connection).addConnectionListener(connectionListener);
        underTest.reset();
        ContactsTree contactsTree = mock(MultiGroupContactsTree.class);
        when(contactsTree.getRootContactGroup()).thenReturn(mock(ContactGroup.class));
        assertThat(underTest.connectLogin(contactsTree)).isInstanceOf(LocalRoster.class);
        verify(underTest.connection).connect();
        verify(underTest.connection).login();
        verify(underTest.roster).setSubscriptionMode(Roster.SubscriptionMode.accept_all);
        verify(underTest.roster).addRosterListener(isA(SmackRosterListener.class));
        underTest.reset();
        ContactEntry participant = mock(ContactEntry.class);
        when(participant.getUser()).thenReturn(TEST_JID);
        underTest.createChat(participant);
        verify(underTest.chatManager).createChat(TEST_JID);
        underTest.reset();
        underTest.disconnect();
        verify(underTest.connection).disconnect();
        underTest.reset(false);
        underTest.disconnect();
        verify(underTest.connection, times(0)).disconnect();
        underTest.reset();
        underTest.enableAutoReconnect();
        verify(underTest.reconnectionManager).enableAutomaticReconnection();
        underTest.reset(false);
        underTest.enableAutoReconnect();
        verify(underTest.reconnectionManager, times(0)).enableAutomaticReconnection();
        underTest.reset();
        String host = "google.com";
        when(underTest.connection.getHost()).thenReturn(host);
        assertThat(underTest.getHostName()).isEqualTo(host);
        underTest.reset();
        int port = 5222;
        when(underTest.connection.getPort()).thenReturn(port);
        assertThat(underTest.getPort()).isEqualTo(port);
        underTest.reset();
        String serverName = "microsoft.com";
        when(underTest.connection.getServiceName()).thenReturn(serverName);
        assertThat(underTest.getServiceName()).isEqualTo(serverName);
        underTest.reset();
        String serviceUser = "mickymouse";
        underTest.user = serviceUser;
        assertThat(underTest.getServiceUser()).isEqualTo(serviceUser);
        underTest.reset(false);
        underTest.user = null;
        assertThat(underTest.getServiceUser()).isEmpty();
        underTest.reset();
        when(underTest.connection.isSecureConnection()).thenReturn(true);
        assertThat(underTest.isSecure()).isTrue();
        underTest.isConnected = false;
        assertThat(underTest.isSecure()).isFalse();
        underTest.reset();
        underTest.sendPresence(au.com.cybersearch2.cybertete.model.Presence.online);
        ArgumentCaptor<Presence> presenceCaptor = ArgumentCaptor.forClass(Presence.class);
        verify(underTest.connection).sendStanza(presenceCaptor.capture());
        assertThat(presenceCaptor.getValue().getMode()).isEqualTo(Mode.available);
        underTest.reset(false);
        underTest.sendPresence(au.com.cybersearch2.cybertete.model.Presence.online);
        verify(underTest.connection, times(0)).sendStanza(any(Presence.class));
        underTest.reset();
        long timeout = 9099;
        underTest.setPacketReplyTimeout(timeout);
        verify(underTest.connection).setPacketReplyTimeout(timeout);
        
   }
    
}
