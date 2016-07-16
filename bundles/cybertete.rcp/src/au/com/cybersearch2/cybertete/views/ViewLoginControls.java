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
package au.com.cybersearch2.cybertete.views;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import au.com.cybersearch2.controls.ButtonBar;
import au.com.cybersearch2.controls.ButtonControl;
import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.cybertete.dialogs.LoginControls;
import au.com.cybersearch2.cybertete.handlers.ConfigNotifier;
import au.com.cybersearch2.cybertete.model.internal.LoginConfig;
import au.com.cybersearch2.cybertete.security.LoginStatus;
import au.com.cybersearch2.cybertete.service.LoginData;

/**
 * ViewLoginControls
 * Extends LoginControls for use in a View.
 * Adds "Apply" and "Delete" buttons. Customizes "Login" button.
 * @author Andrew Bowley
 * 30 Nov 2015
 */
@Creatable
public class ViewLoginControls extends LoginControls
{
    /** SWT widget factory */
    private ControlFactory controlFactory;
    /** Helper object delegate */
    private AdvancedLoginController advancedLoginController;
    Composite composite;
    /** Apply button */
    ButtonControl apply;
    
    /** "Delete User" button pressed */
    SelectionAdapter removeCurrentUserListener = new SelectionAdapter() {
        
        public void widgetSelected(SelectionEvent e) 
        {
            String jid = userSelector.getText();
            if (!jid.isEmpty())
                loginData.deleteSession(jid);
            initializeUsers();
        }
    };

    /** Apply button pressed */
    SelectionAdapter applyListener = new SelectionAdapter() {
        
        public void widgetSelected(SelectionEvent event) 
        {
            LoginConfig loginConfig = getLoginConfig(userSelector.getText());
            applyChanges(loginConfig);
        }
    };

    /** Login button pressed */
    SelectionAdapter loginListener  = new SelectionAdapter() {
        
        public void widgetSelected(SelectionEvent e) 
        {
            okPressed();
        }
    };

    /**
     * @param parent Parent composite
     * @param loginData Container holding information required to log in
     * @param controlFactory SWT widget factory
     * @param configNotifier Post events for login configuration updates 
     * @param advancedLoginController Helper object delegate
     */
    @Inject
    public ViewLoginControls(
        Composite parent, 
        LoginData loginData, 
        ControlFactory controlFactory, 
        ConfigNotifier configNotifier,
        AdvancedLoginController advancedLoginController)
    {
        super(loginData, true /* isView */, configNotifier);
        this.controlFactory = controlFactory;
        this.advancedLoginController = advancedLoginController;
        // Create the top level composite for the login window
        composite = controlFactory.compositeInstance(parent);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.dialogs.LoginControlsBase#onOkPressed()
     */
    @Override
    protected void onOkPressed()
    {   // Delegate login to helper, which shows progress monitor
        // and saves configuration if login succeeds
        advancedLoginController.onLogin();
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.dialogs.LoginControlsBase#setDirty()
     */
    @Override
    protected void setDirty()
    {   
        super.setDirty();
        // Enable "Apply" button if configuration change detected
        if (apply != null)
            apply.setEnabled(true);
    }

    /**
     * @see au.com.cybersearch2.cybertete.dialogs.LoginControlsBase#onUpdateComplete(au.com.cybersearch2.cybertete.security.LoginStatus)
     */
    @Override
    public void onUpdateComplete(LoginStatus loginStatus)
    {
        super.onUpdateComplete(loginStatus);
        // Save configuration if login success. Delegate to helper, which fires 
        // an events to save the configuration and change to default perspective
        if (loginStatus == LoginStatus.noError)
            apply.setEnabled(false);
    }

    /**
     * Creates buttons for view: "Delete User", "Single Signon", "Apply" and "Login"(default) 
     */
    public void createButtonsForButtonBar() 
    {
        ButtonBar buttonBar = controlFactory.buttonBarInstance(composite);
        buttonBar.createButton(
                IDialogConstants.CLIENT_ID, 
                "Delete User", 
                removeCurrentUserListener,
                false);

        buttonBar.createButton(
                IDialogConstants.CLIENT_ID + 2, 
                "Single Signon", 
                getSingleSignonSelectionAdapter(),
                false);
        Button applyButton = buttonBar.createButton(
                IDialogConstants.CLIENT_ID + 1, 
                "Apply", 
                applyListener,
                false);
        apply = new ButtonControl(applyButton);
        apply.setEnabled(false);
        buttonBar.createButton(IDialogConstants.OK_ID, LOGIN_BUTTON_TEXT, loginListener, true);
    }

    /**
     * Create controls for Login
     */
    public void createDialogArea()
    {
        super.createDialogArea(controlFactory, composite);
    }

}
