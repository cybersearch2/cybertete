/**
    Copyright (C) 2015  www.cybersearch2.com.au

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
// Original copyright
/*******************************************************************************
 * Copyright (c) 2009, 2014 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 175735)
 *     Matthew Hall - bugs 262407, 260337
 ******************************************************************************/

package au.com.cybersearch2.cybertete.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * AbstractModelObject
 * Minimal JavaBeans support - from Eclipse Platform UI Project
 * Allows listeners to track changes in model objects
 */
public class AbstractModelObject
{
    /**
     * A utility class that can be used by beans that support bound
     * properties.  It manages a list of listeners and dispatches
     * {@link PropertyChangeEvent}s to them.  You can use an instance of this class
     * as a member field of your bean and delegate these types of work to it.
     * The {@link PropertyChangeListener} can be registered for all properties
     * or for a property specified by name.
     */
    private PropertyChangeSupport propertyChangeSupport = 
            new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) 
    {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Add a PropertyChangeListener for a specific property.  The listener
     * will be invoked only when a call on firePropertyChange names that
     * specific property.
     *
     * @param propertyName  The name of the property to listen on.
     * @param listener  The PropertyChangeListener to be added
     */
   public void addPropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener) 
    {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

   /**
    * Remove a PropertyChangeListener from the listener list.
    * This removes a PropertyChangeListener that was registered
    * for all properties.
    *
    * @param listener  The PropertyChangeListener to be removed
    */
    public void removePropertyChangeListener(PropertyChangeListener listener) 
    {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Remove a PropertyChangeListener for a specific property.
     * If <code>listener</code> was added more than once to the same event
     * source for the specified property, it will be notified one less time
     * after being removed.
     *
     * @param propertyName  The name of the property that was listened on.
     * @param listener  The PropertyChangeListener to be removed
     */
   public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) 
    {
        propertyChangeSupport.removePropertyChangeListener(propertyName,
                listener);
    }

   /**
    * Reports a bound property update to listeners
    * that have been registered to track updates of
    * all properties or a property with the specified name.
    * <p>
    * No event is fired if old and new values are equal and non-null.
    * <p>
    * @param propertyName  the programmatic name of the property that was changed
    * @param oldValue      the old value of the property
    * @param newValue      the new value of the property
    */
    protected void firePropertyChange(String propertyName, Object oldValue,
            Object newValue) 
    {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue,
                newValue);
    }
}