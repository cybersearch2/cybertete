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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.junit.Test;

import au.com.cybersearch2.cybertete.agents.LocalRoster;
import au.com.cybersearch2.cybertete.model.ConnectionError;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactGroup;
import au.com.cybersearch2.cybertete.model.ContactItem;
import au.com.cybersearch2.cybertete.model.Presence;
import au.com.cybersearch2.cybertete.model.RosterEntryData;
import au.com.cybersearch2.cybertete.model.internal.MultiGroupContactsTree;
import au.com.cybersearch2.cybertete.service.XmppConnectionException;

/**
 * SmackRosterTest
 * @author Andrew Bowley
 * 13 Apr 2016
 */
public class SmackRosterTest
{
    private static final String HOST = "cybersearch2.local";
    private static final Integer PORT = 5222;
    private static final String JID_NAD = "nad@cybersearch2.local";
    private static final String GROUP1 = "group1";
    private static final String GROUP2 = "group2";

    
    @SuppressWarnings("unchecked")
    @Test
    public void test_load_no_wait()
    {
        Roster roster = mock(Roster.class);
        when(roster.isLoaded()).thenReturn(true);
        Collection<RosterGroup> noGroups = Collections.emptyList();
        when(roster.getGroups()).thenReturn(noGroups);
        Set<RosterEntry> noEntries = Collections.emptySet();
        when(roster.getUnfiledEntries()).thenReturn(noEntries);
        final SmackRoster underTest = new SmackRoster(roster, HOST, PORT);
        final MultiGroupContactsTree multiGroupContactsTree = mock(MultiGroupContactsTree.class);
        underTest.loadContactsTree(multiGroupContactsTree);
        verify(multiGroupContactsTree).loadRoster(isA(List.class));
        verify(roster).setSubscriptionMode(Roster.SubscriptionMode.accept_all);
    }

    @Test
    public void test_registerLocalRoster()
    {
        Roster roster = mock(Roster.class);
        SmackRoster underTest = new SmackRoster(roster, HOST, PORT);
        LocalRoster localRoster = mock(LocalRoster.class);
        underTest.registerLocalRoster(localRoster);
        verify(roster).addRosterListener(isA(SmackRosterListener.class));
    }
    
    @Test
    public void test_sync_available()
    {
        org.jivesoftware.smack.packet.Presence smackPresence = new org.jivesoftware.smack.packet.Presence(Type.available);
        smackPresence.setMode(Mode.available);
        doSync(smackPresence, Presence.online);
    }
    
    @Test
    public void test_sync_chat()
    {
        org.jivesoftware.smack.packet.Presence smackPresence = new org.jivesoftware.smack.packet.Presence(Type.available);
        smackPresence.setMode(Mode.chat);
        doSync(smackPresence, Presence.online);
    }
    
    @Test
    public void test_sync_away()
    {
        org.jivesoftware.smack.packet.Presence smackPresence = new org.jivesoftware.smack.packet.Presence(Type.available);
        smackPresence.setMode(Mode.away);
        doSync(smackPresence, Presence.away);
    }

    @Test
    public void test_sync_xa()
    {
        org.jivesoftware.smack.packet.Presence smackPresence = new org.jivesoftware.smack.packet.Presence(Type.available);
        smackPresence.setMode(Mode.xa);
        doSync(smackPresence, Presence.away);
    }
    
    @Test
    public void test_sync_dnd()
    {
        org.jivesoftware.smack.packet.Presence smackPresence = new org.jivesoftware.smack.packet.Presence(Type.available);
        smackPresence.setMode(Mode.dnd);
        doSync(smackPresence, Presence.dnd);
    }

    @Test
    public void test_sync_unavailable()
    {
        org.jivesoftware.smack.packet.Presence smackPresence = new org.jivesoftware.smack.packet.Presence(Type.unavailable);
        Roster roster = mock(Roster.class);
        when(roster.getPresence(JID_NAD)).thenReturn(smackPresence);
        SmackRoster underTest = new SmackRoster(roster, HOST, PORT);
        ContactEntry contactEntry = mock(ContactEntry.class);
        when(contactEntry.getUser()).thenReturn(JID_NAD);
        underTest.sync(contactEntry);
        verify(contactEntry).setPresence(Presence.offline);
    }

    @Test
    public void test_sync_error()
    {
        org.jivesoftware.smack.packet.Presence smackPresence = new org.jivesoftware.smack.packet.Presence(Type.available);
        XMPPError error = mock(XMPPError.class);
        smackPresence.setError(error );
        Roster roster = mock(Roster.class);
        when(roster.getPresence(JID_NAD)).thenReturn(smackPresence);
        SmackRoster underTest = new SmackRoster(roster, HOST, PORT);
        ContactEntry contactEntry = mock(ContactEntry.class);
        when(contactEntry.getUser()).thenReturn(JID_NAD);
        underTest.sync(contactEntry);
        verify(contactEntry).setPresence(Presence.offline);
    }

