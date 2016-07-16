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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.junit.Test;

import au.com.cybersearch2.controls.ButtonControl;
import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.LabelControl;
import au.com.cybersearch2.controls.TextControl;
import au.com.cybersearch2.cybertete.handlers.ConfigNotifier;
import au.com.cybersearch2.cybertete.model.ConnectionError;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.service.LoginData;

/**
 * LoginControlsTest
 * @author Andrew Bowley
 * 2 Mar 2016
 */
public class LoginControlsTest
{
    static final String TEST_PASSWORD = "secret";
    static final String TEST_JID = "mickymouse@disney.com";
    private static final String TEST_HOST = "google.talk";
    private static final String TEST_USERNAME = "donald";

    @Test
    public void test_createDialogArea_for_dialog()
    {
        LoginData loginData = mock(LoginData.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getPassword()).thenReturn(TEST_PASSWORD);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        when(loginData.getSessionDetails()).thenReturn(sessionDetails);
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.isAutoLogin()).thenReturn(true);
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        LoginControls loginControls = new LoginControls(loginData, false, configNotifier);
        ControlFactory controlFactory = mock(ControlFactory.class);
        Composite parent = mock(Composite.class);
        Composite composite = mock(Composite.class);
        Composite hostPortContent = mock(Composite.class);
        Composite usernameContent = mock(Composite.class);
        Composite plainSaslContent = mock(Composite.class);
        when(controlFactory.compositeInstance(parent)).thenReturn(composite, hostPortContent, usernameContent, plainSaslContent);
        Label accountLabel = mock(Label.class);
        Label jidLabel = mock(Label.class);
        Combo jidText = mock(Combo.class);
        when(jidText.getItems()).thenReturn(new String[] {});
        when(jidText.indexOf(TEST_JID)).thenReturn(-1, 1);
        Label passwordLabel = mock(Label.class);
        Text passwordText = mock(Text.class);
        Label optionsLabel = mock(Label.class);
        Button autoLoginCheck = mock(Button.class);
        when(controlFactory.buttonInstance(composite, SWT.CHECK)).thenReturn(autoLoginCheck);
        when(controlFactory.comboInstance(composite, SWT.BORDER)).thenReturn(jidText);
        when(controlFactory.textInstance(composite, SWT.BORDER | SWT.PASSWORD)).thenReturn(passwordText);
        when(controlFactory.labelInstance(composite, SWT.NONE)).thenReturn(accountLabel, jidLabel, passwordLabel, optionsLabel);
        Label hostLabel = mock(Label.class);
        Text hostText = mock(Text.class);
        Label portLabel = mock(Label.class);
        Text portText = mock(Text.class);
        when(controlFactory.labelInstance(hostPortContent, SWT.NONE)).thenReturn(hostLabel, portLabel);
        when(controlFactory.textInstance(hostPortContent, SWT.BORDER)).thenReturn(hostText, portText);
        Text usernameText = mock(Text.class);
        Label usernameLabel = mock(Label.class);
        when(controlFactory.labelInstance(usernameContent, SWT.NONE)).thenReturn(usernameLabel);
        when(controlFactory.textInstance(usernameContent, SWT.BORDER)).thenReturn(usernameText);
        Button plainSasl = mock(Button.class);
        when(controlFactory.buttonInstance(plainSaslContent, SWT.CHECK)).thenReturn(plainSasl);
        doToggleContent(parent, hostPortContent, false);
        doToggleContent(parent, usernameContent, false);
        doToggleContent(parent, plainSaslContent, false);

