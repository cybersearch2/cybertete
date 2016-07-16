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

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;

import au.com.cybersearch2.cybertete.model.ApplicationState;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.Presence;
import au.com.cybersearch2.cybertete.model.service.CommsStateListener;
import au.com.cybersearch2.cybertete.model.service.ChatService;
import au.com.cybersearch2.cybertete.model.service.NetworkListener;
import au.com.cybersearch2.cybertete.security.SslSessionData;

/**
 * CommunicationsState
 * Receives Chat Service notifications and dispatches events on state transitions.
 * Also provides application with online status.
 * @author Andrew Bowley
 * 15 Nov 2015
 */
public class CommunicationsState implements CommsStateListener, NetworkListener
{
    /** Online status */
    volatile boolean isOnline;

    /**Event broker service */
    @Inject
    IEventBroker eventBroker;
 
    /**
     * @see au.com.cybersearch2.cybertete.model.service.CommsStateListener#onCommsUp(java.lang.String, java.lang.String)
     */
    @Override
    public void onCommsUp(String hostDomain, String user)
    {
        isOnline = true;
        eventBroker.post(CyberteteEvents.COMMS_UP, user);
        eventBroker.post(CyberteteEvents.PRESENCE, Presence.online);
        eventBroker.post(CyberteteEvents.SESSION_RESUME, "Signed on to " + hostDomain);
    }

    /**
     * @see au.com.cybersearch2.cybertete.model.service.CommsStateListener#onCommsDown(java.lang.String)
     */
    @Override
    public void onCommsDown(String hostDomain)
    {
        boolean wasOnline = isOnline;
        isOnline = false;
        eventBroker.post(CyberteteEvents.COMMS_DOWN, hostDomain);
        eventBroker.post(CyberteteEvents.PRESENCE, Presence.offline);
        if (wasOnline)
            eventBroker.post(CyberteteEvents.SESSION_PAUSE, hostDomain + " unavailable");
    }

    /**
     * @see au.com.cybersearch2.cybertete.model.service.CommsStateListener#onEstablishComms(java.lang.String)
     */
    @Override
    public void onEstablishComms(String hostDomain)
    {
        eventBroker.post(CyberteteEvents.COMMS_ESTABLISH, hostDomain);
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.model.service.CommsStateListener#isOnline()
     */
    @Override
    public boolean isOnline()
    {
        return isOnline;
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.model.service.NetworkListener#onUnavailable(java.lang.String)
     */
    @Override
    public void onUnavailable(String message)
    {
        eventBroker.post(CyberteteEvents.NETWORK_UNAVAILABLE, message);
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.model.service.NetworkListener#onConnected(java.lang.String)
     */
    @Override
    public void onConnected(String hostName)
    {
        eventBroker.post(CyberteteEvents.NETWORK_CONNECTED, hostName);
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.model.service.NetworkListener#onSecured(au.com.cybersearch2.cybertete.security.SslSessionData)
     */
    @Override
    public void onSecured(SslSessionData sslSessionData)
    {
        eventBroker.post(CyberteteEvents.NETWORK_SECURE, sslSessionData);
    }

    /**
     * 
     * @see au.com.cybersearch2.cybertete.model.service.NetworkListener#onAuthenticated()
     */
    @Override
    public void onAuthenticated()
    {
    }

    /**
     * Handle logout event. Update isOnline flag, close Chat service, post event to initiate pause session.
     * @param nextState Next application state - login or shutdown
     * @param chatService Chat service to close
     */
    @Inject @Optional
    void onLogoutHandler(@UIEventTopic(CyberteteEvents.LOGOUT) ApplicationState nextState, ChatService chatService)
    {
        isOnline = false;
        chatService.close();
        eventBroker.post(CyberteteEvents.SESSION_PAUSE, "Logging out...");
        if (nextState == ApplicationState.login)
            startLogin();
        else
            shutdown();
    }
 
    /**
     * Post event to initate user login
     */
    private void startLogin()
    {
        eventBroker.post(CyberteteEvents.LOGIN, ApplicationState.running);
    }
 
    /** 
     * Post event to initiate shutdown 
     */
    private void shutdown()
    {
        eventBroker.post(CyberteteEvents.SHUTDOWN, ApplicationState.shutdown);
    }
}
