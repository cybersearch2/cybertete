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
package au.com.cybersearch2.cybertete.service;

import javax.annotation.PostConstruct;
import org.jxmpp.util.XmppStringUtils;

import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactGroup;
import au.com.cybersearch2.cybertete.model.ContactItem;
import au.com.cybersearch2.cybertete.preferences.UserDataStore;

import org.eclipse.e4.core.di.annotations.Creatable;

/**
 * SessionOwner
 * Contains contact entry of logged in user
 * @author Andrew Bowley
 * 9 Apr 2016
 */
@Creatable
public class SessionOwner 
{
    /** Contact entry of logged in user */
    ContactEntry contact;
 
    /**
     * postConstruct
     * @param userDataStore Login details backed by file system 
     */
    @PostConstruct
    void postConstruct(UserDataStore userDataStore)
    {
        // Intialize contact entry from last user in storage
        String lastUser = userDataStore.getLastUser();
        String lastName = XmppStringUtils.parseLocalpart(lastUser);
        ContactGroup defaultContactGroup = new ContactGroup(ContactItem.UNGROUPED_NAME);
        contact = new ContactEntry(lastName, lastUser, defaultContactGroup);
    }

    /**
     * Returns contact entry
     * @return ContactEntry object
     */
    public ContactEntry getContact()
    {
        return contact;
    }
    
    /**
     * Update name and user from login
     * @param name
     * @param user
     */
    public void update(String name, String user)
    {
        contact.setName(name);
        contact.setUser(user);
    }

    /**
     * Update contact from roster
     * @param contact Contact entry
     */
    public void setContact(ContactEntry contact)
    {
        this.contact = contact;
    }
}
