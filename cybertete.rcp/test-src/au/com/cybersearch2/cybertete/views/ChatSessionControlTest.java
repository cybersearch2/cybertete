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
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.Test;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.TextControl;

/**
 * ChatSessionControlTest
 * @author Andrew Bowley
 * 20 May 2016
 */
public class ChatSessionControlTest
{
    private static final String TEST_BODY = "This is a test message";

    @Test
    public void test_postConstruct()
    {
        ChatSessionControl underTest = new ChatSessionControl();
        Composite parent = mock(Composite.class); 
        when(parent.getShell()).thenReturn(mock(Shell.class));
        Composite composite1 = mock(Composite.class); 
        Composite composite2 = mock(Composite.class); 
        Text transcript = mock(Text.class);
        Display display = mock(Display.class);
        Color bg = new Color(mock(Device.class), 255,255,255);
        Color fg = new Color(mock(Device.class), 0,0,0);
        when(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND)).thenReturn(bg);
        when(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND)).thenReturn(fg);
        when(transcript.getDisplay()).thenReturn(display);
        when(transcript.getCharCount()).thenReturn(34);
        ControlFactory controlFactory = mock(ControlFactory.class); 
        when(controlFactory.compositeInstance(parent)).thenReturn(composite1);
        when(controlFactory.compositeInstance(composite1)).thenReturn(composite2);
        when(controlFactory.textInstance(composite2, SWT.BORDER | SWT.MULTI | SWT.WRAP)).thenReturn(transcript);
        Text entry = mock(Text.class);
        when(controlFactory.textInstance(composite2, SWT.BORDER | SWT.WRAP)).thenReturn(entry);
        underTest.postConstruct(parent, controlFactory);
        verify(transcript).setLayoutData(isA(GridData.class));
        verify(transcript).setEditable(false);
        verify(transcript).setBackground(bg);
        verify(transcript).setForeground(fg);
        verify(entry).setLayoutData(isA(GridData.class));
    }

    @Test
    public void test_onFocus()
    {
        ChatSessionControl underTest = new ChatSessionControl();
        TextControl entry = mock(TextControl.class);
        when(entry.isDisposed()).thenReturn(false);
        underTest.entry = entry;
        underTest.onFocus();
        verify(entry).setFocus();
        entry = mock(TextControl.class);
        when(entry.isDisposed()).thenReturn(true);
        underTest.onFocus();
        verify(entry, times(0)).setFocus();
    }
    
    @Test
    public void test_displayMessage()
    {
        ChatSessionControl underTest = new ChatSessionControl();
        TextControl entry = mock(TextControl.class);
        underTest.entry = entry;
        TextControl transcript = mock(TextControl.class);
        when(transcript.getCharCount()).thenReturn(34);
        underTest.transcript = transcript;
        underTest.displayMessage("<mickymouse>  " + TEST_BODY);
        verify(transcript).append("<mickymouse>  " + TEST_BODY);
        verify(transcript).append("\n");
        verify(transcript).setSelection(34, 34);
        verify(transcript).showSelection();
        verify(entry).setText("");
    }
    
    @Test
    public void test_clear()
    {
        ChatSessionControl underTest = new ChatSessionControl();
        TextControl entry = mock(TextControl.class);
        underTest.entry = entry;
        TextControl transcript = mock(TextControl.class);
        underTest.transcript = transcript;
        underTest.clear();
        verify(transcript).setText("");
        verify(entry).setText("");
   }

    @Test
    public void test_setEnabled()
    {
        ChatSessionControl underTest = new ChatSessionControl();
        TextControl entry = mock(TextControl.class);
        underTest.entry = entry;
        TextControl transcript = mock(TextControl.class);
        underTest.transcript = transcript;
        when(transcript.isEnabled()).thenReturn(true);
        assertThat(underTest.setEnabled(false)).isTrue();
        verify(transcript).setEnabled(false);
        verify(entry).setEnabled(false);
   }
    
    @Test
    public void test_setEnabled_already_enabled()
    {
        ChatSessionControl underTest = new ChatSessionControl();
        TextControl entry = mock(TextControl.class);
        underTest.entry = entry;
        TextControl transcript = mock(TextControl.class);
        underTest.transcript = transcript;
        when(transcript.isEnabled()).thenReturn(true);
        assertThat(underTest.setEnabled(true)).isFalse();
        verify(transcript, times(0)).setEnabled(true);
        verify(entry, times(0)).setEnabled(true);
   }
 
    @Test
    public void test_getText()
    {
        ChatSessionControl underTest = new ChatSessionControl();
        TextControl entry = mock(TextControl.class);
        when(entry.getText()).thenReturn(TEST_BODY);
        underTest.entry = entry;
        assertThat(underTest.getText()).isEqualTo(TEST_BODY);
    }
    
}
