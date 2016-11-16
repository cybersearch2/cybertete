/**
    Copyright (C) 2015  www.cybersearch2.com.au

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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.cybertete.handlers.ConfigNotifier;
import au.com.cybersearch2.cybertete.handlers.LoginConfigEnsemble;
import au.com.cybersearch2.cybertete.model.LoginBean;
import au.com.cybersearch2.cybertete.model.internal.LoginConfig;
import au.com.cybersearch2.cybertete.model.service.ChatAccount;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.service.LoginData;

/**
 * LoginControls
 * Manager of base class which defines and creates all Login controls and on sucessful login, saves the configuration.
 * Shared by Login dialog and view, with some controls optional in dialog (host, port, username, plain sasl).
 * @author Andrew Bowley
 * 26 Nov 2015
     * @see au.com.cybersearch2.cybertete.dialogs.AccountSelectionHandler
 */
public abstract class LoginControls extends LoginControlsBase
{
    /** Login button */
    public static final String LOGIN_BUTTON_TEXT = "Login";

    /** 2-columnn grid layout for both view and dialog. Other composites are used only for dialog. */
    private Composite composite;
    /** Layout for host and port controls */
    Composite hostPortContent;
    /** Flag for host and port control visibility */
    boolean hostPortHidden;
    /** Layout for username control */
    Composite usernameContent;
    /** Flag for username control visibility */
    boolean usernameHidden;
    /** Layout for plain sasl control */
    Composite plainSaslContent;
    /** Flag for plain sasl control visibility */
    boolean plainSaslHidden;
    /** Notifies configuration events */
    ConfigNotifier configNotifier;

    /**
     * Construct LoginControls object
     * @param loginData Container holding information required to log in
     * @param isView Flag set true if login view is being displayed
     */
    public LoginControls(LoginData loginData, boolean isView, ConfigNotifier configNotifier)
    {
        super(loginData, isView);
        this.configNotifier = configNotifier;
    }

