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

import java.util.Collection;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jxmpp.util.XmppStringUtils;

import au.com.cybersearch2.cybertete.agents.LocalRoster;
import au.com.cybersearch2.cybertete.model.RosterEntryData;

/**
 * SmackRosterListener
 * Implements Smack RosterListener to keep local roster in sync with remote one on Chat server
 * @author Andrew Bowley
 * 12 Apr 2016
 */
public class SmackRosterListener implements RosterListener
{
    /** Local roster containing all contact details of currently logged in user */
    LocalRoster localRoster;

    /**
     * Create SmackRosterListener
     * @param localRoster Local roster to be updated by events handled by this listener
     */
    public SmackRosterListener(LocalRoster localRoster)
    {
        this.localRoster = localRoster;
    }
    
    /**
     * Called when roster entries are added.
     * @param addresses The XMPP addresses of the contacts that have been added to the roster
     */
    @Override
    public void entriesAdded(Collection<String> addresses)
    {
        for (String user: addresses)
        {
            RosterEntryData rosterEntry = localRoster.getRosterEntryData(user);
            if (rosterEntry != null)
                localRoster.add(rosterEntry);
        }
    }

    /**
     * Called when a roster entries are updated.
     * @param addresses The XMPP addresses of the contacts whose entries have been updated.
     */
    @Override
    public void entriesUpdated(Collection<String> addresses)
    {
        // Check for removed from existing group, added to new group, or name changed
        for (String user: addresses)
        {
            RosterEntryData rosterEntry = localRoster.getRosterEntryData(user);
            if (rosterEntry != null)
                localRoster.update( rosterEntry);
        }
    }

    /**
     * Called when a roster entries are removed.
     * @param addresses The XMPP addresses of the contacts that have been removed from the roster.
     */
    @Override
    public void entriesDeleted(Collection<String> addresses)
    {
        for (String user: addresses)
            localRoster.deleteUser(user);
    }

    /**
     * Called when the presence of a roster entry is changed. 
     *
     * Note that this listener is triggered for presence (mode) changes only
     * (e.g presence of types available and unavailable. Subscription-related
     * presence packets will not cause this method to be called.
     *
     * @param presence the presence that changed.
     * @see Roster#getPresence(String)
    */
    @Override
    public void presenceChanged(Presence presence)
    {
        String user = XmppStringUtils.parseBareJid(presence.getFrom());
        localRoster.syncUser(user);
    }

}
