/**
    Copyright (C) 2015  www.cybersearch2.com.au

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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.swt.widgets.Composite;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.ImageFactory;
import au.com.cybersearch2.cybertete.model.ApplicationState;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactsTree;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;

/**
 * ContactsView
 * Displays contacts in a tree view with custom label decoration.
 * @author Andrew Bowley
 * 22 Oct 2015
 */
public class ContactsView
{
    /** The ContactsView window ID declared in the application model */
    public static final String ID = "au.com.cybersearch2.cybertete.part.contacts";
 
    /** Adapter for the SWT <code>Tree</code> control to operate as a contacts tree */
    @Inject
    TreeViewerControl contactsViewer;
    /** Chat roster organized as a tree of ContactGroup and ContactEntry items */
    @Inject
    ContactsTree contactsTree;
    
    /**
     * Post construct
     * @param parent Parent composite
     * @param controlFactory SWT widget factory
     * @param imageFactory Image factory with resource manager
     * @param menuService Provides menu management
     */
    @PostConstruct
    public void postConstruct(
        Composite parent,
        ControlFactory controlFactory,
        ImageFactory imageFactory,
        EMenuService menuService,
        ContactsContentProviderFactory contentProviderFactory) 
    {
        menuService.registerContextMenu(contactsViewer.getControl(), "contacts.popup");
        ContactsContentProvider contentProvider = contentProviderFactory.instance();
        contactsViewer.setProviders(contentProvider,
                                    new ContactsLabelProvider(contentProvider, imageFactory));
        contactsViewer.setInput(contactsTree.getRootContactGroup());
        controlFactory.getDefaultLayout().generateLayout(parent);
    }

    /**
     * Set focus on current selection
     */
    @Focus
    void setFocus() 
    {
        contactsViewer.setFocus();
    }

    /**
     * Set selection to given contact entry which must be contained in the contacts tree
     * @param contactEntry Contact entry
     */
    public void setSelection(ContactEntry contactEntry)
    {
        // TODO - Handle nested contacts tree
        // The selection path is specified using a segment arrary
        Object[] segments = new Object[2];
        segments[0] = contactEntry.getParent();
        segments[1] = contactEntry;
        contactsViewer.setSelection(segments, contactEntry); 
    }

    /**
     * Handler for logout. Close all Chat Views 
     * @param nextState 
     */
    @Inject @Optional
    void onLogoutHandler(@UIEventTopic(CyberteteEvents.LOGOUT) ApplicationState nextState)
    {
        contactsTree.clear();
        contactsViewer.setInput(contactsTree.getRootContactGroup());
    }

}
