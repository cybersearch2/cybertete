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
package au.com.cybersearch2.cybertete.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.net.ssl.SSLContext;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.window.Window;

import au.com.cybersearch2.cybertete.CyberteteException;
import au.com.cybersearch2.cybertete.model.ConnectionError;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.InteractiveLogin;
import au.com.cybersearch2.cybertete.model.service.ConnectLoginTask;
import au.com.cybersearch2.cybertete.model.service.KeystoreConfig;
import au.com.cybersearch2.cybertete.model.service.LoginController;
import au.com.cybersearch2.cybertete.model.service.NetworkListener;
import au.com.cybersearch2.cybertete.model.service.ServiceThreadManager;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.security.KeystoreData;
import au.com.cybersearch2.cybertete.security.KeystoreHelper;
import au.com.cybersearch2.cybertete.security.PersistentSecurityData;
import au.com.cybersearch2.cybertete.security.SslSessionData;
import au.com.cybersearch2.dialogs.SyncErrorDialog;

/**
 * LoginController
 * Prompts user for logon details by launching Logon Dialog,
 * then performs logon while displaying progress dialog.
 * May loop if logon fails so user can view error messages and retry.
 * @author Andrew Bowley
 * 27 Oct 2015
 */
public class ChatLoginController implements LoginController, ServiceThreadManager, NetworkListener 
{
    static final String UNEXPECTED_EXCEPTION = "Unexpected error during login";

    /** User cancel flag - when set true, login error reporting is suppressed */
    volatile boolean userCancel;

    /** New login flag - ignore autologin if true */
    boolean newLogin;
    /** SSL context - initialized lazily */ 
    SSLContext sslContext;
    /** List of transient network listeners to monitor login progress */
    List<NetworkListener> networkListenerList;
    /** Logger */
    Logger logger;
    
    /** Logger provider */
    @Inject
    ILoggerProvider loggerProvider;
    
    /** Container holding information required to log in */
    @Inject
    LoginData loginData;
    /** The dialog which collects JID and password from user */
    @Inject
    InteractiveLogin dialog;
    @Inject
    /** SSL configuration saved as preferences */
    PersistentSecurityData persistentSecurityData;
    /** Keystore load and get SSL context helper */
    @Inject
    KeystoreHelper keystoreHelper;
    /** Event broker service */
    @Inject
    IEventBroker eventBroker; 
    /** Error dialog */
    @Inject
    SyncErrorDialog errorDialog;
    
    /**
     * postConstruct()
     */
    @PostConstruct
    public void postConstruct()
    {
        logger = loggerProvider.getClassLogger(ChatLoginController.class);
        networkListenerList = new ArrayList<NetworkListener>();
    }

    @PreDestroy
    public void preDestroy()
    {
        close();
    }

    public void setNewLogin(boolean newLogin)
    {
        this.newLogin = newLogin;
    }

    /**
     * @see au.com.cybersearch2.cybertete.model.service.LoginController#login(au.com.cybersearch2.cybertete.model.service.ConnectLoginTask)
     */
    @Override
    public boolean login(ConnectLoginTask serviceLoginTask)
    {
        boolean isAutologin = !newLogin && loginData.isAutoLogin();
        SessionDetails sessionDetails = loginData.getSessionDetails();
        // Flag first attempt so login dialog will be skipped when auto login true
        boolean firstTry = true;
        // Loop attempting to login until successful or cancel or advance login requested
        while (true)
        {
            if (!isAutologin || sessionDetails.getJid().isEmpty() || !firstTry) 
            {   // Record error in dialog object if repeat attempt 
                if (!firstTry)
                    dialog.setConnectionError(serviceLoginTask.getConnectionError());
                // Open login dialog in main thread and wait for result
                if (dialog.open(loginData) != Window.OK)
                    return false;
            }
            firstTry = false;
            // Run service login task showing progress dialog in separate thread
            if (connectWithProgress(serviceLoginTask))
                break;
        }
        newLogin = false;
        // Update persisted configuration
        dialog.save(loginData.getSessionDetails().getJid());
        return true;
    }

    /**
     * Connect to Chat server, authenticate and load roster
     * @param serviceLoginTask The task to perform the login
     */
    @Override
    public void connectLogin(ConnectLoginTask serviceLoginTask)
    {
        try
        {
            // SSL context is cached locally
            if (sslContext == null)
                initializeSsl();
            serviceLoginTask.connectLogin(loginData.getSessionDetails(), sslContext);
        }
        catch (XmppConnectionException e)
        {
            if (!userCancel)
            {
                logger.error(e);
                errorDialog.showError(e.getMessage(), e.getDetails());
            }
        }
        catch (CyberteteException e)
        {
            if (!userCancel)
            {
                logger.error(e);
                errorDialog.showError("Security Error", e.getMessage());
            }
        }
        finally
        {
            userCancel = false;
        }
    }

