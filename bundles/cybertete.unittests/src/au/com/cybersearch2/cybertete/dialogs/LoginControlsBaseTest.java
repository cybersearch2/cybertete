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
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.junit.Test;

import au.com.cybersearch2.controls.ButtonControl;
import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.TextControl;
import au.com.cybersearch2.cybertete.model.ConnectionError;
import au.com.cybersearch2.cybertete.model.LoginBean;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.security.LoginStatus;
import au.com.cybersearch2.cybertete.service.LoginData;

/**
 * LoginControlsBaseTest
 * @author Andrew Bowley
 * 1 Mar 2016
 */
public class LoginControlsBaseTest
{
    static final String TEST_PASSWORD = "secret";
    static final String TEST_JID = "mickymouse@disney.com";
    static final String TEST_PASSWORD2 = "secret2";
    static final String TEST_JID2 = "adeline@google.com";
    private static final String TEST_HOST = "google.talk";
    private static final String TEST_USERNAME = "donald";
    
    class TestLoginControls extends LoginControlsBase
    {
        public boolean isOnOkPressed;
        public boolean isDirty;
        public int resizeCount;
        public int hideAllFieldsCount;
        public LoginBean loginBean;
        public boolean isLoadKerberos;
        

        TestLoginControls(LoginData loginData, boolean isView)
        {
            super(loginData, isView);
        }

        @Override
        public void onOkPressed()
        {
            isOnOkPressed = true;
        }

        @Override
        public void setDirty()
        {
            isDirty = true;
        }
        
        @Override
        protected void resizeDialog()
        {
            ++resizeCount;
        }
        
        @Override
        public void showUsername()
        {
        }

        @Override
        public void showHostPort()
        {
        }

        @Override
        public void showPlainSasl()
        {
        }

        @Override
        public void showAllFields()
        {
        }

        @Override
        public void hideAllFields()
        {
            ++hideAllFieldsCount;
        }

        @Override
        public void handleEvent(Event event)
        {
        }

        @Override
        public void applyChanges(LoginBean loginBean)
        {
            this.loginBean = loginBean;
        }

        @Override
        public void loadKerberosConfig()
        {
            isLoadKerberos = true;
        }
    }
    
