package au.com.cybersearch2.cybertete.views;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.controls.ButtonBar;
import au.com.cybersearch2.controls.ButtonControl;
import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.cybertete.handlers.ConfigNotifier;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.security.LoginStatus;
import au.com.cybersearch2.cybertete.service.LoginData;

public class ViewLoginControlsTest
{
    static final String TEST_JID = "mickymouse@disney.com";
    static final String TEST_PASSWORD = "secret";
    private static final String TEST_HOST = "google.talk";
    private static final String TEST_USERNAME = "donald";

    @Test
    public void test_constructor()
    {
        Composite parent = mock(Composite.class); 
        Composite composite = mock(Composite.class); 
        ControlFactory controlFactory = mock(ControlFactory.class); 
        when(controlFactory.compositeInstance(parent)).thenReturn(composite);
        LoginData loginData = mock(LoginData.class); 
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        AdvancedLoginController advancedLoginController = mock(AdvancedLoginController.class);
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        ViewLoginControls viewLoginControls = new ViewLoginControls(parent, loginData, controlFactory, configNotifier, advancedLoginController);
        assertThat(viewLoginControls.composite).isEqualTo(composite);
        verify(composite).setLayout(isA(GridLayout.class));
        verify(composite).setLayoutData(isA(GridData.class));
    }

    @Test
    public void test_onOkPressed()
    {
        Composite parent = mock(Composite.class); 
        Composite composite = mock(Composite.class); 
        ControlFactory controlFactory = mock(ControlFactory.class); 
        when(controlFactory.compositeInstance(parent)).thenReturn(composite);
        LoginData loginData = mock(LoginData.class); 
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        AdvancedLoginController advancedLoginController = mock(AdvancedLoginController.class);
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        ViewLoginControls viewLoginControls = new ViewLoginControls(parent, loginData, controlFactory, configNotifier, advancedLoginController);
        viewLoginControls.onOkPressed();
        verify(advancedLoginController).onLogin();
    }
    
    @Test
    public void test_setDirty()
    {
        Composite parent = mock(Composite.class); 
        Composite composite = mock(Composite.class); 
        ControlFactory controlFactory = mock(ControlFactory.class); 
        when(controlFactory.compositeInstance(parent)).thenReturn(composite);
        LoginData loginData = mock(LoginData.class); 
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        AdvancedLoginController advancedLoginController = mock(AdvancedLoginController.class);
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        ViewLoginControls viewLoginControls = new ViewLoginControls(parent, loginData, controlFactory, configNotifier, advancedLoginController);
        ButtonControl apply = mock(ButtonControl.class);
        viewLoginControls.apply = apply;
        viewLoginControls.setDirty();
        verify(apply).setEnabled(true);
    }    

