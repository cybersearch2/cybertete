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

import static org.jxmpp.util.XmppStringUtils.isBareJid;

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
import au.com.cybersearch2.dialogs.SyncErrorDialog;

/**
 * AddContactControls
 * Renders controls to enter JID and name contact details. 
 * The group is determined by currently selected group in contacts view.
 * @author Andrew Bowley
 * 9 May 2016
 */
public class AddContactControls extends CustomControls
{
    /** Error dialog */
    SyncErrorDialog errorDialog;
    /** JID */
    String jid;
    /** Name */
    String nickname;
    
    TextControl jidText;
    TextControl nicknameText;

    /**
     * Construct AddContactControls object
     * @param controlFactory SWT widget factory
     * @param errorDialog Error dialog
     */
    public AddContactControls(ControlFactory controlFactory, SyncErrorDialog errorDialog)
    {
        super(controlFactory);
        this.errorDialog = errorDialog;
        jid = "";
        nickname = "";
    }

    /**
     * Create dialog content
     * @param parent Parent composite
     * @param dialogHandler Handler for exiting and resizing the dialog
     * @return Control object containing all Login controls
     */
    @Override
    public Control createControls(Composite parent, DialogHandler dialogHandler) 
    {
        Composite composite = controlFactory.compositeInstance(parent);
        GridLayout layout = new GridLayout(2, false);
        composite.setLayout(layout);

        LabelControl userIdLabel = new LabelControl(controlFactory, composite, SWT.NONE);
        userIdLabel.setText("JID:");
        userIdLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
                false, false));

        jidText = new TextControl(controlFactory, composite, SWT.BORDER);
        jidText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
                true, false));

        LabelControl nicknameLabel = new LabelControl(controlFactory, composite, SWT.NONE);
        nicknameLabel.setText("Nickname:");
        nicknameLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
                false, false));

        nicknameText = new TextControl(controlFactory, composite, SWT.BORDER);
        GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
                false);
        gridData.widthHint = controlFactory.convertHeightInCharsToPixels(parent, 20);
        nicknameText.setLayoutData(gridData);

        return composite;
    }

    /**
     * Handle default button pressed
     * @param dialogHandler Handler for exiting and resizing the dialog
     * @return flag set true if dialog should be dismissed
     */
    public boolean defaultPressed(DialogHandler dialogHandler)
    {
        nickname = nicknameText.getText();
        jid = jidText.getText();
        return isValid();
    }

    /**
     * Returns flag set true if JID and name are valid
     * @return boolean
     */
    protected boolean isValid() 
    {
        if (nickname.equals("")) 
        {
            errorDialog.showError("Invalid Nickname",
                    "Nickname field must not be blank.");
            return false;
        }
        if (jid.equals("")) 
        {
            errorDialog.showError("Invalid JID",
                    "JID field must not be blank.");
            return false;
        }
        if (!isBareJid(jid)) 
        {
            errorDialog.showError("Invalid JID",
                    "JID field format incorrect.");
            return false;
        }
        return true;
    }

    /**
     * JID
     * @return String
     */
    public String getJid() 
    {
        return jid;
    }

    /**
     * Name
     * @return String
     */
    public String getNickname() 
    {
        return nickname;
    }
}
