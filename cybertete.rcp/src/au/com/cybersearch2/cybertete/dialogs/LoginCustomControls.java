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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.CustomControls;
import au.com.cybersearch2.controls.CustomDialog.ButtonFactory;
import au.com.cybersearch2.cybertete.model.ConnectionError;
import au.com.cybersearch2.dialogs.DialogHandler;


/**
 * LoginCustomControls
 * Adapts DialogLoginControls for use with CustomDialog
 * @author Andrew Bowley
 * 10 May 2016
 */
public class LoginCustomControls extends CustomControls
{
    /** LoginControls extended for use in Dialogs */
    DialogLoginControls loginControls;
    
    /**
     * @param controlFactory SWT widget factory
     * @param loginControls LoginControls extended for use in Dialogs
     */
    public LoginCustomControls(ControlFactory controlFactory, DialogLoginControls loginControls)
    {
        super(controlFactory);
        this.loginControls = loginControls;
    }

    /**
     * @see au.com.cybersearch2.controls.CustomControls#createDialogArea(org.eclipse.swt.widgets.Composite, au.com.cybersearch2.dialogs.DialogHandler)
     */
    @Override
    public Control createControls(Composite parent, DialogHandler dialogHandler)
    {
        loginControls.setDialogHandler(dialogHandler);
        return loginControls.createDialogArea(controlFactory, parent);
    }

    /**
     * Create Buttons For Button Bar. 
     * If not overriden, the the dialog will contain default OK and Cancel buttons
     * @param parent Parent composite
     * @param buttonFactory Creates Button Bar buttons
     * @param dialogHandler Handler for exiting and resizing the dialog
     * @return flag set true if custom buttons created
     */
    @Override
    public boolean createBarButtons(Composite parent, ButtonFactory buttonFactory, final DialogHandler dialogHandler) 
    {
        // Advanced button
        Button advancedButton = buttonFactory.buttonInstance(parent,
                IDialogConstants.CLIENT_ID, "Advanced", false);
        advancedButton.addSelectionListener(new SelectionAdapter() {
            
            public void widgetSelected(SelectionEvent e) 
            {
                dialogHandler.exitDialog(Window.CANCEL + 1);
            }
        });
        if (!loginControls.isPasswordMandatory())
        {
            // Single signon button
            Button singleSignonButton = buttonFactory.buttonInstance(parent,
                    IDialogConstants.CLIENT_ID + 2, "Single Signon", false);
            singleSignonButton.addSelectionListener(loginControls.getSingleSignonSelectionAdapter());
        }
        // OK button
        buttonFactory.buttonInstance(parent, IDialogConstants.OK_ID, LoginControls.LOGIN_BUTTON_TEXT, true);
        // Cancel button
        buttonFactory.buttonInstance(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        // Resize
        dialogHandler.resizeDialog();
        return true;
    }

    /**
     * Handle default button pressed
     * @param dialogHandler Handler for exiting and resizing the dialog
     * @return flag set true if dialog should be dismissed
     */
    @Override
    public boolean defaultSelect(DialogHandler dialogHandler)
    {
        loginControls.okPressed();
        return false;
    }

    /**
     * Set connection error to know when to clear password field (ie. on authentication error)
     * @param connectionError The ConnectionError value 
     */
    public void setConnectionError(ConnectionError connectionError)
    {
        loginControls.setConnectionError(connectionError);
    }

}