    @Test
    public void test_onUpdateComplete()
    {
        Composite parent = mock(Composite.class); 
        Composite composite = mock(Composite.class); 
        ControlFactory controlFactory = mock(ControlFactory.class); 
        when(controlFactory.compositeInstance(parent)).thenReturn(composite);
        LoginData loginData = mock(LoginData.class);
        List<SessionDetails> sessionDetailsList = Collections.emptyList();
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList);
        AdvancedLoginController advancedLoginController = mock(AdvancedLoginController.class);
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        ViewLoginControls viewLoginControls = new ViewLoginControls(parent, loginData, controlFactory, configNotifier, advancedLoginController);
        ButtonControl apply = mock(ButtonControl.class);
        viewLoginControls.apply = apply;
        viewLoginControls.onUpdateComplete(LoginStatus.noError);
        verify(apply).setEnabled(false);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_createButtonsForButtonBar()
    {
        Composite parent = mock(Composite.class); 
        Composite composite = mock(Composite.class); 
        Composite composite1 = mock(Composite.class); 
        ControlFactory controlFactory = mock(ControlFactory.class); 
        when(controlFactory.compositeInstance(parent)).thenReturn(composite);
        when(controlFactory.compositeInstance(composite)).thenReturn(composite1);
        ButtonBar buttonBar = mock(ButtonBar.class);
        when(controlFactory.buttonBarInstance(composite)).thenReturn(buttonBar);
        Button apply = mock(Button.class);
        ArgumentCaptor<SelectionAdapter> applyCaptor = ArgumentCaptor.forClass(SelectionAdapter.class);
        when(buttonBar.createButton(
                eq(IDialogConstants.CLIENT_ID + 1), 
                eq("Apply"), 
                applyCaptor.capture(),
                eq(false))).thenReturn(apply);
        LoginData loginData = mock(LoginData.class); 
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        when(sessionDetails.getPassword()).thenReturn(TEST_PASSWORD);
        List<SessionDetails> sessionDetailsList = Collections.singletonList(sessionDetails);
        Set<SessionDetails> deletedSessionDetailsSet = Collections.singleton(sessionDetails);
        List<SessionDetails> emptyList = Collections.emptyList(); 
        when(loginData.getSessionDetails()).thenReturn(sessionDetails);
        when(loginData.getAllSessionDetails()).thenReturn(sessionDetailsList, emptyList);
        when(loginData.getDeletedSessions()).thenReturn(deletedSessionDetailsSet);
        AdvancedLoginController advancedLoginController = mock(AdvancedLoginController.class);
        ConfigNotifier configNotifier = mock(ConfigNotifier.class);
        ViewLoginControls viewLoginControls = new ViewLoginControls(parent, loginData, controlFactory, configNotifier, advancedLoginController);
        // Call createDialogArea() to inject mock controls as package access not available
        Combo jidText = mock(Combo.class);
        when(jidText.getText()).thenReturn(TEST_JID);
        when(jidText.getItems()).thenReturn(new String[] {});
        Label accountLabel = mock(Label.class);
        Label jidLabel = mock(Label.class);
        Label passwordLabel = mock(Label.class);
        Text passwordText = mock(Text.class);
        Label optionsLabel = mock(Label.class);
        Button autoLoginCheck = mock(Button.class);
        Button plainSasl = mock(Button.class);
        when(controlFactory.buttonInstance(composite1, SWT.CHECK)).thenReturn(autoLoginCheck, plainSasl);
        when(controlFactory.comboInstance(composite1, SWT.BORDER)).thenReturn(jidText);
        when(controlFactory.textInstance(composite1, SWT.BORDER | SWT.PASSWORD)).thenReturn(passwordText);
        Label hostLabel = mock(Label.class);
        Label portLabel = mock(Label.class);
        Label usernameLabel = mock(Label.class);
        when(controlFactory.labelInstance(composite1, SWT.NONE)).thenReturn(accountLabel, jidLabel, passwordLabel, optionsLabel, hostLabel, portLabel, usernameLabel);
        Text hostText = mock(Text.class);
        Text portText = mock(Text.class);
        Text usernameText = mock(Text.class);
        when(controlFactory.textInstance(composite1, SWT.BORDER)).thenReturn(hostText, portText, usernameText);
        when(hostText.getText()).thenReturn(TEST_HOST);
        when(portText.getText()).thenReturn("5222");
        when(passwordText.getText()).thenReturn(TEST_PASSWORD);
        when(usernameText.getText()).thenReturn(TEST_USERNAME);
        when(autoLoginCheck.getSelection()).thenReturn(true);
        when(plainSasl.getSelection()).thenReturn(true);
        viewLoginControls.createDialogArea();
        viewLoginControls.createButtonsForButtonBar();
        verify(buttonBar).createButton(
                eq(IDialogConstants.CLIENT_ID + 2), 
                eq("Single Signon"), 
                isA(SelectionAdapter.class),
                eq(false));
        ArgumentCaptor<SelectionAdapter> removeCaptor = ArgumentCaptor.forClass(SelectionAdapter.class);
        verify(buttonBar).createButton(
                eq(IDialogConstants.CLIENT_ID), 
                eq("Delete User"), 
                removeCaptor.capture(),
                eq(false));
        SelectionAdapter removeAdapter = removeCaptor.getValue();
        removeAdapter.widgetSelected(mock(SelectionEvent.class));
        verify(loginData).deleteSession(TEST_JID);
        verify(jidText, times(2)).select(0);
        SelectionAdapter applyAdapter = applyCaptor.getValue();
        applyAdapter.widgetSelected(mock(SelectionEvent.class));
        verify(apply).setEnabled(false);
    }
}
