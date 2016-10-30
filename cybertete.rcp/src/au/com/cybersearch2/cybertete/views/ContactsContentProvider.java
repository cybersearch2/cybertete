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
package au.com.cybersearch2.cybertete.views;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.list.MultiListProperty;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;

import au.com.cybersearch2.cybertete.model.ContactItem;

/**
 * ContactsContentProvider
 * Content provider for contacts tree. Deals with ContactGroup and ContactEntry items.
 * @author Andrew Bowley
 * 20 May 2016
 */
public class ContactsContentProvider extends ObservableListTreeContentProvider
{
    static IListProperty childrenProperty;
    
    static
    {
        /**
         * Names used to fire property change events of collections
         * @see au.com.cybersearch2.cybertete.model.ContactGroup
         */
        childrenProperty = 
            new MultiListProperty(
                new IListProperty[] 
                { 
                    BeanProperties.list(ContactItem.GROUP_LIST_NAME),
                    BeanProperties.list(ContactItem.ENTRY_LIST_NAME) 
                });

    }
    
    /**
     * Create ContactsContentProvider object
     */
    public ContactsContentProvider()
    {   // IObservableFactory only - no StructureAdvisor
        super(childrenProperty.listFactory(), null);
    }

}
