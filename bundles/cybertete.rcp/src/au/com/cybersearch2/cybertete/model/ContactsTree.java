package au.com.cybersearch2.cybertete.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import au.com.cybersearch2.cybertete.agents.RosterHelper;
import au.com.cybersearch2.cybertete.model.internal.ContactEntryList;

/**
 * ContactsTree
 * Chat roster organized as a tree of ContactGroup and ContactEntry items
 * @author Andrew Bowley
 * 20 May 2016
 */
public interface ContactsTree
{

    /**
     * Load roster organised as a collection of ContactEntry lists, 
     * each list belonging to a group identified by name
     * @param groupCollections Initial roster at time connection is established as list of groups
     */
    void loadRoster(List<GroupCollection> groupCollections);

    /**
     * Returns root of tree
     * @return ContactGroup object
     */
    ContactGroup getRootContactGroup();

    /**
     * Add new contact
     * @param rosterEntryData Contact details from remote roster
     * @param rosterGroups Group membership, which may include new groups
     */
    void addUser(RosterEntryData rosterEntryData,
            Collection<String> rosterGroups);

    /**
     * Update contact
     * @param rosterEntryData Contact details from remote roster
     * @param rosterGroups Group membership, which may include new groups
     * @return flag set true if contact found in tree to update
     */
    boolean updateUser(RosterEntryData rosterEntryData,
            Collection<String> rosterGroups);

    /**
     * Delete contact
     * @param user User JID to identify contact to delete
     * returns flag set false if no user found to delete
     */
    boolean deleteUser(String user);

    /**
     * Sync local contact with remote roster
     * @param user User JID to identify contact to sync
     * @param roster Agent for remote roster
     * returns flag set false if no user found to sync
     */
    boolean syncUser(String user, RosterHelper roster);

    /**
     * Returns Contact Entry List of specified user. The list contains a distinct entry for each group to which the user belongs.
     * @param user JID
     * @return ContactEntry object
     */
    ContactEntryList getContactEntryList(String user);

    /**
     * Returns user contact item in the roster. Note - there may be items for other groups.
     * @param name User JID possibly decorated with resource. Also case-insensitive lookup used.
     * @return ContactEntry object
     */
    ContactEntry getContactEntryByName(String name);

    /**
     * Clear existing tree contents
     */
    void clear();

    /**
     * Returns group membership for contact identifeid by user JID
     * @param user User JID
     * @return List of group names
     */
    List<String> getGroupMembership(String user);

    /**
     * Return container which maps user JID to contact entry list
     * @return contact entry map 
     */
    Map<String, ContactEntryList> getContactMap();

}