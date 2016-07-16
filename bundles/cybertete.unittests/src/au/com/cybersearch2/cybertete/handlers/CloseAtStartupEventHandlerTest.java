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
import org.junit.Test;
import org.osgi.service.event.Event;

import au.com.cybersearch2.cybertete.model.ApplicationState;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.dialogs.SyncQuestionDialog;

/**
 * CloseAtStartupEventHandlerTest
 * @author Andrew Bowley
 * 27 Apr 2016
 */
public class CloseAtStartupEventHandlerTest
{
    @Test
    public void test_handleEvent_shutdown()
    {
        CloseAtStartupEventHandler underTest = new CloseAtStartupEventHandler();
        SyncQuestionDialog questionDialog = mock(SyncQuestionDialog.class);
        when(questionDialog.ask(
                        "Cybertete",
                        "Do you want to quit Cybertete?")).thenReturn(true);
        underTest.questionDialog = questionDialog;
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.eventBroker = eventBroker;
        Event event = mock(Event.class);
        underTest.handleEvent(event);
        verify(eventBroker).post(CyberteteEvents.SHUTDOWN, ApplicationState.shutdown);
    }

    @Test
    public void test_handleEvent_continue()
    {
        CloseAtStartupEventHandler underTest = new CloseAtStartupEventHandler();
        SyncQuestionDialog questionDialog = mock(SyncQuestionDialog.class);
        when(questionDialog.ask(
                        "Cybertete",
                        "Do you want to quit Cybertete?")).thenReturn(false);
        underTest.questionDialog = questionDialog;
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.eventBroker = eventBroker;
        Event event = mock(Event.class);
        underTest.handleEvent(event);
        verify(eventBroker).post(CyberteteEvents.LOGIN, ApplicationState.running);
    }
}
