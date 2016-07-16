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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

/**
 * TextControl
 * @author Andrew Bowley
 * 2 Mar 2016
 */
public class TextControl
{
    Text text;

    /**
     * Construct TextControl object
     * @param controlFactory SWT widget factory
     * @param composite Parent composite
     * @param style Style
     */
    public TextControl(ControlFactory controlFactory, Composite composite, int style)
    {
        text = controlFactory.textInstance(composite, style);
    }

    /**
     * Causes the receiver to have the <em>keyboard focus</em>, 
     * such that all keyboard events will be delivered to it.  Focus
     * reassignment will respect applicable platform constraints.
     * @return <code>true</code> if the control got focus, and <code>false</code> if it was unable to.
     */
    public boolean setFocus()
    {
        return text.setFocus();
    }

    /**
     * Sets the layout data associated with the receiver to the argument.
     * @param gridData The new layout data for the receiver
     */
    public void setLayoutData(GridData gridData)
    {
        text.setLayoutData(gridData);
    }

    /**
     * Adds the keyListener to the collection of listeners who will
     * be notified when keys are pressed and released on the system keyboard, by sending
     * it one of the messages defined in the <code>KeyListener</code>
     * interface.
     * @param keyListener The listener which should be notified
     */
    public void addKeyListener(KeyListener keyListener)
    {
        text.addKeyListener(keyListener);
    }

    /**
     * Adds the verifyListener to the collection of listeners who will
     * be notified when the receiver's text is verified, by sending
     * it one of the messages defined in the <code>VerifyListener</code>
     * interface.
     * @param verifyListener The listener which should be notified
     */
    public void addVerifyListener(VerifyListener verifyListener)
    {
        text.addVerifyListener(verifyListener);
    }

    /**
     * Sets the contents of the receiver to the given string.
     * @param string The new text
     */
    public void setText(String string)
    {
        text.setText(string);
    }

    /**
     * Returns the control's text
     * @return String
     */
    public String getText()
    {
        return text.getText();
    }

    /**
     * Marks the receiver as visible if the argument is <code>true</code>,
     * and marks it invisible otherwise. 
     * @param visible The new visibility state
     */    
    public void setVisible(boolean visible)
    {
        text.setVisible(visible);
    }

    /**
     * Enables the receiver if the argument is <code>true</code>,
     * and disables it otherwise. A disabled control is typically
     * not selectable from the user interface and draws with an
     * inactive or "grayed" look.
     * @param enabled The new enabled state
     */
    public void setEnabled(boolean enabled)
    {
        text.setEnabled(enabled);
    }

    /**
     * Returns <code>true</code> if the receiver is enabled, and
     * <code>false</code> otherwise. A disabled control is typically
     * not selectable from the user interface and draws with an
     * inactive or "grayed" look.
     * @return the receiver's enabled state
     */ 
    public boolean isEnabled()
    {
        return text.isEnabled();
    }


    /**
     * Sets the editable state.
     * @param editable The new editable state
     */
    public void setEditable(boolean editable)
    {
        text.setEditable(editable);
    }

    /**
    * Returns the height of a line.
    * @return the height of a row of text
     */
    public int getLineHeight()
    {
        return text.getLineHeight();
    }

    /**
     * Returns <code>true</code> if the widget has been disposed,
     * and <code>false</code> otherwise.
     * @return <code>true</code> when the widget is disposed and <code>false</code> otherwise
     */
    public boolean isDisposed()
    {
        return text.isDisposed();
    }

    /**
     * Appends a string.
     * @param string the string to be appended
     */
    public void append(String string)
    {
        text.append(string);
    }

    /**
     * Returns the number of characters.
     * @return number of characters in the control
     */
    public int getCharCount()
    {
        return text.getCharCount();
    }

    /**
     * Sets the selection to the range specified
     * by the given start and end indices.
     * <p>
     * Indexing is zero based.  The range of
     * a selection is from 0..N where N is
     * the number of characters in the widget.
     * </p><p>
     * Text selections are specified in terms of
     * caret positions.  In a text widget that
     * contains N characters, there are N+1 caret
     * positions, ranging from 0..N.  This differs
     * from other functions that address character
     * position such as getText () that use the
     * usual array indexing rules.
     * </p>
     * @param start the start of the range
     * @param end the end of the range
     */
    public void setSelection(int start, int end)
    {
        text.setSelection(start, end);
    }

    /**
     * Shows the selection.
     * <p>
     * If the selection is already showing
     * in the receiver, this method simply returns.  Otherwise,
     * lines are scrolled until the selection is visible.
     * </p>
     */
    public void showSelection()
    {
        text.showSelection();
    }

    /**
     * Verifies control has integer value in specified range. The control
     * will not be allowed to be changed to an invalid value except an empty value.
     * @param verifyEvent Event generated when text is about to be modified.
     * @param min Minimum value in range
     * @param max Maximum value in range
     */
    public void verifyRange(VerifyEvent verifyEvent, int min, int max)
    {
        int start = verifyEvent.start;
        int end = verifyEvent.end;
        String text = verifyEvent.text;
        // Combine the current text with the new from the event
        String current = ((Text)verifyEvent.widget).getText();
        String modified =  current.substring(0, start) + text + current.substring(end);
        // Set doit flag true initially to indicate modification is valid
        verifyEvent.doit = true;  
        if (modified.isEmpty())
            return;
        try
        {  
            int intValue = Integer.parseInt(modified);  
            if (intValue >= min && intValue <= max)
                return;  
        }  
        catch(NumberFormatException ex)
        {  
        }  
        verifyEvent.doit = false;  
    }

    /**
     * Set control background and foreground colors to system tooltip values
     */
    public void setSystemColors()
    {
        Display display = text.getDisplay();
        text.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        text.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
    }
    
}
