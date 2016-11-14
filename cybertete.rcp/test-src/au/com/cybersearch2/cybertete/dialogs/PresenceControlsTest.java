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
package au.com.cybersearch2.cybertete.dialogs;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.DefaultGridData;
import au.com.cybersearch2.controls.DefaultLayout;
import au.com.cybersearch2.controls.ImageFactory;
import au.com.cybersearch2.cybertete.model.Presence;
import au.com.cybersearch2.dialogs.DialogHandler;

/**
 * PresenceControlsTest
 * @author Andrew Bowley
 * 11 May 2016
 */
public class PresenceControlsTest
{
    Presence[] presences = new Presence[] { Presence.online, Presence.away, Presence.dnd };
    Image[] images = new Image[3];

    @Test
    public void test_createControls()
    {
        ControlFactory controlFactory = mock(ControlFactory.class);
        ImageFactory imageFactory = mock(ImageFactory.class);
        Display display = mock(Display.class);
        try
        {
            Image online = new Image(display, "icons/online.gif");
            images[0] = online;
            Image away = new Image(display, "icons/away.gif");
            images[1] = away;
            Image dnd = new Image(display, "icons/dnd.gif");
            images[2] = dnd;
            PresenceControls underTest = new  PresenceControls(controlFactory, imageFactory);
            Composite parent = mock(Composite.class);
            Composite composite = mock(Composite.class);
            when(controlFactory.compositeInstance(parent)).thenReturn(composite);
            Group group = mock(Group.class);
            when(controlFactory.groupInstance(composite, SWT.NONE)).thenReturn(group);
            DefaultLayout defaultLayout = mock(DefaultLayout.class);
            when(controlFactory.getDefaultLayout()).thenReturn(defaultLayout );
            DefaultGridData defaultGridData = mock(DefaultGridData.class);
            when(controlFactory.getDefaultGridData()).thenReturn(defaultGridData);
            when(defaultGridData.grab(true, true)).thenReturn(defaultGridData);
            Button button1 = mock(Button.class);
            Button button2 = mock(Button.class);
            Button button3 = mock(Button.class);
            when(imageFactory.getMappedImage(Presence.online)).thenReturn(online);
            when(imageFactory.getMappedImage(Presence.away)).thenReturn(away);
            when(imageFactory.getMappedImage(Presence.dnd)).thenReturn(dnd);
            when(controlFactory.buttonInstance(isA(Group.class), eq(SWT.RADIO))).thenReturn(button1, button2, button3);
            assertThat(underTest.createControls(parent, mock(DialogHandler.class))).isEqualTo(composite);
            verify(composite).setLayout(isA(GridLayout.class));
            verify(defaultLayout).applyTo(group);
            verify(defaultGridData).applyTo(group);
            verifyButton(underTest, button1, 0, imageFactory);
            verifyButton(underTest, button2, 1, imageFactory);
            verifyButton(underTest, button3, 2, imageFactory);
        }
        finally
        {
            if (images[0] != null)
                images[0].dispose();
            if (images[1] != null)
                images[1].dispose();
            if (images[2] != null)
                images[2].dispose();
        }
    }

    private void verifyButton(PresenceControls underTest, Button button, int index, ImageFactory imageFactory)
    {
        String[] values = new String[] { "Online", "Away", "Do not disturb" };
        verify(button).setText(values[index]);
        verify(button).setImage(images[index]);
        verify(button).setData(presences[index]);
        ArgumentCaptor<FocusListener> listenerCaptor = ArgumentCaptor.forClass(FocusListener.class);
        verify(button).addFocusListener(listenerCaptor.capture());
        Event event= new Event();
        Button widget = mock(Button.class);
        when(widget.getData()).thenReturn(presences[index]);
        event.widget = widget;
        FocusEvent e = new FocusEvent(event);
        listenerCaptor.getValue().focusGained(e);
        assertThat(underTest.getPresence()).isEqualTo(presences[index]);
    }

}
