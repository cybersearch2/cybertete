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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import au.com.cybersearch2.cybertete.CyberteteException;
import au.com.cybersearch2.cybertete.agents.RosterHelper;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactGroup;
import au.com.cybersearch2.cybertete.model.Presence;
import au.com.cybersearch2.cybertete.model.RosterEntryData;

/**
 * ContactEntryList
 * Each contact entry is potentially the head of a linked list to cater for case where
 * a user is a member of more than one group.
 * @author Andrew Bowley
 * 15 Apr 2016
 */
public class ContactEntryList
{
    /**
     * ListItem
     * Contacts one ContactEntry object and a link to the next ListItem in the chain.
     * Creates an iterator to walk the chain and visit each contact entry.
     */
    private static final class ListItem
    {
        public ListItem(ContactEntry contactEntry)
        {
            this.contactEntry = contactEntry;
        }
        
        public Iterator<ContactEntry> iterator()
        {
            return new Iterator<ContactEntry>(){
                ListItem current = ListItem.this;
                @Override
                public boolean hasNext()
                {
                    return current != null;
                }

                @Override
                public ContactEntry next()
                {
                    if (current == null)
                        throw new NoSuchElementException();
                    ContactEntry contactEntry = current.contactEntry;
                    current = current.next;
                    return contactEntry;
                }

                @Override
                public void remove()
                {
                    throw new UnsupportedOperationException("Remove not supported");
                }};
        }
        
        public ContactEntry contactEntry;
        public ListItem next;
    }
    
    /** Head of list - never null */
    ListItem head;
    /** Tail of list */
    ListItem tail;

    /**
     * Create ContactEntryList object
     * @param head First contact entry in list
     */
    public ContactEntryList(ContactEntry head)
    {
        ListItem headItem = new ListItem(head);
        this.head = headItem;
        this.tail = headItem;
    }

    /**
     * Create ContactEntryList object using roster information
     * @param rootContactGroup Root of contacts tree
     * @param rosterGroups     Group membership
     * @param rosterEntryData  Roster contact details  
     */
    public ContactEntryList(ContactGroup rootContactGroup, Collection<String> rosterGroups, RosterEntryData rosterEntryData)
    {   // Add groups allows for constructor usage
        addGroups(rootContactGroup, rosterGroups, rosterEntryData);
    }
    
   /**
     * Returns first contact entry in list
     * @return ContactEntry object
     */
    public ContactEntry getHead()
    {
        return head.contactEntry;
    }

    /**
     * Add contact to list. Note this list will have at least one item after construction, so additional entries are chained.
     * @param contactEntry Contact entry
     */
    public void addContact(ContactEntry contactEntry)
    {
        if (!head.contactEntry.getUser().equals(contactEntry.getUser()))
            throw new CyberteteException("Cannot add entry for \"" + contactEntry.getUser() + "\" to list of \"" + head.contactEntry.getUser() + "\"");
        ListItem tailItem = new ListItem(contactEntry);
        this.tail.next = tailItem;
        this.tail = tailItem;
    }

    /**
     * Returns contact entry belonging to group specified by name
     * @param name Group name
     * @return ContactEntry object or null if group not found
     */
    public ContactEntry getEntryByGroup(String name)
    {
        Iterator<ContactEntry> iterator = head.iterator();
        while (iterator.hasNext())
        {
            ContactEntry contactEntry = iterator.next();
            if (contactEntry.getParent().getName().equals(name))
                return contactEntry;
        }
        return null;
    }

    /**
     * Add new list items for all groups in given collection not already in contact's group membership
     * @param rootContactGroup Root of contacts tree
     * @param rosterGroups     Group membership
     * @param rosterEntryData  Roster contact details  
     * @return subset of rosterGroups added to contacts tree
     */
    public Set<ContactGroup> addNewGroups(ContactGroup rootContactGroup, Collection<String> rosterGroups, RosterEntryData rosterEntryData)
    {
        return addGroups(rootContactGroup, findNewGroups(rosterGroups), rosterEntryData);
    }

