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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.controls.ButtonBar;
import au.com.cybersearch2.controls.ButtonControl;
import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.FileFilter;
import au.com.cybersearch2.controls.FileSelectionControl;
import au.com.cybersearch2.controls.LabelControl;
import au.com.cybersearch2.controls.TextControl;
import au.com.cybersearch2.cybertete.model.service.KeystoreConfig;
import au.com.cybersearch2.cybertete.security.SecurityConfig;
import au.com.cybersearch2.cybertete.security.SecurityConfigController;

/**
 * SecurityControlsTest
 * @author Andrew Bowley
 * 7 May 2016
 */
public class SecurityControlsTest
{
    static final String KEYSTORE = "/dir/keystore.jks";
    static final String KEYSTORE_TYPE = SecurityConfig.KEYSTORE_TYPES[1];
    static final String PASSWORD = "changeit";
    
    SecurityControls underTest;
    ControlFactory controlFactory;
    Composite parent;
    SecurityConfigController controller;
    LabelControl keystoreLabel;
    TextControl keystoreText;
    LabelControl keystoreTypeLabel;
    List keystoreTypeList;
    LabelControl passwordLabel;
    TextControl passwordText;
    FileSelectionControl fileSelectionControl;
    ButtonControl apply;
    ButtonControl browseButton;
    ButtonControl clientCertAuthCheck;
    
    @Before
    public void setUp()
    {
        controlFactory = mock(ControlFactory.class);
        parent = mock(Composite.class);
        controller = mock(SecurityConfigController.class);
        underTest = new SecurityControls();
        underTest.controller = controller;
        keystoreLabel = mock(LabelControl.class);
        underTest.keystoreLabel = keystoreLabel;
        keystoreText = mock(TextControl.class);
        underTest.keystoreText = keystoreText;
        keystoreTypeLabel = mock(LabelControl.class);
        underTest.keystoreTypeLabel = keystoreTypeLabel;
        keystoreTypeList = mock(List.class);
        underTest.keystoreTypeList = keystoreTypeList;
        passwordLabel = mock(LabelControl.class);
        underTest.passwordLabel = passwordLabel;
        passwordText = mock(TextControl.class);
        underTest.passwordText =passwordText;
        fileSelectionControl = mock(FileSelectionControl.class);
        underTest.fileSelectionControl = fileSelectionControl;
        
        apply = mock(ButtonControl.class); 
        underTest.apply = apply;
        browseButton = mock(ButtonControl.class);
        underTest.browseButton = browseButton;
        clientCertAuthCheck = mock(ButtonControl.class);
        underTest.clientCertAuthCheck = clientCertAuthCheck;
    }
 
