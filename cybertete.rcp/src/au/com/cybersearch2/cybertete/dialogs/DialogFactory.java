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

import java.security.cert.X509Certificate;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.widgets.Shell;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.CustomDialog;
import au.com.cybersearch2.controls.ImageFactory;
import au.com.cybersearch2.dialogs.ProgressDialog;
import au.com.cybersearch2.dialogs.SyncErrorDialog;

/**
 * DialogFactory
 * Creates instances of application-specific dialogs
 * @author Andrew Bowley
 * 11 May 2016
 */
@Creatable
public class DialogFactory
{
    /** SWT widget factory */
    @Inject 
    ControlFactory controlFactory;
    /** Image factory */
    @Inject
    ImageFactory imageFactory;
    /** Runs task in main thread and waits for completion */
    @Inject
    UISynchronize sync;
    /** Displays error dialog */
    @Inject
    SyncErrorDialog errorDialog;

    /**
     * Returns instance of Login dialog
     * @param title Dialog title
     * @param loginControls Dialog content which must be adapted to work with CustomDialog
     * @return CustomDialog object
     */
    public CustomDialog<LoginCustomControls> loginDialogInstance(String title, DialogLoginControls loginControls)
    {
        // Adapt login controls to Custom Dialog
        LoginCustomControls loginCustomControls = new LoginCustomControls(controlFactory, loginControls);
        return controlFactory.customDialogInstance(loginCustomControls, title);
    }

    /**
     * Returns instance of Add Contact dialog
     * @param title Dialog title
     * @return CustomDialog object
     */
    public CustomDialog<AddContactControls> addContactDialogInstance(String title)
    {
        AddContactControls addContactControls = new AddContactControls(controlFactory, errorDialog);
        return controlFactory.customDialogInstance(addContactControls, title);
    }
 
    /**
     * Returns instance of Password dialog
     * @param title Dialog title
     * @return CustomDialog object
     */
    public CustomDialog<PasswordControls> passwordDialog(String title, String name)
    {
        PasswordControls passwordControls = new PasswordControls(controlFactory, name);
        return controlFactory.customDialogInstance(passwordControls, title);
    }

    /**
     * Returns instance of X509 Certificate dialog
     * @param title Dialog title
     * @return CustomDialog object
     */
    public CustomDialog<X509Controls> x509DialogInstance(Shell activeShell, String title, X509Certificate cert)
    {
        X509Controls customControls = new X509Controls(controlFactory, cert);
        return controlFactory.customDialogInstance(activeShell, customControls, title);
    }

    /**
     * Returns instance of Select Presence dialog
     * @param title Dialog title
     * @return CustomDialog object
     */
    public CustomDialog<PresenceControls> presenceDialogInstance(String title)
    {
        PresenceControls presenceControls = new PresenceControls(controlFactory, imageFactory);
        return controlFactory.customDialogInstance(presenceControls, title);
    }

    /**
     * Returns modal dialog to display progress and can be cancelled by the user
     * @return ProgressDialog object
     */
    public ProgressDialog  progressDialogInstance()
    {   
        return new ProgressDialog(controlFactory.progressMonitorDialogInstance(), sync);
    }
}
