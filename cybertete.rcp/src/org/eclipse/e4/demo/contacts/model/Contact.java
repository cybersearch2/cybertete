/*******************************************************************************
 * Copyright (c) 2009, 2012 Siemens AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Kai Tödter - initial implementation
 ******************************************************************************/

package org.eclipse.e4.demo.contacts.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Contact implements Cloneable 
{

	private PropertyChangeSupport changeSupport;

	private String name = ""; //$NON-NLS-1$
	private String host = ""; //$NON-NLS-1$

	public Contact(String name, String host) 
	{
	    this.name = name;
	    this.host = host;
		changeSupport = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) 
	{
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) 
	{
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}

	public void setName(String name) 
	{
        String oldName = this.name;
        this.name = name;
        changeSupport.firePropertyChange("firstName", oldName, name);
	}

	public String getName() 
	{
		return name;
	}

    public void setHost(String host) 
    {
        String oldHost = this.host;
        this.host = host;
        changeSupport.firePropertyChange("host", oldHost, host);
    }

    public String getHost() 
    {
        return host;
    }

	@Override
	public Object clone() throws CloneNotSupportedException 
	{
		return super.clone();
	}

	@Override
	public String toString() 
	{
		return name;
	}
}
