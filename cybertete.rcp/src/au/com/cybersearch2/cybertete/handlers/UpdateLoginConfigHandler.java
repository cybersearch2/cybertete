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

import static org.jxmpp.util.XmppStringUtils.isBareJid;

import java.security.ProviderException;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;

import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.LoginBean;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.security.LoginStatus;
import au.com.cybersearch2.cybertete.security.PersistentSecurityData;
import au.com.cybersearch2.cybertete.service.LoginData;
import au.com.cybersearch2.dialogs.SyncErrorDialog;
import au.com.cybersearch2.dialogs.SyncQuestionDialog;
import au.com.cybersearch2.e4.JobScheduler;

/**
 * UpdateLoginConfigHandler
 * Handles event UPDATE_LOGIN_CONFIG.
 * Performs in background perservation of current, on-screen login configuration.
 * Validates data and displays error messages.
 * Prompts user before account deletion.
 * @author Andrew Bowley
 * 1 Mar 2016
 */
public class UpdateLoginConfigHandler
{
    public static final int MIN_PASSWORD_LENGTH = 6;
    private static final String UPDATE_ERROR = "Changes not applied";

    /** Logger */
    Logger logger;

    /** Persists current login session configuration */
    @Inject
    SaveLoginSessionHandler saveLoginSessionHandler;
    /** SSL configuration saved as preferences */
    @Inject
    PersistentSecurityData persistentSecurityData;
    /** Container holding information required to log in */
    @Inject
    LoginData loginData;
    /** Job scheduler for performing tasks in background thread */
    @Inject 
    JobScheduler jobScheduler;
    /** Standard confirmation dialog */
    @Inject
    SyncQuestionDialog syncQuestionDialog;
    /** Standard error dialog */
    @Inject
    SyncErrorDialog errorDialog;
    /** Synchronizes with UI thread */
    @Inject
    UISynchronize sync;
    
    /**
     * Post construct.
     * @param loggerProvider Logger factory
     */
    @PostConstruct
    public void postConstruct(ILoggerProvider loggerProvider) 
    {
        logger = loggerProvider.getClassLogger(UpdateLoginConfigHandler.class);
    }

    /**
     * Handle update login configuration event
     * @param loginConfigEnsemble Login configuration details to be saved along with callback method to call on completion.
     */
    @Inject @Optional
    void onSaveLoginConfigHandler(final @UIEventTopic(CyberteteEvents.UPDATE_LOGIN_CONFIG) LoginConfigEnsemble loginConfigEnsemble)
    {
        jobScheduler.schedule("Save login configuration", new Runnable(){

            @Override
            public void run()
            {
                LoginBean loginConfig = loginConfigEnsemble.getLoginBean();
                final LoginStatus loginStatus = saveLoginConfig(loginConfig, loginConfigEnsemble.isShowMessage());
                sync.asyncExec(new Runnable(){

                    @Override
                    public void run()
                    {
                        loginConfigEnsemble.getUpdateLoginConfigEvent().onUpdateComplete(loginStatus);
                    }});
                if (loginStatus == LoginStatus.noError)
                    saveLoginSessionHandler.persistCurrentLoginSession(loginConfig.getJid());
            }});
    }

    /**
     * Handle save last user event
     * @param user User JID 
     */
    @Inject @Optional
    void onSaveLastUserConfigHandler(final @UIEventTopic(CyberteteEvents.LAST_USER_CONFIG) String user)
    {
        jobScheduler.schedule("Save last user configuration", new Runnable(){

            @Override
            public void run()
            {
                loginData.saveLastUser(user);
            }});
    }

    /**
     * Save login configuration
     * @param loginBean Login configation to save
     * @param isShowMessage Flag set true if error messages to be displayed in a standard dialog
     */
    protected LoginStatus saveLoginConfig(final LoginBean loginBean, boolean isShowMessage)
    {
        try
        {   // Save configuration to disk
            LoginStatus loginStatus = applyChanges(loginBean, isShowMessage);
            if (loginStatus == LoginStatus.noError)
            {   // Complete pending session deletions upon user confirmation   
                Set<SessionDetails> deletedSessions = loginData.getDeletedSessions();
                if (!deletedSessions.isEmpty())
                    loginData.applyChanges(deletedSessions, onSessionsDeleted(deletedSessions));
            }
            return loginStatus;
        }
        catch (ProviderException e)
        {
            logger.error(e, UPDATE_ERROR);
            errorDialog.showError(UPDATE_ERROR, e.getMessage());
            return LoginStatus.fail;
        }
    }

    /**
     * Apply changes from given login configuration
     * @param loginBean Login configuration containing updates
     * @param showMessage Flag set true if error messages are to be diplayed to the user
     * @return LoginStatus - no error, fail or invalid password
     */
    protected LoginStatus applyChanges(LoginBean loginBean, boolean showMessage)
    {
        String jid = loginBean.getJid();
        String host = loginBean.getHost();
        String username = loginBean.getUsername();
        if (jid.equals("")) 
        {
            if (showMessage)
                errorDialog.showError("Invalid JID", "JID field must not be blank.");
            return LoginStatus.fail;
        }
        if (!isBareJid(jid)) 
        {
            if (showMessage)
                errorDialog.showError("Invalid JID", "JID field format incorrect.");
            return LoginStatus.fail;
        }
        String gssapiPrincipal = loginBean.getGssapiPrincipal();
        if (gssapiPrincipal != null)
        {
            boolean principalMatch = jid.toLowerCase().startsWith(gssapiPrincipal.toLowerCase());
            if (!principalMatch || (jid.charAt(gssapiPrincipal.length()) != '@'))
            {
                if (showMessage)
                    errorDialog.showError( "Invalid JID",
                            "JID for Single Signon must start with " + gssapiPrincipal + "@");
                return LoginStatus.fail;
            }
        }
        // TODO - Make min password length configurable
        String password = loginBean.getPassword();
        if ((password.length() < MIN_PASSWORD_LENGTH) && 
            (gssapiPrincipal == null) &&
            !persistentSecurityData.isClientCertAuth())
        {
            if (showMessage)
            {
                errorDialog.showError("Invalid Password",
                        "Password must contain at least " + MIN_PASSWORD_LENGTH + " characters");
            }
            return LoginStatus.invalidPassword;
        }
        SessionDetails newSessionDetails = new SessionDetails(jid, password);
        if (!host.isEmpty())
        {
            newSessionDetails.setHost(host);
            int port = loginBean.getPort();
            newSessionDetails.setPort(port);
        }
        if (!username.isEmpty())
            newSessionDetails.setAuthcid(username);
        newSessionDetails.setPlainSasl(loginBean.isPlainSasl());
        newSessionDetails.setGssapi(gssapiPrincipal != null);
        loginData.setSessionDetails(newSessionDetails);
        loginData.updateAutoLogin(loginBean.isAutoLogin());
        return LoginStatus.noError;
    }

    /**
     * Delete given login configurations
     * @param deletedSessions The configurations to delete
     * @return Flag set true if no error
     */
    protected boolean onSessionsDeleted(Set<SessionDetails> deletedSessions)
    {
        StringBuilder builder = new StringBuilder("Delete ");
        if (deletedSessions.size() == 1)
            builder.append("this account");
        else
            builder.append("these accounts");
        builder.append("?\n");
        for (SessionDetails sessionDetails: deletedSessions)
            builder.append(sessionDetails.getJid()).append('\n');
        if (syncQuestionDialog.ask("Cybertete", builder.toString()))
        {
            loginData.remove(deletedSessions);
            logger.info(deletedSessions.size() + " account(s) deleted");
            return true;
        }
        return false;
    }
    
}
