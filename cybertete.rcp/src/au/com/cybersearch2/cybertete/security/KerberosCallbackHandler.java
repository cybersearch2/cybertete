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

import static org.jxmpp.util.XmppStringUtils.parseLocalpart;

import java.io.IOException;
import java.security.Security;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.window.Window;

import au.com.cybersearch2.controls.CustomDialog;
import au.com.cybersearch2.cybertete.CyberteteException;
import au.com.cybersearch2.cybertete.dialogs.DialogFactory;
import au.com.cybersearch2.cybertete.dialogs.PasswordControls;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.service.LoginData;

/**
 * KerberosCallbackHandler
 * Default callback handler for Krb5LoginModule. 
 * Available for case configuration allows prompt for credentials 
 * and normal Kerboros authentication is not available.
 * This handler only supports name and password callbacks where the
 * former provides a default name which is automatically selected.
 * The user only has to enter password.
 * @author Andrew Bowley
 * 30 Apr 2016
 */
public class KerberosCallbackHandler implements CallbackHandler
{
    /** Singleton required to allow zero parameter constructor to work with dependency injection */
    static KerberosCallbackHandler singleton;
    
    /** Logger */
    Logger logger;
    String name;

    /** Dialog factory */
    @Inject
    DialogFactory dialogFactory;
    /** Synchronize back into the UI-Thread */
    @Inject
    UISynchronize sync;
    /** Container holding information required to log in */
    @Inject
    LoginData loginData;
    /** Event broker service */
    @Inject
    IEventBroker eventBroker;

    /**
     * Create KerberosCallbackHandler object - zero parameter constructor required
     */
    public KerberosCallbackHandler()
    {
    }
    
    /**
     * postConstruct 
     * @param loggerProvider Logger factory
     */
    @PostConstruct
    void postConstruct(ILoggerProvider loggerProvider)
    {
        // This object is to be used by objects created with zero parameters
        setSingleton(this);
        logger = loggerProvider.getClassLogger(KerberosCallbackHandler.class);
        // Set system property for default callback handler in case interactive login configured:
        // useTicketCache=false, doNotPrompt=false
        String className = KerberosCallbackHandler.class.getName();
        Security.setProperty("auth.login.defaultCallbackHandler", className);
        // To avoid ClassNotFoundException thrown from LoginContext.loadDefaultCallbackHandler(), 
        // must load class in advance 
        loadClass(className);
    }

    /**
     * Handle callbacks - only name and password callbacks supported
     * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
     */
    @Override
    public void handle(Callback[] callbacks)
            throws IOException, UnsupportedCallbackException
    {
        if (singleton == null) // postConstruct() not called?
            throw new UnsupportedCallbackException(callbacks[0], "Signon support not available");
        for (Callback callback: callbacks) 
        {
            if (callback instanceof TextOutputCallback) 
            {
                // Ignore
            }
            else if (callback instanceof NameCallback) 
            {
                singleton.handleNameCallback((NameCallback) callback);
            }
            else if (callback instanceof PasswordCallback) 
            {
                singleton.handlePasswordCallback((PasswordCallback) callback);
            }
            else if (callback instanceof ConfirmationCallback) 
            {
                // Ignore
            } 
            else 
            {
                throw new UnsupportedCallbackException(
                    callback, "Unrecognized Callback");
            }
        }
    }

    /**
     * Display dialog for user to enter password
     * @param callback PasswordCallback object
     */
    void handlePasswordCallback(final PasswordCallback callback)
    {
        CustomDialog<PasswordControls> passwordDialog = 
            dialogFactory.passwordDialog("Signon " + name, name);
        PasswordControls passwordControls = passwordDialog.getCustomControls();
        if (passwordDialog.syncOpen(sync) == Window.OK)
            callback.setPassword(passwordControls.getPassword());
        else
            // An exception to report authentication failed will now be thrown.
            // As user cancelled, send notification to suppress reporting.
            // Note synchronous send used so error reporting is suppressed by time event broker returns. 
            eventBroker.send(CyberteteEvents.USER_CANCEL, callback.getPrompt());
    }

    /**
     * Aassigns to callback local part of login user JID.
     * This name will be displayed to the user when prompted for password. 
     * @param callback NameCallback object
     * @throws UnsupportedCallbackException
     */
    void handleNameCallback(NameCallback callback) throws UnsupportedCallbackException
    {
        name = parseLocalpart(loginData.getSessionDetails().getJid());
        // Paranoid check
        if (name.isEmpty()) // This is not expected
            throw new UnsupportedCallbackException(
                    callback, "\"" + callback.getPrompt() + "\" is required for for signon");
        callback.setName(name);
     }

    /**
     * Load this class in final class loader. From LoginContext.loadDefaultCallbackHandler().
     */
    void loadClass(String className)
    {
        // Get the context ClassLoader for this Thread. 
        // If null returned, this indicates the system class loader should be used.
        ClassLoader finalLoader =
                Thread.currentThread().getContextClassLoader();
        if (finalLoader == null) 
            // Get the system class loader for delegation.  This is the default
            // delegation parent for new <tt>ClassLoader</tt> instances, and is
            // typically the class loader used to start the application.
            finalLoader = ClassLoader.getSystemClassLoader();
        try
        {
            Class.forName(className, true, finalLoader);
        }
        catch (ClassNotFoundException e)
        {   // This is not expected!
            String message = className + " not found by classloader";
            logger.error(message, e);
            throw new CyberteteException(message, e);
        }
    }
    
    static private void setSingleton(KerberosCallbackHandler kerberosCallbackHandler)
    {
        singleton = kerberosCallbackHandler;
    }
}
