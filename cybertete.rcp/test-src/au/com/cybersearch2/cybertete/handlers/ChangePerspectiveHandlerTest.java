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
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.junit.Test;

import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.Presence;
import au.com.cybersearch2.e4.ApplicationModel;

/**
 * ChangePerspectiveHandlerTest
 * @author Andrew Bowley
 * 28 Apr 2016
 */
public class ChangePerspectiveHandlerTest
{
    @Test
    public void test_postConstruct()
    {
        ChangePerspectiveHandler underTest = new ChangePerspectiveHandler();
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        underTest.postConstruct(loggerProvider );
        verify(loggerProvider).getClassLogger(ChangePerspectiveHandler.class);
    }

    @Test
    public void test_onDefaultHandler()
    {
        ChangePerspectiveHandler underTest = new ChangePerspectiveHandler();
        ApplicationModel applicationModel = mock(ApplicationModel.class);
        when(applicationModel.switchPerspective("au.com.cybersearch2.cybertete.perspective.default")).thenReturn(true);
        underTest.applicationModel = applicationModel;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(ChangePerspectiveHandler.class)).thenReturn(logger );
        underTest.postConstruct(loggerProvider );
        String message = "Signed on";
        underTest.onDefaultHandler(message);
        verify(logger, times(0)).error("Unable to change to Default Persective with message \"" + message + "\"");
    }
    @Test
    public void test_onDefaultHandler_fail()
    {
        ChangePerspectiveHandler underTest = new ChangePerspectiveHandler();
        ApplicationModel applicationModel = mock(ApplicationModel.class);
        when(applicationModel.switchPerspective("au.com.cybersearch2.cybertete.perspective.default")).thenReturn(false);
        underTest.applicationModel = applicationModel;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(ChangePerspectiveHandler.class)).thenReturn(logger );
        underTest.postConstruct(loggerProvider );
        String message = "Signed on";
        underTest.onDefaultHandler(message);
        verify(logger).error("Unable to change to Default Persective with message \"" + message + "\"");
    }

    @Test
    public void test_onOfflineHandler()
    {
        ChangePerspectiveHandler underTest = new ChangePerspectiveHandler();
        ApplicationModel applicationModel = mock(ApplicationModel.class);
        when(applicationModel.switchPerspective("au.com.cybersearch2.cybertete.perspective.offline")).thenReturn(true);
        underTest.applicationModel = applicationModel;
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.eventBroker = eventBroker;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(ChangePerspectiveHandler.class)).thenReturn(logger );
        underTest.postConstruct(loggerProvider );
        String message = "Signed off";
        underTest.onOfflineHandler(message);
        verify(logger, times(0)).error("Unable to change to Offline Persective with message \"" + message + "\"");
        verify(eventBroker).post(CyberteteEvents.COMMS_OFFLINE, message);
        verify(eventBroker).post(CyberteteEvents.PRESENCE, Presence.offline);
    }
    @Test
    public void test_onOfflineHandler_fail()
    {
        ChangePerspectiveHandler underTest = new ChangePerspectiveHandler();
        ApplicationModel applicationModel = mock(ApplicationModel.class);
        when(applicationModel.switchPerspective("au.com.cybersearch2.cybertete.perspective.offline")).thenReturn(false);
        underTest.applicationModel = applicationModel;
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.eventBroker = eventBroker;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(ChangePerspectiveHandler.class)).thenReturn(logger );
        underTest.postConstruct(loggerProvider );
        String message = "Signed off";
        underTest.onOfflineHandler(message);
        verify(logger).error("Unable to change to Offline Persective with message \"" + message + "\"");
        verify(eventBroker).post(CyberteteEvents.COMMS_OFFLINE, message);
        verify(eventBroker).post(CyberteteEvents.PRESENCE, Presence.offline);
    }

}
