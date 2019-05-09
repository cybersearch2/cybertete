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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

import au.com.cybersearch2.controls.ButtonBar;
import au.com.cybersearch2.controls.ButtonControl;
import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.FileFilter;
import au.com.cybersearch2.controls.FileSelectionControl;
import au.com.cybersearch2.controls.LabelControl;
import au.com.cybersearch2.controls.TextControl;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.service.KeystoreConfig;
import au.com.cybersearch2.cybertete.security.SecurityConfig;
import au.com.cybersearch2.cybertete.security.SecurityConfigController;

/**
 * SecurityControls
 * Security configuration control creation and event handling
 * @author Andrew Bowley
 * 25 Mar 2016
 */
@Creatable
public class SecurityControls
{
    
    LabelControl keystoreLabel;
    TextControl keystoreText;
    LabelControl keystoreTypeLabel;
    List keystoreTypeList;
    LabelControl passwordLabel;
    TextControl passwordText;
    ButtonControl apply;
    ButtonControl browseButton;
    ButtonControl clientCertAuthCheck;

    /** Control to select keystore file with standard dialog */
    @Inject
    FileSelectionControl fileSelectionControl;
    /** Security configuration user interface event handling */
    @Inject
    SecurityConfigController controller;

    /** Client certificate authentication checkbox clicked */
    SelectionAdapter clientCertListener = new SelectionAdapter() {
        
        public void widgetSelected(SelectionEvent event)
        {
            boolean isClientCertAuth = clientCertAuthCheck.getSelection();
            // Update Client Cert Auth control state
            setClientCertAuthEnabled(isClientCertAuth);
            controller.onClientCertSelect();
            apply.setEnabled(true);
       }
        
        public void widgetDefaultSelected(SelectionEvent event){
            widgetSelected(event);
        }
    };

    /** Keystore file browse button cliked */
    SelectionAdapter browseListener = new SelectionAdapter() {
        public void widgetSelected(SelectionEvent event)
        {
            String file = fileSelectionControl.getFilePath("Select Keystore");
            if (file != null)
                keystoreText.setText(file);
        }
        public void widgetDefaultSelected(SelectionEvent event){
            widgetSelected(event);
        }
    };

    /** Keystore type selection clicked */
    SelectionListener keystoreTypeListener = new SelectionListener() {
        public void widgetSelected(SelectionEvent event)
        {
            controller.onKeystoreConfigChange();
            apply.setEnabled(true);
        }
        
        public void widgetDefaultSelected(SelectionEvent event){
            widgetSelected(event);
        }
    };

    /** Apply button clicked */
    SelectionAdapter applyListener = new SelectionAdapter() {
        
        public void widgetSelected(SelectionEvent e) 
        {
            if (!controller.onApply(clientCertAuthCheck.getSelection(), getKeystoreConfig()))
                setKeystoreTextFocus();
        }

    };

    /** OK button clicked */
    SelectionAdapter okListener = new SelectionAdapter() {
        
        public void widgetSelected(SelectionEvent e) 
        {
            controller.onOk(clientCertAuthCheck.getSelection(), getKeystoreConfig());
        }
    };

    /** Cancel button clicked */
    SelectionAdapter cancelListener = new SelectionAdapter() {
        
        public void widgetSelected(SelectionEvent e) 
        {
            controller.onCancel();
        }
    };

