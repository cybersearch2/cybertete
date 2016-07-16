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
package au.com.cybersearch2.cybertete.status;

import java.security.cert.X509Certificate;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolTip;

import au.com.cybersearch2.controls.ControlTip;
import au.com.cybersearch2.controls.CustomLabelSpec;
import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.ImageFactory;
import au.com.cybersearch2.statusbar.controls.ItemConfiguration;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.security.SslSessionData;
import au.com.cybersearch2.statusbar.LabelListener;
import au.com.cybersearch2.statusbar.StatusBar;
import au.com.cybersearch2.statusbar.StatusItem;

/**
 * SecurityStatus
 * Status line item displays icon while connected to indicate if is secure, or not.
 * Context menu allows server SSL certificate details to be examined.
 * @author Andrew Bowley
 * 16 Nov 2015
 */
public class SecurityStatus
{
    /** Configures and controls status line item */
    StatusItem statusItem;
    /** SSL information */
    SslSessionData sslSessionData;
    /** Image loader and cache */
    ImageFactory imageFactory;
    
    /** Status line container arranges the items */
    @Inject
    StatusBar statusBar;
    
    /** Event broker service */
    @Inject
    IEventBroker eventBroker;
    /** SWT widget factory */
    @Inject
    ControlFactory controlFactory;

    LabelListener labelListener = new LabelListener(){

        @Override
        public void onLabelCreate(CLabel label)
        {
            ToolTip toolTip = controlFactory.toolTipInstance(label.getParent());
            label.addFocusListener(new ControlTip(toolTip));
            if (sslSessionData.getCertificates().size() > 0)
                statusItem.setMenu(createMenu(label));
            else
                statusItem.setMenu(null);
        }};
        
    /**
     * Construct SecurityStatus object.
     * Set initial image to hide item in status line.
     * @param imageFactory Image loader and cache
     */
    @Inject 
    public SecurityStatus(ImageFactory imageFactory)
    {
        this.imageFactory = imageFactory;
        CustomLabelSpec specification = new ItemConfiguration(imageFactory.getImage("icons/blank.gif"), "", 0);
        statusItem = new StatusItem(specification, CyberteteStatusBar.SECURITY_ID);
    }

    /**
     * Post construct
     */
    @PostConstruct
    public void postConstruct()
    {
        statusItem.setLabelListener(labelListener);
        clearSslInformation();
    }
    
    /**
     * Handle connection event. Indicate the connection is unsecure.
     * @param hostName Server address
     */
    @Inject @Optional
    void onConnectedHandler(@UIEventTopic(CyberteteEvents.NETWORK_CONNECTED) String hostName)
    {
        clearSslInformation();
        statusItem.setImage(imageFactory.getImage("icons/unsecure.gif"));
        statusItem.setTooltip("WARNING: The connection is unsecure!");
     }

    /**
     * Handle SSL session established event. Indicate the connection is secure.
     * @param sslSessionData SSL information
     */
    @Inject @Optional
    void onSecureHandler(@UIEventTopic(CyberteteEvents.NETWORK_SECURE) SslSessionData sslSessionData)
    {
        boolean doRedraw = (this.sslSessionData.getCertificates().size() > 0) || 
                            (sslSessionData.getCertificates().size() > 0);
        this.sslSessionData = sslSessionData;
        statusItem.setImage(imageFactory.getImage("icons/secure.gif"));
        statusItem.setTooltip("Protocol: \"" + sslSessionData.getProtocol() + "\", CipherSuite: \"" + sslSessionData.getCipherSuite() + "\"");
        if (doRedraw) 
            // Must redraw status line to recreate menu with new certificate menu items, or remove them, as appropriate
            statusBar.onRedraw(statusItem);
   }

    /**
     * Handle host unavailable event. Hide item in status line.
     * @param hostName Server address
     */
    @Inject @Optional
    void onUnavailabledHandler(@UIEventTopic(CyberteteEvents.NETWORK_UNAVAILABLE) String hostName)
    {
        clearSslInformation();
        statusItem.setImage(imageFactory.getImage("icons/blank.gif"));
        statusItem.setTooltip("");
    }

    /**
     * @return the status item
     */
    StatusItem getStatusItem()
    {
        return statusItem;
    }
    
   /**
     * Create context menu
     */
    private Menu createMenu(Control control)
    {
        Menu menu = controlFactory.menuInstance(control);
        control.setMenu(menu);
        List<X509Certificate> x509Certs = sslSessionData.getCertificates();
        X509Menu x509Menu = new X509Menu(menu, eventBroker);
        x509Menu.addX509Certs(controlFactory, x509Certs);
        return menu;
    }

    /**
     * Hide item in status line
     */
    private void clearSslInformation()
    {
        sslSessionData = SslSessionData.EMPTY_SSL_SESSION_DATA;
    }
    
}
