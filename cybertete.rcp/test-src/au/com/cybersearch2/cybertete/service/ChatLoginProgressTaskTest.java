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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;

import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.service.ChatLoginProgressTask;
import au.com.cybersearch2.cybertete.service.ServiceThread;

/**
 * ChatLoginProgressTaskTest
 * @author Andrew Bowley
 * 18 Mar 2016
 */
public class ChatLoginProgressTaskTest
{
    static final String TEST_JID = "kerry@google.com";
    
    @Test
    public void testRun() throws InvocationTargetException, InterruptedException
    {
        IProgressMonitor monitor = mock(IProgressMonitor.class);
        ServiceThread serviceThread = mock(ServiceThread.class);
        when(monitor.isCanceled()).thenReturn(false);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        when(serviceThread.isAlive()).thenReturn(true, false);
        ChatLoginProgressTask chatLoginProgressTask = new ChatLoginProgressTask(serviceThread);
        chatLoginProgressTask.run(monitor);
        verify(monitor).beginTask("Connecting...", IProgressMonitor.UNKNOWN);
        verify(monitor).subTask("Contacting host...");
        verify(serviceThread).syncWait(500);
        verify(monitor).done();
    }

    @Test
    public void testRun_authenticating() throws InvocationTargetException, InterruptedException
    {
        IProgressMonitor monitor = mock(IProgressMonitor.class);
        ServiceThread serviceThread = mock(ServiceThread.class);
        when(monitor.isCanceled()).thenReturn(false);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        when(serviceThread.isAlive()).thenReturn(true, false);
        ChatLoginProgressTask chatLoginProgressTask = new ChatLoginProgressTask(serviceThread);
        chatLoginProgressTask.flagConnected = true;
        chatLoginProgressTask.run(monitor);
        verify(monitor).beginTask("Connecting...", IProgressMonitor.UNKNOWN);
        verify(monitor).subTask("Contacting host...");
        verify(monitor).subTask("Authenticating...");
        verify(serviceThread).syncWait(500);
        verify(monitor).done();
        assertThat(chatLoginProgressTask.flagAuthorised).isFalse();
    }

    @Test
    public void testRun_roster() throws InvocationTargetException, InterruptedException
    {
        IProgressMonitor monitor = mock(IProgressMonitor.class);
        ServiceThread serviceThread = mock(ServiceThread.class);
        when(monitor.isCanceled()).thenReturn(false);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        when(serviceThread.isAlive()).thenReturn(true, false);
        ChatLoginProgressTask chatLoginProgressTask = new ChatLoginProgressTask(serviceThread);
        chatLoginProgressTask.flagAuthorised = true;
        chatLoginProgressTask.run(monitor);
        verify(monitor).beginTask("Connecting...", IProgressMonitor.UNKNOWN);
        verify(monitor).subTask("Contacting host...");
        verify(monitor).subTask("Receiving roster...");
        verify(serviceThread).syncWait(500);
        verify(monitor).done();
        assertThat(chatLoginProgressTask.flagAuthorised).isFalse();
    }

    @Test
    public void testRun_cancel() throws InvocationTargetException, InterruptedException
    {
        IProgressMonitor monitor = mock(IProgressMonitor.class);
        ServiceThread serviceThread = mock(ServiceThread.class);
        when(monitor.isCanceled()).thenReturn(true);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        when(serviceThread.isAlive()).thenReturn(true, false);
        ChatLoginProgressTask chatLoginProgressTask = new ChatLoginProgressTask(serviceThread);
        chatLoginProgressTask.run(monitor);
        verify(monitor).beginTask("Connecting...", IProgressMonitor.UNKNOWN);
        verify(monitor).subTask("Contacting host...");
        verify(serviceThread).syncWait(500);
        verify(serviceThread).interrupt();
        verify(monitor).done();
    }

    @Test
    public void testRun_unavailable() throws InvocationTargetException, InterruptedException
    {
        IProgressMonitor monitor = mock(IProgressMonitor.class);
        ServiceThread serviceThread = mock(ServiceThread.class);
        when(monitor.isCanceled()).thenReturn(false);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        when(serviceThread.isAlive()).thenReturn(true);
        ChatLoginProgressTask chatLoginProgressTask = new ChatLoginProgressTask(serviceThread);
        chatLoginProgressTask.unavailableMessage = "Unavailable";
        chatLoginProgressTask.run(monitor);
        verify(monitor).beginTask("Connecting...", IProgressMonitor.UNKNOWN);
        verify(monitor).subTask("Contacting host...");
        verify(serviceThread).syncWait(500);
        verify(monitor).done();
    }

}
