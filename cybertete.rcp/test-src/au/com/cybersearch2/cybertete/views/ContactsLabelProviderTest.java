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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.list.MultiListProperty;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.junit.Test;

import au.com.cybersearch2.controls.ImageFactory;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactGroup;
import au.com.cybersearch2.cybertete.model.ContactItem;
import au.com.cybersearch2.cybertete.model.Presence;

/**
 * ContactsLabelProviderTest
 * @author Andrew Bowley
 * 4 May 2016
 */
public class ContactsLabelProviderTest
{
    static final String TEST_JID = "mickymouse@disney.com";

    @Test
    public void test_getText_entry()
    {
        ContactEntry contactEntry = mock(ContactEntry.class);
        when(contactEntry.getName()).thenReturn("micky");
        IObservableMap userMap = mock(IObservableMap.class);
        when(userMap.get(contactEntry)).thenReturn(TEST_JID);
        IObservableMap presenceMap = mock(IObservableMap.class);
        IObservableMap[] attributeMaps = new IObservableMap[] { userMap, presenceMap };
        ImageFactory imageFactory = mock(ImageFactory.class);
        ContactsLabelProvider underTest = new ContactsLabelProvider(attributeMaps, imageFactory);
        assertThat(underTest.getText(contactEntry)).isEqualTo("micky (" + TEST_JID + ")");
    }

    @Test
    public void test_getText_group()
    {
        ContactGroup contactGroup = mock(ContactGroup.class);
        when(contactGroup.getName()).thenReturn("Colleagues");
        IObservableMap userMap = mock(IObservableMap.class);
        IObservableMap presenceMap = mock(IObservableMap.class);
        IObservableMap[] attributeMaps = new IObservableMap[] { userMap, presenceMap };
        ImageFactory imageFactory = mock(ImageFactory.class);
        ContactsLabelProvider underTest = new ContactsLabelProvider(attributeMaps, imageFactory);
        assertThat(underTest.getText(contactGroup)).isEqualTo("Colleagues");
    }
    
    @Test
    public void test_getImage_entry()
    {
        ContactEntry contactEntry = mock(ContactEntry.class);
        IObservableMap userMap = mock(IObservableMap.class);
        IObservableMap presenceMap = mock(IObservableMap.class);
        when(presenceMap.get(contactEntry)).thenReturn(Presence.online);
        IObservableMap[] attributeMaps = new IObservableMap[] { userMap, presenceMap };
        ImageFactory imageFactory = mock(ImageFactory.class);
        Display display = mock(Display.class);
        Image online = new Image(display, "icons/online.gif");
        when(imageFactory.getMappedImage(Presence.online)).thenReturn(online);
        ContactsLabelProvider underTest = new ContactsLabelProvider(attributeMaps, imageFactory);
        assertThat(underTest.getImage(contactEntry)).isEqualTo(online);
    }

    @Test
    public void test_getImage_group()
    {
        ContactGroup contactGroup = mock(ContactGroup.class);
        IObservableMap userMap = mock(IObservableMap.class);
        IObservableMap presenceMap = mock(IObservableMap.class);
        IObservableMap[] attributeMaps = new IObservableMap[] { userMap, presenceMap };
        ImageFactory imageFactory = mock(ImageFactory.class);
        Display display = mock(Display.class);
        Image groups = new Image(display, "icons/groups.gif");
        when(imageFactory.getImage("icons/groups.gif")).thenReturn(groups);
        ContactsLabelProvider underTest = new ContactsLabelProvider(attributeMaps, imageFactory);
        assertThat(underTest.getImage(contactGroup)).isEqualTo(groups);
    }
    
    @Test
    public void test_getAttributeMaps()
    {
        IListProperty childrenProperty = 
                new MultiListProperty(
                    new IListProperty[] 
                    { 
                        BeanProperties.list(ContactItem.GROUP_LIST_NAME),
                        BeanProperties.list(ContactItem.ENTRY_LIST_NAME) 
                    });

        ObservableListTreeContentProvider contentProvider = 
                new ObservableListTreeContentProvider(childrenProperty.listFactory(), null);
         assertThat(ContactsLabelProvider.getAttributeMaps(contentProvider)).hasSize(2);
    }
}
