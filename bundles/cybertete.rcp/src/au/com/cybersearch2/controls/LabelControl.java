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
package au.com.cybersearch2.controls;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * LabelControl
 * @author Andrew Bowley
 * 2 Mar 2016
 */
public class LabelControl
{
    Label label;
    
    /**
     * Construct LabelControl object
     * @param controlFactory SWT widget factory
     * @param composite Parent composite
     * @param style Style
     */
    public LabelControl(ControlFactory controlFactory, Composite composite, int style)
    {
        label = controlFactory.labelInstance(composite, style);
    }

    /**
     * Marks the label as visible if the argument is <code>true</code>,
     * and marks it invisible otherwise. 
     * @param visible The new visibility state
     */    
    public void setVisible(boolean visible)
    {
        label.setVisible(visible);
    }
    
    /**
     * Sets the contents of the label to the given string.
     * @param string The new text
     */
    public void setText(String string)
    {
        label.setText(string);
    }

    /**
     * Sets the layout data associated with the receiver to the argument.
     * @param gridData The new layout data for the receiver
     */
    public void setLayoutData(GridData gridData)
    {
        label.setLayoutData(gridData);
    }

    /**
     * Enables the label if the argument is <code>true</code>,
     * and disables it otherwise. A disabled control is typically
     * not selectable from the user interface and draws with an
     * inactive or "grayed" look.
     * @param enabled The new enabled state
     */
    public void setEnabled(boolean enabled)
    {
        label.setEnabled(enabled);
    }
}
