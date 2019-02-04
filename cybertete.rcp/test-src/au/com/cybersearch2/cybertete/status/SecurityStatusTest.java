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
package au.com.cybersearch2.cybertete.status;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Collections;

import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolTip;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.ControlTip;
import au.com.cybersearch2.controls.ImageFactory;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.security.SslSessionData;
import au.com.cybersearch2.statusbar.StatusBar;
import au.com.cybersearch2.statusbar.StatusItem;

/**
 * SecurityStatusTest
 * @author Andrew Bowley
 * 25 Mar 2016
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Image.class})
@PowerMockIgnore(
{
    "org.eclipse.swt.internal.win32.OS",
    "org.eclipse.swt.graphics.Drawable", 
    "org.eclipse.swt.graphics.Color", 
    "org.eclipse.swt.graphics.GC",
    "org.eclipse.swt.graphics.GCData",
    "org.eclipse.swt.graphics.Device", 
    "org.eclipse.swt.graphics.DeviceData", 
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
public class SecurityStatusTest
{
    static final String  WARN_TOOL_TIP = "WARNING: The connection is unsecure!";
    static final String TEST_JID = "mickymouse@disney.com";
    static final String TEST_HOST = "google.talk";
    static final String TOOL_TIP = "Protocol: \"SSL 3.0\", CipherSuite: \"TLSv3\"";

    @Before
    public void setUp() throws Exception
    {
    }
    
    @Test
    public void test_postConstruct()
    {
        ImageFactory imageFactory = mock(ImageFactory.class);
        Image blank = PowerMockito.mock(Image.class);
        when(imageFactory.getImage("icons/blank.gif")).thenReturn(blank);
        ControlFactory controlFactory = mock(ControlFactory.class);
        SecurityStatus underTest = new SecurityStatus(imageFactory);
        underTest.controlFactory = controlFactory;
        assertThat(underTest.statusItem.isVisible()).isTrue();
        StatusItem statusItem = mock(StatusItem.class);
        underTest.statusItem = statusItem;
        underTest.postConstruct();
        verify(statusItem).setLabelListener(underTest.labelListener);
        assertThat(underTest.sslSessionData.getCertificates()).isEmpty();
        Composite parent = mock(Composite.class);
        CLabel label = mock(CLabel.class);
        when(label.getParent()).thenReturn(parent);
        ToolTip toolTip = mock(ToolTip.class);
        when(controlFactory.toolTipInstance(parent)).thenReturn(toolTip);
        underTest.labelListener.onLabelCreate(label);
        ArgumentCaptor<ControlTip> controlTipCaptor = ArgumentCaptor.forClass(ControlTip.class);
        verify(label).addFocusListener(controlTipCaptor.capture());
        Event event = mock(Event.class);
        event.widget = label;
        FocusEvent focusEvent = new FocusEvent(event);
        controlTipCaptor.getValue().focusLost(focusEvent);
        verify(toolTip).setVisible(false);
        verify(statusItem).setMenu(null);
    }

    @Test
    public void test_menu_with_certificate()
    {
        SslSessionData sslSessionData = mock(SslSessionData.class);
        X509Certificate cert = mock(X509Certificate.class);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("CN=" + TEST_JID);
        when(cert.getSubjectDN()).thenReturn(principal);
        ImageFactory imageFactory = mock(ImageFactory.class);
        Image yellow = PowerMockito.mock(Image.class);
        when(imageFactory.getImage("icons/yellow_circle.gif")).thenReturn(yellow);
        Image blank = PowerMockito.mock(Image.class);
        when(imageFactory.getImage("icons/blank.gif")).thenReturn(blank);
        ControlFactory controlFactory = mock(ControlFactory.class);
        SecurityStatus underTest = new SecurityStatus(imageFactory);
        underTest.controlFactory = controlFactory;
        EventBroker eventBroker = mock(EventBroker.class);
        underTest.eventBroker = eventBroker;
        Logger logger = mock(Logger.class);
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        when(loggerProvider.getClassLogger(ConnectionStatus.class)).thenReturn(logger);
        StatusItem statusItem = mock(StatusItem.class);
        underTest.statusItem = statusItem;
        underTest.postConstruct();
        underTest.sslSessionData = sslSessionData;
        when(sslSessionData.getCertificates()).thenReturn(Collections.singletonList(cert));
        Composite parent = mock(Composite.class);
        CLabel label = mock(CLabel.class);
        when(label.getParent()).thenReturn(parent);
        ToolTip toolTip = mock(ToolTip.class);
        when(controlFactory.toolTipInstance(parent)).thenReturn(toolTip);
        Menu menu = mock(Menu.class);
        when(controlFactory.menuInstance(label)).thenReturn(menu);
        MenuItem menuItem1 = mock(MenuItem.class);
        when(controlFactory.menuItemInstance(menu, SWT.PUSH)).thenReturn(menuItem1);
        underTest.labelListener.onLabelCreate(label);
        verify(menuItem1).setText(TEST_JID);
        ArgumentCaptor<SelectionAdapter> certItemCaptor = ArgumentCaptor.forClass(SelectionAdapter.class);
        verify(menuItem1).addSelectionListener(certItemCaptor.capture());
        SelectionEvent event = mock(SelectionEvent.class);
        certItemCaptor.getValue().widgetSelected(event);
        verify(eventBroker).post(CyberteteEvents.CERT_INFO_POPUP, cert);
        verify(statusItem).setMenu(menu);
    }
    
    @Test
    public void test_all_handlers()
    {
        Composite parent = mock(Composite.class);
        ImageFactory imageFactory = mock(ImageFactory.class);
        Image blank = PowerMockito.mock(Image.class);
        Image unsecure = PowerMockito.mock(Image.class);
        Image secure = PowerMockito.mock(Image.class);
        when(imageFactory.getImage("icons/blank.gif")).thenReturn(blank);
        when(imageFactory.getImage("icons/unsecure.gif")).thenReturn(unsecure);
        when(imageFactory.getImage("icons/secure.gif")).thenReturn(secure);
        ControlFactory controlFactory = mock(ControlFactory.class);
        ToolTip toolTip = mock(ToolTip.class);
        when(controlFactory.toolTipInstance(parent)).thenReturn(toolTip);
        SecurityStatus underTest = new SecurityStatus(imageFactory);
        StatusBar statusBar = mock(StatusBar.class);
        underTest.statusBar = statusBar;
        underTest.controlFactory = controlFactory;
        StatusItem statusItem = mock(StatusItem.class);
        underTest.statusItem = statusItem;
        underTest.postConstruct();
        X509Certificate cert = mock(X509Certificate.class);
        underTest.sslSessionData =  new SslSessionData(Collections.singletonList(cert), "", "");
        underTest.onConnectedHandler(TEST_HOST);
        assertThat(underTest.sslSessionData.getCertificates()).isEmpty();
        verify(statusItem).setImage(unsecure);
        verify(statusItem).setTooltip(WARN_TOOL_TIP);
        underTest.onSecureHandler(new SslSessionData(Collections.singletonList(cert), "SSL 3.0", "TLSv3"));
        verify(statusBar).onRedraw(statusItem);
        assertThat(underTest.sslSessionData.getCertificates().size()).isEqualTo(1);
        assertThat(underTest.sslSessionData.getCertificates().get(0)).isEqualTo(cert);
        verify(statusItem).setImage(secure);
        verify(statusItem).setTooltip(TOOL_TIP);
        underTest.onUnavailabledHandler(TEST_HOST);
        assertThat(underTest.sslSessionData.getCertificates()).isEmpty();
        verify(statusItem).setImage(blank);
        verify(statusItem).setTooltip("");
    }
}
