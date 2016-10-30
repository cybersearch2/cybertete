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

import org.eclipse.e4.core.services.events.IEventBroker;
import org.junit.Test;

import au.com.cybersearch2.cybertete.model.ApplicationState;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.Presence;
import au.com.cybersearch2.cybertete.model.service.ChatService;
import au.com.cybersearch2.cybertete.security.SslSessionData;
import au.com.cybersearch2.cybertete.service.CommunicationsState;

import static org.mockito.Mockito.*;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * CommunicationsStateTest
 * @author Andrew Bowley
 * 18 Feb 2016
 */
public class CommunicationsStateTest
{
    static String HOST_DOMAIN = "au.com.cybersearch2";
    static String USER = "mickymouse";
    static String COMMS_DOWN = HOST_DOMAIN + " is offline";
    
    @Test
    public void test_onCommsUp()
    {
        CommunicationsState communicationsState = new CommunicationsState();
        IEventBroker eventBroker = mock(IEventBroker.class);
        communicationsState.eventBroker = eventBroker;
        assertThat(communicationsState.isOnline()).isFalse();
        communicationsState.onCommsUp(HOST_DOMAIN, USER);
        verify(eventBroker).post(CyberteteEvents.COMMS_UP, USER);
        verify(eventBroker).post(CyberteteEvents.PRESENCE, Presence.online);
        verify(eventBroker).post(CyberteteEvents.SESSION_RESUME, "Signed on to " + HOST_DOMAIN);
        assertThat(communicationsState.isOnline()).isTrue();
    }

    @Test
    public void test_onCommsDown()
    {
        CommunicationsState communicationsState = new CommunicationsState();
        IEventBroker eventBroker = mock(IEventBroker.class);
        communicationsState.eventBroker = eventBroker;
        communicationsState.isOnline = true;
        assertThat(communicationsState.isOnline()).isTrue();
        communicationsState.onCommsDown(HOST_DOMAIN);
        verify(eventBroker).post(CyberteteEvents.COMMS_DOWN, HOST_DOMAIN);
        verify(eventBroker).post(CyberteteEvents.PRESENCE, Presence.offline);
        verify(eventBroker).post(CyberteteEvents.SESSION_PAUSE, HOST_DOMAIN + " unavailable");
        assertThat(communicationsState.isOnline()).isFalse();
        reset(eventBroker);
        communicationsState.onCommsDown(HOST_DOMAIN);
        verify(eventBroker, times(0)).post(CyberteteEvents.SESSION_PAUSE, HOST_DOMAIN + " unavailable");
        assertThat(communicationsState.isOnline()).isFalse();
    }

    @Test
    public void test_onEstablishComms()
    {
        CommunicationsState communicationsState = new CommunicationsState();
        IEventBroker eventBroker = mock(IEventBroker.class);
        communicationsState.eventBroker = eventBroker;
        communicationsState.onEstablishComms(HOST_DOMAIN);
        verify(eventBroker).post(CyberteteEvents.COMMS_ESTABLISH, HOST_DOMAIN);
        assertThat(communicationsState.isOnline()).isFalse();
    }

    @Test
    public void test_onUnavailable()
    {
        CommunicationsState communicationsState = new CommunicationsState();
        IEventBroker eventBroker = mock(IEventBroker.class);
        communicationsState.eventBroker = eventBroker;
        communicationsState.onUnavailable(COMMS_DOWN);
        verify(eventBroker).post(CyberteteEvents.NETWORK_UNAVAILABLE, COMMS_DOWN);
        assertThat(communicationsState.isOnline()).isFalse();
    }

    @Test
    public void test_onConnected()
    {
        CommunicationsState communicationsState = new CommunicationsState();
        IEventBroker eventBroker = mock(IEventBroker.class);
        communicationsState.eventBroker = eventBroker;
        communicationsState.onConnected(HOST_DOMAIN);
        verify(eventBroker).post(CyberteteEvents.NETWORK_CONNECTED, HOST_DOMAIN);
        assertThat(communicationsState.isOnline()).isFalse();
    }

    @Test
    public void test_onSecured()
    {
        CommunicationsState communicationsState = new CommunicationsState();
        IEventBroker eventBroker = mock(IEventBroker.class);
        communicationsState.eventBroker = eventBroker;
        SslSessionData sslSessionData = mock(SslSessionData.class);
        communicationsState.onSecured(sslSessionData);
        verify(eventBroker).post(CyberteteEvents.NETWORK_SECURE, sslSessionData);
    }

    @Test
    public void test_onAuthenticated()
    {
        CommunicationsState communicationsState = new CommunicationsState();
        IEventBroker eventBroker = mock(IEventBroker.class);
        communicationsState.eventBroker = eventBroker;
        communicationsState.onAuthenticated();
        verify(eventBroker, times(0)).post(any(String.class), anyObject());
    }

    @Test
    public void test_onLogoutHandler_running()
    {
        CommunicationsState communicationsState = new CommunicationsState();
        IEventBroker eventBroker = mock(IEventBroker.class);
        communicationsState.eventBroker = eventBroker;
        communicationsState.isOnline = true;
        ChatService chatService = mock(ChatService.class);
        communicationsState.onLogoutHandler(ApplicationState.login, chatService);
        verify(chatService).close();
        verify(eventBroker).post(CyberteteEvents.SESSION_PAUSE, "Logging out...");
        verify(eventBroker).post(CyberteteEvents.LOGIN, ApplicationState.running);
        assertThat(communicationsState.isOnline()).isFalse();
    }

    @Test
    public void test_onLogoutHandler_shutdown()
    {
        CommunicationsState communicationsState = new CommunicationsState();
        IEventBroker eventBroker = mock(IEventBroker.class);
        communicationsState.eventBroker = eventBroker;
        communicationsState.isOnline = true;
        ChatService chatService = mock(ChatService.class);
        communicationsState.onLogoutHandler(ApplicationState.shutdown, chatService);
        verify(chatService).close();
        verify(eventBroker).post(CyberteteEvents.SESSION_PAUSE, "Logging out...");
        verify(eventBroker).post(CyberteteEvents.SHUTDOWN, ApplicationState.shutdown);
        assertThat(communicationsState.isOnline()).isFalse();
    }
}
