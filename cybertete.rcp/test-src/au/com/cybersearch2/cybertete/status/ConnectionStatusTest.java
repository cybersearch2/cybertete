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
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.security.cert.X509Certificate;

import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.ImageFactory;
import au.com.cybersearch2.cybertete.model.ApplicationState;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.service.ChatLoginController;
import au.com.cybersearch2.statusbar.StatusBar;
import au.com.cybersearch2.statusbar.StatusItem;



/**
 * ConnectionStatusTest
 * @author Andrew Bowley
 * 24 Mar 2016
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Image.class})
@PowerMockIgnore(
{
    "org.eclipse.swt.internal.win32.OS",
    "org.eclipse.swt.graphics.PaletteData", 
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


public class ConnectionStatusTest
{
    static final String TEST_MESSAGE = "Testing 123";
    static final String TOOL_TIP = "Tool tip";
    static final String TEST_JID = "mickymouse@disney.com";

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule(); 

    @Mock
    ControlFactory controlFactory;
    
    @Before
    public void setUp() throws Exception
    {
    }
    
    @Test
    public void test_postConstruct()
    {
        ImageFactory imageFactory = mock(ImageFactory.class);
        Image yellow = PowerMockito.mock(Image.class);
        when(imageFactory.getImage("icons/yellow_circle.gif")).thenReturn(yellow);
        ConnectionStatus underTest = new ConnectionStatus(imageFactory);
        underTest.controlFactory = controlFactory;
        assertThat(underTest.getStatusItem().isVisible()).isTrue();
        assertThat(underTest.controlFactory).isEqualTo(controlFactory);
        assertThat(underTest.certificateChain).isEmpty();
        Logger logger = mock(Logger.class);
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        when(loggerProvider.getClassLogger(ConnectionStatus.class)).thenReturn(logger);
        StatusItem statusItem = mock(StatusItem.class);
        underTest.statusItem = statusItem;
        underTest.postConstruct(loggerProvider);
        verify(statusItem).setLabelListener(underTest.labelListener);
        assertThat(underTest.certificateChain).isEmpty();
        assertThat(underTest.logger).isEqualTo(logger);
        CLabel label = mock(CLabel.class);
        Menu menu = mock(Menu.class);
        when(controlFactory.menuInstance(label)).thenReturn(menu);
        MenuItem menuItem = mock(MenuItem.class);
        when(controlFactory.menuItemInstance(menu, SWT.PUSH)).thenReturn(menuItem);
        underTest.labelListener.onLabelCreate(label);
        verify(statusItem).setMenu(menu);
        verify(label).setMenu(menu);
        verify(menuItem).setText("New Login");
        ArgumentCaptor<SelectionAdapter> selectionCaptor = ArgumentCaptor.forClass(SelectionAdapter.class);
        verify(menuItem).addSelectionListener(selectionCaptor.capture());
        assertThat(selectionCaptor.getValue()).isEqualTo((SelectionAdapter) underTest.selectionAdapter);
    }
    
    @Test
    public void test_menu_with_certificate()
    {
        X509Certificate cert = mock(X509Certificate.class);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("CN=" + TEST_JID);
        when(cert.getSubjectDN()).thenReturn(principal);
        ImageFactory imageFactory = mock(ImageFactory.class);
        Image yellow = PowerMockito.mock(Image.class);
        when(imageFactory.getImage("icons/yellow_circle.gif")).thenReturn(yellow);
        Image blank = PowerMockito.mock(Image.class);
        when(imageFactory.getImage("icons/blank.gif")).thenReturn(blank);
        ConnectionStatus underTest = new ConnectionStatus(imageFactory);
        underTest.controlFactory = controlFactory;
        EventBroker eventBroker = mock(EventBroker.class);
        underTest.eventBroker = eventBroker;
        ChatLoginController loginController = mock(ChatLoginController.class);
        underTest.loginController = loginController;
        Logger logger = mock(Logger.class);
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        when(loggerProvider.getClassLogger(ConnectionStatus.class)).thenReturn(logger);
        StatusItem statusItem = mock(StatusItem.class);
        underTest.statusItem = statusItem;
        underTest.postConstruct(loggerProvider);
        underTest.certificateChain.add(cert);
        CLabel label = mock (CLabel.class);
        Menu menu = mock(Menu.class);
        when(controlFactory.menuInstance(label)).thenReturn(menu);
        MenuItem menuItem1 = mock(MenuItem.class);
        MenuItem menuItem2 = mock(MenuItem.class);
        when(controlFactory.menuItemInstance(menu, SWT.PUSH)).thenReturn(menuItem1, menuItem2);
        underTest.labelListener.onLabelCreate(label);
        verify(menuItem1).setText("New Login");
        verify(menuItem1).addSelectionListener(underTest.selectionAdapter);
        SelectionEvent event = mock(SelectionEvent.class);
        underTest.selectionAdapter.widgetSelected(event);
        assertThat(underTest.logoutPending).isTrue();
        verify(statusItem).setLabel("Logging on...", blank);
        verify(loginController).setNewLogin(true);
        verify(eventBroker).post(CyberteteEvents.LOGOUT, ApplicationState.login);
        verify(menuItem2).setText(TEST_JID);
        ArgumentCaptor<SelectionAdapter> certItemCaptor = ArgumentCaptor.forClass(SelectionAdapter.class);
        verify(menuItem2).addSelectionListener(certItemCaptor.capture());
        certItemCaptor.getValue().widgetSelected(event);
        verify(eventBroker).post(CyberteteEvents.CERT_INFO_POPUP, cert);
    }
    
    @Test
    public void test_onClientCertHandler()
    {
        CLabel label1 = mock(CLabel.class);
        Menu menu1 = mock(Menu.class);
        CLabel label2 = mock(CLabel.class);
        Menu menu2 = mock(Menu.class);
        X509Certificate cert = mock(X509Certificate.class);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("CN=" + TEST_JID);
        when(cert.getSubjectDN()).thenReturn(principal);
        X509Certificate oldCert = mock(X509Certificate.class);
        when(controlFactory.menuInstance(label1)).thenReturn(menu1);
        when(controlFactory.menuInstance(label2)).thenReturn(menu2);
        MenuItem menuItem1 = mock(MenuItem.class);
        MenuItem menuItem2 = mock(MenuItem.class);
        MenuItem menuItem3 = mock(MenuItem.class);
        when(controlFactory.menuItemInstance(menu1, SWT.PUSH)).thenReturn(menuItem1);
        when(controlFactory.menuItemInstance(menu2, SWT.PUSH)).thenReturn(menuItem2, menuItem3);
        ImageFactory imageFactory = mock(ImageFactory.class);
        Image yellow = PowerMockito.mock(Image.class);
        when(imageFactory.getImage("icons/yellow_circle.gif")).thenReturn(yellow);
        ConnectionStatus underTest = new ConnectionStatus(imageFactory);
        underTest.controlFactory = controlFactory;
        EventBroker eventBroker = mock(EventBroker.class);
        underTest.eventBroker = eventBroker;
        StatusBar statusBar = mock(StatusBar.class);
        underTest.statusBar = statusBar;
        Logger logger = mock(Logger.class);
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        when(loggerProvider.getClassLogger(ConnectionStatus.class)).thenReturn(logger);
        StatusItem statusItem = mock(StatusItem.class);
        underTest.statusItem = statusItem;
        underTest.postConstruct(loggerProvider);
        underTest.labelListener.onLabelCreate(label1);
        underTest.certificateChain.add(oldCert);
        X509Certificate[] certificateChain = new X509Certificate[]{cert};
        underTest.onClientCertHandler(certificateChain);
        verify(statusBar).onRedraw(statusItem);
        underTest.labelListener.onLabelCreate(label2);
        assertThat(underTest.certificateChain.size()).isEqualTo(1);
        assertThat(underTest.certificateChain.get(0)).isEqualTo(cert);
        verify(label2).setMenu(menu2);
        verify(menuItem2).setText("New Login");
        verify(menuItem3).setText(TEST_JID);
        verify(menuItem2).addSelectionListener(underTest.selectionAdapter);
        ArgumentCaptor<SelectionAdapter> certItemCaptor = ArgumentCaptor.forClass(SelectionAdapter.class);
        verify(menuItem3).addSelectionListener(certItemCaptor.capture());
        SelectionEvent event = mock(SelectionEvent.class);
        certItemCaptor.getValue().widgetSelected(event);
        verify(eventBroker).post(CyberteteEvents.CERT_INFO_POPUP, cert);
    }
    
    @Test
    public void test_comms_handlers()
    {
        ImageFactory imageFactory = mock(ImageFactory.class);
        Image yellow = PowerMockito.mock(Image.class);
        when(imageFactory.getImage("icons/yellow_circle.gif")).thenReturn(yellow);
        Image green = PowerMockito.mock(Image.class);
        when(imageFactory.getImage("icons/green_circle.gif")).thenReturn(green);
        Image black = PowerMockito.mock(Image.class);
        when(imageFactory.getImage("icons/black_circle.gif")).thenReturn(black);
        Image red = PowerMockito.mock(Image.class);
        when(imageFactory.getImage("icons/red_circle.gif")).thenReturn(red);
        ConnectionStatus underTest = new ConnectionStatus(imageFactory);
        underTest.controlFactory = controlFactory;
        Logger logger = mock(Logger.class);
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        when(loggerProvider.getClassLogger(ConnectionStatus.class)).thenReturn(logger);
        StatusItem statusItem = mock(StatusItem.class);
        underTest.statusItem = statusItem;
        underTest.postConstruct(loggerProvider);
        underTest.commsOfflineHandler("Network down");
        verify(statusItem).setLabel("Network down", black);
        underTest.logoutPending = true;
        reset(statusItem);
        underTest.commsOfflineHandler("Network down");
        verify(statusItem, times(0)).setLabel("Network down", black);
        //underTest.logoutPending = false;
        reset(statusItem);
        underTest.commsEstablishHandler("Connecting...");
        assertThat(underTest.logoutPending).isFalse();
        verify(statusItem).setLabel("Connecting...", yellow);
        reset(statusItem);
        underTest.logoutPending = true;
        underTest.commsUpHandler("Logged in");
        assertThat(underTest.logoutPending).isFalse();
        verify(statusItem).setLabel("Logged in", green);
        reset(statusItem);
        underTest.commsDownHandler("Timeout");
        verify(statusItem).setLabel("Timeout", red);
        reset(statusItem);
        underTest.logoutPending = true;
        underTest.commsDownHandler("Timeout");
        verify(statusItem, times(0)).setLabel("Timeout", red);
    }
}
