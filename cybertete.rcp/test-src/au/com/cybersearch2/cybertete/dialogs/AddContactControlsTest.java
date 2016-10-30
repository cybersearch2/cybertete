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
import static org.mockito.Mockito.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.junit.Test;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.dialogs.DialogHandler;
import au.com.cybersearch2.dialogs.SyncErrorDialog;

/**
 * AddContactControlsTest
 * @author Andrew Bowley
 * 9 May 2016
 */
public class AddContactControlsTest
{
    static final String TEST_JID = "mickymouse@disney.com";
    static final String TEST_NAME = "micky";

    @Test
    public void test_createControls()
    {
        ControlFactory controlFactory = mock(ControlFactory.class);
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        AddContactControls underTest = new AddContactControls(controlFactory, errorDialog);
        assertThat(underTest.jid).isEmpty();
        assertThat(underTest.nickname).isEmpty();
        Composite parent = mock(Composite.class);
        Composite composite = mock(Composite.class);
        when(controlFactory.compositeInstance(parent)).thenReturn(composite);
        Label userIdLabel = mock(Label.class);
        Label nicknameLabel = mock(Label.class); 
        when(controlFactory.labelInstance(composite, SWT.NONE)).thenReturn(userIdLabel, nicknameLabel);
        Text jidText = mock(Text .class);
        Text nicknameText = mock(Text .class);
        when(controlFactory.textInstance(composite, SWT.BORDER)).thenReturn(jidText, nicknameText);
        assertThat(underTest.createControls(parent, mock(DialogHandler.class))).isEqualTo(composite);
        verify(composite).setLayout(isA(GridLayout.class));
        verify(userIdLabel).setText("JID:");
        verify(userIdLabel).setLayoutData(isA(GridData.class));
        verify(jidText).setLayoutData(isA(GridData.class));
        verify(nicknameLabel).setText("Nickname:");
        verify(nicknameLabel).setLayoutData(isA(GridData.class));
        verify(nicknameText).setLayoutData(isA(GridData.class));
    }
    
    @Test
    public void test_isValid()
    {
        ControlFactory controlFactory = mock(ControlFactory.class);
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        AddContactControls underTest = new AddContactControls(controlFactory, errorDialog);
        underTest.jid = TEST_JID;
        underTest.nickname = TEST_NAME;
        assertThat(underTest.isValid()).isTrue();
        assertThat(underTest.getJid()).isEqualTo(TEST_JID);
        assertThat(underTest.getNickname()).isEqualTo(TEST_NAME);
    }

    @Test
    public void test_isValid_name_empty()
    {
        ControlFactory controlFactory = mock(ControlFactory.class);
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        AddContactControls underTest = new AddContactControls(controlFactory, errorDialog);
        underTest.jid = TEST_JID;
        underTest.nickname = "";
        assertThat(underTest.isValid()).isFalse();
        verify(errorDialog).showError("Invalid Nickname",
                "Nickname field must not be blank.");
    }

    @Test
    public void test_isValid_jid_empty()
    {
        ControlFactory controlFactory = mock(ControlFactory.class);
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        AddContactControls underTest = new AddContactControls(controlFactory, errorDialog);
        underTest.jid = "";
        underTest.nickname = TEST_NAME;
        assertThat(underTest.isValid()).isFalse();
        verify(errorDialog).showError("Invalid JID",
                "JID field must not be blank.");
    }

    @Test
    public void test_isValid_jid_invalid_format()
    {
        ControlFactory controlFactory = mock(ControlFactory.class);
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        AddContactControls underTest = new AddContactControls(controlFactory, errorDialog);
        underTest.jid = TEST_NAME;
        underTest.nickname = TEST_NAME;
        assertThat(underTest.isValid()).isFalse();
        verify(errorDialog).showError("Invalid JID",
                "JID field format incorrect.");
    }
}
