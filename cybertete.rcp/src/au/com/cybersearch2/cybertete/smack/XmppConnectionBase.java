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
package au.com.cybersearch2.cybertete.smack;

import java.io.IOException;

import javax.xml.ws.WebServiceException;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import au.com.cybersearch2.cybertete.agents.LocalRoster;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactsTree;
import au.com.cybersearch2.cybertete.model.RosterAgent;
import au.com.cybersearch2.cybertete.model.Presence;

/**
 * XmppConnectionBase
 * Contains most of the XmppConnection implementation and testable
 * @author Andrew Bowley
 * 1 Apr 2016
 */
abstract public class XmppConnectionBase
{
    /** The Smack connection object being wrapped */
    protected AbstractXMPPConnection connection;
    /** Smack Chat Manager used to create Chat sessions on this connection */
    ChatManager chatManager;
 
    /**
     * Create XmppConnectionBase object
     * @param connection XMPP TCP connection
     */
    public XmppConnectionBase(XMPPTCPConnection connection)
    {
        this.connection = connection;
        chatManager = getChatManagerInstance();
    }

    // Hide static methods for testability
    abstract protected ChatManager getChatManagerInstance();
    abstract protected ReconnectionManager getReconnectionManagerInstance();
    abstract protected Roster getRosterInstance();
    
    // AbstractXMPPConnection methods are final, so must be accessed indirectly in testing 
    abstract public boolean isConnected();
    abstract public String getUser();

    /**
     * Sets the number of milliseconds to wait for a response from the server.
     * The default value of 5000 ms may be too short in practice.
     * @param timeout The milliseconds to wait for a response from the server
     */
    public void setPacketReplyTimeout(long timeout) 
    {
        connection.setPacketReplyTimeout(timeout);
    }

    /**
     * Add listener for connection closing and reconnection events
     * @param connectionListener ConnectionListener object
     */
    public void addListener(ConnectionListener connectionListener)
    {
        connection.addConnectionListener(connectionListener);
    }

    /**
     * Connect, authenticate and load roster
     * @param contactsTree hat roster organized as a tree of ContactGroup and ContactEntry items
     * @return Container holding Smack roster associated with this connection
     * @throws SmackException
     * @throws IOException
     * @throws XMPPException
     */
    public RosterAgent connectLogin(ContactsTree contactsTree) throws SmackException, IOException, XMPPException
    {
        /*
         * Establishes a connection to the XMPP server
         * @throws XMPPException if an error occurs on the XMPP protocol level.
         * @throws SmackException if an error occurs somewhere else besides XMPP protocol level.
         * @throws IOException if an I/O error occurs during connection 
         * @throws ConnectionException if Smack is unable to connect to all hosts of a given XMPP service
         */
        connection.connect();
        /*
         * Logs in to the server using the strongest SASL mechanism supported by the server. 
         * If more than the connection's default stanza(/packet) timeout elapses in each step of the 
         * authentication process without a response from the server, a SmackException.NoResponseException will be thrown.
         * @throws XMPPException if an error occurs on the XMPP protocol level.
         * @throws SmackException if an error occurs somewhere else besides XMPP protocol level.
         * @throws IOException if an I/O error occurs during login.
         */
        connection.login();
        SmackRoster roster = new SmackRoster(getRosterInstance(), connection.getHost(), connection.getPort());
        roster.loadContactsTree(contactsTree);
        LocalRoster localRoster = new LocalRoster(roster, contactsTree);
        roster.registerLocalRoster(localRoster);
        return localRoster;
    }
    
    /**
     * Returns new Smack Chat
     * @param participant Contact entry of the remote user
     * @return Chat object
     */
    public Chat createChat(ContactEntry participant) 
    {
        return chatManager.createChat(participant.getUser());
    }

    /**
     * Register a new listener with the ChatManager to recieve events related to chats.
     * @param listener The Chat Manager listener.
     */
    public void addChatListener(ChatManagerListener listener) 
    {
        chatManager.addChatListener(listener);
    }
 
    /**
     * Returns flag set true if connection is encrypted using SSL
     * @return boolean
     */
    public boolean isSecure()
    {
        return isConnected() && connection.isSecureConnection();
    }

    /**
     * Returns authenticated user JID, which may not match configured user JID
     * @return JID or empty String if not connected
     */
    public String getServiceUser()
    {
        return isConnected() ? getUser() : "";
    }
 
    /**
     * Returns host address for this connection
     * @return String
     */
    public String getHostName()
    {
        return connection.getHost();
    }

    /**
     * Returns port for this connection
     * @return int
     */
    public int getPort()
    {
        return connection.getPort();
    }
 
    /**
     * Returns service name (domain part of user JID)
     * @return Service name or empty String if not connected
     */
    public String getServiceName()
    {
        return isConnected() ? connection.getServiceName() : "";
    }

    /**
     * Disconnect
     */
    public void disconnect()
    {
        if (isConnected())
            connection.disconnect();
    }

    /**
     * Update Presence on Chat server
     * @param presence The Presence value to set
     */
    public void sendPresence(Presence presence)
    {
        if (isConnected())
            try
            {
                SmackPresence smackPresence = new SmackPresence(presence);
                connection.sendStanza(smackPresence.getXmppPresence());
            }
            catch (NotConnectedException e)
            {
                // Presence will be sent on reconnect, so just log the error
                throw new WebServiceException("Connection error", e);
            }
    }

    /**
     * Enable auto recovery on this connection
     */
    public void enableAutoReconnect()
    {
        if (isConnected())
        {
            ReconnectionManager reconnectionManager = getReconnectionManagerInstance();
            reconnectionManager.enableAutomaticReconnection();
        }
    }

}
