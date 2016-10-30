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
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import au.com.cybersearch2.cybertete.agents.RosterHelper;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactGroup;
import au.com.cybersearch2.cybertete.model.Presence;
import au.com.cybersearch2.cybertete.model.RosterEntryData;
import au.com.cybersearch2.cybertete.model.internal.ContactEntryList;

/**
 * ContactEntryListTest
 * @author Andrew Bowley
 * 5 May 2016
 */
public class ContactEntryListTest
{
    private static final String GROUP_FRIENDS = "Friends";
    private static final String GROUP_COLLEAGUES = "Colleagues";
    private static final String GROUP_ALLIES = "Allies";
    private static final String NAME_ALIZ = "aliz";
    private static final String JID_ALIZ = "aliz@cybersearch2.local";

    @Test
    public void test_contact_entry_constructor()
    {
        ContactEntry contactEntry =mock(ContactEntry.class);
        ContactEntryList underTest = new ContactEntryList(contactEntry);
        assertThat(underTest.getHead()).isEqualTo(contactEntry);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void test_roster_constructor()
    {
        ContactGroup friends = new ContactGroup(GROUP_FRIENDS);
        ContactGroup colleagues = new ContactGroup(GROUP_COLLEAGUES);
        ContactEntry friendAliz = new ContactEntry(NAME_ALIZ, JID_ALIZ, friends);
        ContactEntry colleagueAliz = new ContactEntry(NAME_ALIZ, JID_ALIZ, colleagues);
        ContactGroup rootContactGroup = mock(ContactGroup.class);
        List<ContactGroup> groupList = new ArrayList<ContactGroup>();
        when(rootContactGroup.getGroups()).thenReturn(groupList );
        Collection<String> rosterGroups = new ArrayList<String>();
        rosterGroups.add(GROUP_COLLEAGUES);
        rosterGroups.add(GROUP_FRIENDS);
        RosterEntryData rosterEntryData = mock(RosterEntryData.class);
        when(rosterEntryData.getName()).thenReturn(NAME_ALIZ);
        when(rosterEntryData.getUser()).thenReturn(JID_ALIZ);
        ContactEntryList underTest = new ContactEntryList(rootContactGroup, rosterGroups, rosterEntryData);
        assertThat(underTest.getEntryByGroup(GROUP_COLLEAGUES)).isNotNull();
        assertThat(underTest.getEntryByGroup(GROUP_FRIENDS)).isNotNull();
        assertThat(underTest.getEntryByGroup(GROUP_COLLEAGUES)).isEqualTo(colleagueAliz);
        assertThat(underTest.getEntryByGroup(GROUP_FRIENDS)).isEqualTo(friendAliz);
        assertThat(groupList.get(0)).isEqualTo(colleagues);
        assertThat(groupList.get(1)).isEqualTo(friends);
        ArgumentCaptor<List> groupsCaptor =  ArgumentCaptor.forClass(List.class);
        verify(rootContactGroup).setGroups(groupsCaptor.capture());
        assertThat(groupsCaptor.getValue().get(0)).isEqualTo(colleagues);
        assertThat(groupsCaptor.getValue().get(1)).isEqualTo(friends);
    }

    @Test
    public void test_addContact()
    {
        ContactGroup friends = new ContactGroup(GROUP_FRIENDS);
        ContactGroup colleagues = new ContactGroup(GROUP_COLLEAGUES);
        ContactEntry friendAliz = new ContactEntry(NAME_ALIZ, JID_ALIZ, friends);
        ContactEntry colleagueAliz = new ContactEntry(NAME_ALIZ, JID_ALIZ, colleagues);
        ContactEntryList underTest = new ContactEntryList(colleagueAliz);
        assertThat(underTest.getHead()).isEqualTo(colleagueAliz);
        underTest.addContact(friendAliz);
        assertThat(underTest.getEntryByGroup(GROUP_FRIENDS)).isEqualTo(friendAliz);
    }

    @Test
    public void test_addNewGroups()
    {
        ContactGroup friends = new ContactGroup(GROUP_FRIENDS);
        ContactGroup colleagues = new ContactGroup(GROUP_COLLEAGUES);
        ContactGroup allies = new ContactGroup(GROUP_ALLIES);
        ContactEntry friendAliz = new ContactEntry(NAME_ALIZ, JID_ALIZ, friends);
        ContactEntry colleagueAliz = new ContactEntry(NAME_ALIZ, JID_ALIZ, colleagues);
        ContactEntry allyAliz = new ContactEntry(NAME_ALIZ, JID_ALIZ, allies);
        ContactGroup rootContactGroup = mock(ContactGroup.class);
        List<ContactGroup> groupList = new ArrayList<ContactGroup>();
        when(rootContactGroup.getGroups()).thenReturn(groupList );
        Collection<String> rosterGroups = new ArrayList<String>();
        rosterGroups.add(GROUP_ALLIES);
        RosterEntryData rosterEntryData = mock(RosterEntryData.class);
        when(rosterEntryData.getName()).thenReturn(NAME_ALIZ);
        when(rosterEntryData.getUser()).thenReturn(JID_ALIZ);
        ContactEntryList underTest = new ContactEntryList(rootContactGroup, rosterGroups, rosterEntryData);
        assertThat(underTest.getHead()).isEqualTo(allyAliz);
        rosterGroups.add(GROUP_COLLEAGUES);
        rosterGroups.add(GROUP_FRIENDS);
        Set<ContactGroup> newGroupSet = underTest.addNewGroups(rootContactGroup, rosterGroups, rosterEntryData);
        assertThat(newGroupSet.size()).isEqualTo(2);
        assertThat(newGroupSet).contains(friends, colleagues);
        assertThat(underTest.getEntryByGroup(GROUP_COLLEAGUES)).isEqualTo(colleagueAliz);
        assertThat(underTest.getEntryByGroup(GROUP_FRIENDS)).isEqualTo(friendAliz);
        assertThat(groupList.get(1)).isEqualTo(colleagues);
        assertThat(groupList.get(2)).isEqualTo(friends);
    }

    @Test
    public void test_addGroups_already_in_tree()
    {
        ContactGroup friends = new ContactGroup(GROUP_FRIENDS);
        ContactGroup colleagues = new ContactGroup(GROUP_COLLEAGUES);
        ContactGroup allies = new ContactGroup(GROUP_ALLIES);
        ContactEntry friendAliz = new ContactEntry(NAME_ALIZ, JID_ALIZ, friends);
        ContactEntry colleagueAliz = new ContactEntry(NAME_ALIZ, JID_ALIZ, colleagues);
        ContactEntry allyAliz = new ContactEntry(NAME_ALIZ, JID_ALIZ, allies);
        ContactGroup rootContactGroup = mock(ContactGroup.class);
        List<ContactGroup> groupList = new ArrayList<ContactGroup>();
        groupList.add(friends);
        groupList.add(colleagues);
        when(rootContactGroup.getGroups()).thenReturn(groupList );
        Collection<String> rosterGroups = new ArrayList<String>();
        rosterGroups.add(GROUP_ALLIES);
        RosterEntryData rosterEntryData = mock(RosterEntryData.class);
        when(rosterEntryData.getName()).thenReturn(NAME_ALIZ);
        when(rosterEntryData.getUser()).thenReturn(JID_ALIZ);
        ContactEntryList underTest = new ContactEntryList(rootContactGroup, rosterGroups, rosterEntryData);
        assertThat(underTest.getHead()).isEqualTo(allyAliz);
        rosterGroups.add(GROUP_COLLEAGUES);
        rosterGroups.add(GROUP_FRIENDS);
        Set<ContactGroup> newGroupSet = underTest.addNewGroups(rootContactGroup, rosterGroups, rosterEntryData);
        assertThat(newGroupSet).isEmpty();
        assertThat(underTest.getEntryByGroup(GROUP_COLLEAGUES)).isEqualTo(colleagueAliz);
        assertThat(underTest.getEntryByGroup(GROUP_FRIENDS)).isEqualTo(friendAliz);
    }

    @Test
    public void test_updateList()
    {
        ContactGroup rootContactGroup = mock(ContactGroup.class);
        List<ContactGroup> groupList = new ArrayList<ContactGroup>();
        when(rootContactGroup.getGroups()).thenReturn(groupList );
        Collection<String> rosterGroups = new ArrayList<String>();
        rosterGroups.add(GROUP_COLLEAGUES);
        rosterGroups.add(GROUP_FRIENDS);
        RosterEntryData rosterEntryData = mock(RosterEntryData.class);
        when(rosterEntryData.getName()).thenReturn(NAME_ALIZ);
        when(rosterEntryData.getUser()).thenReturn(JID_ALIZ);
        ContactEntryList underTest = new ContactEntryList(rootContactGroup, rosterGroups, rosterEntryData);
        when(rosterEntryData.getName()).thenReturn("alice");
        Set<String> newGroupSet = underTest.updateList(rosterEntryData, rosterGroups);
        assertThat(newGroupSet).isEmpty();
        assertThat(underTest.getEntryByGroup(GROUP_COLLEAGUES).getName()).isEqualTo("alice");
        assertThat(underTest.getEntryByGroup(GROUP_FRIENDS).getName()).isEqualTo("alice");
    }

    @Test
    public void test_updateList_delete()
    {
        ContactGroup rootContactGroup = mock(ContactGroup.class);
        List<ContactGroup> groupList = new ArrayList<ContactGroup>();
        when(rootContactGroup.getGroups()).thenReturn(groupList );
        Collection<String> rosterGroups = new ArrayList<String>();
        rosterGroups.add(GROUP_COLLEAGUES);
        rosterGroups.add(GROUP_FRIENDS);
        RosterEntryData rosterEntryData = mock(RosterEntryData.class);
        when(rosterEntryData.getName()).thenReturn(NAME_ALIZ);
        when(rosterEntryData.getUser()).thenReturn(JID_ALIZ);
        ContactEntryList underTest = new ContactEntryList(rootContactGroup, rosterGroups, rosterEntryData);
        underTest.getEntryByGroup(GROUP_COLLEAGUES).setPresence(Presence.deleted);
        rosterGroups.remove(GROUP_FRIENDS);
        when(rosterEntryData.getName()).thenReturn("alice");
        Set<String> newGroupSet = underTest.updateList(rosterEntryData, rosterGroups);
        assertThat(newGroupSet).isEmpty();
        assertThat(underTest.getEntryByGroup(GROUP_FRIENDS).getName()).isEqualTo(NAME_ALIZ);
        assertThat(underTest.getEntryByGroup(GROUP_FRIENDS).getPresence()).isEqualTo(Presence.deleted);
        assertThat(underTest.getEntryByGroup(GROUP_COLLEAGUES).getPresence()).isEqualTo(Presence.online);
        assertThat(underTest.getEntryByGroup(GROUP_COLLEAGUES).getName()).isEqualTo("alice");
    }

    @Test
    public void test_updateList_new_groups()
    {
        ContactGroup rootContactGroup = mock(ContactGroup.class);
        List<ContactGroup> groupList = new ArrayList<ContactGroup>();
        when(rootContactGroup.getGroups()).thenReturn(groupList );
        Collection<String> rosterGroups = new ArrayList<String>();
        rosterGroups.add(GROUP_ALLIES);
        RosterEntryData rosterEntryData = mock(RosterEntryData.class);
        when(rosterEntryData.getName()).thenReturn(NAME_ALIZ);
        when(rosterEntryData.getUser()).thenReturn(JID_ALIZ);
        ContactEntryList underTest = new ContactEntryList(rootContactGroup, rosterGroups, rosterEntryData);
        when(rosterEntryData.getName()).thenReturn("alice");
        rosterGroups.add(GROUP_COLLEAGUES);
        rosterGroups.add(GROUP_FRIENDS);
        Set<String> newGroupSet = underTest.updateList(rosterEntryData, rosterGroups);
        assertThat(newGroupSet.size()).isEqualTo(2);
        assertThat(newGroupSet).contains(GROUP_COLLEAGUES, GROUP_FRIENDS);
        // New groups are handled by calling addGroups
        assertThat(underTest.getEntryByGroup(GROUP_COLLEAGUES)).isNull();
        assertThat(underTest.getEntryByGroup(GROUP_FRIENDS)).isNull();
    }

    @Test
    public void test_markDeleted()
    {
        ContactGroup rootContactGroup = mock(ContactGroup.class);
        List<ContactGroup> groupList = new ArrayList<ContactGroup>();
        when(rootContactGroup.getGroups()).thenReturn(groupList );
        Collection<String> rosterGroups = new ArrayList<String>();
        rosterGroups.add(GROUP_COLLEAGUES);
        rosterGroups.add(GROUP_FRIENDS);
        RosterEntryData rosterEntryData = mock(RosterEntryData.class);
        when(rosterEntryData.getName()).thenReturn(NAME_ALIZ);
        when(rosterEntryData.getUser()).thenReturn(JID_ALIZ);
        ContactEntryList underTest = new ContactEntryList(rootContactGroup, rosterGroups, rosterEntryData);
        underTest.markDeleted();
        assertThat(underTest.getEntryByGroup(GROUP_COLLEAGUES).getPresence()).isEqualTo(Presence.deleted);
        assertThat(underTest.getEntryByGroup(GROUP_FRIENDS).getPresence()).isEqualTo(Presence.deleted);
    }

    @Test
    public void test_getContactEntries()
    {
        ContactGroup rootContactGroup = mock(ContactGroup.class);
        List<ContactGroup> groupList = new ArrayList<ContactGroup>();
        when(rootContactGroup.getGroups()).thenReturn(groupList );
        Collection<String> rosterGroups = new ArrayList<String>();
        rosterGroups.add(GROUP_COLLEAGUES);
        rosterGroups.add(GROUP_FRIENDS);
        RosterEntryData rosterEntryData = mock(RosterEntryData.class);
        when(rosterEntryData.getName()).thenReturn(NAME_ALIZ);
        when(rosterEntryData.getUser()).thenReturn(JID_ALIZ);
        ContactEntryList underTest = new ContactEntryList(rootContactGroup, rosterGroups, rosterEntryData);
        List<ContactEntry> entries = underTest.getContactEntries();
        assertThat(entries.get(0).getUser()).isEqualTo(JID_ALIZ);
        assertThat(entries.get(0).getParent().getName()).isEqualTo(GROUP_COLLEAGUES);
        assertThat(entries.get(1).getUser()).isEqualTo(JID_ALIZ);
        assertThat(entries.get(1).getParent().getName()).isEqualTo(GROUP_FRIENDS);
    }

    @Test
    public void test_sync()
    {
        ContactGroup rootContactGroup = mock(ContactGroup.class);
        List<ContactGroup> groupList = new ArrayList<ContactGroup>();
        when(rootContactGroup.getGroups()).thenReturn(groupList );
        Collection<String> rosterGroups = new ArrayList<String>();
        rosterGroups.add(GROUP_COLLEAGUES);
        rosterGroups.add(GROUP_FRIENDS);
        RosterEntryData rosterEntryData = mock(RosterEntryData.class);
        when(rosterEntryData.getName()).thenReturn(NAME_ALIZ);
        when(rosterEntryData.getUser()).thenReturn(JID_ALIZ);
        ContactEntryList underTest = new ContactEntryList(rootContactGroup, rosterGroups, rosterEntryData);
        RosterHelper roster = mock(RosterHelper.class);
        underTest.sync(roster);
        ArgumentCaptor<ContactEntry> entryCaptor = ArgumentCaptor.forClass(ContactEntry.class);
        verify(roster, times(2)).sync(entryCaptor.capture());
        ContactGroup friends = new ContactGroup(GROUP_FRIENDS);
        ContactGroup colleagues = new ContactGroup(GROUP_COLLEAGUES);
        ContactEntry friendAliz = new ContactEntry(NAME_ALIZ, JID_ALIZ, friends);
        ContactEntry colleagueAliz = new ContactEntry(NAME_ALIZ, JID_ALIZ, colleagues);
        assertThat(entryCaptor.getAllValues().get(0)).isEqualTo(colleagueAliz);
        assertThat(entryCaptor.getAllValues().get(1)).isEqualTo(friendAliz);
   }
}
