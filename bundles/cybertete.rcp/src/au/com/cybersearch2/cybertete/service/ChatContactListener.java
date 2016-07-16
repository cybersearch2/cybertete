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

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;

import au.com.cybersearch2.cybertete.model.ChatPost;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.service.MessageListener;

/**
 * ChatContactListener
 * @author Andrew Bowley
 * 7 Apr 2016
 */
@Creatable
public class ChatContactListener implements MessageListener
{
    @Inject
    IEventBroker eventBroker;

    /**
     * @see au.com.cybersearch2.cybertete.model.service.MessageListener#onMessageReceived(au.com.cybersearch2.cybertete.model.ContactEntry, java.lang.String)
     */
    @Override
    public void onMessageReceived(ContactEntry from,
            String body)
    {
        ChatPost chatPost = new ChatPost(from, body);
        eventBroker.post(CyberteteEvents.POST_CHAT, chatPost);
    }

    /**
     * @see au.com.cybersearch2.cybertete.model.service.MessageListener#onSessionEnd(java.lang.String)
     */
    @Override
    public void onSessionEnd(String message)
    {
    }

}
