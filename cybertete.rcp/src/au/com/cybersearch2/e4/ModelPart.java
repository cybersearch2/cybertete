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
package au.com.cybersearch2.e4;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

/**
 * ModelPart
 * Wrapper for MPart object
 * @author Andrew Bowley
 * 26 Apr 2016
 */
public class ModelPart<T>
{
    /** The object being wrapped */
    MPart part;
    
    /**
     * Create ModelPart object
     * part The object being wrapped
     */
    public ModelPart(MPart part)
    {
        this.part = part;
    }

    /**
     * Returns object being wrapped
     * @return MPart object
     */
    public MPart getPart()
    {
        return part;
    }
 
    /**
     * Returns contribution object or null if it has not been rendered
     * @return Object of generic type T
     */
    @SuppressWarnings("unchecked")
    public T getRenderedArtifact()
    {
        return (T) part.getObject();
    }

    /**
     * Returns flag set true if contribution object has been rendered
     * @return boolean
     */
    public boolean isRendered()
    {
        return part.getObject() != null;
    }

    /**
     * Returns flag set true if part is closeable
     * @return boolean
     */
    public boolean isCloseable()
    {
        return part.isCloseable();
    }

    /**
     * Set flag for part is closeable
     * @param isClosable boolean
     */
    public void setCloseable(boolean isClosable)
    {
        part.setCloseable(isClosable);
    }

    /**
     * Set label
     * @param name Label value
     */
    public void setLabel(String name)
    {
        part.setLabel(name);
    }

}
