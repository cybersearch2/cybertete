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
// Copyright from original StatusLine class
/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <Lars.Vogel@vogella.com> - Bug 440270
 *******************************************************************************/
package au.com.cybersearch2.cybertete.status;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;

import au.com.cybersearch2.controls.CustomDialog;
import au.com.cybersearch2.controls.CustomLabelSpec;
import au.com.cybersearch2.controls.ImageFactory;
import au.com.cybersearch2.statusbar.controls.ItemConfiguration;
import au.com.cybersearch2.cybertete.dialogs.DialogFactory;
import au.com.cybersearch2.cybertete.dialogs.PresenceControls;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.Presence;
import au.com.cybersearch2.statusbar.LabelListener;
import au.com.cybersearch2.statusbar.StatusItem;

/**
 * PresenceStatus
 * A PresenceStatus control is a SWT Composite with a horizontal layout which hosts
 * a number of status indication controls. Typically it is situated below the
 * content area of the window.
 * <p>
 * @author Andrew Bowley
 * 17 Nov 2015
 */
@Creatable
public class PresenceStatus
{
    /** Width in characters - must accommodate "Offline" and presence icon. */
    public static int CHAR_WIDTH = 18;
    
    /** Configures and controls status line item */
    StatusItem statusItem;
    /** Image loader and cache */
    ImageFactory imageFactory;
    
    /** Event broker service */
    @Inject
    IEventBroker eventBroker;
    /** Creates instances of application-specific dialogs */
    @Inject
    DialogFactory dialogFactory;

    /** Mouse click listener to pop up presence dialog */
    MouseListener mouseListener = new MouseListener(){

        @Override
        public void mouseDoubleClick(MouseEvent e)
        {
        }

        @Override
        public void mouseDown(MouseEvent e)
        {
        }

        @Override
        public void mouseUp(MouseEvent e)
        {
            if (e.button == 3)
                displayPresenceDialog();
        }};

    /** Custom Label creation action adds mouse listener to item */
    LabelListener labelListener = new LabelListener(){

        @Override
        public void onLabelCreate(CLabel label)
        {
            label.addMouseListener(mouseListener);
        }};

    /**
     * Construct PresenceStatus object.
     * Set initial image to indicate establish communications (yellow)
     * @param imageFactory Image loader and cache
     */
    @Inject 
    public PresenceStatus(ImageFactory imageFactory)
    {
        this.imageFactory = imageFactory;
        CustomLabelSpec specification = new ItemConfiguration( 
                imageFactory.getMappedImage(Presence.offline), 
                Presence.offline.getDisplayText(), 
                CHAR_WIDTH);
        statusItem = new StatusItem(specification, CyberteteStatusBar.PRESENCE_ID);
    }

    /**
     * postConstruct
     */
    @PostConstruct
    void postConstruct()
    {
        statusItem.setLabelListener(labelListener);
    }
    
    /**
     * Handler for change of presence
     * @param presence Value to set
     */
    @Inject @Optional
    void presenceHandler(@UIEventTopic(CyberteteEvents.PRESENCE) Presence presence)
    {
     	statusItem.setLabel(presence.getDisplayText(), imageFactory.getMappedImage(presence));
    }

    /**
     * @return the status item
     */
    StatusItem getStatusItem()
    {
        return statusItem;
    }

    /**
     * Display dialog to allow user to change presence and post result
     */
    protected void displayPresenceDialog()
    {
        CustomDialog<PresenceControls> presenceDialog = dialogFactory.presenceDialogInstance(PresenceControls.TITLE);
        if (presenceDialog.open() == Window.OK)
        {
            Presence newPresence = presenceDialog.getCustomControls().getPresence();
            eventBroker.post(CyberteteEvents.PRESENCE, newPresence);
        }
     }
    
}
