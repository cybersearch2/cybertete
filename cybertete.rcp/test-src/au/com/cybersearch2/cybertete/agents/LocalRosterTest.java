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
package au.com.cybersearch2.cybertete.agents;

import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.roster.RosterListener;
import org.junit.Before;
import org.junit.Test;

import au.com.cybersearch2.cybertete.agents.LocalRoster;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactGroup;
import au.com.cybersearch2.cybertete.model.ContactsTree;
import au.com.cybersearch2.cybertete.model.GroupCollection;
import au.com.cybersearch2.cybertete.model.Presence;
import au.com.cybersearch2.cybertete.model.RosterEntryData;
import au.com.cybersearch2.cybertete.model.internal.ContactEntryList;
import au.com.cybersearch2.cybertete.model.internal.MultiGroupContactsTree;
import au.com.cybersearch2.cybertete.smack.SmackRoster;
import au.com.cybersearch2.cybertete.smack.SmackRosterListener;

/**
 * LocalRosterTest
 * @author Andrew Bowley
 * 23 Feb 2016
 */
public class LocalRosterTest
{
    class LocalRosterTestTarget
    {
        public LocalRoster localRoster;
        public RosterListener rosterListener;
        public SmackRoster roster;
        public ContactsTree contactsTree;
        public ContactGroup rootContactGroup;
    }

    class TestSmackRosterEntry implements RosterEntryData
    {
        String name;
        String user;
 
        public TestSmackRosterEntry(String name, String user)
        {
            this.name = name;
            this.user = user;
        }
        
        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public String getUser()
        {
            return user;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name)
        {
            this.name = name;
        }
        
    }
    
    private static final String JID_NAD = "nad@cybersearch2.local";
    private static final String GROUP_FRIENDS = "Friends";
    private static final String GROUP_COLLEAGUES = "Colleagues";
    private static final String NAME_ALIZ = "aliz";
    private static final String JID_ALIZ = "aliz@cybersearch2.local";
    private static final String NAME_GEORGE = "George";
    private static final String JID_GEORGE = "george@cybersearch2.local";
    private static final String NAME_SALLY = "sally";
    private static final String JID_SALLY = "sally@cybersearch2.local";
    private static final String NAME_BILL = "bill";
    private static final String JID_BILL = "bill@cybersearch2.local";
    private static final String HOST = "cybersearch2.local";
    private static final Integer PORT = 5222;
    static String SESSION_NAME = "mickymouse";
    static String SESSION_USER = "mickymouse@cybersearch2.local";
    List<ContactEntry> ROSTER_GROUP1;
    ContactGroup group1;
    ContactEntry rosterEntryAliz;
    ContactEntry rosterEntryGeorge;
    ContactEntry rosterEntryMicky1;
    List<ContactEntry> ROSTER_GROUP2;
    ContactGroup group2;
    ContactEntry rosterEntrySally;
    ContactEntry rosterEntryBill;
    ContactEntry rosterEntryMicky2;
    static List<ContactEntry> NO_ROSTER = Collections.emptyList();
 
    @Before
    public void setUp()
    {
        ROSTER_GROUP1 = new ArrayList<ContactEntry>();
        group1 = mock(ContactGroup.class);
        when(group1.getName()).thenReturn(GROUP_FRIENDS);
        // RosterEntry class is final so cannot be mocked
        ROSTER_GROUP1.add(new ContactEntry(NAME_ALIZ, JID_ALIZ, group1));
        ROSTER_GROUP1.add(new ContactEntry(NAME_GEORGE, JID_GEORGE, group1));
        ROSTER_GROUP1.add(new ContactEntry(SESSION_NAME, SESSION_USER, group1));
        rosterEntryAliz = ROSTER_GROUP1.get(0);
        rosterEntryGeorge = ROSTER_GROUP1.get(1);
        rosterEntryMicky1 = ROSTER_GROUP1.get(2);
        when(group1.getContact(JID_ALIZ)).thenReturn(rosterEntryAliz);
        when(group1.getContact(JID_GEORGE)).thenReturn(rosterEntryGeorge);
        when(group1.getContact(SESSION_USER)).thenReturn(rosterEntryMicky1);
        ROSTER_GROUP2 = new ArrayList<ContactEntry>();
        group2 = mock(ContactGroup.class);
        when(group2.getName()).thenReturn(GROUP_COLLEAGUES);
        ROSTER_GROUP2.add(new ContactEntry(NAME_SALLY, JID_SALLY, group2));
        ROSTER_GROUP2.add(new ContactEntry(NAME_BILL, JID_BILL, group2));
        ROSTER_GROUP2.add(new ContactEntry(SESSION_NAME, SESSION_USER, group2));
        rosterEntrySally = ROSTER_GROUP2.get(0);
        rosterEntryBill = ROSTER_GROUP2.get(1);
        rosterEntryMicky2 = ROSTER_GROUP2.get(2);
        when(group2.getContact(JID_SALLY)).thenReturn(rosterEntrySally);
        when(group2.getContact(JID_BILL)).thenReturn(rosterEntryBill);
        when(group2.getContact(SESSION_USER)).thenReturn(rosterEntryMicky2);
   }