    @Test
    public void test_postConstruct()
    {
        Button clientCertAuthCheck = mock(Button.class);
        Button browseButton = mock(Button.class);
        Text keystoreText = mock(Text.class);
        Text passwordText = mock(Text.class);
        underTest.controller = null;
        Label keystoreLabel = mock(Label.class);
        underTest.keystoreText = null;
        Label keystoreTypeLabel = mock(Label.class);
        underTest.keystoreTypeList = null;
        Label passwordLabel = mock(Label.class);
        underTest.passwordText =null;
        underTest.fileSelectionControl = null;
        underTest.apply = null;
        underTest.browseButton = null;
        underTest.clientCertAuthCheck = null;
        when(controller.isClientCertAuth()).thenReturn(true);
        Composite top = mock(Composite.class);
        when(controlFactory.compositeInstance(parent)).thenReturn(top);
        Composite composite = mock(Composite.class);
        when(controlFactory.compositeInstance(top)).thenReturn(composite);
        Label clientLabel = mock(Label.class);
        when(controlFactory.labelInstance(composite, SWT.NONE)).thenReturn(clientLabel, keystoreLabel, keystoreTypeLabel, passwordLabel);
        when(controlFactory.buttonInstance(composite, SWT.CHECK)).thenReturn(clientCertAuthCheck);
        when(controlFactory.textInstance(composite, SWT.BORDER)).thenReturn(keystoreText);
        when(controlFactory.textInstance(composite, SWT.BORDER | SWT.PASSWORD)).thenReturn(passwordText);
        when(controlFactory.listInstance(composite, SWT.BORDER | SWT.SINGLE)).thenReturn(keystoreTypeList);
        when(controlFactory.buttonInstance(composite, SWT.PUSH)).thenReturn(browseButton);
        ButtonBar buttonBar = mock(ButtonBar.class);
        when(controlFactory.buttonBarInstance(parent)).thenReturn(buttonBar);
        when(buttonBar.createButton(
                IDialogConstants.CLIENT_ID + 1, 
                "Apply", 
                underTest.applyListener,
                false)).thenReturn(mock(Button.class));
        when(controller.getKeystoreConfig()).thenReturn(new SecurityConfig(KEYSTORE, KEYSTORE_TYPE, PASSWORD));
        when(keystoreTypeList.indexOf(SecurityConfig.KEYSTORE_TYPES[1])).thenReturn(1);
        FileSelectionControl fileSelectionControl = mock(FileSelectionControl.class);
        FileFilter fileFilter = mock(FileFilter.class);
        when(fileSelectionControl.getFileFilter()).thenReturn(fileFilter );
        underTest.fileSelectionControl = fileSelectionControl;
        underTest.controller = controller;
        underTest.postConstruct(controlFactory, parent);
        verify(top).setLayout(isA(GridLayout.class));
        verify(top).setLayoutData(isA(GridData.class));
        verify(composite).setLayout(isA(GridLayout.class));
        verify(fileFilter).addName("PKCS12", "pfx", "p12");
        verify(fileFilter).addName("JKS", "jks");
        verify(fileFilter).addName("JCEKS", "jceks");
        verify(fileFilter).addName("Any", "*");
        verify(clientLabel).setText("Client certificate");
        verify(clientLabel).setLayoutData(isA(GridData.class));
        verify(clientCertAuthCheck).setText("Login with Certificate");
        verify(clientCertAuthCheck).setSelection(true);
        verify(clientCertAuthCheck).addSelectionListener(underTest.clientCertListener);
        verify(clientCertAuthCheck).setLayoutData(isA(GridData.class));
        verify(keystoreLabel).setText("Keystore:");
        verify(keystoreLabel).setLayoutData(isA(GridData.class));
        verify(keystoreText).addKeyListener(underTest.changeListener);
        verify(keystoreText).setLayoutData(isA(GridData.class));
        verify(browseButton).setText("Browse ...");
        verify(browseButton).addSelectionListener(underTest.browseListener);
        verify(browseButton).setLayoutData(isA(GridData.class));
        verify(keystoreTypeLabel).setText("Keystore Type:");
        verify(keystoreTypeLabel).setLayoutData(isA(GridData.class));
        verify(keystoreTypeList).addSelectionListener(underTest.keystoreTypeListener);
        verify(keystoreTypeList).setLayoutData(isA(GridData.class));
        for (String type: SecurityConfig.KEYSTORE_TYPES)
            verify(keystoreTypeList).add(type);
        verify(passwordLabel).setText("Password:");
        verify(passwordLabel).setLayoutData(isA(GridData.class));
        verify(passwordText).setLayoutData(isA(GridData.class));
        verify(passwordText).addKeyListener(underTest.changeListener);
        verify(keystoreText).setText(KEYSTORE);
        verify(passwordText).setText(PASSWORD);
        verify(keystoreTypeList).select(1);
        verify(buttonBar).createButton(IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, underTest.okListener, true);
        verify(buttonBar).createButton(IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, underTest.cancelListener, false);
    }
   
