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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jxmpp.util.XmppStringUtils;

import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactGroup;
import au.com.cybersearch2.cybertete.model.RosterEntryData;

/**
 * ContactMap
 * @author Andrew Bowley
 * 5 May 2016
 */
public class ContactMap
{
    /** 
     * A Smack contact may belong to more than one group so the user JID is mapped to a contact entry list
     * with each item belonging to a different group 
     */
    private Map<String, ContactEntryList> contactMap;
    
    /**
     * 
     */
    public ContactMap()
    {
        contactMap = Collections.emptyMap();
    }

    /**
     * @see au.com.cybersearch2.cybertete.model.ContactsTree#getContactMap()
     */
    protected Map<String, ContactEntryList> getContactMap()
    {
        return Collections.unmodifiableMap(contactMap);
    }

    protected ContactEntryList get(String user)
    {
        return contactMap.get(user);
    }

    protected ContactEntry getContactEntryByName(String name)
    {
        String bareJid = XmppStringUtils.parseBareJid(name);
        // Return head of contact item chain
        for (Map.Entry<String, ContactEntryList> entry: contactMap.entrySet())
        {
            if (entry.getKey().equalsIgnoreCase(bareJid))
                return entry.getValue().getHead();
        }
        return null;
    }

    protected void put(ContactGroup rootContactGroup, Collection<String> rosterGroups, RosterEntryData rosterEntryData)
    {
        contactMap.put(rosterEntryData.getUser(), new ContactEntryList(rootContactGroup, rosterGroups, rosterEntryData));
    }

    protected void put(ContactGroup contactGroup, List<ContactEntry> contactList)
    {
        if (!contactList.isEmpty())
        {
            contactGroup.setItems(contactList);
            Iterator<ContactEntry> iterator = contactList.iterator();
            while (iterator.hasNext())
                put(iterator.next());
        }
    }
 
    /**
     * Add new contact allowing for multi-group membership
     * @param contactEntry Contact entry to add
     * @return flag set true if new user
     */
    protected boolean put(ContactEntry contactEntry)
    {   // Chain them when a user belongs to more than one group 
        String user = contactEntry.getUser();
        ContactEntryList contactEntryList = null; 
        if (contactMap.isEmpty())
            contactMap = new ConcurrentHashMap<String,ContactEntryList>();
        else
            contactEntryList = contactMap.get(user);
        if (contactEntryList == null)
            contactMap.put(user, new ContactEntryList(contactEntry));
        else
            contactEntryList.addContact(contactEntry);
        return (contactEntryList == null);
    }
    
    protected void clearMap()
    {
        for (ContactEntryList contactEntryList: contactMap.values())
        {
            contactEntryList.clear();
        }
        contactMap.clear();
    }
}
