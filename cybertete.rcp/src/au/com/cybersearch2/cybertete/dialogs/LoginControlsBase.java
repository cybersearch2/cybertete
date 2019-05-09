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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import au.com.cybersearch2.controls.ButtonControl;
import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.LabelControl;
import au.com.cybersearch2.controls.TextControl;
import au.com.cybersearch2.cybertete.handlers.LoadKerberosConfigEvent;
import au.com.cybersearch2.cybertete.handlers.UpdateLoginConfigEvent;
import au.com.cybersearch2.cybertete.model.ConnectionError;
import au.com.cybersearch2.cybertete.model.LoginBean;
import au.com.cybersearch2.cybertete.model.internal.LoginConfig;
import au.com.cybersearch2.cybertete.model.service.ChatAccount;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.security.LoginStatus;
import au.com.cybersearch2.cybertete.service.LoginData;

/**
 * LoginControlsBase
 * Defines and creates all Login controls and on sucessful login, saves the configuration
 * @author Andrew Bowley
 * 29 Feb 2016
 */
public abstract class LoginControlsBase implements UpdateLoginConfigEvent, LoadKerberosConfigEvent, AccountSelectionHandler
{
    protected UserSelector userSelector;
    protected TextControl hostText;
    protected TextControl portText;
    protected TextControl usernameText;
    protected TextControl passwordText;
    protected ButtonControl autoLoginCheck;
    protected ButtonControl plainSasl;
    protected ButtonControl singleSignonCheck;
    protected LabelControl accountLabel;
    protected LabelControl optionsLabel;
    protected LabelControl passwordLabel;
    protected LabelControl hostLabel;
    protected LabelControl portLabel;
    protected LabelControl usernameLabel;
    /** Container holding information required to log in */
    protected LoginData loginData;
    /** Flag set true if login is in a View as opposed to a Dialog */
    protected boolean isView;
    /** Flag set true if SSO enabled (therefore password field inactive) */
    protected boolean singleSignonEnabled;
    /** Connection error - "notAuthorized" signals password to be reset */
    ConnectionError connectionError;
    /** Principal for SSO or null if SSO not applicable */
    String gssapiPrincipal;
    /** Flag set true if login is pending and false if only applying changes */
    protected volatile boolean isLoginPending;
    /** Flag set true if entry field keyed */
    volatile boolean isDirty;

    /** Enable Apply button if key pressed */
    protected KeyListener keyListener = new KeyListener(){

        @Override
        public void keyPressed(KeyEvent e)
        {
            // Assumes any key is pressed to make a configuration change
            setDirty();
        }

        @Override
        public void keyReleased(KeyEvent e)
        {
        }
    };

    /** Enable Apply button if selection changed */
    SelectionListener selectionListener = new SelectionAdapter() {
        
        public void widgetSelected(SelectionEvent e) 
        {
            setDirty();
        }
    };
 
    /** Adjust controls when single signon check clicked */
    SelectionAdapter singleSignonListener = new SelectionAdapter() {
        
        public void widgetSelected(SelectionEvent event) 
        {
            boolean isChecked = singleSignonCheck.getSelection();
            if (isChecked) 
            {
            	for (SessionDetails sessionDetails: loginData.getAllSessionDetails()) 
            	{
            		if (isGssapi(sessionDetails))
            		{
            			userSelector.select(sessionDetails.getJid());
            		}
            	}
            	showSingleSignonFields();
            }
            else
            	hideSingleSignonFields();
        }

    };
 
    /** Only allow valid port number values */
    VerifyListener portListener = new VerifyListener() {  
        @Override  
        public void verifyText(VerifyEvent e) 
        {
            portText.verifyRange(e, 0, 65535);
            if (e.doit) // Doit flag set true only if port value is in range
                setDirty();
        }  
    };