    /**
     * Create content
     * @param controlFactory SWT widget factory
     * @param parent Parent composite
     * @return Control object containing all Login controls
     */
    public Control createDialogArea(ControlFactory controlFactory, Composite parent) 
    {
        // Create 2-column layout
        composite = controlFactory.compositeInstance(parent);
        GridLayout layout = new GridLayout(2, false);
        composite.setLayout(layout);
        // Create main controls which are always visible
        createMainControls(controlFactory, parent, composite);
        // Assume is view
        hostPortContent = composite;
        hostPortHidden = false;
        usernameHidden = false;
        plainSaslHidden = false;
        if (!isView)
        {   // Initiate hidden host and port layout
            hostPortHidden = true;
            hostPortContent = controlFactory.compositeInstance(parent);
            hostPortContent.setLayout(new GridLayout(2, false));

            final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
            hostPortContent.setLayoutData(data);

        }
        usernameContent = composite;
        
        if (!isView)
        {   // Initiate hidden username layout
            usernameHidden = true;
            usernameContent = controlFactory.compositeInstance(parent);
            usernameContent.setLayout(new GridLayout(2, false));

            final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
            usernameContent.setLayoutData(data);

        }
        plainSaslContent = composite;
        
        if (!isView)
        {   // Initiate hidden plain sasl layout
            plainSaslHidden = true;
            plainSaslContent = controlFactory.compositeInstance(parent);
            plainSaslContent.setLayout(new GridLayout(2, false));

            final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
            plainSaslContent.setLayoutData(data);

        }
        // Create potentially hidden controls
        createHostPortContent(controlFactory, parent, hostPortContent);
        createUsernameContent(controlFactory, usernameContent);
        createPlainSaslContent(controlFactory, plainSaslContent);

        if (hostPortHidden)
        {
            toggleContent(hostPortContent);
            hostText.setEditable(false);
            portText.setEditable(false);
        }
        if (usernameHidden)
        {
            toggleContent(usernameContent);
            usernameText.setEditable(false);
        }
        if (plainSaslHidden)
        {
            toggleContent(plainSaslContent);
            plainSasl.setEnabled(false);
        }
        // Hide all optional fields if current configuration has no password
        // or single signon applies
        SessionDetails sessionDetails = loginData.getSessionDetails();
        String password = sessionDetails.getPassword();
        boolean noPassword = (password == null) || password.isEmpty();
        if (noPassword || (singleSignonEnabled && sessionDetails.isGssapi()))
            hideAllFields();
        // Populate control which displays configured users identified by JID
        initializeUsers();
        return composite;
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.dialogs.AccountSelectionHandler#showHostPort()
     */
    @Override
    public void showHostPort()
    {
        if (hostPortHidden)
        {
            toggleContent(hostPortContent);
            hostPortHidden = false;
        }
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.dialogs.AccountSelectionHandler#showUsername()
     */
    @Override
    public void showUsername()
    {
        if (usernameHidden)
        {
            toggleContent(usernameContent);
            usernameHidden = false;
        }
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.dialogs.AccountSelectionHandler#showPlainSasl()
     */
    @Override
    public void showPlainSasl()
    {
        if (plainSaslHidden)
        {
            toggleContent(plainSaslContent);
            plainSaslHidden = false;
        }
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.dialogs.AccountSelectionHandler#hideAllFields()
     */
    @Override
    public void hideAllFields()
    {
        //jidText.setVisible(false);
        hostText.setVisible(false);
        portText.setVisible(false);
        usernameText.setVisible(false);
        passwordText.setEnabled(false);
        plainSasl.setVisible(false);
        //accountLabel.setVisible(false);
        // TODO - Why make this label invisible?
        //jidLabel.setVisible(false);
        passwordLabel.setVisible(false);
        hostLabel.setVisible(false);
        portLabel.setVisible(false);
        usernameLabel.setVisible(false);
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.dialogs.AccountSelectionHandler#showAllFields()
     */
    @Override
    public void showAllFields()
    {
        //jidText.setVisible(true);
        hostText.setVisible(true);
        portText.setVisible(true);
        usernameText.setVisible(true);
        passwordText.setEnabled(true);
        plainSasl.setVisible(true);
        //accountLabel.setVisible(true);
        //jidLabel.setVisible(true);
        passwordLabel.setVisible(true);
        hostLabel.setVisible(true);
        portLabel.setVisible(true);
        usernameLabel.setVisible(true);
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.dialogs.LoginControlsBase#applyChanges(au.com.cybersearch2.cybertete.model.LoginBean)
     */
    @Override
    public void applyChanges(LoginBean loginBean)
    {
        super.applyChanges(loginBean);
        LoginConfigEnsemble loginConfigEnsemble = new LoginConfigEnsemble(loginBean, this, !isView);
        configNotifier.applyChanges(loginConfigEnsemble);
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.dialogs.LoginControlsBase#loadKerberosConfig()
     */
    @Override
    public void loadKerberosConfig()
    {
        configNotifier.loadKerberosConfig(this);
    }
 
    /**
     * Event handler for user selection changed
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    @Override
    public void handleEvent(Event event) 
    {
        if (isDirty && loginData.deselectCurrentAccount())
        {
            //  Prevent login!
            isLoginPending = false;
            LoginConfig loginConfig = getLoginConfig(loginData.getSessionDetails().getJid());
            // A handler launches a job to save the configuration.
            applyChanges(loginConfig);
        }
        // Clear password, host and port text controls
        ChatAccount account = getAccount();
        if (account == null)
        {
            passwordText.setText("");
            hostText.setText("");
            portText.setText("");
            plainSasl.setEnabled(true);
            isDirty = false;
            return; // JID is empty, so nothing to do
        }
        if (event.type == SWT.Selection)
            configNotifier.saveLastUser(account.getJid());
        // Disply host and port if host configured
        // otherwise clear host text
        String host = account.getHost();
        hostText.setText(host);
        if (!host.isEmpty())
        {
            int port = account.getPort();
            portText.setText(Integer.toString(port));
            showHostPort();
        }
        else
            portText.setText("");
        // Display username, if configured
        String username = account.getAuthcid();
        usernameText.setText(username);
        if (!username.isEmpty())
            showUsername();
        // Show plain sasl, if configured
        boolean isPlainSasl = account.isPlainSasl(); 
        plainSasl.setSelection(isPlainSasl);
        if (isPlainSasl)
            showPlainSasl();
        // Show concealed password if control enabled
        if (passwordText.isEnabled())
            passwordText.setText(account.getPassword());
        // Show all optional fields unless SSO
        if (!account.isGssapi() && !isGssapi())
            showAllFields();
        isDirty = false;
    }
}