    /**
     * Handle network connected
     * @see au.com.cybersearch2.cybertete.model.service.NetworkListener#onConnected(java.lang.String)
     */
    @Override
    public void onConnected(String hostName)
    {
        for (NetworkListener networkListener: networkListenerList)
            networkListener.onConnected(hostName);
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.model.service.NetworkListener#onUnavailable(java.lang.String)
     */
    @Override
    public void onUnavailable(String message)
    {
        // Ignore
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.model.service.NetworkListener#onSecured(au.com.cybersearch2.cybertete.security.SslSessionData)
     */
    @Override
    public void onSecured(SslSessionData sslSessionData)
    {
        // Ignore
    }

    /**
     * Handle network authenticated
     * @see au.com.cybersearch2.cybertete.model.service.NetworkListener#onAuthenticated()
     */
    @Override
    public void onAuthenticated()
    {
        for (NetworkListener networkListener: networkListenerList)
            networkListener.onAuthenticated();
    }

    /**
     * Hook network listener to monitor progress
     * @see au.com.cybersearch2.cybertete.model.service.ServiceThreadManager#addNetworkListener(au.com.cybersearch2.cybertete.model.service.NetworkListener)
     */
    @Override
    public void addNetworkListener(NetworkListener networkListener)
    {
        networkListenerList.add(networkListener);
    }

    /**
     * Unhook network listener
     * @see au.com.cybersearch2.cybertete.model.service.ServiceThreadManager#removeNetworkListener(au.com.cybersearch2.cybertete.model.service.NetworkListener)
     */
    @Override
    public void removeNetworkListener(NetworkListener networkListener)
    {
        networkListenerList.remove(networkListener);
    }
    
    /**
     * Log and report uncaught exceptions
     * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
     */
    @Override
    public void uncaughtException(Thread serviceThread, Throwable exception)
    {
        logger.error(exception, UNEXPECTED_EXCEPTION);
        errorDialog.showError(UNEXPECTED_EXCEPTION, exception.getMessage());
    }

    /**
     * Handle user cancel, which causes the service thread to be interrupted
     * @see au.com.cybersearch2.cybertete.model.service.ServiceThreadManager#onInterrupt()
     */
    @Override
    public void onInterrupt()
    {
        userCancel = true;
    }

    /**
     * Handle save keystore configuration
     * @param keystoreConfig The configuration to save
     */
    @Inject @Optional
    void onSaveKeystoreConfigHandler(@UIEventTopic(CyberteteEvents.SAVE_KEYSTORE_CONFIG) KeystoreConfig keystoreConfig)
    {
        // Force SSL Context reset
        sslContext = null;
    }
    
    /**
     * Handle save client certificate configuration
     * @param isClientCertAuth Flag set true if client certificate authentication employed
     */
    @Inject @Optional
    void onSaveClientCertConfigHandler(@UIEventTopic(CyberteteEvents.SAVE_CLIENT_CERT_CONFIG) Boolean isClientCertAuth)
    {
        // Force SSL Context reset
        sslContext = null;
    }

    /**
     * Handle user cancel from authentication dialog
     * @param prompt The prompt to the user at time of cancel key hit
     */
    @Inject @Optional
    void onUserCancelHandler(@UIEventTopic(CyberteteEvents.USER_CANCEL) String prompt)
    {
        logger.info("Cancelling: " + prompt);
        userCancel = true;
    }

    /**
     * Returns current login user JID
     * @return user JID
     */
    public String getUser()
    {
        return loginData.getSessionDetails().getJid();
    }
    
    /**
     * Closes login dialog window, disposes its shell, and removes this window from its
     * window manager (if it has one).
     */
    public void close()
    {
        dialog.close();
    }

    /**
     * Uses Chat Service to perform logon while displaying progress dialog
     * @param serviceLoginTask Connects to Chat server, authenticates and loads roster
     * @return Flag set true if task completed sucessfully
     */
    public boolean connectWithProgress(final ConnectLoginTask serviceLoginTask) 
    {
        // Display connect and login progress in a dialog
        // Modal dialog must be launched on Main thread
        dialog.displayProgressDialog(this, serviceLoginTask);
        return serviceLoginTask.getConnectionError() == ConnectionError.noError;
    }

    /**
     * Obtain and cache SSL Context used to encrypt packets end to end over the network.
     * Also post any client certificates from keystore in use to the status line.
     */
    void initializeSsl()
    {
        KeystoreConfig keystoreConfig = persistentSecurityData.getKeystoreConfig();
        KeystoreData keystoreData = getKeystoreData(keystoreConfig);
        sslContext = keystoreHelper.getSslContext(keystoreData);
        // Send client certificate details to communications status object
        eventBroker.post(CyberteteEvents.CLIENT_CERT, keystoreData.getCertificateChain());
    }

    /**
     * Returns keystore data from specified configurations
     * @param keystoreConfig KeystoreConfig object
     * @return KeystoreData object
     */
    protected KeystoreData getKeystoreData(KeystoreConfig keystoreConfig)
    {
       return new KeystoreData(keystoreConfig, keystoreHelper);
    }
}
