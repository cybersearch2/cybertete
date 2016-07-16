/**
    Copyright (C) 2015  www.cybersearch2.com.au

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactsTree;
import au.com.cybersearch2.cybertete.model.RosterAgent;
import au.com.cybersearch2.cybertete.model.RosterEntryData;

/**
 * LocalRoster
 * Contains model roster which is kept in sync with roster configuration on Chat server.
 * It is hooked to a roster listener to monitors changes on the external roster. 
 * Also adds contacts for the user.
 * @author Andrew Bowley
 * 9 Nov 2015
 */
public class LocalRoster implements RosterAgent
{
    /** A Smack contact may belong to more than group. Such entries are chained into a linked list. */
    ContactsTree contactsTree;
    /** Interface to access the XMPP roster implementation */
    RosterHelper roster;

    /**
     * Construct LocalRoster object
     * @param roster Interface to access the XMPP roster implementation
     * @param contactsTree Container of contact details kept in sync with master roster located at the Chat server
     */
    public LocalRoster(RosterHelper roster, ContactsTree contactsTree)
    {
        this.roster = roster;
        this.contactsTree = contactsTree;
     }

    /**
     * Returns roster entry data for specified user
     * @param user User JID
     * @return RosterEntryData object
     */
    public RosterEntryData getRosterEntryData(String user)
    {
        return roster.getRosterEntry(user);
    }
 
    /**
     * Add new contact on remote Chat server. When complete, this roster will be updated via an "add uers" event.
     * @param entry New contact entry
     * @return flag set true if opertion completed or false if contact already exists in remote roster
     * @see au.com.cybersearch2.cybertete.model.RosterAgent#addContact(au.com.cybersearch2.cybertete.model.ContactEntry)
     */
    @Override
    public boolean addContact(ContactEntry entry)
    {
        String user = entry.getUser();
        String groupName = entry.getParent().getName();
        Collection<String> rosterGroups = roster.getGroupList(user);
        if (rosterGroups.contains(groupName))
            return false;
        List<String> augmentedGroups = new ArrayList<String>(rosterGroups.size() + 1);
        if (!rosterGroups.isEmpty())
            augmentedGroups.addAll(rosterGroups);
        augmentedGroups.add(groupName);
        roster.createEntry(entry, augmentedGroups);
        return true;
    }

    /**
     * Add a new user. Also allows for existing user to join one or more groups.
     * @param rosterEntry Contact details including User JID
     */
    public void add(RosterEntryData rosterEntry)
    {
        // New user not found in external roster is not expected
        if (rosterEntry != null)
        {   // No new groups found is also not expected
            Collection<String> groups = getNewGroups(rosterEntry.getUser());
            if (groups.size() > 0)
                contactsTree.addUser(rosterEntry, groups);
        }
    }

    /**
     * Update user configuration - change name and/or group membership
     * @param rosterEntry Contact details including User JID
     */
    public void update(RosterEntryData rosterEntry)
    {
        // Place external roster group names in set
        // Expect existing user will be found in the contact map
        if (!contactsTree.updateUser(rosterEntry, roster.getGroupList(rosterEntry.getUser())))
            // Need to add this one, not update
            add(rosterEntry);
    }

    /**
     * Mark user as deleted. Entry will remain until the user logs off.
     * @param user User JID
     */
    public void deleteUser(String user)
    {
        contactsTree.deleteUser(user);
    }

    /**
     * Update contact entry to match corresponding remote contact. Applies only to change of presence.
     * @param user User JID
     */
    public void syncUser(String user)
    {
        contactsTree.syncUser(user, roster);
    }

    /**
     * Returns list of group names newly added to external roster
     * @param user JID
     * @return Group name collection
     */
    private Collection<String> getNewGroups(String user)
    {
        Collection<String> latestRosterGroups = roster.getGroupList(user);
        // Remove any group from list in which the user already belongs
        List<String> existingRosterGroups = contactsTree.getGroupMembership(user);
        if (existingRosterGroups.isEmpty())
            return latestRosterGroups;
        List<String> rosterGroups = new ArrayList<String>();
        for (String rosterGroup: latestRosterGroups)
            if (!existingRosterGroups.contains(rosterGroup))
                rosterGroups.add(rosterGroup);
        return rosterGroups;
    }
}
