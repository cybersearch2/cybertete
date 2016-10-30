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
package au.com.cybersearch2.cybertete.dialogs;

import au.com.cybersearch2.controls.CustomDialog;
import au.com.cybersearch2.cybertete.Activator;
import au.com.cybersearch2.cybertete.handlers.ConfigNotifier;
import au.com.cybersearch2.cybertete.model.ConnectionError;
import au.com.cybersearch2.cybertete.model.InteractiveLogin;
import au.com.cybersearch2.cybertete.model.service.ConnectLoginTask;
import au.com.cybersearch2.cybertete.model.service.ServiceThreadManager;
import au.com.cybersearch2.cybertete.service.ChatLoginProgressTask;
import au.com.cybersearch2.cybertete.service.LoginData;
import au.com.cybersearch2.cybertete.service.ServiceThread;
import au.com.cybersearch2.dialogs.ProgressDialog;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;

/**
 * LoginDialog
 * Displays dialogs for user login and progress monitor for Chat server connect, authenticate and load roster
 * @author Andrew Bowley
 * 19 Feb 2016
 */
public class LoginDialog implements InteractiveLogin
{
    private static final String DIALOG_TITLE = Activator.APPLICATION_TITLE + " Login";
    
    /** Dialog which collects JID and password from user */
    CustomDialog<LoginCustomControls> dialog;
    /** Flag set true if Advance key clicked by user in order to show more options */
    boolean advancedOptions;

    /** Notifies configuration events */
    @Inject 
    ConfigNotifier configNotifier;
    /** Dialog factory */
    @Inject 
    DialogFactory dialogFactory;
    /** Login dialog content */
    @Inject
    DialogLoginControls loginControls;
    /** Runs task in main thread and waits for completion */
    @Inject
    UISynchronize sync;

    /**
     * @see au.com.cybersearch2.cybertete.model.InteractiveLogin#setConnectionError(au.com.cybersearch2.cybertete.model.ConnectionError)
     */
    @Override
    public void setConnectionError(ConnectionError connectionError)
    {
        if (dialog != null)
            dialog.getCustomControls().setConnectionError(connectionError);
    }

    /**
     * @see au.com.cybersearch2.cybertete.model.InteractiveLogin#open(au.com.cybersearch2.cybertete.service.LoginData)
     */
    @Override
    public int open(final LoginData loginData)
    {
        // Initialize LoginDialog lazily
        if (dialog == null)
            dialog = dialogFactory.loginDialogInstance(DIALOG_TITLE, loginControls);
        // Open login dialog in main thread and wait for result
        int returnCode = dialog.syncOpen(sync);
        if (returnCode == Window.CANCEL + 1)
             advancedOptions = true;
        return returnCode;
    }

    /**
     * @see au.com.cybersearch2.cybertete.model.InteractiveLogin#save(java.lang.String)
     */
    @Override
    public void save(String user)
    {   
        // Fire at the save login session handler.
        configNotifier.saveLoginConfig(user);
        // Configuration to save is in LoginData object, so dialog no longer needed
        close();
    }

    /**
     * @see au.com.cybersearch2.cybertete.model.InteractiveLogin#close()
     */
    @Override
    public void close()
    {
        if (dialog != null)
        {
            try
            {
                dialog.syncClose(sync);
            }
            finally
            {
                dialog = null;
            }
        }
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.model.InteractiveLogin#showAdvanceOptions()
     */
    @Override
    public boolean showAdvanceOptions()
    {
        return advancedOptions;
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.model.InteractiveLogin#clearAdvanceOptions()
     */
    @Override
    public void clearAdvanceOptions()
    {
        advancedOptions = false;
    }
 
    /**
     * Display progress dialog
     * @see au.com.cybersearch2.cybertete.model.InteractiveLogin#displayProgressDialog(au.com.cybersearch2.cybertete.model.service.ServiceThreadManager, au.com.cybersearch2.cybertete.model.service.ConnectLoginTask)
     */
    @Override
    public void displayProgressDialog(final ServiceThreadManager serviceThreadManager, final ConnectLoginTask serviceLoginTask)
    {
        final ServiceThread serviceThread = 
            new ServiceThread(serviceThreadManager, serviceLoginTask);
        final IRunnableWithProgress runnableWithProgress = 
            new IRunnableWithProgress()
        {
            /**
             * Loop in dedicated thread which monitors changes in network status and Cancel button on dialog.
             * Any uncaught exceptions thrown in this thread will be wrapped in an InvocationTargetException. 
             * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
             */
            @Override
            public void run(IProgressMonitor monitor)
                    throws InvocationTargetException, InterruptedException
            {
                // Create and run task to receive status changes provided by NetworkListener interface
                ChatLoginProgressTask chatLoginProgressTask = new ChatLoginProgressTask(serviceThread);
                serviceThreadManager.addNetworkListener(chatLoginProgressTask);
                try
                {
                    serviceThread.start();
                    chatLoginProgressTask.run(monitor);
                }
                finally
                {
                    serviceThreadManager.removeNetworkListener(chatLoginProgressTask);
                }
            }
        };
        final ProgressDialog progress = dialogFactory.progressDialogInstance();
        // Modal dialog must be launched on UI thread
        progress.open(runnableWithProgress, serviceThreadManager);
    }

}
