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

import javax.xml.ws.WebServiceException;

import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.chat.Chat;

import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.service.ChatSession;


/**
 * SmackChatSession
 * @author Andrew Bowley
 * 5 Nov 2015
 */
public class SmackChatSession implements ChatSession
{
    private ContactEntry participant;
    private ContactEntry sessionOwner;
    Chat chat;

    /**
     * 
     */
    public SmackChatSession(Chat chat, ContactEntry participant, ContactEntry sessionOwner)
    {
        this.chat = chat;
        this.participant = participant;
        this.sessionOwner = sessionOwner;
    }

    @Override
    public ContactEntry getParticipant()
    {
        return participant;
    }

    /**
     * Returns the logged in user contact item from the roster
     * @return ContactEntry object
     */
    public ContactEntry getSessionOwner()
    {
        return sessionOwner;
    }

    /**
     * Send message
     * @see au.com.cybersearch2.cybertete.model.service.ChatSession#sendMessage(java.lang.String)
     */
    @Override
    public void sendMessage(String body)
    {
        try 
        {
            chat.sendMessage(body);
        } 
        catch (NotConnectedException e)
        {
            throw new WebServiceException("Connection error while sending to " + participant.getUser(), e);
        }
    }

    /**
     * Close the Chat session
     * @see au.com.cybersearch2.cybertete.model.service.ChatSession#close(java.lang.String)
     */
    @Override
    public void close(String message)
    {
        participant.setWindowId(null);
        chat.close();
    }
}
