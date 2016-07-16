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
package au.com.cybersearch2.cybertete.views;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;

import au.com.cybersearch2.cybertete.handlers.ConfigNotifier;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.service.ChatService;
import au.com.cybersearch2.cybertete.model.service.ConnectLoginTask;
import au.com.cybersearch2.cybertete.model.service.LoginController;
import au.com.cybersearch2.cybertete.service.ChatLoginController;
import au.com.cybersearch2.e4.JobScheduler;

/**
 * AdvancedLoginController
 * LoginController implementation for advanced login, which bypasses displaying a login dialog to the user
 * @author Andrew Bowley
 * 8 Mar 2016
 */
@Creatable
public class AdvancedLoginController implements LoginController
{
    /** Event broker service */
    @Inject
    IEventBroker eventBroker;
    /** Controls the login process */
    @Inject
    ChatLoginController loginController;
    /** Chat service which logs into Chat server specified by user */
    @Inject
    ChatService chatService;
    /** Notifies configuration events */
    @Inject 
    ConfigNotifier configNotifier;
    /** Job scheduler */
    @Inject 
    JobScheduler jobScheduler;

    /**
     * Process OK button pressed in background
     */
    protected void onLogin()
    {
        jobScheduler.schedule("Advanced Login", new Runnable(){

            @Override
            public void run()
            {
                // Close existing chat service to log off
                chatService.close();
                // Perform user login bypassing ChatLoginController dialog 
                if (chatService.startSession(AdvancedLoginController.this))
                {   // Send event to handler which saves the configuration
                    configNotifier.saveLoginConfig(loginController.getUser());
                    // Send event to switch to default perspective
                    eventBroker.post(CyberteteEvents.PERSPECTIVE_DEFAULT, "Offline");
                }
            }
        });
    }

    /**
     * Overrides ChatLoginController login() to avoid popping up Login Dialog
     * @see au.com.cybersearch2.cybertete.model.service.LoginController#login(au.com.cybersearch2.cybertete.model.service.ConnectLoginTask)
     */
    @Override
    public boolean login(ConnectLoginTask serviceLoginTask)
    {
        return loginController.connectWithProgress(serviceLoginTask);
    }

}
