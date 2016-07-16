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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import au.com.cybersearch2.cybertete.model.service.ConnectLoginTask;
import au.com.cybersearch2.cybertete.model.service.ServiceThreadManager;
import au.com.cybersearch2.cybertete.service.ServiceThread;

/**
 * ServiceThreadTest
 * @author Andrew Bowley
 * 21 Mar 2016
 */
public class ServiceThreadTest
{
    @Test
    public void test_constructor() throws InterruptedException
    {
        ServiceThreadManager serviceThreadManager = mock(ServiceThreadManager.class);
        ConnectLoginTask serviceLoginTask = mock(ConnectLoginTask.class);
        ServiceThread underTest = new ServiceThread(serviceThreadManager, serviceLoginTask);
        underTest.serviceThread.start();
        underTest.serviceThread.join();
        verify(serviceThreadManager).connectLogin(serviceLoginTask);
    }

    @Test
    public void test_interrupt() throws InterruptedException
    {
        ServiceThreadManager serviceThreadManager = mock(ServiceThreadManager.class);
        ConnectLoginTask serviceLoginTask = mock(ConnectLoginTask.class);
        ServiceThread underTest = new ServiceThread(serviceThreadManager, serviceLoginTask);
        Thread internalThread = mock(Thread.class);
        underTest.serviceThread = internalThread;
        underTest.interrupt();
        verify(serviceThreadManager).onInterrupt();
        verify(internalThread).interrupt();
        // Thread.join() is final
        //verify(internalThread).join(5000);
    }
}
