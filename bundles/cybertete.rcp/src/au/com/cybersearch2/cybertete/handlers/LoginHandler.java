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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UIEventTopic;

import au.com.cybersearch2.cybertete.model.ApplicationState;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.InteractiveLogin;
import au.com.cybersearch2.cybertete.model.service.ChatService;
import au.com.cybersearch2.cybertete.service.ChatLoginController;
import au.com.cybersearch2.cybertete.service.LoginData;
import au.com.cybersearch2.dialogs.SyncQuestionDialog;
import au.com.cybersearch2.e4.JobScheduler;

/**
 * LoginHandler
 * Triggered by CyberteteEvents.LOGIN event.
 * Initiates Chat server connect, authenticate and load roster while displaying progress monitor.
 * Also pops up prompt to user if Cancel button pressed.
 * Note: Login is initiated directly in E4LifeCycle and AdvancedLoginController 
 * @see au.com.cybersearch2.cybertete.views.AdvancedLoginController
 * @see au.com.cybersearch2.cybertete.E4LifeCycle
 * @author Andrew Bowley
 * 19 Nov 2015
 */
public class LoginHandler
{
    public static final String QUIT_PROMPT = "Do you want to quit Cybertete?";

    /** Logger */
    private Logger logger;
    
    /** Chat Service to implement login */
    @Inject
    ChatService chatService;
    /** Container holding information required to log in */
    @Inject
    LoginData loginData;
    @Inject
    /** Launches Logon Dialog and then performs logon while displaying progress dialog */
    ChatLoginController loginController;
    /** Event broker service */
    @Inject
    IEventBroker eventBroker;
    /** Manages login dialogs */
    @Inject
    InteractiveLogin loginDialog;
    /** Prompts user for yes/no answer */
    @Inject
    SyncQuestionDialog questionDialog;
    /** Schedules job to run in background thread */
    @Inject 
    JobScheduler jobScheduler;

    /**
     * Post construct. 
     * @param loggerProvider ILoggerProvider object
     */
    @PostConstruct
    void postConstruct(ILoggerProvider loggerProvider) 
    {
        logger = loggerProvider.getClassLogger(LoginHandler.class);
    }

    /**
     * Handle login event. Schedules job to perform login operation.
     * @param nextState Next Application state - assumed to be "running"
     * @param shell Active shell for login dialog. Maybe splash screen prior to application launch. 
     */
    @Inject @Optional
    void loginHandler(@UIEventTopic(CyberteteEvents.LOGIN) ApplicationState nextState) 
    {
        logger.debug("Login event received");
        jobScheduler.schedule("Login", new Runnable(){

            @Override
            public void run()
            {
                login();
            }});
    }

    /**
     * Loop performing login interactively until success or user quits
     */
    private void login()
    {
        while (true)
        {
            boolean exceptionThrown = false;
            boolean isSessionStarted = false;
            try
            {
                isSessionStarted = chatService.startSession(loginController);
            }
            catch(Throwable e)
            {
                logger.error(e, "Error logging in with JID \"" + loginData.getSessionDetails().getJid() + "\"");
                exceptionThrown = true;
            }
            if (!isSessionStarted)
                chatService.close();
            if (isSessionStarted)
                break;
            else if (loginDialog.showAdvanceOptions())
            {
                loginDialog.clearAdvanceOptions();
                eventBroker.post(CyberteteEvents.PERSPECTIVE_OFFLINE, "Login");
                break;
            }
            StringBuilder builder = new StringBuilder();
            if (exceptionThrown)
                builder.append("Unexpected error occured during login.\n");
            builder.append(QUIT_PROMPT);
            boolean quit = questionDialog.ask("Cybertete", builder.toString());
            if (quit)
            {
                // Close the application
                logger.debug("User quits");
                shutdown();
                break;
            }
       }
    }

    /** Post shutdown event after user quits */
    private void shutdown()
    {
        eventBroker.post(CyberteteEvents.SHUTDOWN, ApplicationState.shutdown);
    }

}