    /**
     * Add new list items for all groups in given collection
     * @param rootContactGroup Root of contacts tree
     * @param rosterGroups     Group membership
     * @param rosterEntryData  Roster contact details  
     * @return subset of rosterGroups added to contacts tree
     */
    public Set<ContactGroup> addGroups(ContactGroup rootContactGroup, Collection<String> rosterGroups, RosterEntryData rosterEntryData)
    {
        // Currently all groups reside as children of the root
        List<ContactGroup> groupList = rootContactGroup.getGroups();
        // Collect new groups to return
        Set<ContactGroup> newGroupSet = new HashSet<ContactGroup>();
        for (String name:rosterGroups)
        {
            ContactGroup contactGroup = getContactGroup(name, groupList);
            if (contactGroup == null)
            {   // New group
                contactGroup = new ContactGroup(name);
                groupList.add(contactGroup);
                newGroupSet.add(contactGroup);
            }
            ContactEntry contactEntry = 
                new ContactEntry(rosterEntryData.getName(), rosterEntryData.getUser(), contactGroup);
            contactEntry.addSelfToParent();
            ListItem listItem = new ListItem(contactEntry);
            if (head == null) // Head will be null if this method is called from constructor
                head = listItem;
            else
                tail.next = listItem;
            tail = listItem;
        }
        if (!newGroupSet.isEmpty())
            rootContactGroup.setGroups(groupList);
        return newGroupSet;
    }

    /**
     * Update list
     * @param rosterEntryData  Roster contact details  
     * @param rosterGroups     Group membership
     * @return subset of rosterGroups not found in contacts tree to update
     */
    public Set<String> updateList(RosterEntryData rosterEntryData, Collection<String> rosterGroups)
    {
        // Isolate new groups by elimination
        Set<String> newGroupSet = new HashSet<String>();
        newGroupSet.addAll(rosterGroups);
        // Prepare to update, walking contact chain
        Iterator<ContactEntry> iterator = head.iterator();
        while (iterator.hasNext())
        {
            ContactEntry contactEntry = iterator.next();
            String groupName = contactEntry.getParent().getName();
            if (!rosterGroups.contains(groupName)) // Delete from group
                contactEntry.setPresence(Presence.deleted);
            else
            {   // Remove from group set to remember item in group updated
                newGroupSet.remove(groupName);
                updateContact(contactEntry, rosterEntryData);
            }
        }
        return newGroupSet;
    }

    /**
     * Mark all items as deleted by updating presence to "deleted"
     */
    public void markDeleted()
    {
        Iterator<ContactEntry> iterator = head.iterator();
        while (iterator.hasNext())
            iterator.next().setPresence(Presence.deleted);
    }

    /**
     * Returns list containing all items
     * @return contact entry list
     */
    public List<ContactEntry> getContactEntries()
    {
        List<ContactEntry> contacts = new ArrayList<ContactEntry>();
        Iterator<ContactEntry> iterator = head.iterator();
        while (iterator.hasNext())
            contacts.add(iterator.next());
        return contacts;
    }

    /**
     * Returns subset of given collection of groups not currently in this contact's membership
     * @param groups
     * @return List of group names
     */
    public List<String> findNewGroups(Collection<String> groups)
    {
        // Isolate new groups by elimination
        List<String> newSubset = new ArrayList<String>();
        newSubset.addAll(groups);
        Iterator<ContactEntry> iterator = head.iterator();
        while (iterator.hasNext())
        {
            String name = iterator.next().getParent().getName();
            if (groups.contains(name))
                newSubset.remove(name); 
        }
        return newSubset;
    }

    /**
     * Sychronize every contact entry in list with corresponding remote contact. Applies only to change of presence.
     * @param roster The XMPP roster implementation
     */
    public void sync(RosterHelper roster)
    {
        Iterator<ContactEntry> iterator = head.iterator();
        while (iterator.hasNext())
            roster.sync(iterator.next());
    }
 
    /**
     * Clear the list
     */
    public void clear()
    {
        ListItem listItem = head;
        while (true)
        {
            ListItem next = listItem.next;
            listItem.contactEntry = null;
            listItem.next = null;
            if (next == null)
                break;
            listItem = next;
        }
    }
    
    /**
     * Returns contact group from given list, found by name
     * @param name Group name
     * @param groupList List of contact groups
     * @return ContactGroup object or null if not found
     */
    ContactGroup getContactGroup(String name, List<ContactGroup> groupList)
    {
        for (ContactGroup contactGroup: groupList)
            if (contactGroup.getName().equals(name))
                return contactGroup;
        return null;
    }

    /**
     * Update contact entry
     * @param contact Contact entry to update
     * @param rosterEntryData  Roster contact details  
     */
    void updateContact(ContactEntry contact, RosterEntryData rosterEntryData)
    {
        // Undelete if deleted - not expected
        boolean isDeleted = contact.getPresence() == Presence.deleted;
        if (isDeleted)
            contact.setPresence(Presence.online);
        // Update name, which is currently the only attribute that is editable
        if (!contact.getName().equals(rosterEntryData.getName()))
            contact.setName(rosterEntryData.getName());
    }




}
