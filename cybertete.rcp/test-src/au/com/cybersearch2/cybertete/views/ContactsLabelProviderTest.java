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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.swt.graphics.Image;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import au.com.cybersearch2.controls.ImageFactory;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactGroup;
import au.com.cybersearch2.cybertete.model.Presence;

/**
 * ContactsLabelProviderTest
 * @author Andrew Bowley
 * 4 May 2016
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Image.class})
@PowerMockIgnore(
{
    "org.eclipse.swt.graphics.Drawable", 
    "org.eclipse.swt.graphics.Color", 
    "org.eclipse.swt.graphics.GC",
    "org.eclipse.swt.graphics.GCData",
    "org.eclipse.swt.graphics.Device", 
    "org.eclipse.swt.graphics.Font", 
    "org.eclipse.swt.graphics.ImageData", 
    "org.eclipse.swt.graphics.Rectangle", 
    "org.eclipse.swt.graphics.Region", 
    "org.eclipse.swt.graphics.ImageData",
    "org.eclipse.swt.graphics.ImageDataProvider",
    "org.eclipse.swt.graphics.ImageFileNameProvider",
    "org.eclipse.swt.graphics.Cursor", 
    "org.eclipse.swt.graphics.TextLayout", 
    "org.eclipse.swt.graphics.Point", 
    "org.eclipse.swt.graphics.RGB", 
    "org.eclipse.swt.graphics.RGBA", 
    "org.eclipse.swt.graphics.Font", 
    "org.eclipse.swt.graphics.FontMetrics"
})
public class ContactsLabelProviderTest
{
    static final String TEST_JID = "mickymouse@disney.com";

    @SuppressWarnings("rawtypes")
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

    @SuppressWarnings("rawtypes")
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
    
    @SuppressWarnings("rawtypes")
    @Test
    public void test_getImage_entry()
    {
        ContactEntry contactEntry = mock(ContactEntry.class);
        IObservableMap userMap = mock(IObservableMap.class);
        IObservableMap presenceMap = mock(IObservableMap.class);
        when(presenceMap.get(contactEntry)).thenReturn(Presence.online);
        IObservableMap[] attributeMaps = new IObservableMap[] { userMap, presenceMap };
        ImageFactory imageFactory = mock(ImageFactory.class);
        Image online = PowerMockito.mock(Image.class);
        when(imageFactory.getMappedImage(Presence.online)).thenReturn(online);
        ContactsLabelProvider underTest = new ContactsLabelProvider(attributeMaps, imageFactory);
        assertThat(underTest.getImage(contactEntry)).isEqualTo(online);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void test_getImage_group()
    {
        ContactGroup contactGroup = mock(ContactGroup.class);
        IObservableMap userMap = mock(IObservableMap.class);
        IObservableMap presenceMap = mock(IObservableMap.class);
        IObservableMap[] attributeMaps = new IObservableMap[] { userMap, presenceMap };
        ImageFactory imageFactory = mock(ImageFactory.class);
        Image groups = PowerMockito.mock(Image.class);
        when(imageFactory.getImage("icons/groups.gif")).thenReturn(groups);
        ContactsLabelProvider underTest = new ContactsLabelProvider(attributeMaps, imageFactory);
        assertThat(underTest.getImage(contactGroup)).isEqualTo(groups);
    }
    
}
