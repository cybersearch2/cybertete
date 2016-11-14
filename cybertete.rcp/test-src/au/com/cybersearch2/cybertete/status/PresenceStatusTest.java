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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import au.com.cybersearch2.controls.CustomDialog;
import au.com.cybersearch2.controls.ImageFactory;
import au.com.cybersearch2.cybertete.dialogs.DialogFactory;
import au.com.cybersearch2.cybertete.dialogs.PresenceControls;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.Presence;
import au.com.cybersearch2.statusbar.StatusItem;

/**
 * PresenceStatusTest
 * @author Andrew Bowley
 * 25 Mar 2016
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
public class PresenceStatusTest
{
    static final String TEST_MESSAGE = "Testing 123";
    
    @Before
    public void setUp() throws Exception
    {
        org.eclipse.e4.ui.internal.services.Activator activator = 
            new org.eclipse.e4.ui.internal.services.Activator();
        BundleContext context = mock(BundleContext.class);
        when(context.getBundles()).thenReturn(new Bundle[]{});
        activator.start(context);
    }
    
    @Test
    public void test_constructor()
    {
        ImageFactory imageFactory = mock(ImageFactory.class);
        Image offline = PowerMockito.mock(Image.class);
        when(imageFactory.getMappedImage(Presence.offline)).thenReturn(offline);
        CLabel label = mock(CLabel.class);
        PresenceStatus underTest = new PresenceStatus(imageFactory);
        EventBroker eventBroker = mock(EventBroker.class);
        underTest.eventBroker = eventBroker;
        DialogFactory dialogFactory = mock(DialogFactory.class);
        underTest.dialogFactory = dialogFactory;
        StatusItem statusItem = mock(StatusItem.class);
        underTest.statusItem = statusItem;
        underTest.postConstruct();
        verify(statusItem).setLabelListener(underTest.labelListener);
        underTest.labelListener.onLabelCreate(label);
        verify(label).addMouseListener(underTest.mouseListener);
        MouseEvent event = mock(MouseEvent.class);
        event.button = 3;
        @SuppressWarnings("unchecked")
        CustomDialog<PresenceControls> presenceDialog = mock(CustomDialog.class);
        when(presenceDialog.open()).thenReturn(Window.OK);
        PresenceControls customControls = mock(PresenceControls.class);
        when(customControls.getPresence()).thenReturn(Presence.dnd);
        when(presenceDialog.getCustomControls()).thenReturn(customControls );
        when(dialogFactory.presenceDialogInstance(PresenceControls.TITLE)).thenReturn(presenceDialog);
        underTest.mouseListener.mouseUp(event);
        verify(eventBroker).post(CyberteteEvents.PRESENCE, Presence.dnd);
        //verify(accessible).addAccessibleControlListener(adapterCaptor.capture());
        //AccessibleControlEvent event = new AccessibleControlEvent(this);
        //adapterCaptor.getValue().getRole(event);
        //assertThat(event.detail).isEqualTo(ACC.ROLE_STATUSBAR);
    }


    @Test
    public void test_presenceHandler()
    {
        ImageFactory imageFactory = mock(ImageFactory.class);
        Image offline = PowerMockito.mock(Image.class);
        Image online = PowerMockito.mock(Image.class);
        Image away = PowerMockito.mock(Image.class);
        Image dnd = PowerMockito.mock(Image.class);
        when(imageFactory.getMappedImage(Presence.offline)).thenReturn(offline);
        when(imageFactory.getMappedImage(Presence.online)).thenReturn(online);
        when(imageFactory.getMappedImage(Presence.away)).thenReturn(away);
        when(imageFactory.getMappedImage(Presence.dnd)).thenReturn(dnd);
        PresenceStatus underTest = new PresenceStatus(imageFactory);
        StatusItem statusItem = mock(StatusItem.class);
        underTest.statusItem = statusItem;
        underTest.postConstruct();
        underTest.presenceHandler(Presence.online);
        verify(statusItem).setLabel("Online", online);
        underTest.presenceHandler(Presence.away);
        verify(statusItem).setLabel("Away", away);
        underTest.presenceHandler(Presence.dnd);
        verify(statusItem).setLabel("Do not disturb", dnd);
        underTest.presenceHandler(Presence.offline);
        verify(statusItem).setLabel("Offline", offline);
    }
}
