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

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.security.Principal;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;

import au.com.cybersearch2.cybertete.CyberteteException;
import au.com.cybersearch2.cybertete.GlobalProperties;
import au.com.cybersearch2.dialogs.SyncErrorDialog;

/**
 * KerberosData
 * Keberos login information 
 * @author Andrew Bowley
 * 17 Mar 2016
 */
public class KerberosData
{
    /** The Kerboros config name matches one used by Smack library */
    private static final String KERBEROS_CONFIG_FILE = "gss.conf";

    Logger logger;

    /** Properties such as System properties which are universally accessible */
    @Inject
    GlobalProperties globalProperties;
    /** The security resource environment */
    @Inject
    SecurityResources securityResources;
    /** Creates objects that provide access to the file systems */
    @Inject
    FileSystem fileSystem;
    /** Display error message */
    @Inject 
    SyncErrorDialog errorDialog;

    /**
     * postConstruct()
     * @param loggerProvider Logger factory
     */
    @PostConstruct
    public void postConstruct(ILoggerProvider loggerProvider)
    {
        logger = loggerProvider.getClassLogger(KerberosData.class);
        logger.info("System property \"" + GlobalProperties.AUTH_LOGIN + "\": " + globalProperties.getAuthLoginConfigPath());
    }

    /**
     * Returns flag set true if single signon is enabled by having a valid Java GSS-API authenication configuration
     * @return boolean
     */
    public boolean isSingleSignonEnabled()
    {
    	if (!globalProperties.authLoginConfigExists())
    		return false;
        try
        {
        	AppConfigurationEntry[] appConfig = securityResources.getAppConfigurationEntry();
            return appConfig != null ? appConfig.length > 0 : false;
        }
        catch (SecurityException e)
        {
            throw new CyberteteException("Error reading Kerberos configuration", e);
        }
    }

    /**
     * Returns the Kerberos Principal obtained using Krb5LoginModule. Replicates the LoginContext
     * login() call made by Java GSS internally. The user will be shown the Kerberos Principal and
     * requested to input the host so a SASL-GSSAPI login can proceed. The DialogCallbackHandler
     * passed to the LoginContext is only a fallback when no other options are possible and 
     * "doNotPrompt" in the login configuration is set to "false" or omitted.
     * @return name of GSSAPI principal stripped of domain
     */
    public String getGssapiPrincipal()  
    {
        String gssapiPrincipal = null;
        String loginConfigPath = globalProperties.getAuthLoginConfigPath();
        if (!loginConfigPath.isEmpty())
        {
            try 
            {
                LoginContext lc = securityResources.loginContextInstance();
                // Attempt authentication
                // You might want to do this in a "for" loop to give
                // user more than one chance to enter correct username/password
                lc.login();
                Set<Principal> principals = lc.getSubject().getPrincipals();
                if (principals.size() > 0)
                {
                    gssapiPrincipal = principals.iterator().next().toString().toLowerCase();
                }
                lc.logout();
            } 
            catch (LoginException e) 
            {
                logger.error("Error invoking Login Module " + SecurityResources.KRB5_INITIATOR, e);
                errorDialog.showError("JGSS Authentication not supported", e.getMessage());
             }            
        }
        return gssapiPrincipal;
    }

    /**
     * Create Kerberos configuration file required for single signon.
     * @throws IOException
     */
    public void createLoginConfigFile() throws IOException
    {
        String loginConfigPath = globalProperties.getAuthLoginConfigPath();
        // Use the user's home directory if no file path is specified
        Path configPath = null;
        if (loginConfigPath.isEmpty())
        {
            configPath = fileSystem.getPath(globalProperties.getUserHome(), KERBEROS_CONFIG_FILE);
            globalProperties.setAuthLoginConfigPath(configPath.toString());
        }
        else
            configPath = fileSystem.getPath(loginConfigPath);
        securityResources.createDefaultLoginConfigFile(configPath);
    }


}
