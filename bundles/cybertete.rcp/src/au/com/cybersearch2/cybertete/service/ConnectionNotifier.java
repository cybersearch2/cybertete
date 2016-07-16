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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Creatable;

import au.com.cybersearch2.cybertete.model.service.CommsStateListener;
import au.com.cybersearch2.cybertete.model.service.NetworkListener;
import au.com.cybersearch2.cybertete.security.SslSessionData;

/**
 * ConnectionNotifier
 * Sends notifications of communication state changes to registered listeners
 * @author Andrew Bowley
 * 31 Mar 2016
 */
@Creatable
public class ConnectionNotifier
{
    /** List of communications state changes listeners */
    private List<CommsStateListener> chatConnectionListenerList;
    /** List of listeners for events associated with connecting to a network */
    private List<NetworkListener> networkListenerList;

    /**
     * Create ConnectionNotifier object
     */
    public ConnectionNotifier()
    {
        chatConnectionListenerList = new ArrayList<CommsStateListener>();
        networkListenerList = new ArrayList<NetworkListener>(); 
    }

    /**
     * Add communications state changes listener
     * @param commsStateListener CommsStateListener object
     */
    public void add(CommsStateListener commsStateListener)
    {
        chatConnectionListenerList.add(commsStateListener);
    }
 
    /**
     * Add listener for events associated with connecting to a network 
     * @param networkListener NetworkListener object
     */
    public void add(NetworkListener networkListener)
    {
        networkListenerList.add(networkListener);
    }

    /**
     * Notify Communications is up
     * @param hostname Host address
     * @param serviceUser User JID
     * @param sslSessionData SSL details or null if connection is not secure
     */
    public void notifyCommsUp(String hostname, String serviceUser, SslSessionData sslSessionData)
    {
        for (CommsStateListener commsStateListener: chatConnectionListenerList)
            commsStateListener.onCommsUp(hostname, serviceUser);
        for (NetworkListener  networkListener: networkListenerList)
        {
            networkListener.onConnected(hostname);
            if (sslSessionData != null)
                networkListener.onSecured(sslSessionData);
        }
    }

    /**
     * Notify connection is  ready to handle Chat traffic following reconnection
     */
    public void notifyReconnect()
    {
        // Not required
    }
  
    /**
     * Notify network authenticated
     */
    public void notifyAuthenticated()
    {
        for (NetworkListener  networkListener: networkListenerList)
            networkListener.onAuthenticated();
    }

    /**
     * Notify communications down
     * @param message Reason for event
     * @param hostname Host address
     */
    public void notifyCommsDown(String message, String hostname)
    {
        for (CommsStateListener commsStateListener: chatConnectionListenerList)
            commsStateListener.onCommsDown(hostname);
        for (NetworkListener  networkListener: networkListenerList)
            networkListener.onUnavailable(message);
    }

    /**
     * Notify communications being established
     * @param serviceName Domain part of user JID
     */
    public void notifyCommsEstablish(String serviceName)
    {
        for (CommsStateListener commsStateListener: chatConnectionListenerList)
            commsStateListener.onEstablishComms(serviceName);
    }

}
