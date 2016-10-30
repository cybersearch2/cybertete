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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.junit.Test;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.dialogs.DialogHandler;

/**
 * PasswordControlsTest
 * @author Andrew Bowley
 * 11 May 2016
 */
public class PasswordControlsTest
{
    final static String AUTH_NAME = "mickymouse";
    final static String PASSWORD = "secret";
    
    @Test
    public void test_createControls()
    {
        ControlFactory controlFactory = mock(ControlFactory.class);
        PasswordControls underTest = new PasswordControls(controlFactory, AUTH_NAME);
        Composite parent = mock(Composite.class);
        Composite composite = mock(Composite.class);
        when(controlFactory.compositeInstance(parent)).thenReturn(composite);
        Label nameLabel = mock(Label.class);
        Label passwordLabel = mock(Label.class); 
        when(controlFactory.labelInstance(composite, SWT.NONE)).thenReturn(nameLabel, passwordLabel);
        Text nameText = mock(Text.class);
        Display display = mock(Display.class);
        Color bg = new Color(mock(Device.class), 255,255,255);
        Color fg = new Color(mock(Device.class), 0,0,0);
        when(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND)).thenReturn(bg);
        when(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND)).thenReturn(fg);
        when(nameText.getDisplay()).thenReturn(display);
        when(controlFactory.textInstance(composite, SWT.BORDER)).thenReturn(nameText);
        Text passwordText = mock(Text .class);
        when(controlFactory.textInstance(composite, SWT.BORDER | SWT.PASSWORD)).thenReturn(passwordText);
        assertThat(underTest.createControls(parent, mock(DialogHandler.class))).isEqualTo(composite);
        verify(composite).setLayout(isA(GridLayout.class));
        verify(nameLabel).setText("Name:");
        verify(nameLabel).setLayoutData(isA(GridData.class));
        verify(nameText).setLayoutData(isA(GridData.class));
        verify(nameText).setEditable(false);
        verify(nameText).setBackground(bg);
        verify(nameText).setForeground(fg);
        verify(passwordLabel).setText("Password:");
        verify(passwordLabel).setLayoutData(isA(GridData.class));
        verify(passwordText).setLayoutData(isA(GridData.class));
    }
    
    @Test
    public void test_defaultPressed()
    {
        ControlFactory controlFactory = mock(ControlFactory.class);
        PasswordControls underTest = new PasswordControls(controlFactory, AUTH_NAME);
        Composite parent = mock(Composite.class);
        Composite composite = mock(Composite.class);
        when(controlFactory.compositeInstance(parent)).thenReturn(composite);
        Label nameLabel = mock(Label.class);
        Label passwordLabel = mock(Label.class); 
        when(controlFactory.labelInstance(composite, SWT.NONE)).thenReturn(nameLabel, passwordLabel);
        Text nameText = mock(Text.class);
        Display display = mock(Display.class);
        Color bg = new Color(mock(Device.class), 255,255,255);
        Color fg = new Color(mock(Device.class), 0,0,0);
        when(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND)).thenReturn(bg);
        when(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND)).thenReturn(fg);
        when(nameText.getDisplay()).thenReturn(display);
        when(controlFactory.textInstance(composite, SWT.BORDER)).thenReturn(nameText);
        Text passwordText = mock(Text.class);
        when(controlFactory.textInstance(composite, SWT.BORDER | SWT.PASSWORD)).thenReturn(passwordText);
        assertThat(underTest.createControls(parent, mock(DialogHandler.class))).isEqualTo(composite);
       when(passwordText.getText()).thenReturn(PASSWORD);
       assertThat(underTest.defaultSelect(mock(DialogHandler.class))).isTrue();
       assertThat(underTest.getPassword()).isEqualTo(PASSWORD.toCharArray());
    }
}
