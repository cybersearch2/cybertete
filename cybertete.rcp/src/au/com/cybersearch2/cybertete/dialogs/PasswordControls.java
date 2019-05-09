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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.CustomControls;
import au.com.cybersearch2.controls.LabelControl;
import au.com.cybersearch2.controls.TextControl;
import au.com.cybersearch2.dialogs.DialogHandler;
/**
 * PasswordControls
 * Contents of dialog for user to authenticate a login name by entering a password
 * @author Andrew Bowley
 * 30 Apr 2016
 */
public class PasswordControls extends CustomControls
{
    /** Text control for entering password */
    private TextControl passwordText;
    /** Authentication user ID */
    private String authcid;
    /** Password (not required for single signon) */
    private char[] password;

    /**
     * Create PasswordControls object
     * @param controlFactory SWT widget factory
     * @param authcid Authentication name
     */
    public PasswordControls(ControlFactory controlFactory, String authcid)
    {
        super(controlFactory);
        this.authcid = authcid;
    }

    /**
     * createDialogArea
     * @see au.com.cybersearch2.controls.CustomControls#createControls(org.eclipse.swt.widgets.Composite, au.com.cybersearch2.dialogs.DialogHandler)
     */
    @Override
    public Control createControls(Composite parent, DialogHandler dialogHandler) 
    {
        Composite composite = controlFactory.compositeInstance(parent);
        GridLayout layout = new GridLayout(2, false);
        composite.setLayout(layout);

        LabelControl nameLabel = new LabelControl(controlFactory, composite, SWT.NONE);
        nameLabel.setText("Name:");
        nameLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

        TextControl nameText = new TextControl(controlFactory, composite, SWT.BORDER);
        nameText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
        nameText.setText(authcid);
        nameText.setEditable(false);
        nameText.setSystemColors();

        LabelControl passwordLabel = new LabelControl(controlFactory, composite, SWT.NONE);
        passwordLabel.setText("Password:");
        passwordLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

        passwordText = new TextControl(controlFactory, composite, SWT.BORDER |SWT.PASSWORD);
        GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
        gridData.widthHint = controlFactory.convertHeightInCharsToPixels(parent, 20);
        passwordText.setLayoutData(gridData);
        passwordText.setFocus();
        return composite;
    }

    /**
     * Handle default button pressed
     * @param dialogHandler Handler for exiting and resizing the dialog
     * @return flag set true if dialog should be dismissed
     */
    @Override
    public boolean defaultSelect(DialogHandler dialogHandler)
    {
        password = passwordText.getText().toCharArray();
        return true;
    }

    /**
     * Returns password
     * @return char array
     */
    public char[] getPassword() 
    {
        return password;
    }

}
