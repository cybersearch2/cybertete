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
package au.com.cybersearch2.cybertete.views;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.TextControl;

/**
 * ChatSessionControl
 * @author Andrew Bowley
 * 20 May 2016
 */
@Creatable
public class ChatSessionControl
{
    /** The text widget to display chat conversation */
    TextControl transcript;
    /** The text widget for user to enter text */
    TextControl entry;

    /**
     * postConstruct
     */
    @PostConstruct
    public void postConstruct(
        Composite parent, 
        ControlFactory controlFactory)
    {
        final Composite composite = createComposite(controlFactory, parent);

        transcript = new TextControl(controlFactory, composite, SWT.BORDER | SWT.MULTI | SWT.WRAP);
        transcript.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
                true, true));
        transcript.setEditable(false);
        transcript.setSystemColors();

        entry = new TextControl(controlFactory, composite, SWT.BORDER | SWT.WRAP);
        GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
        gridData.heightHint = entry.getLineHeight() * 2;
        entry.setLayoutData(gridData);
    }

    public void setKeyListener(KeyAdapter keyAdapter)
    {
        // Add key listener to send line when Enter key pressed
        entry.addKeyListener(keyAdapter);
    }

    public String getText()
    {
        return entry.getText();
    }
    
    public void onFocus() 
    {
        if (!entry.isDisposed())
            entry.setFocus();
    }

    /**
     * Display information in transcript control
     * @param info Text to be displayed
     */
    public void displayMessage(String info)
    {
        transcript.append(info);
        transcript.append("\n");
        scrollToEnd();
        entry.setText("");
    }

    /**
     * Clear all text in view.
     */
    public void clear()
    {
        transcript.setText("");
        entry.setText("");
    }
 
    /**
     * Set view enabled state
     * @return Flag set true if view changed state
     */
    public boolean setEnabled(boolean enabled)
    {
        if (transcript.isEnabled() != enabled)
        {
            transcript.setEnabled(enabled);
            entry.setEnabled(enabled);
            return true;
        }
        return false;
    }

    /**
     * Scroll transcript to end of text
     */
    private void scrollToEnd() 
    {
        int n = transcript.getCharCount();
        transcript.setSelection(n, n);
        transcript.showSelection();
    }


    /**
     * Create Composite to contain a view
     * @param parent Parent composite
     * @return Composite object
     */
    private static Composite createComposite(ControlFactory controlFactory, Composite parent) 
    {
        final Composite composite1 = controlFactory.compositeInstance(parent);
        parent.getShell().setBackgroundMode(SWT.INHERIT_DEFAULT);
        final GridLayout layout1 = new GridLayout(1, false);
        layout1.verticalSpacing = 5;
        composite1.setLayout(layout1);
        final Composite composite2 = controlFactory.compositeInstance(composite1);
        composite2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout2 = new GridLayout();
        layout2.marginWidth = 0;
        layout2.marginHeight = 0;
        composite2.setLayout(layout2);
        return composite2;
    }
}
