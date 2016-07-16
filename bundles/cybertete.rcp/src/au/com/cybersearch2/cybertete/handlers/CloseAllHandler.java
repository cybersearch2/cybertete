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

import au.com.cybersearch2.cybertete.model.service.ChatService;
import au.com.cybersearch2.e4.JobScheduler;

public class CloseAllHandler 
{
    @Inject
    ChatService chatService;
    /** Job scheduler */
    @Inject 
    JobScheduler jobScheduler;

    /**
     * execute
     */
	@Execute
	public void execute() 
	{
        jobScheduler.schedule("Close all", new Runnable(){

            @Override
            public void run()
            {   // Just close chat service to trigger more events to close/clear windows
                chatService.close();
            }});
	}

}