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
package au.com.cybersearch2.cybertete.model;

import java.util.List;

/**
 * GroupCollection
 * Contains list of entries belonging to same group
 * @author Andrew Bowley
 * 6 May 2016
 */
public class GroupCollection
{

    ContactGroup group;
    List<ContactEntry> entries;
    
    /**
     * 
     */
    public GroupCollection(ContactGroup group, List<ContactEntry> entries)
    {
        this.group = group;
        this.entries = entries;
    }
    /**
     * @return the group
     */
    public ContactGroup getGroup()
    {
        return group;
    }
    /**
     * @return the entry collection
     */
    public List<ContactEntry> getItems()
    {
        return entries;
    }

}