    @Test
    public void test_constructor_roster_groups()
    {
        LocalRosterTestTarget testTarget = getLocalRosterTestTarget(ROSTER_GROUP1, ROSTER_GROUP2);
        List<ContactGroup> groups = testTarget.rootContactGroup.getGroups();
        assertThat(groups.size()).isEqualTo(2);
        ContactGroup group1 = groups.get(0);
        ContactGroup group2 = groups.get(1);
        assertThat(group1.getName()).isEqualTo(GROUP_FRIENDS);
        assertThat(group2.getName()).isEqualTo(GROUP_COLLEAGUES);
        //assertThat(testTarget.LocalRoster.sessionOwner.getUser()).isEqualTo(JID_NAD);
        Map<String, ContactEntryList> contactMap = testTarget.contactsTree.getContactMap();
        assertThat(contactMap.get(SESSION_USER).getEntryByGroup(GROUP_FRIENDS)).isEqualTo(rosterEntryMicky1);
        assertThat(contactMap.get(SESSION_USER).getEntryByGroup(GROUP_COLLEAGUES)).isEqualTo(rosterEntryMicky2);
        assertThat(contactMap.get(JID_ALIZ).getEntryByGroup(GROUP_FRIENDS)).isEqualTo(rosterEntryAliz);
        assertThat(contactMap.get(JID_ALIZ).getEntryByGroup(GROUP_COLLEAGUES)).isNull();
        assertThat(contactMap.get(JID_GEORGE).getEntryByGroup(GROUP_FRIENDS)).isEqualTo(rosterEntryGeorge);
        assertThat(contactMap.get(JID_GEORGE).getEntryByGroup(GROUP_COLLEAGUES)).isNull();
        assertThat(contactMap.get(JID_SALLY).getEntryByGroup(GROUP_FRIENDS)).isNull();
        assertThat(contactMap.get(JID_SALLY).getEntryByGroup(GROUP_COLLEAGUES)).isEqualTo(rosterEntrySally);
        assertThat(contactMap.get(JID_BILL).getEntryByGroup(GROUP_FRIENDS)).isNull();
        assertThat(contactMap.get(JID_BILL).getEntryByGroup(GROUP_COLLEAGUES)).isEqualTo(rosterEntryBill);
    }

    @Test
    public void test_addContact() throws XMPPErrorException, SmackException
    {
        LocalRosterTestTarget testTarget = getLocalRosterTestTarget(ROSTER_GROUP1, ROSTER_GROUP2);
        ContactGroup group2 = testTarget.rootContactGroup.getGroups().get(0);
        ContactEntry nadContact = new ContactEntry("nad", JID_NAD, group2);
        Collection<String> rosterGroups = Collections.emptyList();
        when(testTarget.roster.getGroupList(JID_NAD)).thenReturn(rosterGroups);
        assertThat(testTarget.localRoster.addContact(nadContact)).isTrue();
        List<String> groupList = new ArrayList<String>();
        groupList.add(GROUP_FRIENDS);
        verify(testTarget.roster).createEntry(nadContact, groupList);
    }
    
    @Test
    public void test_addContact_already_in_group() throws XMPPErrorException, SmackException
    {
        LocalRosterTestTarget testTarget = getLocalRosterTestTarget(ROSTER_GROUP1, ROSTER_GROUP2);
        ContactGroup group2 = testTarget.rootContactGroup.getGroups().get(1);
        ContactEntry nadContact = new ContactEntry("nad", JID_NAD, group2);
        when(testTarget.roster.getGroupList(JID_NAD)).thenReturn(Collections.singletonList(GROUP_COLLEAGUES));
        assertThat(testTarget.localRoster.addContact(nadContact)).isFalse();
    }
   
