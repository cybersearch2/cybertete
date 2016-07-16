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

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;

import au.com.cybersearch2.cybertete.model.ApplicationState;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.service.ChatService;
import au.com.cybersearch2.e4.JobScheduler;

public class ExitHandler 
{
    /** Provides services to establish and monitor Chat sessions */
    @Inject
    ChatService chatService;
    /** Job scheduler */
    @Inject 
    JobScheduler jobScheduler;
    @Inject
    UISynchronize sync;

    /**
     * execute
     * @param eventBroker Event broker service
     */
	@Execute
	public void execute(IEventBroker eventBroker) 
	{   // Post to log out for shutdown
        eventBroker.post(CyberteteEvents.LOGOUT, ApplicationState.shutdown);
	}

	/**
	 * Handles shutdown and application restart events
	 * @param nextState Next application state to distinguish between shutdown and restart
	 * @param workbench A running instance of the workbench
	 */
    @Inject @Optional
    void onShutdownHandler(final @UIEventTopic(CyberteteEvents.SHUTDOWN) ApplicationState nextState, final IWorkbench workbench)
    {
         jobScheduler.schedule("Exit Cybertete", new Runnable(){

            @Override
            public void run()
            {
                exitCybertete(workbench, nextState == ApplicationState.running);
            }});
    }

    /**
     * Close chat service and shutdown or restart according to exit type
     * @param workbench A running instance of the workbench 
     * @param isRestart Flag set true if exit is to restart
     */
    private void exitCybertete(final IWorkbench workbench, final boolean isRestart)
    {
        chatService.close();
        sync.asyncExec(new Runnable(){

            @Override
            public void run()
            {
                if (!isRestart) 
                    workbench.close();
                else
                    workbench.restart();
            }
        });
    }
}