    /** Enable Apply button when key pressed */
    KeyListener changeListener = new KeyListener(){

        @Override
        public void keyPressed(KeyEvent e)
        {
            controller.onKeystoreConfigChange();
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
        // Customize control to select keystore file on file system
        FileFilter fileFilter = fileSelectionControl.getFileFilter();
        fileFilter.addName("PKCS12", "pfx", "p12");
        fileFilter.addName("JKS", "jks");
        fileFilter.addName("JCEKS", "jceks");
        fileFilter.addName("Any", "*");
        // Create text, checkbox and label controls
        createDialogArea(composite, controlFactory, parent);
        createButtonBar(controlFactory, parent);
    }

    /**
     * Create Text, Checkbox and Label controls
     * @param composite Layout for controls
     * @param controlFactory SWT widget factory
     * @param parent Parent composite of View
     */
    protected void createDialogArea(Composite composite, ControlFactory controlFactory, Composite parent)
    {
        LabelControl clientLabel = new LabelControl(controlFactory, composite, SWT.NONE);
        clientLabel.setText("Client certificate");
        GridData clientLabelLayout = 
            new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 3, 1);
        clientLabel.setLayoutData(clientLabelLayout);
        clientCertAuthCheck = new ButtonControl(controlFactory, composite, SWT.CHECK);
        clientCertAuthCheck.setText("Login with Certificate");
        clientCertAuthCheck.setSelection(controller.isClientCertAuth());

        clientCertAuthCheck.addSelectionListener(clientCertListener );
        GridData clientCertAuthLayout = 
            new GridData(SWT.BEGINNING, SWT.CENTER, true, true, 3, 1);
        clientCertAuthCheck.setLayoutData(clientCertAuthLayout);
        keystoreLabel = new LabelControl(controlFactory, composite, SWT.NONE);
        keystoreLabel.setText("Keystore:");
        keystoreLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
        keystoreText = new TextControl(controlFactory, composite, SWT.BORDER);
        keystoreText.addKeyListener(changeListener);
        GridData gridData1 = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData1.widthHint = controlFactory.convertHeightInCharsToPixels(parent, 20);
        keystoreText.setLayoutData(gridData1);
        // Add button to browse file system for keystore file
        browseButton = new ButtonControl(controlFactory, composite, SWT.PUSH);
        browseButton.setText("Browse ...");
        browseButton.addSelectionListener(browseListener);
        GridData gridData2 = new GridData(SWT.BEGINNING, SWT.FILL, true, false);
        gridData2.widthHint = controlFactory.convertHeightInCharsToPixels(parent, 8);
        browseButton.setLayoutData(gridData2);
        keystoreTypeLabel = new LabelControl(controlFactory, composite, SWT.NONE);
        keystoreTypeLabel.setText("Keystore Type:");
        keystoreTypeLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
        keystoreTypeList = controlFactory.listInstance(composite, SWT.BORDER | SWT.SINGLE);

        keystoreTypeList.addSelectionListener(keystoreTypeListener);
        GridData gridData3 = new GridData(SWT.BEGINNING, SWT.FILL, true, false, 2, 1);
        gridData3.widthHint = controlFactory.convertHeightInCharsToPixels(parent, 6);
        keystoreTypeList.setLayoutData(gridData3);
        for (String type: SecurityConfig.KEYSTORE_TYPES)
            keystoreTypeList.add(type);
        passwordLabel = new LabelControl(controlFactory, composite, SWT.NONE);
        passwordLabel.setText("Password:");
        passwordLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
        passwordText = new TextControl(controlFactory, composite, SWT.BORDER | SWT.PASSWORD);
        passwordText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        passwordText.addKeyListener(changeListener);
        updateKeystore();
        // Disable Client Cert Auth if not configured
        if (!controller.isClientCertAuth())
            setClientCertAuthEnabled(false);
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
    public void setKeystoreTextFocus()
    {
        keystoreText.setFocus();
    }

    /**
     * Set Apply button enabled state
     * @param enabled boolean
     */
    public void setApplyEnabled(boolean enabled)
    {
        apply.setEnabled(false);
    }

    /**
     * Return keystore configuration
     * @return KeystoreConfig object
     */
    public KeystoreConfig getKeystoreConfig()
    {
        KeystoreConfig securityConfig = null;
        // Use default keystore configuration if keystore file not available
        if (keystoreText.isEnabled())
        {
            String keystorePath = keystoreText.getText();
            String keystoreType = getKeystoreType();
            String password = passwordText.getText();
            securityConfig = new SecurityConfig(keystorePath, keystoreType, password);
        }
        else
            securityConfig = new SecurityConfig();
        return securityConfig;
    }

    /**
     * Validation complete event handler
     * @param proceed Flag set true if validation succeeded
     */
    @Inject @Optional
    void onKestoreConfigDoneHandler(@UIEventTopic(CyberteteEvents.KEYSTORE_CONFIG_DONE) Boolean proceed)
    {
        if (proceed)
        {   // Disable Apply button
            setApplyEnabled(false);
            controller.saveKeystoreConfig(getKeystoreConfig());
        }
        else // Assume user has seen error message
            setKeystoreTextFocus();
    }

    /**
     * Returns keystore type selection
     * @return keystore type
     */
    private String getKeystoreType()
    {
        return keystoreTypeList.getItem(keystoreTypeList.getSelectionIndex());
    }

    /**
     * Enable/disable controls for Clent Cert Auth
     */
    private void setClientCertAuthEnabled(boolean isEnabled)
    {
        keystoreLabel.setEnabled(isEnabled);
        keystoreText.setEnabled(isEnabled);
        browseButton.setEnabled(isEnabled);
        keystoreTypeLabel.setEnabled(isEnabled);
        keystoreTypeList.setEnabled(isEnabled);
        passwordLabel.setEnabled(isEnabled);
        passwordText.setEnabled(isEnabled);
    }
    
    /**
     * Set keystore control values
     */
    private void updateKeystore()
    {
        KeystoreConfig keystoreConfig = controller.getKeystoreConfig();
        keystoreText.setText(keystoreConfig.getKeystoreFile());
        passwordText.setText(keystoreConfig.getKeystorePassword());
        int index = keystoreTypeList.indexOf(keystoreConfig.getKeystoreType());
        if (index < 0)
            index = 0;
        keystoreTypeList.select(index);
    }


}
