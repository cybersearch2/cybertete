 
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
package au.com.cybersearch2.cybertete.handlers;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.cybertete.model.ApplicationState;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.provisioning.UpdateHelper;
import au.com.cybersearch2.cybertete.provisioning.UpdateHelperImpl;
import au.com.cybersearch2.dialogs.SyncErrorDialog;
import au.com.cybersearch2.dialogs.SyncInfoDialog;
import au.com.cybersearch2.dialogs.SyncQuestionDialog;

/**
 * UpdateHandler
 * Checks for updates and prompts for confirmation if updates are ready to be installed
 * @author Andrew Bowley
 * 21 Apr 2016
 */
public class UpdateHandler extends JobChangeAdapter
{
    // Create platform artifact instances using interface for testability
    static interface ArtifactFactory
    {
         UpdateHelper updateHelperInstance(ProvisioningSession session);
    }

    static private final String ERROR_TITLE = "Update Error";

    static final String JOB_FAILED = "Update failed";

    UpdateHelper updateHelper;
    /** Logger */
    private Logger logger;

    /** Event broker service */
    @Inject
    IEventBroker eventBroker;
    /** Error dialog */
    @Inject
    SyncErrorDialog errorDialog;
    /** Information dialog */
    @Inject
    SyncInfoDialog infoDialog;
    /** Prompts user for yes/no answer */
    @Inject
    SyncQuestionDialog questionDialog;
 
    /**
     * Artifact factory to create platform artifacts. 
     */
    ArtifactFactory artifactFactory = new ArtifactFactory(){

        @Override
        public UpdateHelper updateHelperInstance(ProvisioningSession session)
        {
            return new UpdateHelperImpl(session);
        }
    };
    
    /**
     * postConstruct
     * @param loggerProvider Logger factory
     */
    @PostConstruct
    void postConstruct(ILoggerProvider loggerProvider) 
    {
        logger = loggerProvider.getClassLogger(UpdateHandler.class);
    }

    @Override
    public void done(IJobChangeEvent event) 
    {
        IStatus jobStatus = event.getResult();
        if (jobStatus.isOK())
            onDone();
        else
        {
            String message = jobStatus.getMessage();
            if (message == null)
                message = JOB_FAILED;
            errorDialog.showError(ERROR_TITLE, message);
            // Restart application
            eventBroker.post(CyberteteEvents.LOGOUT, ApplicationState.running);
        }
    }

    /**
     * execute
     * @param controlFactory SWT widget factory
     * @param agent Provisioning agent
     */
    @Execute
    public void execute(ControlFactory controlFactory, 
                         final IProvisioningAgent agent) 
    {
        logger.debug("Update initiated");
        ProgressMonitorDialog progressMonitorDialog = controlFactory.progressMonitorDialogInstance();
        // Update using a progress monitor
        IRunnableWithProgress task = new IRunnableWithProgress() {
            
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException,
                InterruptedException 
            {
                // Update all user-visible installable units
                UpdateHelper operation = artifactFactory.updateHelperInstance(new ProvisioningSession(agent));   
                update(operation, monitor);
            }
        };
        try 
        {
            progressMonitorDialog.run(true, true, task);
        } 
        catch (InvocationTargetException | InterruptedException e) 
        {
            logger.error(e, "Error running Update Provisioning Agent");
        }
    }

    /**
     * Perform update procedure
     * @param operation Update operation
     * @param monitor Progress monitor dialog ready to open
     * @return final status - CANCEL or OK
     */
    private IStatus update(UpdateHelper operation, IProgressMonitor monitor) 
    {
 
        // Check if updates are available
        IStatus status = operation.getAvailabilityStatus(monitor);
        if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) 
        {
            infoDialog.showInfo("Update", "Nothing to update");
            return Status.CANCEL_STATUS;
        }   
        else if (status.isOK()) 
        {
            final ProvisioningJob provisioningJob = operation.getProvisioningJob();
            if (provisioningJob != null) 
            {
                boolean performUpdate = questionDialog.ask(
                        "Updates available",
                        "There are updates available. Do you want to install them now?");
                if (performUpdate) 
                {
                    operation.addListener(provisioningJob, this);
                    // Schedule the provisioning job to ensure it is executed in a background thread
                    provisioningJob.schedule();
                }
            }
            else 
            {
                if (operation.hasResolved()) 
                    errorDialog.showError(ERROR_TITLE, "Couldn't get provisioning job: " + operation.getResolutionResult());
                else 
                    errorDialog.showError(ERROR_TITLE, "Couldn't resolve provisioning job");
            }   
        }
        else
             errorDialog.showError(ERROR_TITLE, status.getMessage());
        return Status.OK_STATUS;
    }
        
    private void onDone() 
    {
        if (questionDialog.ask(
                    "Updates installed, restart?",
                    "Updates have been installed successfully, do you want to restart?"))
            eventBroker.post(CyberteteEvents.LOGOUT, ApplicationState.running);
    }
}