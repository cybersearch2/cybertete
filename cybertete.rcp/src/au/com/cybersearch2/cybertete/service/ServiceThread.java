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
package au.com.cybersearch2.cybertete.service;

import java.lang.Thread.UncaughtExceptionHandler;

import au.com.cybersearch2.cybertete.model.service.ConnectLoginTask;
import au.com.cybersearch2.cybertete.model.service.ServiceThreadManager;

/**
 * ServiceThread
 * Executes connect and login to Chat server in progress monitor fork 
 * @author Andrew Bowley
 * 18 Mar 2016
 */
public class ServiceThread
{
    final static long INTERRUPT_WAIT = 5000;
    
    /** The thread to monitor */
    Thread serviceThread;
    /** Controls login process */
    ServiceThreadManager serviceThreadManager;
    
    /**
     * Construct ServiceThread object
     * @param serviceThreadManager Operations supporting ServiceThread implementation
     * @param serviceLoginTask Connects to Chat server, authenticates and loads roster
     */
    public ServiceThread(final ServiceThreadManager serviceThreadManager, final ConnectLoginTask serviceLoginTask)
    {
        this.serviceThreadManager = serviceThreadManager;
        serviceThread = new Thread() {
            
            @Override
            public void run()
            {
                serviceThreadManager.connectLogin(serviceLoginTask);
            }
        };
    }
    
    /**
     * Returns flag set ture if service thread is alive. 
     * @return boolean
     */
    public boolean isAlive()
    {
        return serviceThread.isAlive();
    }

    /**
     * Waits specified number of milli seconds for service thread to terminate
     * @param timeout Wait ticks
     * @throws InterruptedException
     */
    public void syncWait(long timeout) throws InterruptedException
    {
        synchronized(serviceThread)
        {
            serviceThread.wait(timeout);
        }
    }
    
    /**
     * Interrupts and joins the service thread
     * @throws InterruptedException 
     */
    public void interrupt() throws InterruptedException
    {
        serviceThreadManager.onInterrupt();
        serviceThread.interrupt();
        serviceThread.join(INTERRUPT_WAIT);
    }
    
    /**
     * Wake up the monitor thread for status update
     */
    public void wakeUp()
    {
        synchronized(serviceThread)
        {
            serviceThread.notifyAll();
        }
    }

    /**
     * Start service thread with uncaught exception handler chained to this object
     */
    public void start()
    {
        final UncaughtExceptionHandler chainHandler = serviceThread.getUncaughtExceptionHandler();
        serviceThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){
    
            @Override
            public void uncaughtException(Thread t, Throwable e)
            {
                serviceThreadManager.uncaughtException(t, e);
                chainHandler.uncaughtException(t, e);
            }});
        serviceThread.start();
    }
}

