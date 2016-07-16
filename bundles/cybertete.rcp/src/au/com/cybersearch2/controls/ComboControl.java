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

import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

/**
 * ComboControl
 * @author Andrew Bowley
 * 2 Mar 2016
 */
public class ComboControl
{
    Combo combo;

    /**
     * Construct ComboControl object
     * @param controlFactory SWT widget factory
     * @param composite Parent composite
     * @param style Style
     */
    public ComboControl(ControlFactory controlFactory, Composite composite, int style)
    {
        combo = controlFactory.comboInstance(composite, style);
    }

    /**
     * Returns the control's text
     * @return String
     */
    public String getText()
    {
        return combo.getText();
    }

    /**
     * Sets the layout data associated with the receiver to the argument.
     * @param gridData The new layout data for the receiver
     */
    public void setLayoutData(GridData gridData)
    {
        combo.setLayoutData(gridData);
    }

    /**
     * Adds the listener to the collection of listeners who will
     * be notified when an event of the given type occurs. When the
     * event does occur in the widget, the listener is notified by
     * sending it the <code>handleEvent()</code> message. The event
     * type is one of the event constants defined in class <code>SWT</code>.
     * @param eventType The type of event to listen for
     * @param listener The listener which should be notified when the event occurs
     */
    public void addListener(int eventType, Listener listener)
    {
        combo.addListener(eventType, listener);
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
        combo.addKeyListener(keyListener);
    }

    /**
     * Returns a (possibly empty) array of <code>String</code>s which are
     * the items in the receiver's list. 
     * <p>
     * Note: This is not the actual structure used by the receiver
     * to maintain its list of items, so modifying the array will
     * not affect the receiver. 
     * </p>
     * @return the items in the receiver's list
     */
    public String[] getItems()
    {
        return combo.getItems();
    }

    /**
     * Removes all of the items from the receiver's list and clear the
     * contents of receiver's text field.
     */
    public void removeAll()
    {
        combo.removeAll();
    }

    /**
     * Adds the argument to the end of the receiver's list.
     * @param string The new item
     */
    public void add(String string)
    {
        combo.add(string);
    }

    /**
     * Searches the receiver's list starting at the first item
     * (index 0) until an item is found that is equal to the 
     * argument, and returns the index of that item. If no item
     * is found, returns -1.
     * @param string the search item
     * @return the index of the item
     */
    public int indexOf(String string)
    {
        return combo.indexOf(string);
    }

    /**
     * Selects the item at the given zero-relative index in the receiver's 
     * list.  If the item at the index was already selected, it remains
     * selected. Indices that are out of range are ignored.
     * @param index the index of the item to select
     */
    public void select(int index)
    {
        combo.select(index);
    }

    /**
     * Set focus to this control
     * return flag set true if control gained focus
     */
    public boolean setFocus()
    {
        return combo.setFocus();
    }
}