    /**
     * Construct LoginControlsBase object
     * @param loginData Container holding information required to log in
     * @param isView Flag set true if login view is being displayed 
     */
    protected LoginControlsBase(LoginData loginData, boolean isView)
    {
        this.loginData = loginData;
        this.isView = isView;
        connectionError = ConnectionError.noError;
        // Set flag for SSO enabled if any JID is configured for SSO
        for (SessionDetails sessionDetails: loginData.getAllSessionDetails())
        {
            if (isGssapi(sessionDetails))
            {
                singleSignonEnabled = true;
                break;
            }
        }
        // SSO enabled also if SSO configured for the application  
        if (!singleSignonEnabled)
            singleSignonEnabled = loginData.isSingleSignonEnabled();
    }

	/**
     * Update saved login configuration
     * @param loginBean Java bean contains values to be persisted from Login dialog/view
     */
    public void applyChanges(LoginBean loginBean)
    {
        isDirty = false;
    }
    
    /**
     * Load Kerberos configuration
     */
    abstract public void loadKerberosConfig();
    
    /**
     * Create controls for fields which are always visible
     * @param controlFactory SWT widget factory
     * @param parent Parent composite
     * @param composite Parent of all Login controls 
     */
    protected void createMainControls(ControlFactory controlFactory, Composite parent, Composite composite) 
    {
        accountLabel = new LabelControl(controlFactory, composite, SWT.NONE);
        accountLabel.setText("Account details");
        GridData accountLayoutLayout = 
            new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1);
        accountLabel.setLayoutData(accountLayoutLayout);
        userSelector = new UserSelector(controlFactory, composite, this);
        passwordLabel = new LabelControl(controlFactory, composite, SWT.NONE);
        passwordLabel.setText("Password:");
        GridData passwordLabelLayout =
            new GridData(SWT.END, SWT.CENTER, false, false);
        passwordLabel.setLayoutData(passwordLabelLayout);
        passwordText = new TextControl(controlFactory, composite, SWT.BORDER | SWT.PASSWORD);
        passwordText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        passwordText.addKeyListener(keyListener);
        optionsLabel = new LabelControl(controlFactory, composite, SWT.NONE);
        optionsLabel.setText("Options");
        GridData optionsLabelLayout =
                new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1);
        optionsLabel.setLayoutData(optionsLabelLayout);
        autoLoginCheck = new ButtonControl(controlFactory, composite, SWT.CHECK);
        autoLoginCheck.setText("Login automatically at startup");
        GridData autoLoginCheckLayout =
                new GridData(SWT.BEGINNING, SWT.CENTER, true, true, 2, 1);    
        autoLoginCheck.setLayoutData(autoLoginCheckLayout);
        autoLoginCheck.setSelection(loginData.isAutoLogin());
        autoLoginCheck.addSelectionListener(selectionListener);
        // Single signon check
        singleSignonCheck = new ButtonControl(controlFactory, composite, SWT.CHECK);
        singleSignonCheck.setText("Login using network account");
        GridData singleSignonLayout =
                new GridData(SWT.BEGINNING, SWT.CENTER, true, true, 2, 1);    
        singleSignonCheck.setLayoutData(singleSignonLayout);
        singleSignonCheck.addSelectionListener(singleSignonListener);
    }

    /**
     * Create controls for host and port, which are only visible if required
     * @param controlFactory SWT widget factory
     * @param parent Parent composite
     * @param hostPortContent Parent of controls to be rendered
     */
    protected void createHostPortContent(ControlFactory controlFactory, Composite parent, Composite hostPortContent) 
    {
        hostLabel = new LabelControl(controlFactory, hostPortContent, SWT.NONE);
        hostLabel.setText("Host:");
        hostLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

        hostText = new TextControl(controlFactory, hostPortContent, SWT.BORDER);
        hostText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        if (isView)
            hostText.addKeyListener(keyListener );
        portLabel = new LabelControl(controlFactory, hostPortContent, SWT.NONE);
        portLabel.setText("Port:");
        portLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

        portText = new TextControl(controlFactory, hostPortContent, SWT.BORDER);
        GridData portGridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, SWT.FILL, true, false);
        portGridData.widthHint = controlFactory.convertHeightInCharsToPixels(parent, 5);
        portText.setLayoutData(portGridData);
        // Add listener to only allow digits to be accepted for port value
        portText.addVerifyListener(portListener);
    }

    /**
     * Create controls for username, which are only visible if required
     * @param controlFactory SWT widget factory
     * @param usernameContent  Parent of controls to be rendered
     */
    protected void createUsernameContent(ControlFactory controlFactory, Composite usernameContent) 
    {
        usernameLabel = new LabelControl(controlFactory, usernameContent, SWT.NONE);
        usernameLabel.setText("Username:");
        usernameLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

        usernameText = new TextControl(controlFactory, usernameContent, SWT.BORDER);
        usernameText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
        if (isView)
            usernameText.addKeyListener(keyListener);
    }
 
    /**
     * Create controls for plain Sasl authentication, which are only visible if required
     * @param controlFactory SWT widget factory
     * @param plainSaslContent  Parent of controls to be rendered
     */
    protected void createPlainSaslContent(ControlFactory controlFactory, Composite plainSaslContent) 
    {
        plainSasl = new ButtonControl(controlFactory, plainSaslContent, SWT.CHECK);
        plainSasl.setText("Permit Plain SASL Mechanism");
        GridData plainSaslLayout = 
            new GridData(SWT.BEGINNING, SWT.CENTER, true, true, 2, 1);
        plainSasl.setLayoutData(plainSaslLayout);
        plainSasl.addSelectionListener(selectionListener);
        plainSasl.setSelection(false);
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.handlers.UpdateLoginConfigEvent#onUpdateComplete(au.com.cybersearch2.cybertete.security.LoginStatus)
     */
    @Override   
    public void onUpdateComplete(LoginStatus loginStatus)
    {
        if (loginStatus == LoginStatus.noError)
        {
            connectionError = ConnectionError.noError;
            if (isLoginPending)
                login();
        }
        else if (loginStatus == LoginStatus.invalidPassword)
            passwordText.setFocus();
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.handlers.LoadKerberosConfigEvent#onLoadKerberosConfig(java.lang.String)
     */
    @Override   
    public void onLoadKerberosConfig(String gssapiPrincipal)
    {
        if (gssapiPrincipal != null)
        {
            this.gssapiPrincipal = gssapiPrincipal;
            startSingleSignonConfig(gssapiPrincipal);
    	    LoginConfig loginConfig = getLoginConfig(userSelector.getText());
          	applyChanges(loginConfig);
        }
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.dialogs.AccountSelectionHandler#getAccount()
     */
    @Override
    public ChatAccount getAccount()
    {   // Check for JID combo empty
        String user = userSelector.getText();
        if (user.isEmpty())
            return null;
        return loginData.selectAccount(user, connectionError);
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.dialogs.AccountSelectionHandler#isGssapi()
     */
    @Override
    public boolean isGssapi()
    {
        return gssapiPrincipal != null;
    }

    /**
     * Returns flag set true if the Single Signon enabled
     * return boolean
     */
    public boolean isSingleSignonEnabled()
    {
        return singleSignonEnabled;
    }

    protected boolean isGssapi(ChatAccount sessionDetails) {
		String singleSignonUser = loginData.getSingleSignonUser();
		return !singleSignonUser.isEmpty() && singleSignonUser.equalsIgnoreCase(sessionDetails.getJid()) ;
	}

    /**
     * Returns flag set true if password is mandatory.
     * True means both password field is not empty and Single Signon does not apply
     * @return boolean
     */
    protected boolean isPasswordMandatory()
    {
        String password = loginData.getSessionDetails().getPassword();
        boolean noPassword = (password == null) || password.isEmpty();
        return !(singleSignonEnabled || noPassword);
    }
 
    /**
     * Set connection error to know when to clear password field (ie. on authentication error)
     * @param connectionError The ConnectionError value 
     */
    public void setConnectionError(ConnectionError connectionError)
    {
        this.connectionError = connectionError;
    }

    /** KeyListener records when an entry field has changed  */
    protected void setDirty()
    {
        isDirty = true;
    }

    /**
     *  Update JID combo using current Login configuration data   
     */
    protected void initializeUsers() 
    {   
        isDirty = false;
        userSelector.clear();
        // Clear password, host and port text controls
        passwordText.setText("");
        hostText.setText("");
        portText.setText("");
        isDirty = false;
        // Add JID set. The first item on the list is the current item.
        // TODO - investigate excluding any pending deletion
        String currentJid = userSelector.initializeUsers(loginData.getUserList());
        loginData.selectAccount(currentJid, ConnectionError.noError);
        if (isGssapi(loginData.getSessionDetails()))
        	singleSignonCheck.setSelection(true);
        
        // Reset variables that may be set during login
        isLoginPending = false;
        gssapiPrincipal = null;
        connectionError = ConnectionError.noError;
   }

    /**
     * Set JID for Single Signon, which is the SSO username + "@".
     * The user is to fill in the host to complete the entry.
     * @param gssapiPrincipal
     */
    protected void startSingleSignonConfig(String gssapiPrincipal)
    {
        userSelector.startSingleSignonConfig(gssapiPrincipal);
        // Clear password too
        passwordText.setText("");
        isDirty = false;
    }

    /**
     * Toggle visibility of controls sharing specified parent.
     * Pack and resize allows dialog to collapse and expand. 
     * @param content Parent composite
     */
    protected void toggleContent(Composite content)
    {
        GridData data = (GridData) content.getLayoutData();
        if (data == null)
        {
        	for (Control child: content.getChildren()) {
                GridData childData = (GridData) content.getLayoutData();
                if (childData != null) {
                	childData.exclude = !childData.exclude;
                	child.setVisible(!childData.exclude);
                	child.getParent().pack();
                }
        	}
        }
        else 
        {
	        data.exclude = !data.exclude;
	        content.setVisible(!data.exclude);
	        content.getParent().pack();
        }
        resizeDialog();
    }

    /**
     * Placeholder for override to resize dialog
     */
    protected void resizeDialog()
    {
    }

    /**
     * Handle OK button pressed. Apply changes.
     */
    protected void okPressed() 
    {
        // Set isLoginPending true to get login to proceed after changes applied
        isLoginPending = true;
        if (singleSignonCheck.getSelection())
        	startSingleSignon();
        else
        {
    	    LoginConfig loginConfig = getLoginConfig(userSelector.getText());
        	applyChanges(loginConfig);
        }
    }
 
	/**
     * Ultimate response to Login button pressed
     */
    protected abstract void login();

    /**
     * Returns object containing Login Configuration and controls for
     * saving the configuration
     * @return LoginConfig object
     */
    protected LoginConfig getLoginConfig(String jid)
    {
        LoginConfig loginConfig = new LoginConfig();
        loginConfig.setJid(jid);
        loginConfig.setHost(hostText.getText());
        String port = portText.getText();
        if (port.isEmpty())
            loginConfig.setPort(0);
        else
            loginConfig.setPort(Integer.parseInt(port));
        loginConfig.setUsername(usernameText.getText());
        loginConfig.setPassword(passwordText.getText());
        loginConfig.setAutoLogin(autoLoginCheck.getSelection());
        loginConfig.setPlainSasl(plainSasl.getSelection());
        loginConfig.setGssapiPrincipal(gssapiPrincipal);
        return loginConfig;
    }
 
    private void startSingleSignon() {
        if (gssapiPrincipal != null)
            onLoadKerberosConfig(gssapiPrincipal);
        else
            loadKerberosConfig();
	}

}