    @Test
    public void test_postConstruct_not_client_cert_auth()
    {
        Button clientCertAuthCheck = mock(Button.class);
        Button browseButton = mock(Button.class);
        Text keystoreText = mock(Text.class);
        Text passwordText = mock(Text.class);
        underTest.controller = null;
        underTest.controller = null;
        Label keystoreLabel = mock(Label.class);
        underTest.keystoreText = null;
        Label keystoreTypeLabel = mock(Label.class);
        underTest.keystoreTypeList = null;
        Label passwordLabel = mock(Label.class);
        underTest.passwordText =null;
        underTest.fileSelectionControl = null;
        underTest.apply = null;
        underTest.browseButton = null;
        underTest.clientCertAuthCheck = null;
        when(controller.isClientCertAuth()).thenReturn(false);
        Composite top = mock(Composite.class);
        when(controlFactory.compositeInstance(parent)).thenReturn(top);
        Composite composite = mock(Composite.class);
        when(controlFactory.compositeInstance(top)).thenReturn(composite);
        Label clientLabel = mock(Label.class);
        when(controlFactory.labelInstance(composite, SWT.NONE)).thenReturn(clientLabel, keystoreLabel, keystoreTypeLabel, passwordLabel);
        when(controlFactory.buttonInstance(composite, SWT.CHECK)).thenReturn(clientCertAuthCheck);
        when(controlFactory.textInstance(composite, SWT.BORDER)).thenReturn(keystoreText);
        when(controlFactory.textInstance(composite, SWT.BORDER | SWT.PASSWORD)).thenReturn(passwordText);
        when(controlFactory.listInstance(composite, SWT.BORDER | SWT.SINGLE)).thenReturn(keystoreTypeList);
        when(controlFactory.buttonInstance(composite, SWT.PUSH)).thenReturn(browseButton);
        ButtonBar buttonBar = mock(ButtonBar.class);
        when(controlFactory.buttonBarInstance(parent)).thenReturn(buttonBar);
        when(buttonBar.createButton(
                IDialogConstants.CLIENT_ID + 1, 
                "Apply", 
                underTest.applyListener ,
                false)).thenReturn(mock(Button.class));
        when(controller.getKeystoreConfig()).thenReturn(new SecurityConfig(KEYSTORE, KEYSTORE_TYPE, PASSWORD));
        when(keystoreTypeList.indexOf(SecurityConfig.KEYSTORE_TYPES[1])).thenReturn(1);
        FileSelectionControl fileSelectionControl = mock(FileSelectionControl.class);
        FileFilter fileFilter = mock(FileFilter.class);
        when(fileSelectionControl.getFileFilter()).thenReturn(fileFilter );
        underTest.fileSelectionControl = fileSelectionControl;
        underTest.controller = controller;
        underTest.postConstruct(controlFactory, parent);
        verify(top).setLayout(isA(GridLayout.class));
        verify(top).setLayoutData(isA(GridData.class));
        verify(composite).setLayout(isA(GridLayout.class));
        verify(clientLabel).setText("Client certificate");
        verify(clientLabel).setLayoutData(isA(GridData.class));
        verify(clientCertAuthCheck).setText("Login with Certificate");
        verify(clientCertAuthCheck).setSelection(false);
        verify(clientCertAuthCheck).addSelectionListener(underTest.clientCertListener);
        verify(clientCertAuthCheck).setLayoutData(isA(GridData.class));
        verify(keystoreLabel).setText("Keystore:");
        verify(keystoreLabel).setLayoutData(isA(GridData.class));
        verify(keystoreText).addKeyListener(underTest.changeListener);
        verify(keystoreText).setLayoutData(isA(GridData.class));
        verify(browseButton).setText("Browse ...");
        verify(browseButton).addSelectionListener(underTest.browseListener);
        verify(browseButton).setLayoutData(isA(GridData.class));
        verify(keystoreTypeLabel).setText("Keystore Type:");
        verify(keystoreTypeLabel).setLayoutData(isA(GridData.class));
        verify(keystoreTypeList).addSelectionListener(underTest.keystoreTypeListener);
        verify(keystoreTypeList).setLayoutData(isA(GridData.class));
        for (String type: SecurityConfig.KEYSTORE_TYPES)
            verify(keystoreTypeList).add(type);
        verify(passwordLabel).setText("Password:");
        verify(passwordLabel).setLayoutData(isA(GridData.class));
        verify(passwordText).setLayoutData(isA(GridData.class));
        verify(passwordText).addKeyListener(underTest.changeListener);
        verify(keystoreLabel).setEnabled(false);
        verify(keystoreTypeLabel).setEnabled(false);
        verify(passwordLabel).setEnabled(false);
        verify(keystoreText).setEnabled(false);
        verify(passwordText).setEnabled(false);
        verify(browseButton).setEnabled(false);
        verifyEnableClientCertAuth(false);
        verify(keystoreText).setText(KEYSTORE);
        verify(passwordText).setText(PASSWORD);
        verify(keystoreTypeList).select(1);
        verify(buttonBar).createButton(IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, underTest.okListener, true);
        verify(buttonBar).createButton(IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, underTest.cancelListener, false);
    }
    
