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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;

import au.com.cybersearch2.cybertete.agents.LocalRoster;
import au.com.cybersearch2.cybertete.agents.RosterHelper;
import au.com.cybersearch2.cybertete.model.ConnectionError;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactsTree;
import au.com.cybersearch2.cybertete.model.GroupCollection;
import au.com.cybersearch2.cybertete.model.Presence;
import au.com.cybersearch2.cybertete.model.RosterEntryData;
import au.com.cybersearch2.cybertete.service.XmppConnectionException;

/**
 * SmackRoster
 * Loads roster and implements RosterHelper interface required by LocalRoster
 * @see au.com.cybersearch2.cybertete.agents.LocalRoster
 * @author Andrew Bowley
 * 24 Feb 2016
 */
public class SmackRoster implements RosterHelper
{
    /** Maximum time in milli seconds to wait for roster to complete loading */
    public static final long MAX_WAIT_MILLIS = 30000;
    /** Empty list of group names */
    private static List<String> EMPTY_LIST;
    
    /** Roster object of Smack library */
    private Roster roster;
    /** Chat server host name */
    private String hostName;
    /** Chat server port */
    private int port;

    static
    {
        EMPTY_LIST = Collections.emptyList();
    }

    /**
     * Create SmackRoster object
     * @param roster Roster object of Smack library
     * @param hostName Chat server host name
     * @param port Chat server port
     */
    public SmackRoster(Roster roster, String hostName, int port)
    {
        this.roster = roster;
        this.hostName = hostName;
        this.port = port;
    }

    /**
     * @return the hostName
     */
    public String getHostName()
    {
        return hostName;
    }

    /**
     * @return the port
     */
    public int getPort()
    {
        return port;
    }

    /**
     * Add roster listener for local roster to monitor changes to the remote roster located at the Chat server
     * @param localRoster Contains model roster which is kept in sync with roster configuration on Chat server
     */
    public void registerLocalRoster(LocalRoster localRoster)
    {
         roster.addRosterListener(new SmackRosterListener(localRoster));
    }

    /**
     * Load supplied contacts tree with contents of remote roster
     * @see au.com.cybersearch2.cybertete.agents.RosterHelper#loadContactsTree(au.com.cybersearch2.cybertete.model.ContactsTree)
     */
    @Override
    public void loadContactsTree(ContactsTree contactsTree)
    {
        // Load roster possibly blocks thread waiting for completion
        List<GroupCollection> groupCollections = load();
        // Initialize model roster, set super groups and items
        contactsTree.loadRoster(groupCollections);
    }

    /**
     * Sychronize contact entry with corresponding remote contact. Applies only to change of presence.
     * @see au.com.cybersearch2.cybertete.agents.RosterHelper#sync(au.com.cybersearch2.cybertete.model.ContactEntry)
     */
    @Override
    public void sync(ContactEntry contactEntry)
    {
         org.jivesoftware.smack.packet.Presence presence = 
            roster.getPresence(contactEntry.getUser());
        if ((presence.getType() != Type.available) || (presence.getError() != null))
        {
            contactEntry.setPresence(Presence.offline);
            return;
        }
        switch (presence.getMode())
        {
        case available: contactEntry.setPresence(Presence.online); break;
        case chat:      contactEntry.setPresence(Presence.online); break;
        case away:      contactEntry.setPresence(Presence.away); break;
        case xa:        contactEntry.setPresence(Presence.away); break;
        case dnd:       contactEntry.setPresence(Presence.dnd); break;
        default:
                         contactEntry.setPresence(Presence.offline);
        }
    }

    /**
     * Returns list of group names to which specified user belongs
     * @see au.com.cybersearch2.cybertete.agents.RosterHelper#getGroupList(java.lang.String)
     */
    @Override
    public Collection<String> getGroupList(String user)
    {
        Collection<RosterGroup> rosterGroups = getRosterEntryGroups(user);
        int groupListSize = rosterGroups == null ? 0 : rosterGroups.size();
        List<String> groupList = (groupListSize == 0) ? EMPTY_LIST : new ArrayList<String>(groupListSize);
        if (groupListSize > 0)
        {
            Iterator<RosterGroup> iterator = rosterGroups.iterator();
            while (iterator.hasNext())
                groupList.add(iterator.next().getName());
        }
        return groupList;
    }

    /**
     * Add new contact to remote roster
     * @see au.com.cybersearch2.cybertete.agents.RosterHelper#createEntry(au.com.cybersearch2.cybertete.model.ContactEntry, java.util.Collection)
     */
    @Override
    public void createEntry(ContactEntry entry, Collection<String> groups)
    {
        String groupName = entry.getParent().getName();
        int groupSize = groups.size();
        // Check if the entry parent group is included in the groups collection and adjust accordingly
        boolean isContained = groups.contains(groupName);
        if (!isContained)
            ++groupSize;
        String[] groupsArray = new String[groupSize];
        if (!groups.isEmpty())
            groups.toArray(groupsArray);
        if (!isContained)
            groupsArray[groupSize - 1] = groupName;
        try 
        {
            // Creates a new roster entry and presence subscription. The server will asynchronously
            // update the roster with the subscription status.
            roster.createEntry(entry.getUser(), entry.getName(), groupsArray);
        } 
        catch (SmackException | XMPPErrorException e)
        {
            ConnectionError connectionError = ConnectionError.classifyException(e);
            throw new XmppConnectionException("Error adding contact " + entry.getUser(), e, connectionError, getHostName(), getPort());
        }
    }
    
    /**
     * Returns remote roster entry data used by local roster
     * @see au.com.cybersearch2.cybertete.agents.RosterHelper#getRosterEntry(java.lang.String)
     */
    @Override
    public RosterEntryData getRosterEntry(String user)
    {
        RosterEntry rosterEntry = roster.getEntry(user);
        if (rosterEntry == null)
            return null;
        return createSmackRosterEntry(rosterEntry.getName(), rosterEntry.getUser());
    }

    /**
     * Returns remote roster entry data used by local roster 
     * @param rosterEntry Smack roster entry
     * @return RosterEntryData object
     */
    RosterEntryData createSmackRosterEntry(final String name, final String user)
    {
        return new RosterEntryData(){

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
        }; 
    }

    /**
     * Returns groups in roster entry specified by user
     * @param user User JID
     * @return GroupCollection collection
     */
    protected Collection<RosterGroup> getRosterEntryGroups(String user)
    {   // RosterEntry is not mockable, hence this method to allow override 
        RosterEntry rosterEntry = roster.getEntry(user);
        return rosterEntry == null ? null : rosterEntry.getGroups();
    }
 
    /**
     * Returns roster as a set of ContactEntry lists. May block thread waiting for roster load to complete.
     * @return List of GroupCollection objects
     */
    private List<GroupCollection> load()
    {
        if (!roster.isLoaded())
            waitForRosterLoad(roster);
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
        return new SmackRosterEntrySet(roster).getRosterGroups();
    }


    /**
     * Block thread waiting for roster to complete loading
     * @param roster
     */
    private static void waitForRosterLoad(Roster roster)
    {
        synchronized(roster)
        {
            try
            {
                roster.wait(MAX_WAIT_MILLIS);
            }
            catch (InterruptedException e)
            {
            }
        }
    }

}
