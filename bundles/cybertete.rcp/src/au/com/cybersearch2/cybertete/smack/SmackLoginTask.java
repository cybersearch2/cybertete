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

import java.io.IOException;

import javax.net.ssl.SSLContext;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import au.com.cybersearch2.cybertete.model.ConnectionError;
import au.com.cybersearch2.cybertete.model.ContactsTree;
import au.com.cybersearch2.cybertete.model.RosterAgent;
import au.com.cybersearch2.cybertete.model.service.ConnectLoginTask;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.service.XmppConnectionException;

/**
 * SmackLoginTask
 * Smack implementation of interface to connect to Chat server, authenticate and load roster
 * @see au.com.cybersearch2.cybertete.model.service.ConnectLoginTask
 * @author Andrew Bowley
 * 22 Nov 2015
 */
public class SmackLoginTask implements ConnectLoginTask
{
    /** Cybertete connection error */
    private ConnectionError connectionError;

    /** Chat service delegated to performing task */
    SmackChatService chatService;
    ContactsTree contactsTree;

    /**
     * Create SmackLoginTask object
     * @param chatService Chat service delegated to performing task
     * @param contactsTree Chat roster organized as a tree of ContactGroup and ContactEntry items
     */
    public SmackLoginTask(SmackChatService chatService, ContactsTree contactsTree)
    {
        this.chatService = chatService;
        this.contactsTree = contactsTree;
    }
 
    /**
     * Connect to Chat server, authenticate and load roster 
     * @see au.com.cybersearch2.cybertete.model.service.ConnectLoginTask#connectLogin(au.com.cybersearch2.cybertete.model.service.SessionDetails, javax.net.ssl.SSLContext)
     */
    @Override
    public void connectLogin(SessionDetails sessionDetails, SSLContext sslContext)
    {
        boolean success = false;
        connectionError = ConnectionError.unclassified ;
        // Get a quiescent connection for XMPP connections over TCP
        SmackConnectionListener connectionListener = chatService.connect(sessionDetails, sslContext);
        XmppConnection xmppConnection = connectionListener.getXmppConnection();
        try
        {            
            success = connectLogin(xmppConnection, sessionDetails);
        }
        catch (XMPPException | SmackException | IOException e)
        {
            connectionError = ConnectionError.classifyException(e);
            throw new XmppConnectionException("Connect, log in and load roster failed", e, 
                                                connectionError, 
                                                xmppConnection.getHostName(), 
                                                xmppConnection.getPort());
        }
        finally
        {
            if (!success)
            {   // Finalize failure state
                if (connectionError == ConnectionError.noError)
                    connectionError = ConnectionError.unclassified;
                xmppConnection.disconnect();
            }
        }
        if (success)
        {
            // Fire communications up event
            connectionListener.notifyCommsUp();
            // Enable auto recovery
            xmppConnection.enableAutoReconnect();
        }

    }

    /**
     * Implement connect to Chat server, authenticate and load roster. Thread will likely block while operation is in progress.     
     * @param xmppConnection Object to perform connection
     * @param sessionDetails Information required for one user, identified by JID, to log in
     * @return Flag set true if operation completes successfully
     * @throws SmackException
     * @throws IOException
     * @throws XMPPException
     */
    boolean connectLogin(XmppConnection xmppConnection, SessionDetails sessionDetails) throws SmackException, IOException, XMPPException
    {   // The SmackConnectionListener now fires as progress milestones reached.
        RosterAgent rosterAgent = xmppConnection.connectLogin(contactsTree);
        // Create Chat connection to manage XMPP connection
        chatService.startChatConnection(rosterAgent, sessionDetails);
        // Indicate success
        connectionError = ConnectionError.noError;
        return true;
    }

    /**
     * Returns Cybertete connection error
     * @see au.com.cybersearch2.cybertete.model.service.ConnectLoginTask#getConnectionError()
     */
    @Override
    public ConnectionError getConnectionError()
    {
        return connectionError;
    }


}
