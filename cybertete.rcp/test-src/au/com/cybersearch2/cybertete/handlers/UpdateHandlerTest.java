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
package au.com.cybersearch2.cybertete.handlers;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.cybertete.model.ApplicationState;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.provisioning.UpdateHelper;
import au.com.cybersearch2.dialogs.SyncErrorDialog;
import au.com.cybersearch2.dialogs.SyncInfoDialog;
import au.com.cybersearch2.dialogs.SyncQuestionDialog;

/**
 * UpdateHandlerTest
 * @author Andrew Bowley
 * 21 Apr 2016
 */
public class UpdateHandlerTest
{
    @Test
    public void test_execute() throws InvocationTargetException, InterruptedException
    {
        final boolean[] addListener = new boolean[] {false};
        final ProgressMonitorDialog progressMonitorDialog = mock(ProgressMonitorDialog.class);
        final IProgressMonitor monitor = mock(IProgressMonitor.class);
        final IStatus status = mock(IStatus.class);
        when(status.getCode()).thenReturn(0);
        when(status.isOK()).thenReturn(true);
        final ProvisioningJob provisioningJob = mock(ProvisioningJob.class);
        UpdateHandler.ArtifactFactory artifactFactory = new UpdateHandler.ArtifactFactory()
        {

            @Override
            public UpdateHelper updateHelperInstance(
                    ProvisioningSession session)
            {
                assertThat(session).isNotNull();
                return new UpdateHelper(){

                    @Override
                    public IStatus getAvailabilityStatus(
                            IProgressMonitor progressMonitor)
                    {
                        assertThat(progressMonitor).isEqualTo(monitor);
                        return status;
                    }

                    @Override
                    public ProvisioningJob getProvisioningJob()
                    {
                        return provisioningJob;
                    }

                    @Override
                    public boolean hasResolved()
                    {
                        return true;
                    }

                    @Override
                    public String getResolutionResult()
                    {
                        return "Success";
                    }

                    @Override
                    public void addListener(ProvisioningJob provisioningJob,
                            JobChangeAdapter jobChangeListener)
                    {
                        assertThat(provisioningJob).isNotNull();
                        assertThat(jobChangeListener).isNotNull();
                        addListener[0] = true;
                    }};
            }
        };
        UpdateHandler underTest = new UpdateHandler();
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.eventBroker = eventBroker;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(UpdateHandler.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        SyncQuestionDialog questionDialog = mock(SyncQuestionDialog.class);
        when(questionDialog.ask(
                        "Updates available",
                        "There are updates available. Do you want to install them now?")).thenReturn(true);
        underTest.questionDialog = questionDialog;
        underTest.artifactFactory = artifactFactory;
        IProvisioningAgent agent = mock(IProvisioningAgent.class);
        ControlFactory controlFactory = mock(ControlFactory.class);
        when(controlFactory.progressMonitorDialogInstance()).thenReturn(progressMonitorDialog);
        underTest.execute(controlFactory, agent);
        ArgumentCaptor<IRunnableWithProgress> taskCaptor = ArgumentCaptor.forClass(IRunnableWithProgress.class);
        verify(progressMonitorDialog).run(eq(true), eq(true), taskCaptor.capture());
        taskCaptor.getValue().run(monitor);
        assertThat(addListener[0]).isTrue();
        verify(provisioningJob).schedule();
        IJobChangeEvent event = mock(IJobChangeEvent.class);
        IStatus jobStatus = mock(IStatus.class);
        when(jobStatus.isOK()).thenReturn(true);
        when(event.getResult()).thenReturn(jobStatus);
        when(questionDialog.ask(
                    "Updates installed, restart?",
                    "Updates have been installed successfully, do you want to restart?")).thenReturn(true);
        underTest.done(event);
        verify(eventBroker).post(CyberteteEvents.LOGOUT, ApplicationState.running);
    }

    @Test
    public void test_execute_provisioning_error() throws InvocationTargetException, InterruptedException
    {
        final boolean[] addListener = new boolean[] {false};
        final ProgressMonitorDialog progressMonitorDialog = mock(ProgressMonitorDialog.class);
        final IProgressMonitor monitor = mock(IProgressMonitor.class);
        final IStatus status = mock(IStatus.class);
        when(status.getCode()).thenReturn(0);
        when(status.isOK()).thenReturn(true);
        final ProvisioningJob provisioningJob = mock(ProvisioningJob.class);
        UpdateHandler.ArtifactFactory artifactFactory = new UpdateHandler.ArtifactFactory()
        {

            @Override
            public UpdateHelper updateHelperInstance(
                    ProvisioningSession session)
            {
                assertThat(session).isNotNull();
                return new UpdateHelper(){

                    @Override
                    public IStatus getAvailabilityStatus(
                            IProgressMonitor progressMonitor)
                    {
                        assertThat(progressMonitor).isEqualTo(monitor);
                        return status;
                    }

                    @Override
                    public ProvisioningJob getProvisioningJob()
                    {
                        return provisioningJob;
                    }

                    @Override
                    public boolean hasResolved()
                    {
                        return true;
                    }

                    @Override
                    public String getResolutionResult()
                    {
                        return "Success";
                    }

                    @Override
                    public void addListener(ProvisioningJob provisioningJob,
                            JobChangeAdapter jobChangeListener)
                    {
                        assertThat(provisioningJob).isNotNull();
                        assertThat(jobChangeListener).isNotNull();
                        addListener[0] = true;
                    }};
            }
        };
        UpdateHandler underTest = new UpdateHandler();
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        underTest.errorDialog = errorDialog;
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.eventBroker = eventBroker;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(UpdateHandler.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        SyncQuestionDialog questionDialog = mock(SyncQuestionDialog.class);
        when(questionDialog.ask(
                        "Updates available",
                        "There are updates available. Do you want to install them now?")).thenReturn(true);
        underTest.questionDialog = questionDialog;
        underTest.artifactFactory = artifactFactory;
        IProvisioningAgent agent = mock(IProvisioningAgent.class);
        ControlFactory controlFactory = mock(ControlFactory.class);
        when(controlFactory.progressMonitorDialogInstance()).thenReturn(progressMonitorDialog);
        underTest.execute(controlFactory, agent);
        ArgumentCaptor<IRunnableWithProgress> taskCaptor = ArgumentCaptor.forClass(IRunnableWithProgress.class);
        verify(progressMonitorDialog).run(eq(true), eq(true), taskCaptor.capture());
        taskCaptor.getValue().run(monitor);
        assertThat(addListener[0]).isTrue();
        verify(provisioningJob).schedule();
        IJobChangeEvent event = mock(IJobChangeEvent.class);
        IStatus jobStatus = mock(IStatus.class);
        when(jobStatus.getMessage()).thenReturn("Network error");
        when(jobStatus.isOK()).thenReturn(false);
        when(event.getResult()).thenReturn(jobStatus);
        underTest.done(event);
        verify(errorDialog).showError("Update Error", "Network error");
        verify(eventBroker).post(CyberteteEvents.LOGOUT, ApplicationState.running);
    }

   @Test
    public void test_execute_no_updates() throws InvocationTargetException, InterruptedException
    {
        final ProgressMonitorDialog progressMonitorDialog = mock(ProgressMonitorDialog.class);
        final IProgressMonitor monitor = mock(IProgressMonitor.class);
        final IStatus status = mock(IStatus.class);
        when(status.getCode()).thenReturn(UpdateOperation.STATUS_NOTHING_TO_UPDATE);
        when(status.isOK()).thenReturn(true);
        final ProvisioningJob provisioningJob = mock(ProvisioningJob.class);
        UpdateHandler.ArtifactFactory artifactFactory = new UpdateHandler.ArtifactFactory()
        {

            @Override
            public UpdateHelper updateHelperInstance(
                    ProvisioningSession session)
            {
                assertThat(session).isNotNull();
                return new UpdateHelper(){

                    @Override
                    public IStatus getAvailabilityStatus(
                            IProgressMonitor progressMonitor)
                    {
                        assertThat(progressMonitor).isEqualTo(monitor);
                        return status;
                    }

                    @Override
                    public ProvisioningJob getProvisioningJob()
                    {
                        return provisioningJob;
                    }

                    @Override
                    public boolean hasResolved()
                    {
                        return true;
                    }

                    @Override
                    public String getResolutionResult()
                    {
                        return "Success";
                    }
                
                    @Override
                    public void addListener(ProvisioningJob provisioningJob,
                            JobChangeAdapter jobChangeListener)
                    {
                    }
                };
            }
        };
        UpdateHandler underTest = new UpdateHandler();
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(UpdateHandler.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        SyncInfoDialog infoDialog = mock(SyncInfoDialog.class);
        underTest.infoDialog = infoDialog;
        underTest.artifactFactory = artifactFactory;
        IProvisioningAgent agent = mock(IProvisioningAgent.class);
        ControlFactory controlFactory = mock(ControlFactory.class);
        when(controlFactory.progressMonitorDialogInstance()).thenReturn(progressMonitorDialog);
        underTest.execute(controlFactory, agent);
        ArgumentCaptor<IRunnableWithProgress> taskCaptor = ArgumentCaptor.forClass(IRunnableWithProgress.class);
        verify(progressMonitorDialog).run(eq(true), eq(true), taskCaptor.capture());
        taskCaptor.getValue().run(monitor);
        verify(provisioningJob, times(0)).schedule();
        verify(infoDialog).showInfo("Update", "Nothing to update");
    }
    
    @Test
    public void test_execute_status_error() throws InvocationTargetException, InterruptedException
    {
        final ProgressMonitorDialog progressMonitorDialog = mock(ProgressMonitorDialog.class);
        final IProgressMonitor monitor = mock(IProgressMonitor.class);
        final IStatus status = mock(IStatus.class);
        when(status.getMessage()).thenReturn("Repository not found");
        when(status.getCode()).thenReturn(0);
        when(status.isOK()).thenReturn(false);
        final ProvisioningJob provisioningJob = mock(ProvisioningJob.class);
        UpdateHandler.ArtifactFactory artifactFactory = new UpdateHandler.ArtifactFactory()
        {
            @Override
            public UpdateHelper updateHelperInstance(
                    ProvisioningSession session)
            {
                assertThat(session).isNotNull();
                return new UpdateHelper(){

                    @Override
                    public IStatus getAvailabilityStatus(
                            IProgressMonitor progressMonitor)
                    {
                        assertThat(progressMonitor).isEqualTo(monitor);
                        return status;
                    }

                    @Override
                    public ProvisioningJob getProvisioningJob()
                    {
                        return provisioningJob;
                    }

                    @Override
                    public boolean hasResolved()
                    {
                        return true;
                    }

                    @Override
                    public String getResolutionResult()
                    {
                        return "Success";
                    }

                    @Override
                    public void addListener(ProvisioningJob provisioningJob,
                            JobChangeAdapter jobChangeListener)
                    {
                    }};
            }
        };
        UpdateHandler underTest = new UpdateHandler();
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(UpdateHandler.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        underTest.errorDialog = errorDialog;
        underTest.artifactFactory = artifactFactory;
        IProvisioningAgent agent = mock(IProvisioningAgent.class);
        ControlFactory controlFactory = mock(ControlFactory.class);
        when(controlFactory.progressMonitorDialogInstance()).thenReturn(progressMonitorDialog);
        underTest.execute(controlFactory, agent);
        ArgumentCaptor<IRunnableWithProgress> taskCaptor = ArgumentCaptor.forClass(IRunnableWithProgress.class);
        verify(progressMonitorDialog).run(eq(true), eq(true), taskCaptor.capture());
        taskCaptor.getValue().run(monitor);
        verify(errorDialog).showError("Update Error", "Repository not found");
        verify(provisioningJob, times(0)).schedule();
    }
    
    @Test
    public void test_execute_no_provisioning_job() throws InvocationTargetException, InterruptedException
    {
        final ProgressMonitorDialog progressMonitorDialog = mock(ProgressMonitorDialog.class);
        final IProgressMonitor monitor = mock(IProgressMonitor.class);
        final IStatus status = mock(IStatus.class);
        when(status.getCode()).thenReturn(0);
        when(status.isOK()).thenReturn(true);
         UpdateHandler.ArtifactFactory artifactFactory = new UpdateHandler.ArtifactFactory()
        {
            @Override
            public UpdateHelper updateHelperInstance(
                    ProvisioningSession session)
            {
                assertThat(session).isNotNull();
                return new UpdateHelper(){

                    @Override
                    public IStatus getAvailabilityStatus(
                            IProgressMonitor progressMonitor)
                    {
                        assertThat(progressMonitor).isEqualTo(monitor);
                        return status;
                    }

                    @Override
                    public ProvisioningJob getProvisioningJob()
                    {
                        return null;
                    }

                    @Override
                    public boolean hasResolved()
                    {
                        return false;
                    }

                    @Override
                    public String getResolutionResult()
                    {
                        return "Success";
                    }

                    @Override
                    public void addListener(ProvisioningJob provisioningJob,
                            JobChangeAdapter jobChangeListener)
                    {
                    }};
            }
        };
        UpdateHandler underTest = new UpdateHandler();
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(UpdateHandler.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        underTest.errorDialog = errorDialog;
        underTest.artifactFactory = artifactFactory;
        IProvisioningAgent agent = mock(IProvisioningAgent.class);
        ControlFactory controlFactory = mock(ControlFactory.class);
        when(controlFactory.progressMonitorDialogInstance()).thenReturn(progressMonitorDialog);
        underTest.execute(controlFactory, agent);
        ArgumentCaptor<IRunnableWithProgress> taskCaptor = ArgumentCaptor.forClass(IRunnableWithProgress.class);
        verify(progressMonitorDialog).run(eq(true), eq(true), taskCaptor.capture());
        taskCaptor.getValue().run(monitor);
        verify(errorDialog).showError("Update Error", "Couldn't resolve provisioning job");
    }
    
    @Test
    public void test_execute_resolved_provisioning_job() throws InvocationTargetException, InterruptedException
    {
        final ProgressMonitorDialog progressMonitorDialog = mock(ProgressMonitorDialog.class);
        final IProgressMonitor monitor = mock(IProgressMonitor.class);
        final IStatus status = mock(IStatus.class);
        when(status.getCode()).thenReturn(0);
        when(status.isOK()).thenReturn(true);
         UpdateHandler.ArtifactFactory artifactFactory = new UpdateHandler.ArtifactFactory()
        {
            @Override
            public UpdateHelper updateHelperInstance(
                    ProvisioningSession session)
            {
                assertThat(session).isNotNull();
                return new UpdateHelper(){

                    @Override
                    public IStatus getAvailabilityStatus(
                            IProgressMonitor progressMonitor)
                    {
                        assertThat(progressMonitor).isEqualTo(monitor);
                        return status;
                    }

                    @Override
                    public ProvisioningJob getProvisioningJob()
                    {
                        return null;
                    }

                    @Override
                    public boolean hasResolved()
                    {
                        return true;
                    }

                    @Override
                    public String getResolutionResult()
                    {
                        return "Repository empty";
                    }

                    @Override
                    public void addListener(ProvisioningJob provisioningJob,
                            JobChangeAdapter jobChangeListener)
                    {
                    }};
            }
        };
        UpdateHandler underTest = new UpdateHandler();
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(UpdateHandler.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        underTest.errorDialog = errorDialog;
        underTest.artifactFactory = artifactFactory;
        IProvisioningAgent agent = mock(IProvisioningAgent.class);
        ControlFactory controlFactory = mock(ControlFactory.class);
        when(controlFactory.progressMonitorDialogInstance()).thenReturn(progressMonitorDialog);
        underTest.execute(controlFactory, agent);
        ArgumentCaptor<IRunnableWithProgress> taskCaptor = ArgumentCaptor.forClass(IRunnableWithProgress.class);
        verify(progressMonitorDialog).run(eq(true), eq(true), taskCaptor.capture());
        taskCaptor.getValue().run(monitor);
        verify(errorDialog).showError("Update Error","Couldn't get provisioning job: Repository empty");
    }
}
