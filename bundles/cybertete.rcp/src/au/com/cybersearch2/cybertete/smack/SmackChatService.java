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


import static org.jxmpp.util.XmppStringUtils.parseLocalpart;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.xml.ws.WebServiceException;

import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactsTree;
import au.com.cybersearch2.cybertete.model.Presence;
import au.com.cybersearch2.cybertete.model.RosterAgent;
import au.com.cybersearch2.cybertete.model.service.CommsStateListener;
import au.com.cybersearch2.cybertete.model.service.ChatService;
import au.com.cybersearch2.cybertete.model.service.LoginController;
import au.com.cybersearch2.cybertete.model.service.NetworkListener;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.service.ChainHostnameVerifier;
import au.com.cybersearch2.cybertete.service.ChatContactListener;
import au.com.cybersearch2.cybertete.service.ConnectionNotifier;
import au.com.cybersearch2.cybertete.service.SessionOwner;

/**
 * SmackChatService
 * Provides services to establish and monitor Chat sessions using Smack XMPP library
 * @author Andrew Bowley
 * 5 Nov 2015
 */
public class SmackChatService implements ChatService
{
    /** XMPP resource - identifies location of user */
    public static final String DEFAULT_RESOURCE = "cybertete";

    /** Listens for and notifies changes of connection state */ 
    SmackConnectionListener connectionListener;
    /** Connection container - initially null, may have closed connection */
    XmppConnection xmppConnection;
    /**  Updates remote roster */
    RosterAgent rosterAgent;
    
    /** Listens for messages arriving in Chat packets */
    @Inject
    ChatContactListener messageListener;
    @Inject
    SmackChatResponder chatResponder;
    /** Delegate for sending notifications of communication state changes */
    @Inject
    ConnectionNotifier connectionNotifier;
    /** Hook Host verifier to access session details and extract certificates */
    @Inject
    ChainHostnameVerifier hostnameVerifier;
    /** Container of roster contacts kept in sync with master located at the Chat server */
    @Inject
    ContactsTree contactsTree;
    /** Contact entry of currently logged in user */
    @Inject
    SessionOwner sessionOwner;
    /** Connection factory */
    @Inject
    XmppConnectionFactory xmppConnectionFactory;
 
    /**
     * Post construct
     */
    @PostConstruct
    public void postConstruct()
    {
        // The service registers listeners to the notifier embedded in the connection listener
        // Hook Host verifier to access session details and extract certificates
        // chainHostnameVerifier chain handler is set when XmppConnectionFactory creates a connection
        // XmppConnectionFactory disables auto recovery when a connection is created so user can respond 
        // to errors until connection succeeds
        //Map<String,String> mechs = SASLAuthentication.getRegisterdSASLMechanisms();
        //for (String mech: mechs.keySet())
        //    System.out.println(mech);
    }

    /**
     * Establish connection using supplied login controller to manage the user interaction
     * @see au.com.cybersearch2.cybertete.model.service.ChatService#startSession(au.com.cybersearch2.cybertete.model.service.LoginController)
     */
    @Override
    public boolean startSession(LoginController loginController)
    {
        // Make sure of closed state in case of retry following error
        close();
        // Launch login controller passing task to establish the connection through stages connect, authenticate, load roster
        return loginController.login(new SmackLoginTask(this, contactsTree));
    }

    /**
     * Returns listener to new XMPP TCP connection specified by supplied parameters
     * @param sessionDetails Information required for one user, identified by JID, to log in
     * @param sslContext Java SSLContext object or null
     * @return SmackConnectionListener object
     */
    public SmackConnectionListener connect(SessionDetails sessionDetails, SSLContext sslContext)
    {
        // Get a quiescent connection configuration for XMPP connections over TCP
        xmppConnection = xmppConnectionFactory.getConnection(sessionDetails, sslContext);
        // Assign connection to listener so notifications will fire when communications state changes 
        return new SmackConnectionListener(xmppConnection, connectionNotifier, hostnameVerifier);
    }
    
    /**
     * Add chat connection listener
     * @see au.com.cybersearch2.cybertete.model.service.ChatService#addChatConnectionListener(au.com.cybersearch2.cybertete.model.service.CommsStateListener)
     */
    @Override
    public void addChatConnectionListener(
            CommsStateListener commsStateListener)
    {
        connectionNotifier.add(commsStateListener);
    }

    /**
     * Add network listener
     * @see au.com.cybersearch2.cybertete.model.service.ChatService#addNetworkListener(au.com.cybersearch2.cybertete.model.service.NetworkListener)
     */
    @Override
    public void addNetworkListener(NetworkListener networkListener)
    {
        connectionNotifier.add(networkListener);
    }

    /**
     * chatExists
     * @see au.com.cybersearch2.cybertete.model.ChatContacts#chatExists(au.com.cybersearch2.cybertete.model.ContactEntry)
     */
    @Override
    public boolean chatExists(ContactEntry participant)
    {
        return chatResponder.chatExists(participant);
    }

    /**
     * Start Chat session for specified participant
     * @see au.com.cybersearch2.cybertete.model.ChatAgent#startChat(au.com.cybersearch2.cybertete.model.ContactEntry)
     */
    @Override
    public void startChat(ContactEntry participant)
    {
        chatResponder.onStartChat(xmppConnection.createChat(participant), participant, null);
    }

    /**
     * Add new contact at Chat server
     * @see au.com.cybersearch2.cybertete.model.RosterAgent#addContact(au.com.cybersearch2.cybertete.model.ContactEntry)
     */
    @Override
    public boolean addContact(ContactEntry entry)
    {
        if ((rosterAgent == null) || !xmppConnection.isConnected())
            throw new WebServiceException("Cannot add contact \"" + entry.getUser() + "\" while offline");
        return rosterAgent.addContact(entry);
    }


    /**
     * Notify change presence of currently logged in user
     * @see au.com.cybersearch2.cybertete.model.ChatAgent#sendPresence(au.com.cybersearch2.cybertete.model.Presence)
     */
    @Override
    public void sendPresence(Presence presence)
    {
        if (xmppConnection != null)
            xmppConnection.sendPresence(presence);
        xmppConnectionFactory.setSendPresence(true);
    }

    /**
     * Shutdown
     * @see au.com.cybersearch2.cybertete.model.service.ChatService#close()
     */
    @Override
    public void close()
    {
        chatResponder.close();
        hostnameVerifier.clearCertificates();
        if (xmppConnection != null)
            xmppConnection.disconnect();
    }

    /**
     * Establish connection for Chat sessions
     * @param rosterAgent Performs operations on remoter roster on behalf of user
     * @param sessionDetails Information required for one user, identified by JID, to log in
     */
    protected void startChatConnection(RosterAgent rosterAgent, SessionDetails sessionDetails)
    {
        this.rosterAgent = rosterAgent;
        // If session owner not encountered in roster, add orphan contact representing not belonging to any group
        String user = sessionDetails.getJid();
        ContactEntry userContact = contactsTree.getContactEntryByName(user);
        if (userContact == null)
            sessionOwner.update(parseLocalpart(user), user);
        else
            sessionOwner.setContact(userContact);
        // Attach a listener for new Chat sessions
        xmppConnection.addChatListener(chatResponder);
    }

    /**
     * Returns Chat server connection, which provides connection details and ChatManager 
     * @return XmppConnection object
     */
    protected XmppConnection getSmackConnection()
    {
        return xmppConnection;
    }


}
