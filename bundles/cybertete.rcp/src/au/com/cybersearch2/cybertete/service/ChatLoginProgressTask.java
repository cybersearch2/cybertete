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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import au.com.cybersearch2.cybertete.model.service.NetworkListener;
import au.com.cybersearch2.cybertete.security.SslSessionData;

/**
 * ChatLoginProgressTask
 * Displays progress" Contacting host", ""Authenticating" and "Receiving roster" in a modal dialog
 * @author Andrew Bowley
 * 18 Nov 2015
 */
public class ChatLoginProgressTask implements IRunnableWithProgress, NetworkListener
{
    /** Interval for checking if cancel button pressed */
    static final long WAIT_MILLIS = 500;
    
    /** Flag set when network connected */
    volatile boolean flagConnected;
    /** Flag set when connection authorised */
    volatile boolean flagAuthorised;
    /** Message received if connection not achieved or fails */
    volatile String unavailableMessage;
    /** Thead to execute Connect and Login Task. Dialog closes when this thread terminates. */
    ServiceThread serviceThread;
    
    /**
     * Create ChatLoginProgressTask object
     * @param serviceThread Thead to execute connect and login task
     */
    public ChatLoginProgressTask(ServiceThread serviceThread)
    {
        this.serviceThread = serviceThread;
    }

    /**
     * Loop in dedicated thread which monitors changes in network status and Cancel button on dialog.
     * Any uncaught exceptions thrown in this thread will be wrapped in an InvocationTargetException. 
     * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void run(IProgressMonitor monitor)
            throws InvocationTargetException, InterruptedException
    {  
        try 
        { 
            monitor.beginTask("Connecting...", IProgressMonitor.UNKNOWN);
            monitor.subTask("Contacting host...");
            // Loop until the service thread is terminated or Cancel button pressed
            while (serviceThread.isAlive())
            {   
                if (flagConnected)
                {
                    flagConnected = false;
                    monitor.subTask("Authenticating...");
                }
                if (flagAuthorised)
                {
                    flagAuthorised = false;
                    monitor.subTask("Receiving roster...");
                }
                // Sample cancel at specified rate
                serviceThread.syncWait(WAIT_MILLIS);
                if (monitor.isCanceled())
                    // Interrupt service thread and wait for it to terminate
                    serviceThread.interrupt();
                else if (unavailableMessage != null)
                    break;
            }
        } 
        finally 
        {
            monitor.done();
        }
   }

    /**
     * Returns host unavailable message
     * @return Message or null if connection established successfully
     */
    public String getUnavailableMessage()
    {
        return unavailableMessage;
    }

    /**
     * Connection error/closed event is implemented, but redundant as exception will terminate Service thread
     * @see au.com.cybersearch2.cybertete.model.service.NetworkListener#onUnavailable(java.lang.String)
     */
    @Override
    public void onUnavailable(String message)
    {
        this.unavailableMessage = message;
        serviceThread.wakeUp();
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.model.service.NetworkListener#onConnected(java.lang.String)
     */
    @Override
    public void onConnected(String hostName)
    {
        flagConnected = true;
        serviceThread.wakeUp();
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.model.service.NetworkListener#onAuthenticated()
     */
    @Override
    public void onAuthenticated()
    {
        flagAuthorised = true;
        serviceThread.wakeUp();
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.model.service.NetworkListener#onSecured(au.com.cybersearch2.cybertete.security.SslSessionData)
     */
    @Override
    public void onSecured(SslSessionData sslSessionData)
    {
        // Secured connection indicated on status line
    }

}
