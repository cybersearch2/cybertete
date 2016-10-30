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

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;

import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactGroup;
import au.com.cybersearch2.cybertete.model.ContactItem;
import au.com.cybersearch2.cybertete.model.GroupCollection;

/**
 * SmackRosterEntrySet
 * @author Andrew Bowley
 * 2 Apr 2016
 */
public class SmackRosterEntrySet
{
    List<GroupCollection> rosterGroups;

    /**
     * 
     */
    public SmackRosterEntrySet(Roster roster)
    {
        rosterGroups = new ArrayList<GroupCollection>();
        for (RosterGroup rosterGroup: roster.getGroups())
        {
            ContactGroup contactGroup = new ContactGroup(rosterGroup.getName());
            rosterGroups.add(new GroupCollection(contactGroup, getContactEntries(rosterGroup, contactGroup)));
        }
        ContactGroup detachedContactGroup = new ContactGroup(ContactItem.UNGROUPED_NAME);
        List<ContactEntry> ungrouped = getContactEntries(roster, detachedContactGroup);
        if (!ungrouped.isEmpty())
            rosterGroups.add(new GroupCollection(detachedContactGroup, ungrouped));
    }

    public List<GroupCollection> getRosterGroups()
    {
        return Collections.unmodifiableList(rosterGroups);
    }

    private List<ContactEntry> getContactEntries(RosterGroup rosterGroup, ContactGroup parent)
    {
        return getContactEntries(rosterGroup.getEntries(), parent);
    }
    
    private List<ContactEntry> getContactEntries(Roster roster, ContactGroup parent)
    {
        return getContactEntries(roster.getUnfiledEntries(), parent);
    }
    
    private List<ContactEntry> getContactEntries(Collection<RosterEntry> rosterEntries, ContactGroup parent)
    {
        Iterator<RosterEntry> rosterItr = rosterEntries.iterator();
        List<ContactEntry> rosterEntryList = new ArrayList<ContactEntry>();
        while (rosterItr.hasNext())
        {
            RosterEntry rosterEntry = rosterItr.next();
            rosterEntryList.add(new ContactEntry(rosterEntry.getName(), rosterEntry.getUser(), parent)); 
        }
        return rosterEntryList;
    }

}
