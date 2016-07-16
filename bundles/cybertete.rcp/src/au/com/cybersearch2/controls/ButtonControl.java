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

import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * ButtonControl
 * @author Andrew Bowley
 * 2 Mar 2016
 */
public class ButtonControl
{

    private Button button;
    
    /**
     * Construct ButtonControl object
     * @param controlFactory SWT widget factory
     * @param composite Parent composite
     * @param style Style
     */
    public ButtonControl(ControlFactory controlFactory, Composite composite, int style)
    {
        button = controlFactory.buttonInstance(composite, style);
    }

    /**
     * Construct ButtonControl object wrapping given button
     * @param button Button object
     */
    public ButtonControl(Button button)
    {
        this.button = button;
    }
   
    /**
     * Sets button text to the given string.
     * @param string The new text
     */
    public void setText(String string)
    {
        button.setText(string);
    }

    /**
     * Sets the layout data associated with the button to the argument.
     * @param gridData The new layout data for the button
     */
    public void setLayoutData(GridData gridData)
    {
        button.setLayoutData(gridData);
    }

    /**
     * Sets the selection state of the receiver, if it is of type <code>CHECK</code>, 
     * <code>RADIO</code>, or <code>TOGGLE</code>.
     * <p>
     * When the receiver is of type <code>CHECK</code> or <code>RADIO</code>,
     * it is selected when it is checked. When it is of type <code>TOGGLE</code>,
     * it is selected when it is pushed in.
     * @param selected The new selection state
     */
    public void setSelection(boolean selected)
    {
        button.setSelection(selected);
    }

    /**
     * Adds the selectionAdapter to the collection of listeners who will
     * be notified when the control is selected by the user, by sending
     * it one of the messages defined in the <code>SelectionListener</code>
     * interface.
     * @param selectionAdapter The listener which should be notified
     */
    public void addSelectionListener(SelectionAdapter selectionAdapter)
    {
        button.addSelectionListener(selectionAdapter);
    }

    /**
     * Returns <code>true</code> if the receiver is selected,
     * and false otherwise.
     * <p>
     * When the receiver is of type <code>CHECK</code> or <code>RADIO</code>,
     * it is selected when it is checked. When it is of type <code>TOGGLE</code>,
     * it is selected when it is pushed in. If the receiver is of any other type,
     * this method returns false.
     * @return the selection state
     */
    public boolean getSelection()
    {
        return button.getSelection();
    }

    /**
     * Marks the button as visible if the argument is <code>true</code>,
     * and marks it invisible otherwise. 
     * @param visible The new visibility state
     */    
    public void setVisible(boolean visible)
    {
        button.setVisible(visible);
    }

    /**
     * Enables the button if the argument is <code>true</code>,
     * and disables it otherwise. A disabled control is typically
     * not selectable from the user interface and draws with an
     * inactive or "grayed" look.
     * @param enabled The new enabled state
     */
    public void setEnabled(boolean enabled)
    {
        button.setEnabled(enabled);
    }

    /**
     * Adds the listener to the collection of listeners who will
     * be notified when the control is selected by the user, by sending
     * it one of the messages defined in the <code>SelectionListener</code>
     * interface.
     * @param listener The listener which should be notified
     */
    public void addSelectionListener(SelectionListener listener)
    {
        button.addSelectionListener(listener);
    }

    /**
     * Sets the receiver's image to the argument, which may be
     * <code>null</code> indicating that no image should be displayed.
     * @param image The image to display on the receiver (may be <code>null</code>)
     */
    public void setImage(Image image)
    {
        button.setImage(image);
    }

    /**
     * Adds the listener to the collection of listeners who will
     * be notified when the control gains or loses focus, by sending
     * it one of the messages defined in the <code>FocusListener</code>
     * interface.
     * @param listener The listener which should be notified
     */
    public void addFocusListener(FocusListener listener)
    {
        button.addFocusListener(listener);
    }

    /**
     * Sets the application defined widget data associated
     * with the receiver to be the argument. The <em>widget
     * data</em> is a single, unnamed field that is stored
     * with every widget. 
     * <p>
     * Applications may put arbitrary objects in this field. If
     * the object stored in the widget data needs to be notified
     * when the widget is disposed of, it is the application's
     * responsibility to hook the Dispose event on the widget and
     * do so.
     * </p>
     * @param data The widget data
     */
    public void setData(Object data)
    {
        button.setData(data);
    }

    /**
     * Returns the application defined widget data associated
     * with the receiver, or null if it has not been set. The
     * <em>widget data</em> is a single, unnamed field that is
     * stored with every widget. 
     * @return the widget data
     */
    public Object getData()
    {
        return button.getData();
    }
}