    @Test
    public void test_clientCertListener_true()
    {
        SelectionEvent e = mock(SelectionEvent.class);
        when(clientCertAuthCheck.getSelection()).thenReturn(true);
        underTest.clientCertListener.widgetSelected(e);
        verify(keystoreLabel).setEnabled(true);
        verify(keystoreTypeLabel).setEnabled(true);
        verify(passwordLabel).setEnabled(true);
        verify(keystoreText).setEnabled(true);
        verify(passwordText).setEnabled(true);
        verify(browseButton).setEnabled(true);
        verifyEnableClientCertAuth(true);
        verify(apply).setEnabled(true);
        verify(controller).onClientCertSelect();
    }
    
    @Test
    public void test_clientCertListener_false()
    {
        SelectionEvent e = mock(SelectionEvent.class);
        when(clientCertAuthCheck.getSelection()).thenReturn(false);
        underTest.clientCertListener.widgetSelected(e);
        verify(keystoreLabel).setEnabled(false);
        verify(keystoreTypeLabel).setEnabled(false);
        verify(passwordLabel).setEnabled(false);
        verify(keystoreText).setEnabled(false);
        verify(passwordText).setEnabled(false);
        verify(browseButton).setEnabled(false);
        verifyEnableClientCertAuth(false);
        verify(apply).setEnabled(true);
        verify(controller).onClientCertSelect();
    }

    @Test
    public void test_browseListener()
    {
        when(fileSelectionControl.getFilePath("Select Keystore")).thenReturn(KEYSTORE);
        SelectionEvent e = mock(SelectionEvent.class);
        underTest.browseListener.widgetSelected(e);
        verify(keystoreText).setText(KEYSTORE);
    }
    
    @Test
    public void test_keystoreTypeListener()
    {
        SelectionEvent e = mock(SelectionEvent.class);
        when(keystoreTypeList.getSelectionIndex()).thenReturn(1);
        when(keystoreTypeList.getItem(1)).thenReturn(KEYSTORE_TYPE);
        underTest.keystoreTypeListener.widgetSelected(e);
        verify(controller).onKeystoreConfigChange();;
        verify(apply).setEnabled(true);
    }

    @Test
    public void test_applyListener()
    {
        SelectionEvent e = mock(SelectionEvent.class);
        when(clientCertAuthCheck.getSelection()).thenReturn(true);
        when(keystoreText.getText()).thenReturn(KEYSTORE);
        when(keystoreTypeList.getSelectionIndex()).thenReturn(1);
        when(keystoreTypeList.getItem(1)).thenReturn(KEYSTORE_TYPE);
        when(passwordText.getText()).thenReturn(PASSWORD);
        when(keystoreText.isEnabled()).thenReturn(true);
        underTest.applyListener.widgetSelected(e);
        ArgumentCaptor<KeystoreConfig> ksConfigCaptor = ArgumentCaptor.forClass(KeystoreConfig.class);
        verify(controller).onApply(eq(true), ksConfigCaptor.capture());
        KeystoreConfig keystoreConfig = ksConfigCaptor.getValue();
        assertThat(keystoreConfig.getKeystoreFile()).isEqualTo(KEYSTORE);
        assertThat(keystoreConfig.getKeystorePassword()).isEqualTo(PASSWORD);
        assertThat(keystoreConfig.getKeystoreType()).isEqualTo(KEYSTORE_TYPE);
    }
    
