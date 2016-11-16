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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.controls.ButtonControl;
import au.com.cybersearch2.controls.TextControl;
import au.com.cybersearch2.cybertete.handlers.ConfigNotifier;
import au.com.cybersearch2.cybertete.handlers.LoginConfigEnsemble;
import au.com.cybersearch2.cybertete.model.LoginBean;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.service.LoginData;
import au.com.cybersearch2.dialogs.DialogHandler;

/**
 * DialogLoginControlsTest
 * @author Andrew Bowley
 * 9 Mar 2016
 */
public class DialogLoginControlsTest
{
    static final String TEST_PASSWORD = "secret";
    private static final String TEST_HOST = "google.talk";
    static final String TEST_JID = "mickymouse@disney.com";
    
    @Test
    public void test_login()
    {
        LoginData loginData = mock(LoginData.class); 
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.isDirty()).thenReturn(false);
        when(loginData.getSessionDetails()).thenReturn(sessionDetails );
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        DialogHandler loginDialog = mock(DialogHandler.class);
        DialogLoginControls dialogLoginControls = new DialogLoginControls(loginData, configNotifier);
        dialogLoginControls.setDialogHandler(loginDialog);
        UserSelector userSelector = mock(UserSelector.class);
        dialogLoginControls.userSelector = userSelector;
        when(userSelector.getText()).thenReturn(TEST_JID);
        TextControl hostText = mock(TextControl.class);
        dialogLoginControls.hostText = hostText;
        when(dialogLoginControls.hostText.getText()).thenReturn(TEST_HOST);
        TextControl portText = mock(TextControl.class);
        dialogLoginControls.portText = portText;
        when(dialogLoginControls.portText.getText()).thenReturn("5222");
        dialogLoginControls.usernameText = mock(TextControl.class);
        dialogLoginControls.passwordText = mock(TextControl.class);
        dialogLoginControls.autoLoginCheck = mock(ButtonControl.class);
        when(dialogLoginControls.autoLoginCheck.getSelection()).thenReturn(false);
        dialogLoginControls.plainSasl = mock(ButtonControl.class);
        dialogLoginControls.login();
        // Logon proceeds when the dialog is dismissed
        verify(loginDialog).dismissDialog();
    }
    
    @Test
    public void test_onResizeDialog()
    {
        LoginData loginData = mock(LoginData.class); 
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        DialogHandler loginDialog = mock(DialogHandler.class);
        DialogLoginControls dialogLoginControls = new DialogLoginControls(loginData, configNotifier);
        dialogLoginControls.setDialogHandler(loginDialog);
        dialogLoginControls.resizeDialog();
        verify(loginDialog).resizeDialog();
    }
   
    @Test
    public void test_applyChanges()
    {
        LoginBean loginBean = mock(LoginBean.class);
        LoginData loginData = mock(LoginData.class); 
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getPassword()).thenReturn(TEST_PASSWORD);
        when(loginData.getSessionDetails()).thenReturn(sessionDetails );
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        DialogLoginControls dialogLoginControls = new DialogLoginControls(loginData, configNotifier);
        dialogLoginControls.applyChanges(loginBean);
        ArgumentCaptor<LoginConfigEnsemble> ensembleCaptor = ArgumentCaptor.forClass(LoginConfigEnsemble.class);
        verify(configNotifier).applyChanges(ensembleCaptor.capture());
        LoginConfigEnsemble loginConfigEnsemble = ensembleCaptor.getValue();
        assertThat(loginConfigEnsemble.getUpdateLoginConfigEvent()).isEqualTo(dialogLoginControls);
        assertThat(loginConfigEnsemble.getLoginBean()).isEqualTo(loginBean);
        assertThat(loginConfigEnsemble.isShowMessage()).isTrue();
    }

    @Test
    public void test_loadKerberosConfig()
    {
        LoginData loginData = mock(LoginData.class); 
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getPassword()).thenReturn(TEST_PASSWORD);
        when(loginData.getSessionDetails()).thenReturn(sessionDetails );
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        DialogLoginControls dialogLoginControls = new DialogLoginControls(loginData, configNotifier);
        dialogLoginControls.loadKerberosConfig();
        verify(configNotifier).loadKerberosConfig(dialogLoginControls);
     }
}
