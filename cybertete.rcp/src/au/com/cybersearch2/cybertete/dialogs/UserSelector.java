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

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import au.com.cybersearch2.controls.ComboControl;
import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.LabelControl;

/**
 * UserSelector
 * @author Andrew Bowley
 * 18 May 2016
 */
public class UserSelector 
{
    protected ComboControl jidText;
    protected LabelControl jidLabel;

    /**
     * Create UserSelector
     * @param controlFactory SWT widget factory
     * @param composite Parent of all controls 
     */
    public UserSelector(ControlFactory controlFactory, Composite composite, Listener listener)
    {
        jidLabel = new LabelControl(controlFactory, composite, SWT.NONE);
        jidLabel.setText("JID:");
        GridData jidLabelLayout = 
            new GridData(SWT.END, SWT.CENTER, false, false);   
        jidLabel.setLayoutData(jidLabelLayout);
        jidText = new ComboControl(controlFactory, composite, SWT.BORDER);
        GridData jidTextLayout = new GridData(SWT.FILL, SWT.FILL, true, false);
        jidTextLayout.widthHint = controlFactory.convertHeightInCharsToPixels(composite.getParent(), 20);
        jidText.setLayoutData(jidTextLayout);
        jidText.addListener(SWT.Modify, listener);
        jidText.addListener(SWT.Selection , listener);
    }

    /**
     * Set combo enabled state
     * @param isEnabled Value to set
     */
    public void setEnabled(boolean isEnabled)
    {
    	jidText.setEnabled(isEnabled);
    }
 
    public void selectNewJid() 
    {
    	jidText.select(0);
    }

	public void select(String jid) 
	{
        int index = jidText.indexOf(jid);
        if (index < 0)
			jidText.select(0);
	}

    public void selectCurrentJid() 
    {
    	int index = jidText.getItems().length > 1 ? 1 : 0;
    	jidText.select(index);
    }
    
    protected void clear()
    {
        // Combo removeAll() required to correctly reset it if it contains items
        if (jidText.getItems().length > 0)
            jidText.removeAll();
        
    }
    
    /**
     *  Update JID combo using current Login configuration data   
     *  @return currently selected JID or empty string if none configured
     */
    protected String initializeUsers(List<String> userList) 
    {   // Add JID set. The first item on the list is the current item.
        String selection1 = null;
        if (userList.size() > 0)
            selection1 = userList.get(0);
        // Select item containing current JID, if configured
        int index = 0;
        // Handle case of empty user list (not expected)
        if (selection1 == null)
            setJid("");
        // Handle case current selection is not empty
        else if (!selection1.isEmpty())
        {
            setJid("");
            index = 1;
        }
        for (String jid: userList)
            setJid(jid);
        // Select current JID
        jidText.select(index);
        return getText();
    }

	/**
     * Set JID for Single Signon
     * @param gssapiPrincipal
     */
    protected void startSingleSignonConfig(String gssapiPrincipal)
    {
        // Select this item if it already exists, otherwise create it
        int index = jidText.indexOf(gssapiPrincipal);
        if (index < 0)
        {
            setJid(gssapiPrincipal);
            index = jidText.indexOf(gssapiPrincipal);
        }
        jidText.select(index);
    }

    /**
     * Change focus to combo
     */
    public void setFocus()
    {
        jidText.setFocus();
    }

    /**
     * Returns combo text 
     * @return User JID which may be empty if no user has been entered
     */
    public String getText()
    {
        return jidText.getText();
    }

    void setJid(String jid)
    {
        jidText.add(jid);
    }

}
