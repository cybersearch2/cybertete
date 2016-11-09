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
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.swt.graphics.Image;

import au.com.cybersearch2.controls.ImageFactory;
import au.com.cybersearch2.cybertete.model.ContactItem;
import au.com.cybersearch2.cybertete.model.Presence;

/**
 * ContactsLabelProvider
 * Label provider for contacts tree. Deals with ContactGroup and ContactEntry items.
 * @author Andrew Bowley
 * 4 May 2016
 */
public class ContactsLabelProvider extends ObservableMapLabelProvider
{
    /** Image factory with resource manager */
    ImageFactory imageFactory;
    
    /**
     * @param contentProvider Content provider for contacts tree
     * @param imageFactory Image factory with resource manager
     */
    public ContactsLabelProvider(ContactsContentProvider contentProvider, ImageFactory imageFactory)
    {
        super(getAttributeMaps(contentProvider));
        this.imageFactory = imageFactory;
    }

    /**
     * Constructor used for tests
     * @param attributeMaps Attribute mapping for fields: name, user, presence
     * @param imageFactory Image factory with resource manager
     */
    ContactsLabelProvider(@SuppressWarnings("rawtypes") IObservableMap[] attributeMaps, ImageFactory imageFactory)
    {
        super(attributeMaps);
        this.imageFactory = imageFactory;
    }
    
    /**
     * getText
     * @see org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText(Object element) 
    {
        ContactItem item = (ContactItem)element;
        String user = getUser(element);
        if (user != null)
            return item.getName() + " (" + user + ")";
        return item.getName();
    }

    /**
     * getImage
     * @see org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage(Object element) 
    {
        Presence presence = getPresence(element);
        if (presence != null)
            return imageFactory.getMappedImage(presence);
        return imageFactory.getImage("icons/groups.gif");
    }

    /**
     * Returns user from Contact item if it is a contact entry
     * @param element Contact item
     * @return User JID or null if item not a contact entry
     */
    String getUser(Object element)
    {
        return (String)attributeMaps[0].get(element);
    }

    /**
     * Returns presence from Contact item if it is a contact entry
     * @param element Contact item
     * @return presence or null if item not a contact entry
     */
    Presence getPresence(Object element)
    {
        return (Presence)attributeMaps[1].get(element);
    }

    /**
     * Returns attribute maps used to extract  user and presence fields from contact entries
     * @param contentProvider ObservableListTreeContentProvider object
     * @return IObservableMap arrary
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static IObservableMap[] getAttributeMaps(ObservableListTreeContentProvider contentProvider)
    {
        IObservableSet knownElements = contentProvider.getKnownElements();
        // Use name attribute to set label text
        IObservableMap[] attributeMaps =
        {
            BeanProperties.value("user").observeDetail(knownElements),
            BeanProperties.value("presence").observeDetail(knownElements)
        };
        return attributeMaps;
    }
}
