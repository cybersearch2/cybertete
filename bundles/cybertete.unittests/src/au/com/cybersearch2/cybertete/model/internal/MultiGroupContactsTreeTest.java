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
package au.com.cybersearch2.cybertete.model.internal;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import au.com.cybersearch2.cybertete.agents.RosterHelper;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactGroup;
import au.com.cybersearch2.cybertete.model.ContactItem;
import au.com.cybersearch2.cybertete.model.GroupCollection;
import au.com.cybersearch2.cybertete.model.RosterEntryData;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * MultiGroupContactsTreeTest
 * Tests cover areas omitted by LocalRosterTest
 * @author Andrew Bowley
 * 6 May 2016
 */
public class MultiGroupContactsTreeTest
{
    class TestTarget
    {
        public List<GroupCollection> groupCollections;
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
    private static final String NAME_NAD = "nad";
    private static final String GROUP_FRIENDS = "Friends";
    private static final String GROUP_COLLEAGUES = "Colleagues";
    private static final String GROUP_ALLIES = "Allies";
    private static final String NAME_ALIZ = "aliz";
    private static final String JID_ALIZ = "aliz@cybersearch2.local";
    private static final String NAME_GEORGE = "George";
    private static final String JID_GEORGE = "george@cybersearch2.local";
    private static final String NAME_SALLY = "sally";
    private static final String JID_SALLY = "sally@cybersearch2.local";
    private static final String NAME_BILL = "bill";
    private static final String JID_BILL = "bill@cybersearch2.local";
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
    ContactEntry rosterNad;
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
    public void test_clear()
    {
        MultiGroupContactsTree underTest = new MultiGroupContactsTree();
        TestTarget testTarget = getTestTarget(ROSTER_GROUP1, ROSTER_GROUP2);
        underTest.loadRoster(testTarget.groupCollections);
        assertThat(underTest.getContactMap()).isNotEmpty();
        List<ContactEntry> ungrouped = underTest.getRootContactGroup().getItems();
        assertThat(ungrouped.size()).isEqualTo(1);
        assertThat(ungrouped.get(0).getName()).isEqualTo(NAME_NAD);
        List<ContactGroup> groups = underTest.getRootContactGroup().getGroups();
        assertThat(groups.size()).isEqualTo(3);
        // Chect ContactEntry.attach() detaches too
        assertThat(groups.get(2).getItems()).isEmpty();
        underTest.clear();
        assertThat(underTest.getContactMap()).isEmpty();
        assertThat(underTest.getRootContactGroup().getItems()).isEmpty();
        assertThat(underTest.getRootContactGroup().getGroups()).isEmpty();
    }
    @Test
    public void test_user_not_found()
    {
        MultiGroupContactsTree underTest = new MultiGroupContactsTree();
        TestTarget testTarget = getTestTarget(ROSTER_GROUP1, ROSTER_GROUP2);
        underTest.loadRoster(testTarget.groupCollections);
        assertThat(underTest.deleteUser("anonymous")).isFalse();
        RosterHelper roster = mock(RosterHelper.class);
        assertThat(underTest.syncUser("anonymous", roster)).isFalse();
        verify(roster, times(0)).sync(isA(ContactEntry.class));
        assertThat(underTest.getContactEntryList("anonymous")).isNull();
    }

    @Test
    public void test_getContactEntryList()
    {
        MultiGroupContactsTree underTest = new MultiGroupContactsTree();
        TestTarget testTarget = getTestTarget(ROSTER_GROUP1, ROSTER_GROUP2);
        underTest.loadRoster(testTarget.groupCollections);
        ContactEntryList contactEntryList = underTest.getContactEntryList(SESSION_USER); 
        assertThat(contactEntryList.getEntryByGroup(GROUP_COLLEAGUES)).isNotNull();
        assertThat(contactEntryList.getEntryByGroup(GROUP_FRIENDS)).isNotNull();
    }

    @Test
    public void test_getContactEntryByName()
    {
        MultiGroupContactsTree underTest = new MultiGroupContactsTree();
        TestTarget testTarget = getTestTarget(ROSTER_GROUP1, ROSTER_GROUP2);
        underTest.loadRoster(testTarget.groupCollections);
        ContactEntry contactEntry = underTest.getContactEntryByName(SESSION_USER + "/koisk");
        assertThat(contactEntry).isEqualTo(rosterEntryMicky1);
        assertThat(underTest.getContactEntryByName("anonymous")).isNull();
    }
    
    TestTarget getTestTarget(List<ContactEntry> rosterGroup1, List<ContactEntry> rosterGroup2)
    {
        TestTarget testTarget = new TestTarget();
        List<GroupCollection> groupCollections = new ArrayList<GroupCollection>();
        if (!rosterGroup1.isEmpty())
            groupCollections.add(new GroupCollection(group1, rosterGroup1));
        if (!rosterGroup2.isEmpty())
            groupCollections.add(new GroupCollection(group2, rosterGroup2));
        List<ContactEntry> emptyItems = Collections.emptyList();
        groupCollections.add(new GroupCollection(new ContactGroup(GROUP_ALLIES), emptyItems));
        rosterNad = new ContactEntry(NAME_NAD, JID_NAD, new ContactGroup(ContactItem.UNGROUPED_NAME));
        groupCollections.add(new GroupCollection((ContactGroup) rosterNad.getParent(), Collections.singletonList(rosterNad)));
        testTarget.groupCollections = groupCollections;
        return testTarget;
    }
}
