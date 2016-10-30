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

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.cybertete.model.service.ChatService;
import au.com.cybersearch2.e4.JobScheduler;

/**
 * CloseAllHandlerTest
 * @author Andrew Bowley
 * 27 Apr 2016
 */
public class CloseAllHandlerTest
{
    @Test
    public void test_execute()
    {
        CloseAllHandler underTest = new CloseAllHandler();
        JobScheduler jobScheduler = mock(JobScheduler.class);
        ChatService chatService = mock(ChatService.class);
        underTest.jobScheduler = jobScheduler;
        underTest.chatService = chatService;
        underTest.execute();
        ArgumentCaptor<Runnable> jobCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(jobScheduler).schedule(eq("Close all"), jobCaptor.capture());
        jobCaptor.getValue().run();
        verify(chatService).close();
    }
}
