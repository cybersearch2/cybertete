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

import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration.Builder;

/**
 * XmppConnection
 * Wrapper for XMPPTCPConnection to hide low-level details concerning it's use
 * Extends a base class, containing most of the implementation and testable
 * @author Andrew Bowley
 * 1 Apr 2016
 */
public class XmppConnection extends XmppConnectionBase
{
    /**
     * Construct a XmppConnection object
     * @param connectionConfgBuilder XMPP TCP connection configuration builder
     */
    public XmppConnection(Builder connectionConfgBuilder)
    {
        super(new XMPPTCPConnection(connectionConfgBuilder.build()));
    }

    /**
     * Returns flag set true if connection is in running state
     */
    @Override
    public boolean isConnected()
    {   // isConnected() is final and cannot be used in mocking
        return connection.isConnected();
    }

    /**
     * Returns the full JID of the authenticated user, as returned by the resource binding response of the server.
     * <p>
     * It is important that we don't infer the user from the login() arguments and the configurations service name, as,
     * for example, when SASL External is used, the username is not given to login but taken from the 'external'
     * certificate.
     * </p>
     */
    @Override
    public String getUser()
    {   // getUser() is final and cannot be used in mocking
        return connection.getUser();
    }

    protected Roster getRosterInstance()
    {   // Hide static method for testability
        return Roster.getInstanceFor(connection);
    }
    
    /**
     * Returns ChatManager associated with this connection
     * @see au.com.cybersearch2.cybertete.smack.XmppConnectionBase#getChatManagerInstance()
     */
    protected ChatManager getChatManagerInstance()
    {   // Hide static method for testability
        return ChatManager.getInstanceFor(connection);
    }
     
    /**
     * Returns ReconnectionManager associated with this connection
     * @see au.com.cybersearch2.cybertete.smack.XmppConnectionBase#getReconnectionManagerInstance()
     */
    protected ReconnectionManager getReconnectionManagerInstance()
    {   // Hide static method for testability
        return ReconnectionManager.getInstanceFor(connection);
    }

}   

