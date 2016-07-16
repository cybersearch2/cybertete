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

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.CustomDialog.ButtonFactory;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.service.LoginData;
import au.com.cybersearch2.dialogs.DialogHandler;

/**
 * LoginCustomControlsTest
 * @author Andrew Bowley
 * 10 May 2016
 */
public class LoginCustomControlsTest
{
    static final String TEST_PASSWORD = "secret";
    
    @Test
    public void test_createControls()
    {
        ControlFactory controlFactory = mock(ControlFactory.class);
        DialogLoginControls loginControls = mock(DialogLoginControls.class);
        LoginCustomControls underTest = new LoginCustomControls(controlFactory, loginControls);
        Composite parent = mock(Composite.class);
        DialogHandler dialogHandler = mock(DialogHandler.class);
        Control control = mock(Control.class);
        when(loginControls.createDialogArea(controlFactory, parent)).thenReturn(control);
        assertThat(underTest.createControls(parent, dialogHandler)).isEqualTo(control);
        verify(loginControls).setDialogHandler(dialogHandler);
    }

    @Test
    public void test_createButtonsForButtonBar()
    {
        LoginData loginData = mock(LoginData.class); 
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getPassword()).thenReturn(TEST_PASSWORD);
        when(loginData.getSessionDetails()).thenReturn(sessionDetails );
        DialogHandler loginDialog = mock(DialogHandler.class);
        ButtonFactory buttonFactory = mock(ButtonFactory.class);
        Composite parent = mock(Composite.class);
        Button advanced = mock(Button.class);
        when(buttonFactory.buttonInstance(parent, IDialogConstants.CLIENT_ID, "Advanced", false))
        .thenReturn(advanced);
        ArgumentCaptor<SelectionAdapter> advancedCaptor = ArgumentCaptor.forClass(SelectionAdapter.class);
        ControlFactory controlFactory = mock(ControlFactory.class);
        DialogLoginControls loginControls = mock(DialogLoginControls.class);
        SelectionAdapter ssoAdapter = mock(SelectionAdapter.class);
        when(loginControls.getSingleSignonSelectionAdapter()).thenReturn(ssoAdapter );
        Button singleSignonButton = mock(Button.class);
        when(buttonFactory.buttonInstance(parent, IDialogConstants.CLIENT_ID + 2, "Single Signon", false))
        .thenReturn(singleSignonButton);
        LoginCustomControls underTest = new LoginCustomControls(controlFactory, loginControls);
        underTest.createBarButtons(parent, buttonFactory, loginDialog);
        verify(singleSignonButton).addSelectionListener(ssoAdapter);
        verify(advanced).addSelectionListener(advancedCaptor.capture());
        advancedCaptor.getValue().widgetSelected(mock(SelectionEvent.class));
        verify(loginDialog).exitDialog(Window.CANCEL + 1);
        verify(buttonFactory).buttonInstance(parent, IDialogConstants.OK_ID, "Login", true);
        verify(buttonFactory).buttonInstance(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        verify(loginDialog).resizeDialog();
    }
    
    @Test
    public void test_createButtonsForButtonBar_3_buttons()
    {
        LoginData loginData = mock(LoginData.class); 
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getPassword()).thenReturn(TEST_PASSWORD);
        when(loginData.getSessionDetails()).thenReturn(sessionDetails );
        DialogHandler loginDialog = mock(DialogHandler.class);
        ButtonFactory buttonFactory = mock(ButtonFactory.class);
        Composite parent = mock(Composite.class);
        Button advanced = mock(Button.class);
        when(buttonFactory.buttonInstance(parent, IDialogConstants.CLIENT_ID, "Advanced", false))
        .thenReturn(advanced);
        ArgumentCaptor<SelectionAdapter> advancedCaptor = ArgumentCaptor.forClass(SelectionAdapter.class);
        ControlFactory controlFactory = mock(ControlFactory.class);
        DialogLoginControls loginControls = mock(DialogLoginControls.class);
        when(loginControls.isPasswordMandatory()).thenReturn(true);
        LoginCustomControls underTest = new LoginCustomControls(controlFactory, loginControls);
        underTest.createBarButtons(parent, buttonFactory, loginDialog);
        verify(advanced).addSelectionListener(advancedCaptor.capture());
        advancedCaptor.getValue().widgetSelected(mock(SelectionEvent.class));
        verify(loginDialog).exitDialog(Window.CANCEL + 1);
        verify(buttonFactory).buttonInstance(parent, IDialogConstants.OK_ID, "Login", true);
        verify(buttonFactory).buttonInstance(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        verify(loginDialog).resizeDialog();
    }

    @Test
    public void test_defaultPressed()
    {
        ControlFactory controlFactory = mock(ControlFactory.class);
        DialogLoginControls loginControls = mock(DialogLoginControls.class);
        LoginCustomControls underTest = new LoginCustomControls(controlFactory, loginControls);
        DialogHandler dialogHandler = mock(DialogHandler.class);
        assertThat(underTest.defaultSelect(dialogHandler)).isFalse();
        verify(loginControls).okPressed();
    }
}
