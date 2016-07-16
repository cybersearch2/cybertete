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

import javax.xml.ws.WebServiceException;

import org.mockito.ArgumentCaptor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.junit.Test;

import au.com.cybersearch2.cybertete.model.ApplicationState;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.InteractiveLogin;
import au.com.cybersearch2.cybertete.model.service.ChatService;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.service.ChatLoginController;
import au.com.cybersearch2.cybertete.service.LoginData;
import au.com.cybersearch2.dialogs.SyncQuestionDialog;
import au.com.cybersearch2.e4.JobScheduler;

/**
 * LoginHandlerTest
 * @author Andrew Bowley
 * 11 Mar 2016
 */
public class LoginHandlerTest
{
    static final String TEST_JID = "mickymouse@disney.com";

    class TestSetup
    {
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        JobScheduler jobScheduler = mock(JobScheduler.class);
        InteractiveLogin loginDialog = mock(InteractiveLogin.class);
        IEventBroker eventBroker = mock(IEventBroker.class);
        ArgumentCaptor<Runnable> loginTaskCaptor = ArgumentCaptor.forClass(Runnable.class);

        TestSetup(LoginHandler loginHandler)
        {
            loginHandler.eventBroker = eventBroker;
            loginHandler.jobScheduler = jobScheduler;
            loginHandler.loginDialog = loginDialog;
            when(loggerProvider.getClassLogger(LoginHandler.class)).thenReturn(logger );
            loginHandler.postConstruct(loggerProvider);
            loginHandler.loginHandler(ApplicationState.running);
            verify(logger).debug("Login event received");
            verify(jobScheduler).schedule(eq("Login"), loginTaskCaptor.capture());
        }
    }
    
    @Test
    public void test_loginHandler()
    {
        LoginHandler loginHandler = new LoginHandler();
        TestSetup underTest = new TestSetup(loginHandler);
        ChatLoginController loginController = mock(ChatLoginController.class);
        loginHandler.loginController = loginController;
        ChatService chatService = mock(ChatService.class);
        loginHandler.chatService = chatService;
        when(chatService.startSession(loginController)).thenReturn(true);
        underTest.loginTaskCaptor.getValue().run();
    }

    @Test
    public void test_loginHandler_exception()
    {
        LoginHandler loginHandler = new LoginHandler();
        TestSetup underTest = new TestSetup(loginHandler);
        SyncQuestionDialog questionDialog = mock(SyncQuestionDialog.class);
        // Quit, else infinite loop
        when(questionDialog.ask("Cybertete", "Unexpected error occured during login.\nDo you want to quit Cybertete?")).thenReturn((true));
        loginHandler.questionDialog = questionDialog;
        ChatLoginController loginController = mock(ChatLoginController.class);
        loginHandler.loginController = loginController;
        Throwable throwable = new WebServiceException("Network down");
        ChatService chatService = mock(ChatService.class);
        loginHandler.chatService = chatService;
        LoginData loginData = mock(LoginData.class);
        loginHandler.loginData = loginData; 
        when(chatService.startSession(loginController)).thenThrow(throwable);
        SessionDetails sessionDetails= mock(SessionDetails.class);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        when(loginData.getSessionDetails()).thenReturn(sessionDetails);
        underTest.loginTaskCaptor.getValue().run();
        verify(underTest.logger).error(throwable, "Error logging in with JID \"" + TEST_JID + "\"");
        verify(chatService).close();
        verify(underTest.logger).debug("User quits");
        verify(underTest.eventBroker).post(CyberteteEvents.SHUTDOWN, ApplicationState.shutdown);
    }

    @Test
    public void test_loginHandler_advanced()
    {
        LoginHandler loginHandler = new LoginHandler();
        TestSetup underTest = new TestSetup(loginHandler);
        ChatLoginController loginController = mock(ChatLoginController.class);
        loginHandler.loginController = loginController;
        ChatService chatService = mock(ChatService.class);
        loginHandler.chatService = chatService;
        when(chatService.startSession(loginController)).thenReturn(false);
        when(underTest.loginDialog.showAdvanceOptions()).thenReturn(true);
        LoginData loginData = mock(LoginData.class);
        loginHandler.loginData = loginData; 
        underTest.loginTaskCaptor.getValue().run();
        verify(chatService).close();
        verify(underTest.loginDialog).clearAdvanceOptions();
        verify(underTest.eventBroker).post(CyberteteEvents.PERSPECTIVE_OFFLINE, "Login");
    }

    @Test
    public void test_loginHandler_fail()
    {
        LoginHandler loginHandler = new LoginHandler();
        TestSetup underTest = new TestSetup(loginHandler);
        SyncQuestionDialog questionDialog = mock(SyncQuestionDialog.class);
        // Quit, else infinite loop
        when(questionDialog.ask("Cybertete", "Do you want to quit Cybertete?")).thenReturn((true));
        loginHandler.questionDialog = questionDialog;
        ChatLoginController loginController = mock(ChatLoginController.class);
        loginHandler.loginController = loginController;
        ChatService chatService = mock(ChatService.class);
        loginHandler.chatService = chatService;
        when(chatService.startSession(loginController)).thenReturn(false);
        LoginData loginData = mock(LoginData.class);
        loginHandler.loginData = loginData; 
        SessionDetails sessionDetails= mock(SessionDetails.class);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        when(loginData.getSessionDetails()).thenReturn(sessionDetails);
        underTest.loginTaskCaptor.getValue().run();
        verify(chatService).close();
        verify(underTest.logger).debug("User quits");
        verify(underTest.eventBroker).post(CyberteteEvents.SHUTDOWN, ApplicationState.shutdown);
    }

}
