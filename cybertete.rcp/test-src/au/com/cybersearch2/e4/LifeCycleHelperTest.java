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

import static org.mockito.Mockito.*;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.junit.Test;
import org.osgi.service.event.EventHandler;

/**
 * LifeCycleHelperTest
 * @author Andrew Bowley
 * 24 May 2016
 */
public class LifeCycleHelperTest
{
    static final String[] COMMAND_LINE_ARGS = 
    {
        "-os","win32","-ws","win32","-arch","x86","-consoleLog","-clearPersistedState","-data","@/users/mickymouse","-debug"   
    };
    
    @Test
    public void test_subscribeStartupComplete()
    {
        LifeCycleHelper underTest = new LifeCycleHelper();
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.eventBroker = eventBroker;
        EventHandler eventHandler = mock(EventHandler.class);
        underTest.subscribeStartupComplete(eventHandler);
        verify(eventBroker).subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, eventHandler);
    }
    
    @Test
    public void test_getArgValue()
    {
        LifeCycleHelper underTest = new LifeCycleHelper();
        List<String> args = new ArrayList<String>();
        for (String arg: COMMAND_LINE_ARGS)
            args.add(arg);
        assertThat(underTest.getArgValue("clearPersistedState", args, true)).isEqualTo("true");
        assertThat(underTest.getArgValue("data", args, false)).isEqualTo("@/users/mickymouse");
        assertThat(underTest.getArgValue("nonsense", args, false)).isNull();
        assertThat(underTest.getArgValue("nonsense", args, true)).isEqualTo("false");
    }
}
