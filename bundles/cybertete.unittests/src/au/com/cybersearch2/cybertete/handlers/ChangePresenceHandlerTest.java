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
package au.com.cybersearch2.cybertete.handlers;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.xml.ws.WebServiceException;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.jface.window.Window;
import org.jivesoftware.smack.SmackException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import au.com.cybersearch2.controls.CustomDialog;
import au.com.cybersearch2.cybertete.dialogs.DialogFactory;
import au.com.cybersearch2.cybertete.dialogs.PresenceControls;
import au.com.cybersearch2.cybertete.model.ChatAgent;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.Presence;
import au.com.cybersearch2.cybertete.service.SessionOwner;
import au.com.cybersearch2.e4.JobScheduler;

/**
 * ChangePresenceHandlerTest
 * @author Andrew Bowley
 * 22 Apr 2016
 */
public class ChangePresenceHandlerTest
{
    @Test
    public void test_postConstruct()
    {
        ChangePresenceHandler underTest = new ChangePresenceHandler();
        JobScheduler jobScheduler = mock(JobScheduler.class);
        underTest.jobScheduler = jobScheduler;
        ChatAgent chatAgent = mock(ChatAgent.class);
        underTest.chatAgent = chatAgent;
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.eventBroker = eventBroker;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(ChangePresenceHandler.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        assertThat(underTest.presence).isEqualTo(Presence.online);
        ArgumentCaptor<EventHandler> handlerCaptor = ArgumentCaptor.forClass(EventHandler.class);
        verify(eventBroker).subscribe(eq(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE), handlerCaptor.capture());
        Event event = mock(Event.class);
        handlerCaptor.getValue().handleEvent(event);
        verify(eventBroker).post(CyberteteEvents.PRESENCE + "/" + Presence.online.toString(), "Online");
        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(jobScheduler).schedule(eq("Send presence update"), taskCaptor.capture());
        taskCaptor.getValue().run();
        verify(chatAgent).sendPresence(Presence.online);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void test_execute()
    {
        ChangePresenceHandler underTest = new ChangePresenceHandler();
        UISynchronize sync = mock(UISynchronize.class);
        underTest.sync = sync;
        JobScheduler jobScheduler = mock(JobScheduler.class);
        underTest.jobScheduler = jobScheduler;
        ChatAgent chatAgent = mock(ChatAgent.class);
        underTest.chatAgent = chatAgent;
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.eventBroker = eventBroker;
        SessionOwner sessionOwner = mock(SessionOwner.class);
        ContactEntry contact = mock(ContactEntry.class);
        when(sessionOwner.getContact()).thenReturn(contact );
        underTest.sessionOwner = sessionOwner;
        DialogFactory dialogFactory = mock(DialogFactory.class);
        PresenceControls presenceControls = mock(PresenceControls.class);
        CustomDialog dialog = mock(CustomDialog.class);
        when(dialog.syncOpen(sync)).thenReturn(Window.OK);
        when(dialog.getCustomControls()).thenReturn(presenceControls);
        when(presenceControls.getPresence()).thenReturn(Presence.dnd);
        when(dialogFactory.presenceDialogInstance(PresenceControls.TITLE)).thenReturn(dialog);
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(ChangePresenceHandler.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        underTest.execute(dialogFactory);
        ArgumentCaptor<Runnable> jobCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(jobScheduler).schedule(eq(PresenceControls.TITLE), jobCaptor.capture());
        jobCaptor.getValue().run();
        assertThat(underTest.presence).isEqualTo(Presence.dnd);
        verify(contact).setPresence(Presence.dnd);
        verify(eventBroker).post(CyberteteEvents.PRESENCE + "/" + Presence.dnd.toString(), "Do not disturb");
        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(jobScheduler).schedule(eq("Send presence update"), taskCaptor.capture());
        taskCaptor.getValue().run();
        verify(chatAgent).sendPresence(Presence.dnd);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void test_execute_no_change()
    {
        ChangePresenceHandler underTest = new ChangePresenceHandler();
        UISynchronize sync = mock(UISynchronize.class);
        underTest.sync = sync;
        JobScheduler jobScheduler = mock(JobScheduler.class);
        underTest.jobScheduler = jobScheduler;
        ChatAgent chatAgent = mock(ChatAgent.class);
        underTest.chatAgent = chatAgent;
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.eventBroker = eventBroker;
        SessionOwner sessionOwner = mock(SessionOwner.class);
        ContactEntry contact = mock(ContactEntry.class);
        when(sessionOwner.getContact()).thenReturn(contact );
        underTest.sessionOwner = sessionOwner;
        DialogFactory dialogFactory = mock(DialogFactory.class);
        PresenceControls presenceControls = mock(PresenceControls.class);
        CustomDialog dialog = mock(CustomDialog.class);
        when(dialog.syncOpen(sync)).thenReturn(Window.OK);
        when(dialog.getCustomControls()).thenReturn(presenceControls);
        when(presenceControls.getPresence()).thenReturn(Presence.online);
        when(dialogFactory.presenceDialogInstance(PresenceControls.TITLE)).thenReturn(dialog);
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(ChangePresenceHandler.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        underTest.execute(dialogFactory);
        ArgumentCaptor<Runnable> jobCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(jobScheduler).schedule(eq(PresenceControls.TITLE), jobCaptor.capture());
        jobCaptor.getValue().run();
        assertThat(underTest.presence).isEqualTo(Presence.online);
        verify(contact, times(0)).setPresence(Presence.online);
        verify(eventBroker, times(0)).post(CyberteteEvents.PRESENCE + "/" + Presence.online.toString(), "Online");
    }
    
    @Test
    public void test_offlineHandler()
    {
        ChangePresenceHandler underTest = new ChangePresenceHandler();
        JobScheduler jobScheduler = mock(JobScheduler.class);
        underTest.jobScheduler = jobScheduler;
        ChatAgent chatAgent = mock(ChatAgent.class);
        underTest.chatAgent = chatAgent;
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.eventBroker = eventBroker;
        SessionOwner sessionOwner = mock(SessionOwner.class);
        ContactEntry contact = mock(ContactEntry.class);
        when(sessionOwner.getContact()).thenReturn(contact );
        underTest.sessionOwner = sessionOwner;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(ChangePresenceHandler.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        underTest.presenceHandler(Presence.offline);
        assertThat(underTest.presence).isEqualTo(Presence.offline);
        verify(contact).setPresence(Presence.offline);
        verify(eventBroker).post(CyberteteEvents.PRESENCE + "/" + Presence.offline.toString(), "Offline");
        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(jobScheduler).schedule(eq("Send presence update"), taskCaptor.capture());
        taskCaptor.getValue().run();
        verify(chatAgent).sendPresence(Presence.offline);
    }

    @Test
    public void test_notifyPresence()
    {
        ChangePresenceHandler underTest = new ChangePresenceHandler();
        JobScheduler jobScheduler = mock(JobScheduler.class);
        underTest.jobScheduler = jobScheduler;
        ChatAgent chatAgent = mock(ChatAgent.class);
        underTest.chatAgent = chatAgent;
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.eventBroker = eventBroker;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(ChangePresenceHandler.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        underTest.presence = Presence.away;
        underTest.notifyPresence();
        assertThat(underTest.presence).isEqualTo(Presence.away);
        verify(eventBroker).post(CyberteteEvents.PRESENCE + "/" + Presence.away.toString(), "Away");
        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(jobScheduler).schedule(eq("Send presence update"), taskCaptor.capture());
        taskCaptor.getValue().run();
        verify(chatAgent).sendPresence(Presence.away);
    }

    @Test
    public void test_notifyPresence_exception()
    {
        ChangePresenceHandler underTest = new ChangePresenceHandler();
        JobScheduler jobScheduler = mock(JobScheduler.class);
        underTest.jobScheduler = jobScheduler;
        ChatAgent chatAgent = mock(ChatAgent.class);
        underTest.chatAgent = chatAgent;
        IEventBroker eventBroker = mock(IEventBroker.class);
        underTest.eventBroker = eventBroker;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(ChangePresenceHandler.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        underTest.presence = Presence.away;
        underTest.notifyPresence();
        assertThat(underTest.presence).isEqualTo(Presence.away);
        verify(eventBroker).post(CyberteteEvents.PRESENCE + "/" + Presence.away.toString(), "Away");
        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(jobScheduler).schedule(eq("Send presence update"), taskCaptor.capture());
        SmackException cause = new SmackException.NotConnectedException();
        WebServiceException exception = new WebServiceException("Network error", cause);
        doThrow(exception)
        .when(chatAgent).sendPresence(Presence.away);
        taskCaptor.getValue().run();
        verify(logger).error(cause, "Network error");
    }
}
