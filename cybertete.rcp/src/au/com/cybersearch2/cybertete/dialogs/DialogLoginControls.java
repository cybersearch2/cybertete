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

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;

import au.com.cybersearch2.cybertete.handlers.ConfigNotifier;
import au.com.cybersearch2.cybertete.service.LoginData;
import au.com.cybersearch2.dialogs.DialogHandler;

/**
 * DialogLoginControls
 * Extends LoginControls for use in a Dialog.
 * @author Andrew Bowley
 * 8 Mar 2016
 */
@Creatable
public class DialogLoginControls extends LoginControls
{
    /** Handler for exiting and resizing the dialog */
    DialogHandler dialogHandler;

    /**
     * Construct DialogLoginControls object
     * @param loginData Container holding information required to log in
     * @param configNotifier Post events for login configuration updates 
     */
    @Inject
    public DialogLoginControls(LoginData loginData, ConfigNotifier configNotifier)
    {
        super(loginData, false, configNotifier);
    }

    /**
     * Set handler for exiting and resizing the dialog
     * @param dialogHandler DialogHandler implementation
     */
    public void setDialogHandler(DialogHandler dialogHandler)
    {
        this.dialogHandler = dialogHandler;
    }
    

    protected void dismissDialog()
    {
        // Dismiss dialog and return OK code (at end of processing initiated by Login button pressed)
        dialogHandler.dismissDialog();
    }

    /**
     * Resize dialog
     */
    @Override
    protected void resizeDialog()
    {
        dialogHandler.resizeDialog();
    }

    /**
     * Login button pressed, so dismiss dialog to initiate ChatLoginController
     */
    @Override
    protected void login()
    {
        dismissDialog();
    }

}
