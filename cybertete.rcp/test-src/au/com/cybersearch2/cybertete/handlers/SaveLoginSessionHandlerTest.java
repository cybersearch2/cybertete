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

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.security.ProviderException;

import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.cybertete.service.LoginData;
import au.com.cybersearch2.dialogs.SyncErrorDialog;
import au.com.cybersearch2.e4.JobScheduler;

/**
 * SaveLoginSessionHandlerTest
 * @author Andrew Bowley
 * 28 Apr 2016
 */
public class SaveLoginSessionHandlerTest
{
    static final String TEST_JID = "mickymouse@disney.com";

    @Test
    public void test_postConstruct()
    {
        SaveLoginSessionHandler underTest = new SaveLoginSessionHandler();
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(SaveLoginSessionHandler.class)).thenReturn(logger );
        underTest.postConstruct(loggerProvider );
        assertThat(underTest.logger).isEqualTo(logger);
    }

    @Test
    public void test_saveLoginSessionHandler()
    {
        SaveLoginSessionHandler underTest = new SaveLoginSessionHandler();
        LoginData loginData = mock(LoginData.class);
        underTest.loginData = loginData;
        JobScheduler jobScheduler = mock(JobScheduler.class);
        underTest.jobScheduler = jobScheduler;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(SaveLoginSessionHandler.class)).thenReturn(logger );
        underTest.postConstruct(loggerProvider );
        underTest.saveLoginSessionHandler(TEST_JID);
        ArgumentCaptor<Runnable> jobCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(jobScheduler).schedule(eq("Save login configuration for " + TEST_JID), jobCaptor.capture());
        jobCaptor.getValue().run();
        verify(logger).info("Saving configuration for " + TEST_JID);
        verify(loginData).persist(TEST_JID);
    }

    @Test
    public void test_saveLoginSessionHandler_exception()
    {
        SaveLoginSessionHandler underTest = new SaveLoginSessionHandler();
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        underTest.errorDialog = errorDialog;
        LoginData loginData = mock(LoginData.class);
        ProviderException exception = new ProviderException("File error", new IOException("Unexpected EOF"));
        doThrow(exception)
        .when(loginData).persist(TEST_JID);
        underTest.loginData = loginData;
        JobScheduler jobScheduler = mock(JobScheduler.class);
        underTest.jobScheduler = jobScheduler;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(SaveLoginSessionHandler.class)).thenReturn(logger );
        underTest.postConstruct(loggerProvider );
        underTest.saveLoginSessionHandler(TEST_JID);
        ArgumentCaptor<Runnable> jobCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(jobScheduler).schedule(eq("Save login configuration for " + TEST_JID), jobCaptor.capture());
        jobCaptor.getValue().run();
        verify(logger).info("Saving configuration for " + TEST_JID);
        verify(logger).error("Configuration backup to disk failed", exception.getCause());
        verify(errorDialog).showError("Configuration Error", "Configuration backup to disk failed");
    }
}
