package au.com.cybersearch2.cybertete.agents;

import java.util.Collection;

import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactsTree;
import au.com.cybersearch2.cybertete.model.RosterEntryData;

/**
 * RosterHelper
 * Interface to access the XMPP roster implementation
 * @author Andrew Bowley
 * 16 Apr 2016
 */
public interface RosterHelper
{
    /**
     * Sychronize contact entry with corresponding remote contact. Applies only to change of presence.
     * @param contactEntry Contact entry in local roster
     */
    void sync(ContactEntry contactEntry);

    /**
     * Returns list of group names to which specified user belongs
     * @param user User JID
     * @return List of names or empty list if user not found
     */
    Collection<String> getGroupList(String user);

    /**
     * Add new contact to remote roster
     * @param entry Contact entry to add. Parent group is expected to be excluded from groups of the next parameter.
     * @param groups List of groups to which user currently belongs
     */
    void createEntry(ContactEntry entry, Collection<String> groups);

    /**
     * Returns remote roster entry data required by local roster
     * @param user User JID
     * @return RosterEntryData object
     */
    RosterEntryData getRosterEntry(String user);

    /**
     * Load supplied contacts tree with contents of remote roster 
     * @param contactsTree The contacts tree to load
     */
    void loadContactsTree(ContactsTree contactsTree);
}