    @Test
    public void test_addContact_two_groups() throws XMPPErrorException, SmackException
    {
        LocalRosterTestTarget testTarget = getLocalRosterTestTarget(ROSTER_GROUP1, ROSTER_GROUP2);
        ContactGroup group2 = testTarget.rootContactGroup.getGroups().get(0);
        ContactEntry nadContact = new ContactEntry("nad", JID_NAD, group2);
        Collection<String> rosterGroups = Collections.singletonList(GROUP_COLLEAGUES);
        when(testTarget.roster.getGroupList(JID_NAD)).thenReturn(rosterGroups);
        testTarget.localRoster.addContact(nadContact);
        List<String> groupList = new ArrayList<String>();
        groupList.add(GROUP_COLLEAGUES);
        groupList.add(GROUP_FRIENDS);
        verify(testTarget.roster).createEntry(nadContact, groupList);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void test_entriesAdded()
    {
        LocalRosterTestTarget testTarget = getLocalRosterTestTarget(ROSTER_GROUP1, ROSTER_GROUP2);
        Collection<String> addresses = new ArrayList<String>();
        RosterEntryData rosterEntryNad = mock(RosterEntryData.class);
        when(rosterEntryNad.getName()).thenReturn("nad");
        when(rosterEntryNad.getUser()).thenReturn(JID_NAD);
        when(testTarget.roster.getRosterEntry(JID_NAD)).thenReturn(rosterEntryNad);
        Collection<String> rosterGroups = Collections.singletonList(GROUP_FRIENDS);
        when(testTarget.roster.getGroupList(JID_NAD)).thenReturn(rosterGroups);
        testTarget.rosterListener.entriesAdded(addresses);
        //verify(testTarget.LocalRoster.contactEntries.contactMap, times(0)).put(eq(JID_NAD), any(ContactEntry.class));
        assertThat(testTarget.localRoster.contactsTree.getContactMap().get(JID_NAD)).isNull();
        addresses.add(JID_NAD);
        // Support group1 getItems() for new ContactEntry call addSelfToParent()
        List<ContactEntry> group1Contacts = new ArrayList<ContactEntry>();
        group1Contacts.add(rosterEntryAliz);
        group1Contacts.add(rosterEntryGeorge);
        group1Contacts.add(rosterEntryMicky1);
        when(group1.getItems()).thenReturn(group1Contacts);
        testTarget.rosterListener.entriesAdded(addresses);
        assertThat(testTarget.localRoster.contactsTree.getContactMap().get(JID_NAD)).isNotNull();
        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> itemsCapture1 = ArgumentCaptor.forClass(List.class);
        verify(group1, times(2)).setItems(itemsCapture1.capture());
        assertThat(itemsCapture1.getAllValues().get(0).size()).isEqualTo(3);
        assertThat(itemsCapture1.getAllValues().get(1).size()).isEqualTo(4);
    }
 
    @Test 
    public void test_add_null()
    {
        LocalRosterTestTarget testTarget = getLocalRosterTestTarget(ROSTER_GROUP1, ROSTER_GROUP2);
        testTarget.localRoster.add(null);
    }
    
    @Test 
    public void test_add_no_new_groups()
    {
        LocalRosterTestTarget testTarget = getLocalRosterTestTarget(ROSTER_GROUP1, ROSTER_GROUP2);
        RosterEntryData rosterEntryNad = mock(RosterEntryData.class);
        when(rosterEntryNad.getName()).thenReturn("aliz");
        when(rosterEntryNad.getUser()).thenReturn(JID_ALIZ);
        Collection<String> rosterGroups = Collections.singletonList(GROUP_FRIENDS);
        when(testTarget.roster.getGroupList(JID_ALIZ)).thenReturn(rosterGroups);
        testTarget.localRoster.add(rosterEntryNad);
    }
    
    @Test
    public void test_entriesAdded_new_group()
    {
        String GROUP_NEW = "New";
        LocalRosterTestTarget testTarget = getLocalRosterTestTarget(ROSTER_GROUP1, ROSTER_GROUP2);
        Collection<String> addresses = new ArrayList<String>();
        RosterEntryData rosterEntryNad = mock(RosterEntryData.class);
        when(rosterEntryNad.getName()).thenReturn("nad");
        when(rosterEntryNad.getUser()).thenReturn(JID_NAD);
        when(testTarget.roster.getRosterEntry(JID_NAD)).thenReturn(rosterEntryNad);
        Collection<String> rosterGroups = Collections.singletonList(GROUP_NEW);
        when(testTarget.roster.getGroupList(JID_NAD)).thenReturn(rosterGroups);
        addresses.add(JID_NAD);
        testTarget.rosterListener.entriesAdded(addresses);
        //verify(testTarget.LocalRoster.contactEntries.contactMap).put(eq(JID_NAD), any(ContactEntry.class));
        assertThat(testTarget.localRoster.contactsTree.getContactMap().get(JID_NAD)).isNotNull();
        List<ContactGroup> groups =testTarget.rootContactGroup.getGroups();
        assertThat(groups.size()).isEqualTo(3);
        ContactGroup contactGroup = groups.get(2);
        assertThat(contactGroup.getName()).isEqualTo(GROUP_NEW);
        assertThat(contactGroup.getItems().size()).isEqualTo(1);
        assertThat(contactGroup.getItems().get(0).getUser()).isEqualTo(JID_NAD);
    }
     
    @SuppressWarnings("unchecked")
    @Test
    public void test_entriesAdded_one_existing_group()
    {
        LocalRosterTestTarget testTarget = getLocalRosterTestTarget(ROSTER_GROUP1, ROSTER_GROUP2);
        ContactEntry sally = testTarget.localRoster.contactsTree.getContactMap().get(JID_SALLY).getHead();
        assertThat(sally.getParent().getName()).isEqualTo(GROUP_COLLEAGUES);
        Collection<String> addresses = new ArrayList<String>();
        RosterEntryData rosterEntrySally = mock(RosterEntryData.class);
        when(rosterEntrySally.getName()).thenReturn(NAME_SALLY);
        when(rosterEntrySally.getUser()).thenReturn(JID_SALLY);
        when(testTarget.roster.getRosterEntry(JID_SALLY)).thenReturn(rosterEntrySally);
        Collection<String> rosterGroups = new ArrayList<String>();
        rosterGroups.add(GROUP_COLLEAGUES);
        rosterGroups.add(GROUP_FRIENDS);
        when(testTarget.roster.getGroupList(JID_SALLY)).thenReturn(rosterGroups);
        addresses.add(JID_SALLY);
        // Support group1 getItems() for new ContactEntry call addSelfToParent()
        List<ContactEntry> group1Contacts = new ArrayList<ContactEntry>();
        group1Contacts.add(rosterEntryAliz);
        group1Contacts.add(rosterEntryGeorge);
        group1Contacts.add(rosterEntryMicky1);
        when(group1.getItems()).thenReturn(group1Contacts);
        testTarget.rosterListener.entriesAdded(addresses);
        Map<String, ContactEntryList> contactMap = testTarget.contactsTree.getContactMap();
        assertThat(contactMap.get(JID_SALLY).getEntryByGroup(GROUP_FRIENDS)).isNotNull();
        assertThat(contactMap.get(JID_SALLY).getEntryByGroup(GROUP_COLLEAGUES)).isEqualTo(sally);
        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> itemsCapture1 = ArgumentCaptor.forClass(List.class);
        verify(group1, times(2)).setItems(itemsCapture1.capture());
        assertThat(itemsCapture1.getAllValues().get(0).size()).isEqualTo(3);
        assertThat(itemsCapture1.getAllValues().get(1).size()).isEqualTo(4);
    }
 
    @SuppressWarnings("unchecked")
    @Test // TODO - Is this adding a group?
    public void test_updateEntries_add_group()
    {
        LocalRosterTestTarget testTarget = getLocalRosterTestTarget(ROSTER_GROUP1, ROSTER_GROUP2);
        ContactEntry sally = testTarget.localRoster.contactsTree.getContactMap().get(JID_SALLY).getHead();
        Collection<String> addresses = new ArrayList<String>();
        RosterEntryData rosterEntrySally = mock(RosterEntryData.class);
        when(rosterEntrySally.getName()).thenReturn(NAME_SALLY);
        when(rosterEntrySally.getUser()).thenReturn(JID_SALLY);
        when(testTarget.roster.getRosterEntry(JID_SALLY)).thenReturn(rosterEntrySally);
        Collection<String> rosterGroups = new ArrayList<String>();
        rosterGroups.add(GROUP_COLLEAGUES);
        rosterGroups.add(GROUP_FRIENDS);
        when(testTarget.roster.getGroupList(JID_SALLY)).thenReturn(rosterGroups);
        addresses.add(JID_SALLY);
        // Support group1 getItems() for new ContactEntry call addSelfToParent()
        List<ContactEntry> group1Contacts = new ArrayList<ContactEntry>();
        group1Contacts.add(rosterEntryAliz);
        group1Contacts.add(rosterEntryGeorge);
        group1Contacts.add(rosterEntryMicky1);
        when(group1.getItems()).thenReturn(group1Contacts);
        testTarget.rosterListener.entriesUpdated(addresses);
        Map<String, ContactEntryList> contactMap = testTarget.contactsTree.getContactMap();
        assertThat(contactMap.get(JID_SALLY).getEntryByGroup(GROUP_FRIENDS)).isNotNull();
        assertThat(contactMap.get(JID_SALLY).getEntryByGroup(GROUP_COLLEAGUES)).isEqualTo(sally);
        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> itemsCapture1 = ArgumentCaptor.forClass(List.class);
        verify(group1, times(2)).setItems(itemsCapture1.capture());
        assertThat(itemsCapture1.getAllValues().get(0).size()).isEqualTo(3);
        assertThat(itemsCapture1.getAllValues().get(1).size()).isEqualTo(4);
    }
    
    @Test 
    public void test_update_new_contact()
    {
        String GROUP_NEW = "New";
        LocalRosterTestTarget testTarget = getLocalRosterTestTarget(ROSTER_GROUP1, ROSTER_GROUP2);
        Collection<String> addresses = new ArrayList<String>();
        RosterEntryData rosterEntryNad = mock(RosterEntryData.class);
        when(rosterEntryNad.getName()).thenReturn("nad");
        when(rosterEntryNad.getUser()).thenReturn(JID_NAD);
        Collection<String> rosterGroups = Collections.singletonList(GROUP_NEW);
        when(testTarget.roster.getGroupList(JID_NAD)).thenReturn(rosterGroups);
        testTarget.localRoster.update(rosterEntryNad);
        testTarget.rosterListener.entriesAdded(addresses);
        //verify(testTarget.LocalRoster.contactEntries.contactMap).put(eq(JID_NAD), any(ContactEntry.class));
        assertThat(testTarget.localRoster.contactsTree.getContactMap().get(JID_NAD)).isNotNull();
        List<ContactGroup> groups =testTarget.rootContactGroup.getGroups();
        assertThat(groups.size()).isEqualTo(3);
        ContactGroup contactGroup = groups.get(2);
        assertThat(contactGroup.getName()).isEqualTo(GROUP_NEW);
        assertThat(contactGroup.getItems().size()).isEqualTo(1);
        assertThat(contactGroup.getItems().get(0).getUser()).isEqualTo(JID_NAD);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void test_entriesAdded_additional_group()
    {
        LocalRosterTestTarget testTarget = getLocalRosterTestTarget(ROSTER_GROUP1, ROSTER_GROUP2);
        Collection<String> addresses = new ArrayList<String>();
        RosterEntryData rosterEntryNad = mock(RosterEntryData.class);
        when(rosterEntryNad.getName()).thenReturn("nad");
        when(rosterEntryNad.getUser()).thenReturn(JID_NAD);
        when(testTarget.roster.getRosterEntry(JID_NAD)).thenReturn(rosterEntryNad);
        Collection<String> rosterGroups = new ArrayList<String>();
        rosterGroups.add(GROUP_COLLEAGUES);
        rosterGroups.add(GROUP_FRIENDS);
        when(testTarget.roster.getGroupList(JID_NAD)).thenReturn(rosterGroups);
        addresses.add(JID_NAD);
        // Support group1/2 getItems() for new ContactEntry call addSelfToParent()
        List<ContactEntry> group1Contacts = new ArrayList<ContactEntry>();
        group1Contacts.add(rosterEntryAliz);
        group1Contacts.add(rosterEntryGeorge);
        group1Contacts.add(rosterEntryMicky1);
        when(group1.getItems()).thenReturn(group1Contacts);
        List<ContactEntry> group2Contacts = new ArrayList<ContactEntry>();
        group2Contacts.add(rosterEntrySally);
        group2Contacts.add(rosterEntryBill);
        group2Contacts.add(rosterEntryMicky2);
        when(group2.getItems()).thenReturn(group2Contacts);
        testTarget.rosterListener.entriesAdded(addresses);
        Map<String, ContactEntryList> contactMap = testTarget.contactsTree.getContactMap();
        ContactEntry headContact = contactMap.get(JID_NAD).getHead();
        assertThat(headContact).isNotNull();
        assertThat(contactMap.get(JID_NAD).getEntryByGroup(GROUP_FRIENDS)).isNotNull();
        assertThat(contactMap.get(JID_NAD).getEntryByGroup(GROUP_COLLEAGUES)).isEqualTo(headContact);
        // Same group order as supplied list expected
        assertThat(headContact.getParent().getName()).isEqualTo(GROUP_COLLEAGUES);
        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> itemsCapture1 = ArgumentCaptor.forClass(List.class);
        verify(group1, times(2)).setItems(itemsCapture1.capture());
        assertThat(itemsCapture1.getAllValues().get(0).size()).isEqualTo(3);
        assertThat(itemsCapture1.getAllValues().get(1).size()).isEqualTo(4);
        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> itemsCapture2 = ArgumentCaptor.forClass(List.class);
        verify(group2, times(2)).setItems(itemsCapture2.capture());
        assertThat(itemsCapture2.getAllValues().get(0).size()).isEqualTo(3);
        assertThat(itemsCapture2.getAllValues().get(1).size()).isEqualTo(4);
    }
    
    @Test
    public void test_updateEntries_change_name()
    {
        LocalRosterTestTarget testTarget = getLocalRosterTestTarget(ROSTER_GROUP1, ROSTER_GROUP2);
        Collection<String> addresses = new ArrayList<String>();
        RosterEntryData rosterEntryAlice = mock(RosterEntryData.class);
        when(rosterEntryAlice.getName()).thenReturn("Alice");
        when(rosterEntryAlice.getUser()).thenReturn(JID_ALIZ);
        when(testTarget.roster.getRosterEntry(JID_ALIZ)).thenReturn(rosterEntryAlice);
        Collection<String> rosterGroups = Collections.singletonList(GROUP_FRIENDS);
        when(testTarget.roster.getGroupList(JID_ALIZ)).thenReturn(rosterGroups);
        addresses.add(JID_ALIZ);
        testTarget.rosterListener.entriesUpdated(addresses);
        assertThat(rosterEntryAliz.getName()).isEqualTo("Alice");
    }
    
    @Test
    public void test_updateEntries_delete()
    {
        LocalRosterTestTarget testTarget = getLocalRosterTestTarget(ROSTER_GROUP1, ROSTER_GROUP2);
        Collection<String> addresses = new ArrayList<String>();
        RosterEntryData rosterEntryAlice = mock(RosterEntryData.class);
        when(rosterEntryAlice.getName()).thenReturn("Alice");
        when(rosterEntryAlice.getUser()).thenReturn(JID_ALIZ);
        when(testTarget.roster.getRosterEntry(JID_ALIZ)).thenReturn(rosterEntryAlice);
        Collection<String> rosterGroups = Collections.emptyList();
        when(testTarget.roster.getGroupList(JID_ALIZ)).thenReturn(rosterGroups);
        addresses.add(JID_ALIZ);
        testTarget.rosterListener.entriesUpdated(addresses);
        assertThat(rosterEntryAliz.getPresence()).isEqualTo(Presence.deleted);
    }
    
    @Test
    public void test_updateEntries_undelete()
    {
        LocalRosterTestTarget testTarget = getLocalRosterTestTarget(ROSTER_GROUP1, ROSTER_GROUP2);
        Collection<String> addresses = new ArrayList<String>();
        RosterEntryData rosterEntryAlice = mock(RosterEntryData.class);
        when(rosterEntryAlice.getName()).thenReturn(NAME_ALIZ);
        when(rosterEntryAlice.getUser()).thenReturn(JID_ALIZ);
        when(testTarget.roster.getRosterEntry(JID_ALIZ)).thenReturn(rosterEntryAlice);
        Collection<String> rosterGroups = Collections.singletonList(GROUP_FRIENDS);
        when(testTarget.roster.getGroupList(JID_ALIZ)).thenReturn(rosterGroups);
        rosterEntryAliz.setPresence(Presence.deleted);
        addresses.add(JID_ALIZ);
        testTarget.rosterListener.entriesUpdated(addresses);
        assertThat(rosterEntryAliz.getPresence()).isEqualTo(Presence.online);
    }
    
    @Test
    public void test_deleteEntries()
    {
        LocalRosterTestTarget testTarget = getLocalRosterTestTarget(ROSTER_GROUP1, ROSTER_GROUP2);
        Collection<String> addresses = new ArrayList<String>();
        RosterEntryData rosterEntryAlice = mock(RosterEntryData.class);
        when(rosterEntryAlice.getName()).thenReturn(NAME_ALIZ);
        when(rosterEntryAlice.getUser()).thenReturn(JID_ALIZ);
        when(testTarget.roster.getRosterEntry(JID_ALIZ)).thenReturn(rosterEntryAlice);
        Collection<String> rosterGroups = Collections.singletonList(GROUP_FRIENDS);
        when(testTarget.roster.getGroupList(JID_ALIZ)).thenReturn(rosterGroups);
        // Check for default presence to ensure value changed
        assertThat(rosterEntryAliz.getPresence()).isEqualTo(Presence.offline);
        addresses.add(JID_ALIZ);
        testTarget.rosterListener.entriesDeleted(addresses);
        assertThat(rosterEntryAliz.getPresence()).isEqualTo(Presence.deleted);
    }
    
    @Test
    public void test_presenceChanged()
    {
        LocalRosterTestTarget testTarget = getLocalRosterTestTarget(ROSTER_GROUP1, ROSTER_GROUP2);
         RosterEntryData rosterEntryAlice = mock(RosterEntryData.class);
        when(rosterEntryAlice.getName()).thenReturn(SESSION_NAME);
        when(rosterEntryAlice.getUser()).thenReturn(SESSION_USER);
        when(testTarget.roster.getRosterEntry(SESSION_USER)).thenReturn(rosterEntryAlice);
        Collection<String> rosterGroups = new ArrayList<String>();
        rosterGroups.add(GROUP_COLLEAGUES);
        rosterGroups.add(GROUP_FRIENDS);
        when(testTarget.roster.getGroupList(SESSION_USER)).thenReturn(rosterGroups);
        // Check for default presence to ensure value changed
        assertThat(rosterEntryMicky1.getPresence()).isEqualTo(Presence.offline);
        assertThat(rosterEntryMicky2.getPresence()).isEqualTo(Presence.offline);
        // Presence class is final so cannot be mocked
        org.jivesoftware.smack.packet.Presence presence = 
            new org.jivesoftware.smack.packet.Presence(org.jivesoftware.smack.packet.Presence.Type.available);
        presence.setFrom(SESSION_USER);
        testTarget.rosterListener.presenceChanged(presence);
        verify(testTarget.roster).sync(rosterEntryMicky1);
        verify(testTarget.roster).sync(rosterEntryMicky2);
    }
  
    LocalRosterTestTarget getLocalRosterTestTarget(List<ContactEntry> rosterGroup1, List<ContactEntry> rosterGroup2)
    {
        ContactsTree contactsTree = new MultiGroupContactsTree();
        ContactGroup rootContactGroup = contactsTree.getRootContactGroup();
        LocalRosterTestTarget testTarget = new LocalRosterTestTarget();
        SmackRoster roster = mock(SmackRoster.class);
        when(roster.getHostName()).thenReturn(HOST);
        when(roster.getPort()).thenReturn(PORT);
        testTarget.roster = roster;
        //when(chatService.getSmackConnection()).thenReturn(connection);
        List<GroupCollection> groupCollections = new ArrayList<GroupCollection>();
        if (!rosterGroup1.isEmpty())
            groupCollections.add(new GroupCollection(group1, rosterGroup1));
        if (!rosterGroup2.isEmpty())
            groupCollections.add(new GroupCollection(group2, rosterGroup2));
        testTarget.contactsTree = contactsTree;
        testTarget.rootContactGroup = rootContactGroup;
        contactsTree.loadRoster(groupCollections);
        testTarget.localRoster = new LocalRoster(roster, contactsTree);
        testTarget.rosterListener = new SmackRosterListener(testTarget.localRoster);
        return testTarget;
    }
 }