    @Test
    public void test_applyListener_default_ssl()
    {
        SelectionEvent e = mock(SelectionEvent.class);
        when(clientCertAuthCheck.getSelection()).thenReturn(false);
        when(keystoreText.isEnabled()).thenReturn(false);
        underTest.applyListener.widgetSelected(e);
        ArgumentCaptor<KeystoreConfig> ksConfigCaptor = ArgumentCaptor.forClass(KeystoreConfig.class);
        verify(controller).onApply(eq(false), ksConfigCaptor.capture());
        KeystoreConfig keystoreConfig = ksConfigCaptor.getValue();
        assertThat(keystoreConfig.getKeystoreFile()).isNull();
        assertThat(keystoreConfig.getKeystorePassword()).isNull();
        assertThat(keystoreConfig.getKeystoreType()).isNull();
    }
 
    @Test
    public void test_okListener()
    {
        SelectionEvent e = mock(SelectionEvent.class);
        when(clientCertAuthCheck.getSelection()).thenReturn(true);
        when(keystoreText.getText()).thenReturn(KEYSTORE);
        when(keystoreTypeList.getSelectionIndex()).thenReturn(1);
        when(keystoreTypeList.getItem(1)).thenReturn(KEYSTORE_TYPE);
        when(passwordText.getText()).thenReturn(PASSWORD);
        when(keystoreText.isEnabled()).thenReturn(true);
        underTest.okListener.widgetSelected(e);
        ArgumentCaptor<KeystoreConfig> ksConfigCaptor = ArgumentCaptor.forClass(KeystoreConfig.class);
        verify(controller).onOk(eq(true), ksConfigCaptor.capture());
        KeystoreConfig keystoreConfig = ksConfigCaptor.getValue();
        assertThat(keystoreConfig.getKeystoreFile()).isEqualTo(KEYSTORE);
        assertThat(keystoreConfig.getKeystorePassword()).isEqualTo(PASSWORD);
        assertThat(keystoreConfig.getKeystoreType()).isEqualTo(KEYSTORE_TYPE);
    }
    
    @Test
    public void test_okListener_default_ssl()
    {
        SelectionEvent e = mock(SelectionEvent.class);
        when(clientCertAuthCheck.getSelection()).thenReturn(false);
        when(keystoreText.isEnabled()).thenReturn(false);
        underTest.okListener.widgetSelected(e);
        ArgumentCaptor<KeystoreConfig> ksConfigCaptor = ArgumentCaptor.forClass(KeystoreConfig.class);
        verify(controller).onOk(eq(false), ksConfigCaptor.capture());
        KeystoreConfig keystoreConfig = ksConfigCaptor.getValue();
        assertThat(keystoreConfig.getKeystoreFile()).isNull();
        assertThat(keystoreConfig.getKeystorePassword()).isNull();
        assertThat(keystoreConfig.getKeystoreType()).isNull();
    }
 
    @Test
    public void test_cancelListener()
    {
        SelectionEvent e = mock(SelectionEvent.class);
        underTest.cancelListener.widgetSelected(e);
        verify(controller).onCancel();
    }
    
    @Test
    public void test_changeListener()
    {
        KeyEvent e = mock(KeyEvent.class);
        underTest.changeListener.keyPressed(e);
        verify(controller).onKeystoreConfigChange();
        verify(apply).setEnabled(true);
    }
    
    private void verifyEnableClientCertAuth(boolean isEnabled)
    {
        verify(keystoreTypeList).setEnabled(isEnabled);
    }

}
