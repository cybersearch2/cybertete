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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.controls.ComboControl;
import au.com.cybersearch2.controls.ControlFactory;

/**
 * UserSelectorTest
 * @author Andrew Bowley
 * 18 May 2016
 */
public class UserSelectorTest
{
    static final String TEST_JID = "mickymouse@disney.com";
    static final String TEST_JID2 = "adeline@google.com";

    @Test
    public void test_constructor()
    {
        Label jidLabel = mock(Label.class);
        Combo jidText = mock(Combo.class);
        ControlFactory controlFactory = mock(ControlFactory.class);
        Composite parent = mock(Composite.class);
        Composite composite = mock(Composite.class);
        when(composite.getParent()).thenReturn(parent);
        Listener listener = mock(Listener.class);
        when(controlFactory.labelInstance(composite, SWT.NONE)).thenReturn(jidLabel);
        when(controlFactory.comboInstance(composite, SWT.BORDER)).thenReturn(jidText);
        new UserSelector(controlFactory, composite, listener);
        verify(jidLabel).setText("JID:");
        verify(jidLabel).setLayoutData(isA(GridData.class));
        verify(controlFactory).convertHeightInCharsToPixels(parent, 20);
        verify(jidText).addListener(SWT.Modify, listener);
        verify(jidText).addListener(SWT.Selection, listener);
   }

    @Test
    public void test_initializeUsers()
    {
        List<String> userList = new ArrayList<String>();
        userList.add(TEST_JID);
        userList.add(TEST_JID2);
        ControlFactory controlFactory = mock(ControlFactory.class);
        Composite parent = mock(Composite.class);
        Composite composite = mock(Composite.class);
        when(composite.getParent()).thenReturn(parent);
        Listener listener = mock(Listener.class);
        Label jidLabel = mock(Label.class);
        ComboControl jidText = mock(ComboControl.class);
        when(controlFactory.labelInstance(composite, SWT.NONE)).thenReturn(jidLabel);
        when(controlFactory.comboInstance(composite, SWT.BORDER)).thenReturn(mock(Combo.class));
        UserSelector underTest = new UserSelector(controlFactory, composite, listener);
        underTest.jidText = jidText;
        underTest.initializeUsers(userList);
        ArgumentCaptor<String> jidCaptor = ArgumentCaptor.forClass(String.class);
        verify(jidText, times(3)).add(jidCaptor.capture());
        assertThat(jidCaptor.getAllValues().toArray(new String[2])).isEqualTo(new String[]{"", TEST_JID, TEST_JID2});
        verify(jidText).select(1);
    }

    @Test
    public void test_initializeUsers_new_session_empty_jid()
    {
        List<String> userList = new ArrayList<String>();
        userList.add("");
        userList.add(TEST_JID);
        ControlFactory controlFactory = mock(ControlFactory.class);
        Composite parent = mock(Composite.class);
        Composite composite = mock(Composite.class);
        when(composite.getParent()).thenReturn(parent);
        Listener listener = mock(Listener.class);
        Label jidLabel = mock(Label.class);
        ComboControl jidText = mock(ComboControl.class);
        when(controlFactory.labelInstance(composite, SWT.NONE)).thenReturn(jidLabel);
        when(controlFactory.comboInstance(composite, SWT.BORDER)).thenReturn(mock(Combo.class));
        UserSelector underTest = new UserSelector(controlFactory, composite, listener);
        underTest.jidText = jidText;
        underTest.initializeUsers(userList);
        ArgumentCaptor<String> jidCaptor = ArgumentCaptor.forClass(String.class);
        verify(jidText, times(2)).add(jidCaptor.capture());
        assertThat(jidCaptor.getAllValues().toArray(new String[2])).isEqualTo(new String[]{"", TEST_JID});
        verify(jidText).select(0);
    }

    @Test
    public void clear()
    {
        ControlFactory controlFactory = mock(ControlFactory.class);
        Composite parent = mock(Composite.class);
        Composite composite = mock(Composite.class);
        when(composite.getParent()).thenReturn(parent);
        Listener listener = mock(Listener.class);
        Label jidLabel = mock(Label.class);
        ComboControl jidText = mock(ComboControl.class);
        when(controlFactory.labelInstance(composite, SWT.NONE)).thenReturn(jidLabel);
        when(controlFactory.comboInstance(composite, SWT.BORDER)).thenReturn(mock(Combo.class));
        when(jidText.getItems()).thenReturn(new String[] {TEST_JID,TEST_JID2});
        UserSelector underTest = new UserSelector(controlFactory, composite, listener);
        underTest.jidText = jidText;
        underTest.clear();
        verify(jidText).removeAll();
    }    
    
    @Test
    public void test_startSingleSignonConfig_reselect()
    {
        ControlFactory controlFactory = mock(ControlFactory.class);
        Composite parent = mock(Composite.class);
        Composite composite = mock(Composite.class);
        when(composite.getParent()).thenReturn(parent);
        Listener listener = mock(Listener.class);
        Label jidLabel = mock(Label.class);
        ComboControl jidText = mock(ComboControl.class);
        when(controlFactory.labelInstance(composite, SWT.NONE)).thenReturn(jidLabel);
        when(controlFactory.comboInstance(composite, SWT.BORDER)).thenReturn(mock(Combo.class));
        UserSelector underTest = new UserSelector(controlFactory, composite, listener);
        underTest.jidText = jidText;
        when(jidText.indexOf(TEST_JID)).thenReturn(2);
        underTest.jidText = jidText;
        underTest.startSingleSignonConfig(TEST_JID);
        verify(jidText).select(2);
   }


    @Test
    public void test_startSingleSignonConfig_first_time()
    {
        ControlFactory controlFactory = mock(ControlFactory.class);
        Composite parent = mock(Composite.class);
        Composite composite = mock(Composite.class);
        when(composite.getParent()).thenReturn(parent);
        Listener listener = mock(Listener.class);
        Label jidLabel = mock(Label.class);
        ComboControl jidText = mock(ComboControl.class);
        when(controlFactory.labelInstance(composite, SWT.NONE)).thenReturn(jidLabel);
        when(controlFactory.comboInstance(composite, SWT.BORDER)).thenReturn(mock(Combo.class));
        UserSelector underTest = new UserSelector(controlFactory, composite, listener);
        underTest.jidText = jidText;
        when(jidText.indexOf(TEST_JID)).thenReturn(-1, 0);
        underTest.startSingleSignonConfig(TEST_JID);
        verify(jidText).add(TEST_JID);
        verify(jidText).select(0);
   }

    @Test
    public void test_short_methods()
    {
        ControlFactory controlFactory = mock(ControlFactory.class);
        Composite parent = mock(Composite.class);
        Composite composite = mock(Composite.class);
        when(composite.getParent()).thenReturn(parent);
        Listener listener = mock(Listener.class);
        Label jidLabel = mock(Label.class);
        ComboControl jidText = mock(ComboControl.class);
        when(controlFactory.labelInstance(composite, SWT.NONE)).thenReturn(jidLabel);
        when(controlFactory.comboInstance(composite, SWT.BORDER)).thenReturn(mock(Combo.class));
        UserSelector underTest = new UserSelector(controlFactory, composite, listener);
        underTest.jidText = jidText;
        when(jidText.getText()).thenReturn(TEST_JID);
        assertThat(underTest.getText()).isEqualTo(TEST_JID);
        underTest.setFocus();
        verify(jidText).setFocus();
    }
}
