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
package au.com.cybersearch2.cybertete.smack;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import au.com.cybersearch2.cybertete.model.service.CommsStateListener;
import au.com.cybersearch2.cybertete.security.SslSessionData;
import au.com.cybersearch2.cybertete.service.ChainHostnameVerifier;
import au.com.cybersearch2.cybertete.service.ConnectionNotifier;

/**
 * SmackConnectionListener
 * Implements Smack interface to listen for connection state change events.
 * @author Andrew Bowley
 * 14 Nov 2015
 */
public class SmackConnectionListener implements ConnectionListener
{
    ConnectionNotifier connectionNotifier;
    ChainHostnameVerifier hostnameVerifier;
    XmppConnection xmppConnection;
    
    /**
     * 
     */
    public SmackConnectionListener(XmppConnection xmppConnection, ConnectionNotifier connectionNotifier, ChainHostnameVerifier hostnameVerifier)
    {
        this.xmppConnection = xmppConnection;
        this.hostnameVerifier = hostnameVerifier;
        this.connectionNotifier = connectionNotifier; 
        xmppConnection.addListener(this);
    }
    
    public XmppConnection getXmppConnection()
    {
        return xmppConnection;
    }
    
    /**
     * @see org.jivesoftware.smack.ConnectionListener#connected(org.jivesoftware.smack.XMPPConnection)
     */
    @Override
    public void connected(XMPPConnection connection)
    {
        // Notification of this event not required;
    }

    /**
     * @see org.jivesoftware.smack.ConnectionListener#authenticated(org.jivesoftware.smack.XMPPConnection, boolean)
     */
    @Override
    public void authenticated(XMPPConnection connection, boolean resumed)
    {
        connectionNotifier.notifyAuthenticated();
    }

    /**
     * @see org.jivesoftware.smack.ConnectionListener#connectionClosed()
     */
    @Override
    public void connectionClosed()
    {
        notifyCommsDown(CommsStateListener.CONNECTION_CLOSED);
    }

    /**
     * @see org.jivesoftware.smack.ConnectionListener#connectionClosedOnError(java.lang.Exception)
     */
    @Override
    public void connectionClosedOnError(Exception e)
    {
        notifyCommsDown(e.getMessage());
    }

    /**
     * @see org.jivesoftware.smack.ConnectionListener#reconnectionSuccessful()
     */
    @Override
    public void reconnectionSuccessful()
    {
        connectionNotifier.notifyReconnect();
        notifyCommsUp();
    }

    /**
     * @see org.jivesoftware.smack.ConnectionListener#reconnectingIn(int)
     */
    @Override
    public void reconnectingIn(int seconds)
    {
        if (seconds == 0)
            notifyCommsEstablish();
    }

    /**
     * @see org.jivesoftware.smack.ConnectionListener#reconnectionFailed(java.lang.Exception)
     */
    @Override
    public void reconnectionFailed(Exception e)
    {
        notifyCommsDown(e.getMessage());
    }

    protected void notifyCommsUp()
    {
        if (xmppConnection.isConnected())
        {
            SslSessionData sslSessionData = xmppConnection.isSecure() ? hostnameVerifier : null;
            connectionNotifier.notifyCommsUp(getHostName(), xmppConnection.getServiceUser(), sslSessionData);
        }
    }

    private void notifyCommsDown(String message)
    {
        connectionNotifier.notifyCommsDown(message, getHostName());
        hostnameVerifier.clearCertificates();
    }

    private void notifyCommsEstablish()
    {
        String serviceName = xmppConnection.getServiceName();
        if (!serviceName.isEmpty())
            connectionNotifier.notifyCommsEstablish(serviceName);
    }

    private String getHostName()
    {
        String hostName = xmppConnection.getHostName();
        return hostName.isEmpty() ? hostnameVerifier.getHostName() : hostName;
    }

    
}
