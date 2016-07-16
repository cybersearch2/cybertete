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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.com.cybersearch2.cybertete.agents.RosterHelper;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactGroup;
import au.com.cybersearch2.cybertete.model.ContactItem;
import au.com.cybersearch2.cybertete.model.ContactsTree;
import au.com.cybersearch2.cybertete.model.RosterEntryData;
import au.com.cybersearch2.cybertete.model.GroupCollection;

/**
 * MultiGroupContactsTree
 * Container of contact details, hosted by LocalRoster, is kept in sync with master roster located at the Chat server.
 * Each contact entry is potentially a member of more than one group.
 * @author Andrew Bowley
 * 1 Apr 2016
 */
public class MultiGroupContactsTree extends ContactMap implements ContactsTree
{
    static final List<ContactGroup> EMPTY_GROUPS;
    static final List<ContactEntry> EMPTY_ITEMS;
    
    /** Root of tree */
    final ContactGroup rootContactGroup;

    static
    {
        EMPTY_GROUPS = Collections.emptyList();
        EMPTY_ITEMS = Collections.emptyList();
    }
    
    /**
     * Construct empty MultiGroupContactsTree object. Populate tree by calling loadRoster().
     */
    public MultiGroupContactsTree()
    {
        super();
        rootContactGroup = new ContactGroup(ContactItem.ROOT_GROUP_NAME);
    }
 
    /**
     * @see au.com.cybersearch2.cybertete.model.ContactsTree#loadRoster(java.util.List)
     */
    @Override
    public void loadRoster(List<GroupCollection> groupCollections)
    {
        // Build contacts tree. Note clear() must be called prior if reloading tree.
        // To initialize model roster, set super groups and items
        List<ContactGroup> contactGroups = new ArrayList<ContactGroup>();
        for (GroupCollection groupCollection: groupCollections)
        {
            List<ContactEntry> contactList = groupCollection.getItems();
            ContactGroup contactGroup = groupCollection.getGroup();
            if (contactGroup.getName().equals(ContactItem.UNGROUPED_NAME))
                addUngroupedContacts(contactList);
            else 
            {
                if (!contactList.isEmpty())
                    put(contactGroup, contactList);
                contactGroups.add(contactGroup);
            }
        }
        rootContactGroup.setGroups(contactGroups);
    }

    /**
     * Returns root of tree
     * @see au.com.cybersearch2.cybertete.model.ContactsTree#getRootContactGroup()
     */
    @Override
    public ContactGroup getRootContactGroup()
    {
        return rootContactGroup;
    }

    /**
     * Add new contact
     * @see au.com.cybersearch2.cybertete.model.ContactsTree#addUser(au.com.cybersearch2.cybertete.model.RosterEntryData, java.util.Collection)
     */
    @Override
    public void addUser(RosterEntryData rosterEntryData, Collection<String> rosterGroups)
    {
        String user = rosterEntryData.getUser();
        ContactEntryList contactEntryList = get(user);
        if (contactEntryList == null) 
            put(rootContactGroup, rosterGroups, rosterEntryData);
        else // Contact already exists, so just add new groups, if any found
            contactEntryList.addNewGroups(rootContactGroup, rosterGroups, rosterEntryData);
    }

    /**
     * Update contact
     * @see au.com.cybersearch2.cybertete.model.ContactsTree#updateUser(au.com.cybersearch2.cybertete.model.RosterEntryData, java.util.Collection)
     */
    @Override
    public boolean updateUser(RosterEntryData rosterEntryData, Collection<String> rosterGroups)
    {
        ContactEntryList contactEntryList = get(rosterEntryData.getUser());
        if (contactEntryList == null)
            return false;
        // Update contact entry procedure returns set of new groups to add to tree
        Set<String> newGroupSet = contactEntryList.updateList(rosterEntryData, rosterGroups);
        if (!newGroupSet.isEmpty())
            contactEntryList.addGroups(rootContactGroup, newGroupSet, rosterEntryData);
        return true;
    }

    /**
     * Delete contact
     * @see au.com.cybersearch2.cybertete.model.ContactsTree#deleteUser(java.lang.String)
     */
    @Override
    public boolean deleteUser(String user)
    {
        ContactEntryList contactEntryList = get(user);
        if (contactEntryList != null)
            contactEntryList.markDeleted();
        return contactEntryList != null;
    }

    /**
     * Sync local contact with remote roster
     * @see au.com.cybersearch2.cybertete.model.ContactsTree#syncUser(java.lang.String, au.com.cybersearch2.cybertete.agents.RosterHelper)
     */
    @Override
    public boolean syncUser(String user, RosterHelper roster)
    {
        ContactEntryList contactEntryList = get(user);
        if (contactEntryList != null)
            contactEntryList.sync(roster);
        return contactEntryList != null;
    }

    /**
     * Returns Contact Entry List of specified user. The list contains a distinct entry for each group to which the user belongs.
     * @see au.com.cybersearch2.cybertete.model.ContactsTree#getContactEntryList(java.lang.String)
     */
    @Override
    public ContactEntryList getContactEntryList(String user)
    {
        // Return head of contact item chain
        ContactEntryList contactEntryList = get(user);
        return contactEntryList != null ? contactEntryList : null;
    }

    /**
     * Returns user contact item in the roster for given jid, using case-insensitive and resource-stripped match. 
     * Note - there may be items for other groups.
     * @see au.com.cybersearch2.cybertete.model.ContactsTree#getContactEntryByName(java.lang.String)
     */
    @Override
    public ContactEntry getContactEntryByName(String name)
    {
        return super.getContactEntryByName(name);
    }
    
    /**
     * @see au.com.cybersearch2.cybertete.model.ContactsTree#clear()
     */
    @Override
    public void clear()
    {
        for (ContactGroup contactGroup: rootContactGroup.getGroups())
        {
            contactGroup.getItems().clear();
            // Set empty list to fire property change
            contactGroup.setItems(EMPTY_ITEMS);
        }
        rootContactGroup.getGroups().clear();
        rootContactGroup.getItems().clear();
        rootContactGroup.setGroups(EMPTY_GROUPS);
        clearMap();
    }

    /**
     * Clear existing tree contents
     * @see au.com.cybersearch2.cybertete.model.ContactsTree#getGroupMembership(java.lang.String)
     */
    @Override
    public List<String> getGroupMembership(String user)
    {
        List<String> groupList = new ArrayList<String>();
        for (ContactGroup contactGroup: rootContactGroup.getGroups())
            if (contactGroup.getContact(user) != null)
                groupList.add(contactGroup.getName());
        return groupList;

    }
 
    /**
    * Return container which maps user JID to contact entry list
     * @see au.com.cybersearch2.cybertete.model.ContactsTree#getContactMap()
     */
    @Override
    public Map<String, ContactEntryList> getContactMap()
    {
        return super.getContactMap();
    }

    /**
     * Attach ungroup collection to the root and add to tree 
     * @param contactList Collection of ungrouped contact entries
     */
    protected void addUngroupedContacts(List<ContactEntry> contactList)
    {
        Iterator<ContactEntry> iterator = contactList.iterator();
        while (iterator.hasNext())
        {
            ContactEntry contactEntry = iterator.next();
            contactEntry.attach(rootContactGroup);
            put(contactEntry);
        }
    }

}
