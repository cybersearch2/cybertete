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

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

/**
 * ApplicationModel
 * Container for E4 MApplication object, which is not available until "Process Additions" E4 life cycle event occurs.
 * Provides common operations involving contained objects such as change perspective and activate/hide part.
 * @author Andrew Bowley
 * 12 Apr 2016
 */
public class ApplicationModel
{
    static final List<MPartStack> EMPTY_PARTSTACK;
    
    static
    {
        EMPTY_PARTSTACK = Collections.emptyList();
    }
    
    /** Flag set true when application module is loaded */
    private boolean isModelLoaded;
    /** A representation of the model object '<em><b>Application</b></em>' */
    private MApplication applicationModel;
    /** The <b>Factory</b> for the model */
    private MBasicFactory modelFactory;
    /** Finds, creates and handles model elements */
    private EModelService modelService;
    /** Provides clients with the functionalities of showing and hiding parts */
    private EPartService partService;

    /**
     * Returns flag set true if model is loaded ie. "Process Additions" E4 life cycle stage achieved.
     * @return boolean
     */
    public boolean isModelLoaded()
    {
        return isModelLoaded;
    }
    
   /**
     * Return representation of model object '<em><b>Application</b></em>
     * @return MApplication object
     */
    public MApplication getApplicationModel()
    {
        return applicationModel;
    }

    /**
     * Set the application model components
     * @param applicationModel Representation of model object '<em><b>Application</b></em>
     * @param modelService Finds, creates and handles model elements
     * @param partService Provides clients with the functionalities of showing and hiding parts
     * @param modelFactory The <b>Factory</b> for the model
     */
    public void setApplicationModel(
            MApplication applicationModel, 
            EModelService modelService, 
            EPartService partService,
            MBasicFactory modelFactory)
    {
        this.applicationModel = applicationModel;
        this.modelService = modelService;
        this.partService = partService;
        this.modelFactory = modelFactory;
        isModelLoaded = true;
    }

    /**
     * @return the modelService
     */
    public EModelService getModelService()
    {
        return modelService;
    }

    /**
     * @return the partService
     */
    public EPartService getPartService()
    {
        return partService;
    }

    /**
     * @return the model factory
     */
    public MBasicFactory getModelFactory()
    {
        return modelFactory;
    }
 
    /**
     * Returns part found by id
     * @param id The id of the part to search for
     * @return MPart object or null if part not found
     */
    public MPart findPart(String id)
    {
        return partService.findPart(id);
    }
    
    /**
     * Returns perspective specified by identity
     * @param id Perspective identity
     * @return MPerspective object
     */
    public MPerspective getPerspective(String id)
    {
        return isModelLoaded ? (MPerspective) modelService.find(id, applicationModel) : null;
    }
 
    /**
     * Returns part stack list
     * @return MPartStack list
     */
    public List<MPartStack> getStacks()
    {
        return isModelLoaded ? modelService.findElements(applicationModel, null, MPartStack.class, null) : EMPTY_PARTSTACK;
    }

    /**
     * Shows and activates a part identified by id. Must be called in main thread. 
     * @param id The identifier of the part, must not be <code>null</code>
     * @return the shown part, or <code>null</code> if no parts or part descriptors can be found
     *          that match the specified id
     */
    public MPart activatePart(String id)
    {
        return partService.showPart(id, PartState.ACTIVATE);
    }

    /**
     * Shows and activates the given part 
     * @param part Part to show and activate
     * @return same part
     */
    public MPart activatePart(MPart part)
    {
        return partService.showPart(part, PartState.ACTIVATE);
    }

    /**
     * Hides the given part
     * @param part The part to hide 
     */
    public void hidePart(MPart part)
    {
        partService.hidePart(part);
    }

    /**
     * Change perspective to given id
     * @param id The id
     * @return flag set true if change succeeded
     */
    public boolean switchPerspective(String id)
    {
        if (isModelLoaded)
        {   
            MPerspective perspective = (MPerspective) modelService.find(id, applicationModel);
            // perspective is expected to be found
            if (perspective != null)
            {
                partService.switchPerspective(perspective);
                return true;
            }
        }
        return false;
    }
}
