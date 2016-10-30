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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;

/**
 * ModelPartsList
 * Wraps stack element list
 * @author Andrew Bowley
 * 26 Apr 2016
 */
public class ModelPartsList<T>
{
    /** The stack element list being wrapped */
    List<MStackElement> stackList;
    /** List of model parts - lazy loaded */
    List<ModelPart<T>> partList;
 
    /**
     * Create ModelPartsList object
     * @param stackList The stack element list being wrapped
     */
    public ModelPartsList(List<MStackElement> stackList)
    {
        this.stackList = stackList;
    }

    /**
     * Returns the stack element list being wrapped
     * @return MStackElement list
     */
    public List<MStackElement> getStackList()
    {
        return stackList;
    }
 
    /**
     * Returns list of model parts 
     * @return List of objects of generic type T
     */
    public List<ModelPart<T>> getParts()
    {
        if (partList == null)
        {
            partList = new ArrayList<ModelPart<T>>(stackList.size());
            for (MStackElement element: stackList)
                partList.add(new ModelPart<T>((MPart) element));
        }
        return  partList;  
    }
 
    /**
     * Returns true if stack is empty or contains only a single unrendered element 
     * @return boolean
     */
    public boolean isNewList()
    {
        return ((stackList.size() == 0) || // Empty list not expected!
                ((stackList.size() == 1) && !isRendered(stackList.get(0))));    
    }

    /**
     * Add given part to stack
     * @param activePart MPart object (configured contribution class of generic type T)
     */
    public void addToStack(MPart activePart)
    {
        stackList.add(activePart);
    }

    /**
     * Returns flag set true if contribution object (typed as MStackElement) has been rendered
     * @param element The contribution object
     * @return flag set true if element is rendered
     */
    boolean isRendered(MStackElement element)
    {
        return ((MPart)element).getObject() != null;
    }
    
}
