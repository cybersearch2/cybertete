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
import java.text.SimpleDateFormat;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.CustomControls;
import au.com.cybersearch2.controls.CustomDialog;
import au.com.cybersearch2.controls.LabelControl;
import au.com.cybersearch2.controls.TextControl;
import au.com.cybersearch2.dialogs.DialogHandler;

/**
 * X509Controls
 * @author Andrew Bowley
 * 9 May 2016
 */
public class X509Controls extends CustomControls
{
    X509Certificate cert;

    /**
     * Construct X509Controls object
     * @param controlFactory SWT widget factory
     */
    public X509Controls(ControlFactory controlFactory, X509Certificate cert)
    {
        super(controlFactory);
        this.cert = cert;
    }

    /**
     * Create dialog content
     * @param parent Parent composite
     * @return Control object
     */
    @Override
    public Control createControls(Composite parent, DialogHandler dialogHandler) 
    {
        Composite container = controlFactory.compositeInstance(parent);
        container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        container.setLayout(new GridLayout(2, false));
        String subject = cert.getSubjectDN().getName().replaceAll(",", "\n");
        createText(container, "Subject DN", subject);
        String issuer = cert.getIssuerX500Principal().getName().replaceAll(",", "\n");
        createText(container, "Issuer DN", issuer);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        createText(container, "Issued on", dateFormat.format(cert.getNotBefore()));
        createText(container, "Expires on", dateFormat.format(cert.getNotAfter()));
        return container;
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
    protected boolean createBarButtons(Composite parent, CustomDialog.ButtonFactory buttonFactory, DialogHandler dialogHandler) 
    {
        buttonFactory.buttonInstance(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        return true;
    }
    
    /**
     * Create text field
     * @param parent Parent composite
     * @param labelText Label
     * @param value Text to display
     */
    private void createText(Composite parent, String labelText, String value) 
    {
        LabelControl label = new LabelControl(controlFactory, parent, SWT.NONE);
        label.setText(labelText + "   "); // the extra space is due to a bug in
        // font formatting when using css styling
        GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
        gridData.horizontalIndent = 20;
        label.setLayoutData(gridData);

        TextControl text = new TextControl(controlFactory, parent, SWT.MULTI | SWT.WRAP);
        text.setEditable(false);
        GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
        gridData2.horizontalIndent = 0;
        text.setLayoutData(gridData2);
        text.setText(value);
    }

}
