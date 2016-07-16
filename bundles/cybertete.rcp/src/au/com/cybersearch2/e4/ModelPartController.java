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

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;

/**
 * ModelPartController
 * Applies Application services to model parts
 * @author Andrew Bowley
 * 25 Apr 2016
 */
@Creatable
public class ModelPartController<T>
{
    static final  List<MStackElement> EMPTY_STACK_ELEMENT_LIST;
    
    /** Application model artifacts - only available when @ProcessAdditions E4 Lifecycle stage reached */
    @Inject
    ApplicationModel applicationModel;
    /** Contribution URI builder */
    @Inject
    Contribution contribution;
 
    static
    {
        EMPTY_STACK_ELEMENT_LIST = Collections.emptyList();
    }

    public ModelPart<T> createPart(String elementId, Class<T> contributionClass)
    {
        MPart modelPart = applicationModel.getModelFactory().createPart();
        modelPart.setElementId(elementId);
        modelPart.setContributionURI(contribution.uriForClass(contributionClass));
        return new ModelPart<T>(modelPart);
    }

    /**
     * Returns part found by id
     * @param id
     * @return MPart object or null if part not found
     */
    public ModelPart<T> findPart(String id)
    {
        MPart part = applicationModel.findPart(id);
        return part != null ? new ModelPart<T>(part) : null;
    }

    /**
     * Shows and activates a part identified by id. 
     * @param id The identifier of the part, must not be <code>null</code>
     * @return the shown part, or <code>null</code> if no parts or part descriptors can be found
     *          that match the specified id
     */
    public ModelPart<T> activatePart(String id)
    {
        MPart part = applicationModel.activatePart(id);
        return part != null ? new ModelPart<T>(part) : null;
    }

    /**
     * Shows and activates the given part 
     * @param modelPart Model part to show and activate
     */
    public void activatePart(ModelPart<T> modelPart)
    {
        applicationModel.activatePart(modelPart.getPart());
    }

    /**
     * Hides the given part cast as a stack element
     * @param modelPart The part to hide 
     */
    public void hidePart(final ModelPart<T> modelPart)
    {
        applicationModel.hidePart(modelPart.getPart());
    }
 
    /**
     * Return model parts list for given stack ID
     * @param stackId The stack ID
     * @return ModelPartsList object
     */
    public ModelPartsList<T> getPartList(String stackId)
    {
        return new ModelPartsList<T>(getWindowList(stackId));
    }
    
    /**
     * Returns list of parts in Chat View stack
     * @return List of MStackElement objects which must be cast to MPart and then ChatSessionView 
     */
    List<MStackElement> getWindowList(String stackId)
    {
        for (MPartStack partStack: applicationModel.getStacks())
        {
            if (partStack.getElementId().equals(stackId))
                return partStack.getChildren();
        }
        return EMPTY_STACK_ELEMENT_LIST;
    }

}
