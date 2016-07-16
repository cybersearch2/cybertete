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

import org.junit.Test;
import static org.mockito.Mockito.*;

import au.com.cybersearch2.cybertete.model.service.CommsStateListener;
import au.com.cybersearch2.cybertete.model.service.NetworkListener;
import au.com.cybersearch2.cybertete.security.SslSessionData;
import au.com.cybersearch2.cybertete.service.ConnectionNotifier;

/**
 * ConnectionNotifierTest
 * @author Andrew Bowley
 * 5 Apr 2016
 */
public class ConnectionNotifierTest
{
    class TestEnsemble 
    {
        public CommsStateListener commsStateListener;
        public NetworkListener networkListener;
        public ConnectionNotifier underTest;
        
        public TestEnsemble()
        {
            commsStateListener = mock(CommsStateListener.class);
            networkListener = mock(NetworkListener.class);
            underTest = new ConnectionNotifier();
            underTest.add(commsStateListener);
            underTest.add(networkListener);
        }
    }

    static final String TEST_HOST = "google.talk";
    static final String TEST_JID = "mickymouse@disney.com";
    static final String CONNECTION_CLOSED = "Connection closed";

    @Test
    public void test_notifyReconnect_null_connection()
    {
        TestEnsemble testEnsemble = new TestEnsemble();
        testEnsemble.underTest.notifyReconnect();
    }

    @Test
    public void test_notifyCommsUp()
    {
        TestEnsemble testEnsemble = new TestEnsemble();
        SslSessionData sslSessionData = mock(SslSessionData.class);
        testEnsemble.underTest.notifyCommsUp(TEST_HOST, TEST_JID, sslSessionData);
        verify(testEnsemble.commsStateListener).onCommsUp(TEST_HOST, TEST_JID);
        verify(testEnsemble.networkListener).onConnected(TEST_HOST);
        verify(testEnsemble.networkListener).onSecured(sslSessionData);
    }

    @Test
    public void test_notifyCommsUp_insecure()
    {
        TestEnsemble testEnsemble = new TestEnsemble();
        testEnsemble.underTest.notifyCommsUp(TEST_HOST, TEST_JID, null);
        verify(testEnsemble.commsStateListener).onCommsUp(TEST_HOST, TEST_JID);
        verify(testEnsemble.networkListener).onConnected(TEST_HOST);
        verify(testEnsemble.networkListener, times(0)).onSecured(any(SslSessionData.class));
    }

    @Test
    public void test_notifyCommsDown()
    {
        TestEnsemble testEnsemble = new TestEnsemble();
        testEnsemble.underTest.notifyCommsDown(CONNECTION_CLOSED, TEST_HOST);
        verify(testEnsemble.commsStateListener).onCommsDown(TEST_HOST);
        verify(testEnsemble.networkListener).onUnavailable(CONNECTION_CLOSED);
    }

    @Test
    public void test_notifyCommsEstablish()
    {
        TestEnsemble testEnsemble = new TestEnsemble();
        testEnsemble.underTest.notifyCommsEstablish(TEST_HOST);
        verify(testEnsemble.commsStateListener).onEstablishComms(TEST_HOST);
    }
    
    @Test
    public void test_notifyAuthenticated()
    {
        TestEnsemble testEnsemble = new TestEnsemble();
        testEnsemble.underTest.notifyAuthenticated();
        verify(testEnsemble.networkListener).onAuthenticated();
    }


}
