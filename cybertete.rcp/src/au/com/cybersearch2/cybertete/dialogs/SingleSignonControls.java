/**
    Copyright (C) 2019  www.cybersearch2.com.au

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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import au.com.cybersearch2.controls.ButtonBar;
import au.com.cybersearch2.controls.ButtonControl;
import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.LabelControl;
import au.com.cybersearch2.controls.TextControl;
import au.com.cybersearch2.cybertete.security.SingleSignonController;

/**
 * SingleSignonControls
 * Security configuration control creation and event handling
 * @author Andrew Bowley
 * 25 Mar 2016
 */
@Creatable
public class SingleSignonControls {

    TextControl accountJidText;
    ButtonControl apply;

    /** Security configuration user interface event handling */
    @Inject
    SingleSignonController controller;

    /** Enable Apply button when key pressed */
    KeyListener changeListener = new KeyListener(){

        @Override
        public void keyPressed(KeyEvent e)
        {
            controller.onAccountJidChange();
            apply.setEnabled(true);
       }

        @Override
        public void keyReleased(KeyEvent e)
        {
        }};


    /**
     * postConstruct
     * @param controlFactory SWT widget factory
     * @param parent Parent composite
     */
    @PostConstruct
    public void postConstruct(ControlFactory controlFactory, Composite parent)
    {
        // Create the top level composite for the dialog
        Composite top = controlFactory.compositeInstance(parent);
        GridLayout topLayout = new GridLayout();
        topLayout.marginHeight = 0;
        topLayout.marginWidth = 0;
        topLayout.verticalSpacing = 0;
        top.setLayout(topLayout);
        top.setLayoutData(new GridData(GridData.FILL_BOTH));
        Composite composite = controlFactory.compositeInstance(top);
        GridLayout layout = new GridLayout(3, false);
        composite.setLayout(layout);
        // Create text, checkbox and label controls
        createDialogArea(composite, controlFactory, parent);
        createButtonBar(controlFactory, parent);
    }

    /** Apply button clicked */
    SelectionAdapter applyListener = new SelectionAdapter() {
        
        public void widgetSelected(SelectionEvent e) 
        {
            if (!controller.onApply(accountJidText.getText()))
            	setAccountJidFocus();
        }

    };

    /** OK button clicked */
    SelectionAdapter okListener = new SelectionAdapter() {
        
        public void widgetSelected(SelectionEvent e) 
        {
            controller.onOk(accountJidText.getText());
        }
    };

    /** Cancel button clicked */
    SelectionAdapter cancelListener = new SelectionAdapter() {
        
        public void widgetSelected(SelectionEvent e) 
        {
        	controller.onCancel();
        }
    };

    /**
     * Create Text, Checkbox and Label controls
     * @param composite Layout for controls
     * @param controlFactory SWT widget factory
     * @param parent Parent composite of View
     */
    protected void createDialogArea(Composite composite, ControlFactory controlFactory, Composite parent)
    {
        LabelControl clientLabel = new LabelControl(controlFactory, composite, SWT.NONE);
        clientLabel.setText("Account JID");
        GridData clientLabelLayout = 
            new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 3, 1);
        clientLabel.setLayoutData(clientLabelLayout);
        accountJidText = new TextControl(controlFactory, composite, SWT.BORDER);
        GridData gridData1 = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData1.widthHint = controlFactory.convertHeightInCharsToPixels(parent, 32);
        accountJidText.setLayoutData(gridData1);
        accountJidText.addKeyListener(changeListener);
        String accountJid = controller.getAccountJid();
        if (!accountJid.isEmpty())
        	accountJidText.setText(accountJid);
    }


    /**
     * Create push buttons
     * @param controlFactory SWT widget factory
     * @param parent Layout for button bar
    */
    protected void createButtonBar(ControlFactory controlFactory, Composite parent)
    {
        ButtonBar buttonBar = controlFactory.buttonBarInstance(parent);
        Button applyButton = buttonBar.createButton(
                IDialogConstants.CLIENT_ID + 1, 
                "Apply", 
                applyListener ,
                false);
        apply = new ButtonControl(applyButton);
        apply.setEnabled(false);
        buttonBar.createButton(IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, okListener, true);
        buttonBar.createButton(IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, cancelListener, false);
    }

    /**
     * Set focus on keystore field
     */
    public void setAccountJidFocus()
    {
    	accountJidText.setFocus();
    }

    /**
     * Set Apply button enabled state
     * @param enabled boolean
     */
    public void setApplyEnabled(boolean enabled)
    {
        apply.setEnabled(false);
    }

}