    @Test
    public void test_getGroupList()
    {
        Roster roster = mock(Roster.class);
        final List<RosterGroup> groups = new ArrayList<RosterGroup>();
        RosterGroup rosterGroup1 = mock(RosterGroup.class);
        when(rosterGroup1.getName()).thenReturn(GROUP1);
        groups.add(rosterGroup1);
        RosterGroup rosterGroup2 = mock(RosterGroup.class);
        when(rosterGroup2.getName()).thenReturn(GROUP2);
        groups.add(rosterGroup2);
        SmackRoster underTest = new SmackRoster(roster, HOST, PORT)
        {
            @Override
            protected Collection<RosterGroup> getRosterEntryGroups(String user)
            {
                return groups;
            }
        };
        Collection<String> groupList = underTest.getGroupList(JID_NAD);
        assertThat(groupList.size()).isEqualTo(2);
        Iterator<String> iterator = groupList.iterator();
        assertThat(iterator.next()).isEqualTo(GROUP1);
        assertThat(iterator.next()).isEqualTo(GROUP2);
    }

    @Test
    public void test_getGroupList_empty()
    {
        Roster roster = mock(Roster.class);
        SmackRoster underTest = new SmackRoster(roster, HOST, PORT);
        Collection<String> groupList = underTest.getGroupList(JID_NAD);
        assertThat(groupList.size()).isEqualTo(0);
    }

    @Test
    public void test_createEntry() throws SmackException, XMPPErrorException
    {
        Roster roster = mock(Roster.class);
        SmackRoster underTest = new SmackRoster(roster, HOST, PORT);
        ContactEntry entry = mock(ContactEntry.class);
        when(entry.getUser()).thenReturn(JID_NAD);
        when(entry.getName()).thenReturn("nad");
        ContactItem group = mock(ContactGroup.class);
        when(group.getName()).thenReturn(GROUP1);
        when(entry.getParent()).thenReturn(group);
        Collection<String> groups = new ArrayList<String>();
        groups.add(GROUP2);
        underTest.createEntry(entry , groups);
        verify(roster).createEntry(JID_NAD, "nad", new String[]{GROUP2,GROUP1});
    }

    @Test
    public void test_createEntry_roster_contains_group() throws SmackException, XMPPErrorException
    {
        Roster roster = mock(Roster.class);
        SmackRoster underTest = new SmackRoster(roster, HOST, PORT);
        ContactEntry entry = mock(ContactEntry.class);
        when(entry.getUser()).thenReturn(JID_NAD);
        when(entry.getName()).thenReturn("nad");
        ContactItem group = mock(ContactGroup.class);
        when(group.getName()).thenReturn(GROUP1);
        when(entry.getParent()).thenReturn(group);
        Collection<String> groups = new ArrayList<String>();
        groups.add(GROUP1);
        groups.add(GROUP2);
        underTest.createEntry(entry , groups);
        verify(roster).createEntry(JID_NAD, "nad", new String[]{GROUP1,GROUP2});
    }

    @Test
    public void test_createEntry_exception() throws SmackException, XMPPErrorException
    {
        Roster roster = mock(Roster.class);
        SmackRoster underTest = new SmackRoster(roster, HOST, PORT);
        ContactEntry entry = mock(ContactEntry.class);
        when(entry.getUser()).thenReturn(JID_NAD);
        when(entry.getName()).thenReturn("nad");
        ContactItem group = mock(ContactGroup.class);
        when(group.getName()).thenReturn(GROUP1);
        when(entry.getParent()).thenReturn(group);
        Collection<String> groups = new ArrayList<String>();
        groups.add(GROUP1);
        groups.add(GROUP2);
         NotLoggedInException exception = new SmackException.NotLoggedInException();
        doThrow(exception)
        .when(roster).createEntry(JID_NAD, "nad", new String[]{GROUP1,GROUP2});
        try
        {
            underTest.createEntry(entry , groups);
            failBecauseExceptionWasNotThrown(XmppConnectionException.class);
        }
        catch (XmppConnectionException e)
        {
            assertThat(e.getMessage()).isEqualTo("Error adding contact " + JID_NAD);
            assertThat(e.getHost()).isEqualTo(HOST);
            assertThat(e.getPort()).isEqualTo(PORT);
            assertThat(e.getConnectionError()).isEqualTo(ConnectionError.classifyException(exception));
        }
    }
 
    @Test
    public void test_getRosterEntry_null()
    {
        Roster roster = mock(Roster.class);
        SmackRoster underTest = new SmackRoster(roster, HOST, PORT);
        assertThat(underTest.getRosterEntry(JID_NAD)).isNull();
    }
    
    @Test
    public void test_createSmackRosterEntry()
    {
        Roster roster = mock(Roster.class);
        SmackRoster underTest = new SmackRoster(roster, HOST, PORT);
        RosterEntryData data = underTest.createSmackRosterEntry("nad", JID_NAD);
        assertThat(data.getName()).isEqualTo("nad");
        assertThat(data.getUser()).isEqualTo(JID_NAD);
    }
    
    void doSync(org.jivesoftware.smack.packet.Presence smackPresence, Presence presence)
    {
        Roster roster = mock(Roster.class);
        when(roster.getPresence(JID_NAD)).thenReturn(smackPresence);
        SmackRoster underTest = new SmackRoster(roster, HOST, PORT);
        ContactEntry contactEntry = mock(ContactEntry.class);
        when(contactEntry.getUser()).thenReturn(JID_NAD);
        underTest.sync(contactEntry);
        verify(contactEntry).setPresence(presence);
    }
}