        assertThat(loginControls.createDialogArea(controlFactory, parent)).isEqualTo(composite);
        verify(composite).setLayout(isA(GridLayout.class));
    }

    @Test
    public void test_createDialogArea_for_view()
    {
        LoginData loginData = mock(LoginData.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getPassword()).thenReturn(TEST_PASSWORD);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        when(loginData.getSessionDetails()).thenReturn(sessionDetails);
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.isAutoLogin()).thenReturn(true);
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        LoginControls loginControls = new LoginControls(loginData, true, configNotifier);
        ControlFactory controlFactory = mock(ControlFactory.class);
        Composite parent = mock(Composite.class);
        Composite composite = mock(Composite.class);
        when(controlFactory.compositeInstance(parent)).thenReturn(composite);
        Label accountLabel = mock(Label.class);
        Label jidLabel = mock(Label.class);
        Combo jidText = mock(Combo.class);
        when(jidText.getItems()).thenReturn(new String[] {});
        when(jidText.indexOf(TEST_JID)).thenReturn(-1, 1);
        Label passwordLabel = mock(Label.class);
        Text passwordText = mock(Text.class);
        Label optionsLabel = mock(Label.class);
        Button autoLoginCheck = mock(Button.class);
        Button plainSasl = mock(Button.class);
        when(controlFactory.buttonInstance(composite, SWT.CHECK)).thenReturn(autoLoginCheck, plainSasl);
        when(controlFactory.comboInstance(composite, SWT.BORDER)).thenReturn(jidText);
        when(controlFactory.textInstance(composite, SWT.BORDER | SWT.PASSWORD)).thenReturn(passwordText);
        Label hostLabel = mock(Label.class);
        Label portLabel = mock(Label.class);
        Label usernameLabel = mock(Label.class);
        when(controlFactory.labelInstance(composite, SWT.NONE)).thenReturn(accountLabel, jidLabel, passwordLabel, optionsLabel, hostLabel, portLabel, usernameLabel);
        Text hostText = mock(Text.class);
        Text portText = mock(Text.class);
        Text usernameText = mock(Text.class);
        when(controlFactory.textInstance(composite, SWT.BORDER)).thenReturn(hostText, portText, usernameText);
        loginControls.createDialogArea(controlFactory, parent);
        verify(composite).setLayout(isA(GridLayout.class));
    }
    
    @Test
    public void test_createDialogArea_for_view_no_password()
    {
        LoginData loginData = mock(LoginData.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getPassword()).thenReturn("");
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        when(loginData.getSessionDetails()).thenReturn(sessionDetails);
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.isAutoLogin()).thenReturn(true);
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        LoginControls loginControls = new LoginControls(loginData, true, configNotifier);
        ControlFactory controlFactory = mock(ControlFactory.class);
        Composite parent = mock(Composite.class);
        Composite composite = mock(Composite.class);
        when(controlFactory.compositeInstance(parent)).thenReturn(composite);
        Label accountLabel = mock(Label.class);
        Label jidLabel = mock(Label.class);
        Combo jidText = mock(Combo.class);
        when(jidText.getItems()).thenReturn(new String[] {});
        when(jidText.indexOf(TEST_JID)).thenReturn(-1, 1);
        Label passwordLabel = mock(Label.class);
        Text passwordText = mock(Text.class);
        Label optionsLabel = mock(Label.class);
        Button autoLoginCheck = mock(Button.class);
        Button plainSasl = mock(Button.class);
        when(controlFactory.buttonInstance(composite, SWT.CHECK)).thenReturn(autoLoginCheck, plainSasl);
        when(controlFactory.comboInstance(composite, SWT.BORDER)).thenReturn(jidText);
        when(controlFactory.textInstance(composite, SWT.BORDER | SWT.PASSWORD)).thenReturn(passwordText);
        Label hostLabel = mock(Label.class);
        Label portLabel = mock(Label.class);
        Label usernameLabel = mock(Label.class);
        when(controlFactory.labelInstance(composite, SWT.NONE)).thenReturn(accountLabel, jidLabel, passwordLabel, optionsLabel, hostLabel, portLabel, usernameLabel);
        Text hostText = mock(Text.class);
        Text portText = mock(Text.class);
        Text usernameText = mock(Text.class);
        when(controlFactory.textInstance(composite, SWT.BORDER)).thenReturn(hostText, portText, usernameText);
        loginControls.createDialogArea(controlFactory, parent);
        verify(composite).setLayout(isA(GridLayout.class));
        verify(hostText).setVisible(false);
        verify(portText).setVisible(false);
        verify(usernameText).setVisible(false);
        verify(passwordText).setEnabled(false);
        verify(plainSasl).setVisible(false);
        verify(passwordLabel).setVisible(false);
        verify(hostLabel).setVisible(false);
        verify(portLabel).setVisible(false);
        verify(usernameLabel).setVisible(false);
    }
    
    @Test
    public void test_createDialogArea_for_view_sso()
    {
        LoginData loginData = mock(LoginData.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getPassword()).thenReturn(TEST_PASSWORD);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        when(sessionDetails.isGssapi()).thenReturn(true);
        when(loginData.getSessionDetails()).thenReturn(sessionDetails);
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.isAutoLogin()).thenReturn(true);
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        LoginControls loginControls = new LoginControls(loginData, true, configNotifier);
        loginControls.singleSignonEnabled = true;
        ControlFactory controlFactory = mock(ControlFactory.class);
        Composite parent = mock(Composite.class);
        Composite composite = mock(Composite.class);
        when(controlFactory.compositeInstance(parent)).thenReturn(composite);
        Label accountLabel = mock(Label.class);
        Label jidLabel = mock(Label.class);
        Combo jidText = mock(Combo.class);
        when(jidText.getItems()).thenReturn(new String[] {});
        when(jidText.indexOf(TEST_JID)).thenReturn(-1, 1);
        Label passwordLabel = mock(Label.class);
        Text passwordText = mock(Text.class);
        Label optionsLabel = mock(Label.class);
        Button autoLoginCheck = mock(Button.class);
        Button plainSasl = mock(Button.class);
        when(controlFactory.buttonInstance(composite, SWT.CHECK)).thenReturn(autoLoginCheck, plainSasl);
        when(controlFactory.comboInstance(composite, SWT.BORDER)).thenReturn(jidText);
        when(controlFactory.textInstance(composite, SWT.BORDER | SWT.PASSWORD)).thenReturn(passwordText);
        Label hostLabel = mock(Label.class);
        Label portLabel = mock(Label.class);
        Label usernameLabel = mock(Label.class);
        when(controlFactory.labelInstance(composite, SWT.NONE)).thenReturn(accountLabel, jidLabel, passwordLabel, optionsLabel, hostLabel, portLabel, usernameLabel);
        Text hostText = mock(Text.class);
        Text portText = mock(Text.class);
        Text usernameText = mock(Text.class);
        when(controlFactory.textInstance(composite, SWT.BORDER)).thenReturn(hostText, portText, usernameText);
        loginControls.createDialogArea(controlFactory, parent);
        verify(composite).setLayout(isA(GridLayout.class));
        verify(hostText).setVisible(false);
        verify(portText).setVisible(false);
        verify(usernameText).setVisible(false);
        verify(passwordText).setEnabled(false);
        verify(plainSasl).setVisible(false);
        verify(passwordLabel).setVisible(false);
        verify(hostLabel).setVisible(false);
        verify(portLabel).setVisible(false);
        verify(usernameLabel).setVisible(false);
    }

    @Test
    public void test_showHostPort()
    {
        LoginData loginData = mock(LoginData.class);
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        LoginControls loginControls = new LoginControls(loginData, false, configNotifier);
        loginControls.hostPortHidden = true;
        Composite parent = mock(Composite.class);
        Composite hostPortContent = mock(Composite.class);
        loginControls.hostPortContent = hostPortContent;
        doToggleContent(parent, hostPortContent, true);
        loginControls.showHostPort();
        assertThat(loginControls.hostPortHidden).isFalse();
        verify(hostPortContent).setVisible(true);
        verify(parent).pack();
        loginControls.showHostPort();
        assertThat(loginControls.hostPortHidden).isFalse();
        verify(hostPortContent, times(1)).setVisible(true);
        verify(parent, times(1)).pack();
    }   
    
    @Test
    public void test_showUsername()
    {
        LoginData loginData = mock(LoginData.class);
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        LoginControls loginControls = new LoginControls(loginData, false, configNotifier);
        loginControls.usernameHidden = true;
        Composite parent = mock(Composite.class);
        Composite usernameContent = mock(Composite.class);
        loginControls.usernameContent = usernameContent;
        doToggleContent(parent, usernameContent, true);
        loginControls.showUsername();
        assertThat(loginControls.usernameHidden).isFalse();
        verify(usernameContent).setVisible(true);
        verify(parent).pack();
        loginControls.showUsername();
        assertThat(loginControls.usernameHidden).isFalse();
        verify(usernameContent, times(1)).setVisible(true);
        verify(parent, times(1)).pack();
    }   
    
    @Test
    public void test_showPlainSasl()
    {
        LoginData loginData = mock(LoginData.class);
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        LoginControls loginControls = new LoginControls(loginData, false, configNotifier);
        loginControls.plainSaslHidden = true;
        Composite parent = mock(Composite.class);
        Composite plainSaslContent = mock(Composite.class);
        loginControls.plainSaslContent = plainSaslContent;
        doToggleContent(parent, plainSaslContent, true);
        loginControls.showPlainSasl();
        assertThat(loginControls.plainSaslHidden).isFalse();
        verify(plainSaslContent).setVisible(true);
        verify(parent).pack();
        loginControls.showPlainSasl();
        assertThat(loginControls.plainSaslHidden).isFalse();
        verify(plainSaslContent, times(1)).setVisible(true);
        verify(parent, times(1)).pack();
    }   
 
    @Test
    public void test_showAllFields()
    {
        LoginData loginData = mock(LoginData.class);
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        LoginControls loginControls = new LoginControls(loginData, false, configNotifier);
        loginControls.hostText = mock(TextControl.class);
        loginControls.portText = mock(TextControl.class);
        loginControls.usernameText = mock(TextControl.class);
        loginControls.passwordText = mock(TextControl.class);
        loginControls.plainSasl = mock(ButtonControl.class);
        loginControls.passwordLabel = mock(LabelControl.class);
        loginControls.hostLabel = mock(LabelControl.class);
        loginControls.portLabel = mock(LabelControl.class);
        loginControls.usernameLabel = mock(LabelControl.class);
        loginControls.showAllFields();
        verify(loginControls.hostText).setVisible(true);
        verify(loginControls.portText).setVisible(true);
        verify(loginControls.usernameText).setVisible(true);
        verify(loginControls.passwordText).setEnabled(true);
        verify(loginControls.plainSasl).setVisible(true);
        verify(loginControls.passwordLabel).setVisible(true);
        verify(loginControls.hostLabel).setVisible(true);
        verify(loginControls.portLabel).setVisible(true);
        verify(loginControls.usernameLabel).setVisible(true);
    }

    @Test
    public void test_handleEvent()
    {
        LoginData loginData = mock(LoginData.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getHost()).thenReturn(TEST_HOST);
        when(sessionDetails.getPort()).thenReturn(5222);
        when(sessionDetails.getAuthcid()).thenReturn(TEST_USERNAME);
        when(sessionDetails.isPlainSasl()).thenReturn(true);
        when(sessionDetails.getPassword()).thenReturn(TEST_PASSWORD);
        List<SessionDetails> sessionDetailsList = Collections.singletonList(sessionDetails);
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        LoginControls loginControls = new LoginControls(loginData, false, configNotifier);
        TextControl passwordText = mock(TextControl.class);
        when(passwordText.isEnabled()).thenReturn(true);
        loginControls.passwordText = passwordText;
        TextControl hostText = mock(TextControl.class);
        loginControls.hostText = hostText;
        TextControl portText = mock(TextControl.class);
        loginControls.portText = portText;
        TextControl usernameText = mock(TextControl.class);
        loginControls.usernameText = usernameText;
        ButtonControl plainSasl = mock(ButtonControl.class);
        loginControls.plainSasl = plainSasl;
        UserSelector userSelector = mock(UserSelector.class);
        loginControls.userSelector = userSelector;
        when(userSelector.getText()).thenReturn(TEST_JID);
        loginControls.passwordLabel = mock(LabelControl.class);
        loginControls.hostLabel = mock(LabelControl.class);
        loginControls.portLabel = mock(LabelControl.class);
        loginControls.usernameLabel = mock(LabelControl.class);
        loginControls.connectionError = ConnectionError.noError;
        when(loginData.selectAccount(TEST_JID, ConnectionError.noError)).thenReturn(sessionDetails);
        loginControls.hostPortHidden = true;
        Composite parent = mock(Composite.class);
        Composite hostPortContent = mock(Composite.class);
        loginControls.hostPortContent = hostPortContent;
        doToggleContent(parent, hostPortContent, true);
        loginControls.usernameHidden = true;
        Composite usernameContent = mock(Composite.class);
        loginControls.usernameContent = usernameContent;
        doToggleContent(parent, usernameContent, true);
        loginControls.plainSaslHidden = true;
        Composite plainSaslContent = mock(Composite.class);
        loginControls.plainSaslContent = plainSaslContent;
        doToggleContent(parent, plainSaslContent, true);
        Event event = mock(Event.class);
        loginControls.handleEvent(event);
        verify(hostText).setText(TEST_HOST);
        verify(portText).setText("5222");
        verify(usernameText).setText(TEST_USERNAME);
        verify(plainSasl).setSelection(true);
        verify(passwordText).setText(TEST_PASSWORD);
        verify(hostPortContent).setVisible(true);
        verify(usernameContent).setVisible(true);
        verify(plainSaslContent).setVisible(true);
        verify(parent, times(3)).pack();
        verify(loginControls.hostText).setVisible(true);
        verify(loginControls.portText).setVisible(true);
        verify(loginControls.usernameText).setVisible(true);
        verify(loginControls.passwordText).setEnabled(true);
        verify(loginControls.plainSasl).setVisible(true);
        verify(loginControls.passwordLabel).setVisible(true);
        verify(loginControls.hostLabel).setVisible(true);
        verify(loginControls.portLabel).setVisible(true);
        verify(loginControls.usernameLabel).setVisible(true);
    }
    
    @Test
    public void test_handleEvent_new_sso_session()
    {
        LoginData loginData = mock(LoginData.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getHost()).thenReturn("");
        when(sessionDetails.getAuthcid()).thenReturn("");
        when(sessionDetails.isPlainSasl()).thenReturn(false);
        when(sessionDetails.getPassword()).thenReturn("");
        when(sessionDetails.isGssapi()).thenReturn(true);
        List<SessionDetails> sessionDetailsList = Collections.singletonList(sessionDetails);
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        LoginControls loginControls = new LoginControls(loginData, false, configNotifier);
        TextControl passwordText = mock(TextControl.class);
        when(passwordText.isEnabled()).thenReturn(false);
        loginControls.passwordText = passwordText;
        TextControl hostText = mock(TextControl.class);
        loginControls.hostText = hostText;
        TextControl portText = mock(TextControl.class);
        loginControls.portText = portText;
        TextControl usernameText = mock(TextControl.class);
        loginControls.usernameText = usernameText;
        ButtonControl plainSasl = mock(ButtonControl.class);
        loginControls.plainSasl = plainSasl;
        UserSelector userSelector = mock(UserSelector.class);
        loginControls.userSelector = userSelector;
        when(userSelector.getText()).thenReturn(TEST_JID);
        loginControls.passwordLabel = mock(LabelControl.class);
        loginControls.hostLabel = mock(LabelControl.class);
        loginControls.portLabel = mock(LabelControl.class);
        loginControls.usernameLabel = mock(LabelControl.class);
        loginControls.connectionError = ConnectionError.noError;
        when(loginData.selectAccount(TEST_JID, ConnectionError.noError)).thenReturn(sessionDetails);
        loginControls.hostPortHidden = true;
        Composite hostPortContent = mock(Composite.class);
        loginControls.hostPortContent = hostPortContent;
        loginControls.usernameHidden = true;
        Composite usernameContent = mock(Composite.class);
        loginControls.usernameContent = usernameContent;
        loginControls.plainSaslHidden = true;
        Composite plainSaslContent = mock(Composite.class);
        loginControls.plainSaslContent = plainSaslContent;
        Event event = mock(Event.class);
        loginControls.handleEvent(event);
        verify(hostText).setText("");
        verify(portText).setText("");
        verify(usernameText).setText("");
        verify(plainSasl).setSelection(false);
     }

    @Test
    public void test__handleEvent_empty_jid()
    {
        LoginData loginData = mock(LoginData.class);
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        LoginControls underTest = new LoginControls(loginData, false, configNotifier);
        TextControl passwordText = mock(TextControl.class);
        TextControl hostText = mock(TextControl.class);
        TextControl portText = mock(TextControl.class);
        underTest.passwordText = passwordText;
        underTest.hostText = hostText;
        underTest.portText = portText;
        ButtonControl plainSasl = mock(ButtonControl.class);
        underTest.plainSasl = plainSasl;
        UserSelector userSelector = mock(UserSelector.class);
        underTest.userSelector = userSelector;
        when(userSelector.getText()).thenReturn("");
        Event event = mock(Event.class);
        underTest.handleEvent(event);
        verify(loginData, times(0)).selectAccount(any(String.class), any(ConnectionError.class));
        verify(passwordText).setText("");
        verify(hostText).setText("");
        verify(portText).setText("");
        verify(plainSasl).setEnabled(true);
    }
    
    private void doToggleContent(Composite parent, Composite content, boolean exclude)
    {
        when(content.getParent()).thenReturn(parent);
        // GridData is final
        GridData gridData = new GridData(GridData.END, GridData.CENTER, false, false);
        gridData.exclude = exclude;
        when(content.getLayoutData()).thenReturn(gridData);
    }
}
