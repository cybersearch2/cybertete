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
package au.com.cybersearch2.cybertete.security;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.security.Principal;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.junit.Before;
import org.junit.Test;

import au.com.cybersearch2.cybertete.CyberteteException;
import au.com.cybersearch2.cybertete.GlobalProperties;
import au.com.cybersearch2.dialogs.SyncErrorDialog;

/**
 * KerberosDataTest
 * @author Andrew Bowley
 * 29 Apr 2016
 */
public class KerberosDataTest
{
    static final String KRB_NT_PRINCIPAL  = "micky@DISNEY.COM";
    static final String AUTH_CONFIG_PATH = "/jaas/loginconfig";

    GlobalProperties globalProperties;

    @Before
    public void setUp()
    {
        globalProperties = mock(GlobalProperties.class);
    }
    
    @Test
    public void test_postConstruct()
    {
        KerberosData underTest = new KerberosData();
        underTest.globalProperties = globalProperties;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(KerberosData.class)).thenReturn(logger );
        when(globalProperties.getAuthLoginConfigPath()).thenReturn(AUTH_CONFIG_PATH);
        underTest.postConstruct(loggerProvider );
        assertThat(underTest.logger).isEqualTo(logger);
    }

    @Test
    public void test_isSingleSignonEnabled()
    {
        SecurityResources securityResources = mock(SecurityResources.class);
        AppConfigurationEntry appConfigurationEntry = mock(AppConfigurationEntry.class);  
        AppConfigurationEntry[] loginConfig = new AppConfigurationEntry[]{appConfigurationEntry};  
        when(securityResources.getAppConfigurationEntry()).thenReturn(loginConfig);
        when(globalProperties.getAuthLoginConfigPath()).thenReturn(AUTH_CONFIG_PATH);
        KerberosData underTest = new KerberosData();
        underTest.globalProperties = globalProperties;
        underTest.securityResources = securityResources;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(KerberosData.class)).thenReturn(logger );
        when(globalProperties.authLoginConfigExists()).thenReturn(true);
        when(globalProperties.getAuthLoginConfigPath()).thenReturn(AUTH_CONFIG_PATH);
        underTest.postConstruct(loggerProvider );
        assertThat(underTest.isSingleSignonEnabled()).isTrue();
    }

    @Test
    public void test_isSingleSignonEnabled_empty()
    {
        SecurityResources securityResources = mock(SecurityResources.class);
        AppConfigurationEntry[] loginConfig = new AppConfigurationEntry[]{};  
        when(securityResources.getAppConfigurationEntry()).thenReturn(loginConfig);
        when(globalProperties.getAuthLoginConfigPath()).thenReturn(AUTH_CONFIG_PATH);
        KerberosData underTest = new KerberosData();
        underTest.globalProperties = globalProperties;
        underTest.securityResources = securityResources;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(KerberosData.class)).thenReturn(logger );
        when(globalProperties.getAuthLoginConfigPath()).thenReturn(AUTH_CONFIG_PATH);
        underTest.postConstruct(loggerProvider );
        assertThat(underTest.isSingleSignonEnabled()).isFalse();
    }

    @Test
    public void test_isSingleSignonEnabled_exception()
    {
        SecurityResources securityResources = mock(SecurityResources.class);
        SecurityException exception = new SecurityException("Corrupt file");
        when(securityResources.getAppConfigurationEntry()).thenThrow(exception);
        KerberosData underTest = new KerberosData();
        underTest.securityResources = securityResources;
        underTest.globalProperties = globalProperties;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(KerberosData.class)).thenReturn(logger );
        when(globalProperties.authLoginConfigExists()).thenReturn(true);
        when(globalProperties.getAuthLoginConfigPath()).thenReturn(AUTH_CONFIG_PATH);
        underTest.postConstruct(loggerProvider );
        try
        {
            underTest.isSingleSignonEnabled();
            failBecauseExceptionWasNotThrown(CyberteteException.class);
        }
        catch(CyberteteException e)
        {
            assertThat(e.getMessage()).isEqualTo("Error reading Kerberos configuration");
            assertThat(e.getCause()).isEqualTo(exception);
        }
    }

    @Test
    public void test_getGssapiPrincipal() throws LoginException
    {
        SecurityResources securityResources = mock(SecurityResources.class);
        Principal principal = new KerberosPrincipal(KRB_NT_PRINCIPAL);
        Subject subject = new Subject();
        subject.getPrincipals().add(principal);
        LoginContext loginContext = mock(LoginContext.class);
        when(securityResources.loginContextInstance()).thenReturn(loginContext);
        when(loginContext.getSubject()).thenReturn(subject);
        when(globalProperties.getAuthLoginConfigPath()).thenReturn(AUTH_CONFIG_PATH);
        KerberosData underTest = new KerberosData();
        underTest.securityResources = securityResources;
        underTest.globalProperties = globalProperties;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(KerberosData.class)).thenReturn(logger );
        underTest.postConstruct(loggerProvider );
        assertThat(underTest.getGssapiPrincipal()).isEqualTo("micky@disney.com");
        verify(loginContext).login();
        verify(loginContext).logout();
    }

    @Test
    public void test_getGssapiPrincipal_exception() throws LoginException
    {
        SecurityResources securityResources = mock(SecurityResources.class);
        LoginContext loginContext = mock(LoginContext.class);
        when(securityResources.loginContextInstance()).thenReturn(loginContext);
        GlobalProperties globalProperties = mock(GlobalProperties.class);
        when(globalProperties.getAuthLoginConfigPath()).thenReturn(AUTH_CONFIG_PATH);
        KerberosData underTest = new KerberosData();
        underTest.globalProperties = globalProperties;
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        underTest.securityResources = securityResources;
        underTest.errorDialog = errorDialog;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(KerberosData.class)).thenReturn(logger);
        LoginException exception = new LoginException("Kerboros down");
        doThrow(exception)
        .when(loginContext).login();
        underTest.postConstruct(loggerProvider );
        assertThat(underTest.getGssapiPrincipal()).isNull();
        verify(logger).error("Error invoking Login Module com.sun.security.jgss.initiate", exception);
        verify(errorDialog).showError("JGSS Authentication not supported", "Kerboros down");

    }

    @Test
    public void test_getGssapiPrincipal_no_config()
    {
       when(globalProperties.getAuthLoginConfigPath()).thenReturn("");
        KerberosData underTest = new KerberosData();
        underTest.globalProperties = globalProperties;
       assertThat(underTest.getGssapiPrincipal()).isNull();
    }
    
    @Test
    public void test_createLoginConfigFile() throws IOException
    {
        SecurityResources securityResources = mock(SecurityResources.class);
        final Path filePath = mock(Path.class);
        GlobalProperties globalProperties = mock(GlobalProperties.class);
        when(globalProperties.getAuthLoginConfigPath()).thenReturn(AUTH_CONFIG_PATH);
        KerberosData underTest = new KerberosData();
        underTest.securityResources = securityResources;
        underTest.globalProperties = globalProperties;
        FileSystem fileSystem = mock(FileSystem.class);
        when(fileSystem.getPath(AUTH_CONFIG_PATH)).thenReturn(filePath);
        underTest.fileSystem = fileSystem;
        underTest.createLoginConfigFile();
        verify(securityResources).createDefaultLoginConfigFile(filePath);
    }
    
    @Test
    public void test_createLoginConfigFile_no_config() throws IOException
    {
        SecurityResources securityResources = mock(SecurityResources.class);
        final Path filePath = mock(Path.class);
        //final ArrayList<String> fileLines = new ArrayList<String>();
        //for (String line: CONFIG_CONTENTS)
        //    fileLines.add(line);
        String userHomePath = "/users/micky";
        GlobalProperties globalProperties = mock(GlobalProperties.class);
        when(globalProperties.getAuthLoginConfigPath()).thenReturn("");
        when(globalProperties.getUserHome()).thenReturn(userHomePath);
        KerberosData underTest = new KerberosData();
        underTest.securityResources = securityResources;
        underTest.globalProperties = globalProperties;
        FileSystem fileSystem = mock(FileSystem.class);
        when(fileSystem.getPath(userHomePath, "gss.conf")).thenReturn(filePath);
        underTest.fileSystem = fileSystem;
        underTest.createLoginConfigFile();
        verify(securityResources).createDefaultLoginConfigFile(filePath);
        verify(globalProperties).setAuthLoginConfigPath(filePath.toString());
    }

}