    @Test
    public void test_constructor()
    {
        LoginData loginData = mock(LoginData.class);
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        TestLoginControls underTest = new TestLoginControls(loginData, false);
        assertThat(underTest.singleSignonEnabled).isFalse();
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.isGssapi()).thenReturn(true);
        loginData = mock(LoginData.class);
        sessionDetailsList = Collections.singletonList(sessionDetails);
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        underTest = new TestLoginControls(loginData, false);
        assertThat(underTest.singleSignonEnabled).isTrue();
        loginData = mock(LoginData.class);
        sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.isGssapi()).thenReturn(false);
        sessionDetailsList = Collections.singletonList(sessionDetails);
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        underTest = new TestLoginControls(loginData, false);
        assertThat(underTest.singleSignonEnabled).isFalse();
        loginData = mock(LoginData.class);
        sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.isGssapi()).thenReturn(false);
        sessionDetailsList = Collections.singletonList(sessionDetails);
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        when(loginData.isSingleSignonEnabled()).thenReturn(true);
        underTest = new TestLoginControls(loginData, false);
        assertThat(underTest.singleSignonEnabled).isTrue();
        System.out.println("Success!");
    }
    
    @Test
    public void test_onUpdateComplete()
    {
        LoginData loginData = mock(LoginData.class);
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        TestLoginControls underTest = new TestLoginControls(loginData, false);
        underTest.isLoginPending = true;
        underTest.onUpdateComplete(LoginStatus.noError);
        assertThat(underTest.connectionError).isEqualTo(ConnectionError.noError);
        assertThat(underTest.isOnOkPressed).isTrue();
        underTest = new TestLoginControls(loginData, false);
        TextControl passwordText = mock(TextControl.class);
        underTest.passwordText = passwordText;
        underTest.onUpdateComplete(LoginStatus.invalidPassword);
       verify(passwordText).setFocus();
    }
    
    @Test
    public void test_getAccount()
    {
        LoginData loginData = mock(LoginData.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        List<SessionDetails> sessionDetailsList = Collections.singletonList(sessionDetails);
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        when(loginData.selectAccount(TEST_JID, ConnectionError.noError)).thenReturn(sessionDetails);
        TestLoginControls underTest = new TestLoginControls(loginData, false);
        underTest.connectionError = ConnectionError.noError;
        UserSelector userSelector = mock(UserSelector.class);
        underTest.userSelector = userSelector;
        when(userSelector.getText()).thenReturn(TEST_JID);
        underTest.userSelector = userSelector;
        assertThat(underTest.getAccount()).isEqualTo(sessionDetails);
        userSelector = mock(UserSelector.class);
        underTest.userSelector = userSelector;
        when(userSelector.getText()).thenReturn("");
        assertThat(underTest.getAccount()).isNull();
    }
    
    @Test
    public void test_isGssapi()
    {
        LoginData loginData = mock(LoginData.class);
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        TestLoginControls underTest = new TestLoginControls(loginData, false);
        assertThat(underTest.isGssapi()).isFalse();
        underTest.gssapiPrincipal = TEST_JID;
        assertThat(underTest.isGssapi()).isTrue();
        
    }

    @Test
    public void test_isPasswordMandatory()
    {
        LoginData loginData = mock(LoginData.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getPassword()).thenReturn(TEST_PASSWORD);
        when(loginData.getSessionDetails()).thenReturn(sessionDetails);
        TestLoginControls underTest = new TestLoginControls(loginData, false);
        assertThat(underTest.isPasswordMandatory()).isEqualTo(true);
        underTest.singleSignonEnabled = true;
        assertThat(underTest.isPasswordMandatory()).isEqualTo(false);
        underTest.singleSignonEnabled = false;
        sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getPassword()).thenReturn("");
        when(loginData.getSessionDetails()).thenReturn(sessionDetails);
        assertThat(underTest.isPasswordMandatory()).isEqualTo(false);
    }
    
    @Test
    public void test_createMainControls()
    {
        LoginData loginData = mock(LoginData.class);
        when(loginData.isAutoLogin()).thenReturn(true);
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        TestLoginControls underTest = new TestLoginControls(loginData, false);
        ControlFactory controlFactory = mock(ControlFactory.class);
        Composite parent = mock(Composite.class);
        Composite composite = mock(Composite.class);
        when(composite.getParent()).thenReturn(parent);
        Label accountLabel = mock(Label.class);
        Label jidLabel = mock(Label.class);
        Combo jidText = mock(Combo.class);
        Label passwordLabel = mock(Label.class);
        Text passwordText = mock(Text.class);
        Label optionsLabel = mock(Label.class);
        Button autoLoginCheck = mock(Button.class);
        when(controlFactory.comboInstance(composite, SWT.BORDER)).thenReturn(jidText);
        when(controlFactory.buttonInstance(composite, SWT.CHECK)).thenReturn(autoLoginCheck);
        when(controlFactory.textInstance(composite, SWT.BORDER | SWT.PASSWORD)).thenReturn(passwordText);
        when(controlFactory.labelInstance(composite, SWT.NONE)).thenReturn(accountLabel, jidLabel, passwordLabel, optionsLabel);
        underTest.createMainControls(controlFactory, parent, composite);
        verify(accountLabel).setText("Account details");
        verify(accountLabel).setLayoutData(isA(GridData.class));
        verify(jidLabel).setText("JID:");
        verify(jidLabel).setLayoutData(isA(GridData.class));
        verify(controlFactory).convertHeightInCharsToPixels(parent, 20);
        verify(jidText).addListener(SWT.Modify, underTest);
        verify(jidText).addListener(SWT.Selection, underTest);
        verify(passwordLabel).setText("Password:");
        verify(passwordLabel).setLayoutData(isA(GridData.class));
        verify(passwordText).setLayoutData(isA(GridData.class));
        verify(passwordText).addKeyListener(underTest.keyListener);
        verify(optionsLabel).setText("Options");
        verify(optionsLabel).setLayoutData(isA(GridData.class));
        verify(autoLoginCheck).setText("Login automatically at startup");
        verify(autoLoginCheck).setLayoutData(isA(GridData.class));
        verify(autoLoginCheck).setSelection(true);
        ArgumentCaptor<SelectionListener> listenerCaptor = ArgumentCaptor.forClass(SelectionListener.class);
        verify(autoLoginCheck).addSelectionListener(listenerCaptor.capture());
        listenerCaptor.getValue().widgetSelected(mock(SelectionEvent.class));
        assertThat(underTest.isDirty).isTrue();
   }
    
    @Test
    public void test_createHostPortContent()
    {
        LoginData loginData = mock(LoginData.class);
        when(loginData.isAutoLogin()).thenReturn(true);
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        TestLoginControls underTest = new TestLoginControls(loginData, true);
        ControlFactory controlFactory = mock(ControlFactory.class);
        Composite parent = mock(Composite.class);
        Composite hostPortContent = mock(Composite.class);
        Label hostLabel = mock(Label.class);
        Text hostText = mock(Text.class);
        Label portLabel = mock(Label.class);
        Text portText = mock(Text.class);
        when(portText.getText()).thenReturn("5222");
        when(controlFactory.labelInstance(hostPortContent, SWT.NONE)).thenReturn(hostLabel, portLabel);
        when(controlFactory.textInstance(hostPortContent, SWT.BORDER)).thenReturn(hostText, portText);
        underTest.createHostPortContent(controlFactory, parent, hostPortContent);
        verify(hostLabel).setText("Host:");
        verify(hostLabel).setLayoutData(isA(GridData.class));
        verify(hostText).setLayoutData(isA(GridData.class));
        verify(hostText).addKeyListener(underTest.keyListener);
        verify(portLabel).setText("Port:");
        verify(controlFactory).convertHeightInCharsToPixels(parent, 5);
        verify(portLabel).setLayoutData(isA(GridData.class));
        verify(portText).setLayoutData(isA(GridData.class));
        ArgumentCaptor<VerifyListener> listenerCaptor = ArgumentCaptor.forClass(VerifyListener.class);
        verify(portText).addVerifyListener(listenerCaptor.capture());
        // verify(portText).verifyRange(verifyEvent, 0, 65535);
        // Cannot mock VerifyEvent because it is final
        Event event = new Event();
        event.widget = portText;
        VerifyEvent verifyEvent = new VerifyEvent(event);
        verifyEvent.start = verifyEvent.end = 0;
        verifyEvent.text = "";
        verifyEvent.doit = true;
        listenerCaptor.getValue().verifyText(verifyEvent);
        assertThat(underTest.isDirty).isTrue();
        assertThat(verifyEvent.doit).isTrue();
    }
    
    @Test
    public void test_createUsernameContent()
    {
        LoginData loginData = mock(LoginData.class);
        when(loginData.isAutoLogin()).thenReturn(true);
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        TestLoginControls underTest = new TestLoginControls(loginData, true);
        ControlFactory controlFactory = mock(ControlFactory.class);
        Composite usernameContent = mock(Composite.class);
        Text usernameText = mock(Text.class);
        Label usernameLabel = mock(Label.class);
        when(controlFactory.labelInstance(usernameContent, SWT.NONE)).thenReturn(usernameLabel);
        when(controlFactory.textInstance(usernameContent, SWT.BORDER)).thenReturn(usernameText);
        underTest.createUsernameContent(controlFactory, usernameContent);
        verify(usernameLabel).setText("Username:");
        verify(usernameLabel).setLayoutData(isA(GridData.class));
        verify(usernameText).setLayoutData(isA(GridData.class));
        verify(usernameText).addKeyListener(underTest.keyListener);
    }
    
    @Test
    public void test_createPlainSaslContent()
    {
        LoginData loginData = mock(LoginData.class);
        when(loginData.isAutoLogin()).thenReturn(true);
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        TestLoginControls underTest = new TestLoginControls(loginData, true);
        ControlFactory controlFactory = mock(ControlFactory.class);
        Composite plainSaslContent = mock(Composite.class);
        Button plainSasl = mock(Button.class);
        when(controlFactory.buttonInstance(plainSaslContent, SWT.CHECK)).thenReturn(plainSasl);
        underTest.createPlainSaslContent(controlFactory, plainSaslContent);
        verify(plainSasl).setText("Permit Plain SASL Mechanism");
        verify(plainSasl).setLayoutData(isA(GridData.class));
        ArgumentCaptor<SelectionListener> listenerCaptor = ArgumentCaptor.forClass(SelectionListener.class);
        verify(plainSasl).setSelection(false);
        verify(plainSasl).addSelectionListener(listenerCaptor.capture());
        listenerCaptor.getValue().widgetSelected(mock(SelectionEvent.class));
        assertThat(underTest.isDirty).isTrue();
   }
    
    @Test
    public void test_initializeUsers()
    {
        LoginData loginData = mock(LoginData.class);
        SessionDetails sessionDetails1 = mock(SessionDetails.class);
        when(sessionDetails1.getJid()).thenReturn(TEST_JID);
        when(loginData.getSessionDetails()).thenReturn(sessionDetails1);
        SessionDetails sessionDetails2= mock(SessionDetails.class);
        when(sessionDetails2.getJid()).thenReturn(TEST_JID2);
        List<SessionDetails> sessionDetailsList = new ArrayList<SessionDetails>();
        sessionDetailsList.add(sessionDetails1);
        sessionDetailsList.add(sessionDetails2);
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        List<String> userList = new ArrayList<String>();
        userList.add(TEST_JID);
        userList.add(TEST_JID2);
        when(loginData.getUserList()).thenReturn(userList);
        TestLoginControls underTest = new TestLoginControls(loginData, false);
        
        underTest.connectionError = ConnectionError.noError;
        
        UserSelector userSelector = mock(UserSelector.class);
        underTest.userSelector = userSelector;
        TextControl passwordText = mock(TextControl.class);
        TextControl hostText = mock(TextControl.class);
        TextControl portText = mock(TextControl.class);
        underTest.passwordText = passwordText;
        underTest.hostText = hostText;
        underTest.portText = portText;
        underTest.initializeUsers();
        verify(userSelector).clear();
        verify(passwordText).setText("");
        verify(hostText).setText("");
        verify(portText).setText("");
        verify(userSelector).initializeUsers(userList);
    }

    @Test
    public void test_startSingleSignonConfig()
    {
        LoginData loginData = mock(LoginData.class);
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        TestLoginControls underTest = new TestLoginControls(loginData, false);
        UserSelector userSelector = mock(UserSelector.class);
        underTest.userSelector = userSelector;
        TextControl passwordText = mock(TextControl.class);
        underTest.passwordText = passwordText;
        underTest.startSingleSignonConfig(TEST_JID);
        verify(userSelector).startSingleSignonConfig(TEST_JID);
        // Clear password too
        verify(passwordText).setText("");
   }

    @Test
    public void test_getSingleSignonSelectionAdapter()
    {
        LoginData loginData = mock(LoginData.class);
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        TestLoginControls underTest = new TestLoginControls(loginData, false);
        UserSelector userSelector = mock(UserSelector.class);
        underTest.userSelector = userSelector;
        TextControl passwordText = mock(TextControl.class);
        underTest.passwordText = passwordText;
        SelectionAdapter selectionAdapter = underTest.getSingleSignonSelectionAdapter();
        SelectionEvent event = mock(SelectionEvent.class);
        selectionAdapter.widgetSelected(event);
        assertThat(underTest.hideAllFieldsCount).isEqualTo(1);
        assertThat(underTest.isLoadKerberos).isTrue();
        underTest.onLoadKerberosConfig(TEST_JID);
        verify(userSelector).startSingleSignonConfig(TEST_JID);
        verify(passwordText).setText("");
        verify(userSelector).setFocus();
    }
    
    @Test
    public void test_okPressed()
    {
        LoginData loginData = mock(LoginData.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(loginData.getSessionDetails()).thenReturn(sessionDetails);
        when(sessionDetails.isDirty()).thenReturn(true);
        List<SessionDetails> sessionDetailsList = Collections.singletonList(sessionDetails);
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        TestLoginControls underTest = new TestLoginControls(loginData, false);
        UserSelector userSelector = mock(UserSelector.class);
        underTest.userSelector = userSelector;
        when(userSelector.getText()).thenReturn(TEST_JID);
        setControls(underTest);
        underTest.okPressed();
        assertThat(underTest.isLoginPending).isTrue();
        LoginBean loginConfig = underTest.loginBean;
        assertThat(loginConfig.isAutoLogin()).isTrue();
        assertThat(loginConfig.getGssapiPrincipal()).isEqualTo(TEST_JID2);
        assertThat(loginConfig.getHost()).isEqualTo(TEST_HOST);
        assertThat(loginConfig.getJid()).isEqualTo(TEST_JID);
        assertThat(loginConfig.getPassword()).isEqualTo(TEST_PASSWORD);
        assertThat(loginConfig.isPlainSasl()).isTrue();
        assertThat(loginConfig.getPort()).isEqualTo(5222);
        assertThat(loginConfig.getUsername()).isEqualTo(TEST_USERNAME);
    }

    private void setControls(LoginControlsBase loginControls)
    {
        TextControl hostText = mock(TextControl.class);
        when(hostText.getText()).thenReturn(TEST_HOST);
        loginControls.hostText = hostText;
        TextControl portText = mock(TextControl.class);
        when(portText.getText()).thenReturn("5222");
        loginControls.portText = portText;
        TextControl passwordText = mock(TextControl.class);
        when(passwordText.getText()).thenReturn(TEST_PASSWORD);
        loginControls.passwordText = passwordText;
        TextControl usernameText = mock(TextControl.class);
        when(usernameText.getText()).thenReturn(TEST_USERNAME);
        loginControls.usernameText = usernameText;
        ButtonControl autoLoginCheck = mock(ButtonControl.class);
        when(autoLoginCheck.getSelection()).thenReturn(true);
        loginControls.autoLoginCheck =autoLoginCheck;
        ButtonControl plainSasl = mock(ButtonControl.class);
        when(plainSasl.getSelection()).thenReturn(true);
        loginControls.plainSasl = plainSasl;
        loginControls.gssapiPrincipal = TEST_JID2;
    }
}
