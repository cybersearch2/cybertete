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
// Adapted from Hyperbola sample code which had following copyright:
/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package au.com.cybersearch2.cybertete.status;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.CustomLabelSpec;
import au.com.cybersearch2.controls.ImageFactory;
import au.com.cybersearch2.statusbar.controls.ItemConfiguration;
import au.com.cybersearch2.cybertete.model.ApplicationState;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.service.ChatLoginController;
import au.com.cybersearch2.statusbar.LabelListener;
import au.com.cybersearch2.statusbar.StatusItem;
import au.com.cybersearch2.statusbar.StatusBar;

/**
 * ConnectionStatus
 * Displays current connection status with a color circle icon and information such as host name and user JID
 * @author Andrew Bowley
 * 13 Nov 2015
 */
public class ConnectionStatus
{
    /** Default width in characters. Required but not used as this item takes all remaining free space on status line. */
    public static int CHAR_WIDTH = 80;

    /** Configures and controls status line item */
    StatusItem statusItem;
    /** Flag set true if session is logging out */
    boolean logoutPending;
    /** Client certificate chain. Will be null if client cert. authentication not in use */
    List<X509Certificate> certificateChain;

    /** Logger */
    Logger logger;
    /** Image loader and cache */
    ImageFactory imageFactory;

    /** Status line container arranges the items */
    @Inject
    StatusBar statusBar;
    
    /** Controls the login process */
    @Inject
    ChatLoginController loginController;
    /** Event broker service */
    @Inject
    IEventBroker eventBroker;
    /** SWT widget factory */
    @Inject
    ControlFactory controlFactory;

    /** Menu item command for new login */
    SelectionListener selectionAdapter = 
        new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) 
        {   // Clear color icon at start of new login
            logoutPending = true;
            statusItem.setLabel("Logging on...", imageFactory.getImage("icons/blank.gif"));
            // Set new login flag on login controller so it ignores auto login preference
            loginController.setNewLogin(true);
            eventBroker.post(CyberteteEvents.LOGOUT, ApplicationState.login);
        }
    };

    /** Custom Label creation action (re-)creates context menu */
    LabelListener labelListener = new LabelListener(){

        @Override
        public void onLabelCreate(CLabel label)
        {
            statusItem.setMenu(createMenu(label));
        }};

    /**
     * Construct ConnectionStatus object.
     * Set initial image to indicate establish communications (yellow)
     * @param imageFactory Image loader and cache
     */
    @Inject 
    public ConnectionStatus(ImageFactory imageFactory)
    {
        this.imageFactory = imageFactory;
        // Initial image - establish communications
        CustomLabelSpec specification = new ItemConfiguration(imageFactory.getImage("icons/yellow_circle.gif"), "", CHAR_WIDTH);
        statusItem = new StatusItem(specification, CyberteteStatusBar.CONNECTION_ID);
        certificateChain = new ArrayList<X509Certificate>();
    }

    /**
     * Post construct
     * @param loggerProvider Logger factory
     */
    @PostConstruct
    public void postConstruct(ILoggerProvider loggerProvider)
    {
        logger = loggerProvider.getClassLogger(ConnectionStatus.class);
        statusItem.setLabelListener(labelListener);
    }
 
    /**
     * Handle communications up event. Color = green
     * @param info Host domain
     */
    @Inject @Optional
    void commsUpHandler(@UIEventTopic(CyberteteEvents.COMMS_UP) String info)
    {
        logoutPending = false;
        statusItem.setLabel(info, imageFactory.getImage("icons/green_circle.gif"));
    }

    /**
     * Handle communications established event. Color = yellow
     * @param info Host domain
     */
    @Inject @Optional
    void commsEstablishHandler(@UIEventTopic(CyberteteEvents.COMMS_ESTABLISH) String info)
    {
        logoutPending = false;
        statusItem.setLabel(info, imageFactory.getImage("icons/yellow_circle.gif"));
    }
    
    /**
     * Handle communications down event. Color = red
     * @param info Host domain
     */
    @Inject @Optional
    void commsDownHandler(@UIEventTopic(CyberteteEvents.COMMS_DOWN) String info)
    {
        if (logoutPending)
            return;
        statusItem.setLabel(info, imageFactory.getImage("icons/red_circle.gif"));
    }
    
   /**
    * Handle communications offline event. Color = black
    * @param info Context - "Application startup" or "Login"
    */
   @Inject @Optional
    void commsOfflineHandler(@UIEventTopic(CyberteteEvents.COMMS_OFFLINE) String info)
    {
        if (logoutPending)
            return;
        statusItem.setLabel(info, imageFactory.getImage("icons/black_circle.gif"));
    }

   /**
    * Handle client certificate event. Update certificate chain details
    * @param certificateChain X509Certificate array
    */
    @Inject @Optional
    void onClientCertHandler(@UIEventTopic(CyberteteEvents.CLIENT_CERT) X509Certificate[] certificateChain)
    {
        this.certificateChain.clear();
        for (X509Certificate x509Cert: certificateChain)
            this.certificateChain.add(x509Cert);
        // Redraw status line to recreate the menu
        statusBar.onRedraw(statusItem);
    }

    /**
     * @return the status item
     */
    StatusItem getStatusItem()
    {
        return statusItem;
    }
    
    /**
     * Add "New Login" item to context menu
     */
    void addNewLogin(Menu menu)
    {
        MenuItem menuItem = controlFactory.menuItemInstance(menu, SWT.PUSH);
        menuItem.setText("New Login"); 
        menuItem.addSelectionListener(selectionAdapter);
    }

    /**
     * Create context menu
     */
    Menu createMenu(CLabel label)
    {
        Menu menu = controlFactory.menuInstance(label);
        label.setMenu(menu);
        addNewLogin(menu);
        if (certificateChain.size() > 0)
        {
            X509Menu x509Menu = new X509Menu(menu, eventBroker);
            x509Menu.addX509Certs(controlFactory, certificateChain);
        }
        return menu;
    }


}
