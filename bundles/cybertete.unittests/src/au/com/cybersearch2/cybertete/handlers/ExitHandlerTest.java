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

import static org.mockito.Mockito.*;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.cybertete.model.ApplicationState;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.service.ChatService;
import au.com.cybersearch2.e4.JobScheduler;

/**
 * ExitHandlerTest
 * @author Andrew Bowley
 * 27 Apr 2016
 */
public class ExitHandlerTest
{

    @Test
    public void test_execute()
    {
        ExitHandler underTest = new ExitHandler();
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.execute(eventBroker);
        verify(eventBroker).post(CyberteteEvents.LOGOUT, ApplicationState.shutdown);
    }
    
    @Test
    public void test_onShutdownHandler()
    {
        UISynchronize sync = mock(UISynchronize.class);
        ApplicationState nextState = ApplicationState.shutdown;
        IWorkbench workbench = mock(IWorkbench.class);
        ExitHandler underTest = new ExitHandler();
        underTest.sync = sync;
        JobScheduler jobScheduler = mock(JobScheduler.class);
        ChatService chatService = mock(ChatService.class);
        underTest.jobScheduler = jobScheduler;
        underTest.chatService = chatService;
        underTest.onShutdownHandler(nextState, workbench);
        ArgumentCaptor<Runnable> jobCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(jobScheduler).schedule(eq("Exit Cybertete"), jobCaptor.capture());
        jobCaptor.getValue().run();
        verify(chatService).close();
        ArgumentCaptor<Runnable> syncCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(sync).asyncExec(syncCaptor.capture());
        syncCaptor.getValue().run();
        verify(workbench).close();
    }

    @Test
    public void test_onShutdownHandler_restart()
    {
        UISynchronize sync = mock(UISynchronize.class);
        ApplicationState nextState = ApplicationState.running;
        IWorkbench workbench = mock(IWorkbench.class);
        ExitHandler underTest = new ExitHandler();
        underTest.sync = sync;
        JobScheduler jobScheduler = mock(JobScheduler.class);
        ChatService chatService = mock(ChatService.class);
        underTest.jobScheduler = jobScheduler;
        underTest.chatService = chatService;
        underTest.onShutdownHandler(nextState, workbench);
        ArgumentCaptor<Runnable> jobCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(jobScheduler).schedule(eq("Exit Cybertete"), jobCaptor.capture());
        jobCaptor.getValue().run();
        verify(chatService).close();
        ArgumentCaptor<Runnable> syncCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(sync).asyncExec(syncCaptor.capture());
        syncCaptor.getValue().run();
        verify(workbench).restart();
    }
}
