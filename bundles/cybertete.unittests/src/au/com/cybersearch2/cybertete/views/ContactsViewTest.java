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
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.DefaultLayout;
import au.com.cybersearch2.controls.ImageFactory;
import au.com.cybersearch2.cybertete.model.ApplicationState;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactGroup;
import au.com.cybersearch2.cybertete.model.ContactsTree;
import au.com.cybersearch2.cybertete.model.internal.MultiGroupContactsTree;

/**
 * ContactsViewTest
 * @author Andrew Bowley
 * 4 May 2016
 */
public class ContactsViewTest
{
    @Test
    public void test_postConstruct()
    {
        final ContactGroup root = mock(ContactGroup.class);
        ContactsView underTest = new ContactsView();
        ContactsTree contactsTree = mock(MultiGroupContactsTree.class);
        when(contactsTree.getRootContactGroup()).thenReturn(root);
        underTest.contactsTree = contactsTree;
        Composite parent = mock(Composite.class);
        ControlFactory controlFactory = mock(ControlFactory.class);
        TreeViewerControl contactsViewer = mock(TreeViewerControl.class);
        Control control = mock(Control.class);
        when(contactsViewer.getControl()).thenReturn(control);
        underTest.contactsViewer = contactsViewer;
        DefaultLayout defaultLayout = mock(DefaultLayout.class);
        when(controlFactory.getDefaultLayout()).thenReturn(defaultLayout);
        ImageFactory imageFactory = mock(ImageFactory.class);
        EMenuService menuService = mock(EMenuService.class);
         underTest.postConstruct(parent, controlFactory, imageFactory, menuService);
        verify(menuService).registerContextMenu(control, "contacts.popup");
        verify(contactsViewer).setProviders(isA(ContactsContentProvider.class),isA(ContactsLabelProvider.class));
        verify(defaultLayout).generateLayout(parent);
        verify(contactsViewer).setInput(root);
    }
    
    @Test
    public void test_setFocus()
    {
        ContactsView underTest = new ContactsView();
        TreeViewerControl contactsViewer = mock(TreeViewerControl.class);
        underTest.contactsViewer = contactsViewer;
        underTest.setFocus();
        verify(contactsViewer).setFocus();
    }
    
    @Test
    public void test_setSelection()
    {
        ContactEntry contactEntry = mock(ContactEntry.class);
        ContactGroup parent = mock(ContactGroup.class);
        when(contactEntry.getParent()).thenReturn(parent);
        ContactsView underTest = new ContactsView();
        TreeViewerControl contactsViewer = mock(TreeViewerControl.class);
        underTest.contactsViewer = contactsViewer;
        underTest.setSelection(contactEntry);
        ArgumentCaptor<Object[]> segmentsCaptor = ArgumentCaptor.forClass(Object[].class);
        verify(contactsViewer).setSelection(segmentsCaptor.capture(), eq(contactEntry)); 
        assertThat(segmentsCaptor.getValue()[0]).isEqualTo(parent);
        assertThat(segmentsCaptor.getValue()[1]).isEqualTo(contactEntry);
    }

    @Test
    public void test_onLogoutHandler()
    {
        ContactsView underTest = new ContactsView();
        TreeViewerControl contactsViewer = mock(TreeViewerControl.class);
        underTest.contactsViewer = contactsViewer;
        ContactsTree contactsTree = mock(MultiGroupContactsTree.class);
        ContactGroup root = mock(ContactGroup.class);
        when(contactsTree.getRootContactGroup()).thenReturn(root);
        underTest.contactsTree = contactsTree;
        underTest.onLogoutHandler(ApplicationState.login);
        verify(contactsTree).clear();
        verify(contactsViewer).setInput(root);
    }
    
}
