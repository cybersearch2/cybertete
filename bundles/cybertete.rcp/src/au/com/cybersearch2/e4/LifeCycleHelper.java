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

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.event.EventHandler;

/**
 * LifeCycleHelper
 * Support for LifeCycle handlers
 * @author Andrew Bowley
 * 24 May 2016
 */
@Creatable
public class LifeCycleHelper
{
    /* Event broker service */
    @Inject 
    IEventBroker eventBroker;

    /**
     * Subscribes a handler for application startup complete event
     * @param eventHandler Event handler
     */
    public void subscribeStartupComplete(EventHandler eventHandler)
    {
        eventBroker.subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, eventHandler);
    }
    
    /**
     * Get argument from command line
     * @param argName Name
     * @param args Command line arguments
     * @param singledCmdArgValue Flag set if single argument on command line
     * @return argument or null if not found
     */
    public String getArgValue(String argName, List<String> args, boolean singledCmdArgValue) 
    {
        // Is it in the arg list ?
        if (argName == null || argName.length() == 0)
            return null;

        if (singledCmdArgValue) 
        {
            for (String arg : args) 
            {
                if (("-" + argName).equals(arg))
                    return Boolean.TRUE.toString();
            }
            return Boolean.FALSE.toString();
        } 
        // not a singleCmdArgValue
        for (int i = 0; i < args.size(); i++) 
        {
            if (("-" + argName).equals(args.get(i)) && (i + 1 < args.size()))
                return args.get(i + 1);
        }
        return null;
     } 

}
