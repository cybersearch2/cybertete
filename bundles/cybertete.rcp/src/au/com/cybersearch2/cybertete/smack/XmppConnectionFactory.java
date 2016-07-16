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

import static org.jxmpp.util.XmppStringUtils.parseDomain;

import javax.inject.Inject;
import javax.net.ssl.SSLContext;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.ReconnectionManager.ReconnectionPolicy;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration.Builder;

import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.service.ChainHostnameVerifier;

/**
 * XmppConnectionFactory
 * Creates XMPP TCP connection instances
 * @author Andrew Bowley
 * 3 Apr 2016
 */
@Creatable
public class XmppConnectionFactory
{
    // Create XmppConnection instances using interface for testability
    static interface ArtifactFactory
    {
        XmppConnection getXmppConnectionInstance(Builder connectionConfgBuilder);
        Builder getConfigBuilderInstance();
    }

    /** Up packet reply timeout from 5 seconds to 30 as default is too short */
    private static long PACKET_REPLY_TIMEOUT = 30000;
    /** Use backoff recovery strategy */
    private static ReconnectionPolicy RECONNECTION_POLICY = ReconnectionPolicy.RANDOM_INCREASING_DELAY;
    
    /** Flags if an initial available presence will be sent to the server. 
     *  After log in there needs to be a delay in sending available presence while the main window is prepared. */
    boolean sendPresence;
    
    /** Hooks host verifier to access SSL details and extract certificates */
    @Inject
    ChainHostnameVerifier chainHostnameVerifier;
    
    /**
     * Artifact factory to create Smack library objects. 
     */
    ArtifactFactory artifactFactory = new ArtifactFactory(){

        @Override
        public XmppConnection getXmppConnectionInstance(
               Builder connectionConfgBuilder)
        {
            return new XmppConnection(connectionConfgBuilder);
        }

        @Override
        public Builder getConfigBuilderInstance()
        {
            return XMPPTCPConnectionConfiguration.builder();
        }};
    
    /**
     * Create XmppConnectionFactory object
     */
    public XmppConnectionFactory()
    {
        // These are the default policies, so just making them explicit
        ReconnectionManager.setDefaultReconnectionPolicy(RECONNECTION_POLICY);
        ReconnectionManager.setEnabledPerDefault(false);
    }

    /**
     * Set flag for initial available presence sent to the server
     * @param sendPresence boolean
     */
    public void setSendPresence(boolean sendPresence)
    {
        this.sendPresence = sendPresence;
    }
 
    /**
     * Returns an XMPP TCP connection wrapped in a XmppConnection object
     * @param sessionDetails Information required for one user, identified by JID, to log in
     * @param sslContext Java SSL context
     * @return XmppConnection object
     */
    public XmppConnection getConnection(SessionDetails sessionDetails, SSLContext sslContext)
    {
        // Set Plain SASL allowed or disallowed, which is a user configuration option
        if (sessionDetails.isPlainSasl())
            SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
        else 
            SASLAuthentication.blacklistSASLMechanism("PLAIN");
        Builder configBuilder = 
            configureConnection(sessionDetails, sslContext);
        XmppConnection xmppConnection = artifactFactory.getXmppConnectionInstance(configBuilder);
        xmppConnection.setPacketReplyTimeout(PACKET_REPLY_TIMEOUT);
        return xmppConnection;
    }

    /**
     * Configures a builder for XMPP connections over TCP
     * @param configBuilder XMPP TCP config builder
     * @param sessionDetails Information required for one user, identified by JID, to log in
     * @param sslContext Java SSL context
     */
    XMPPTCPConnectionConfiguration.Builder configureConnection(
        SessionDetails sessionDetails, 
        SSLContext sslContext)
    {
        XMPPTCPConnectionConfiguration.Builder configBuilder = 
            artifactFactory.getConfigBuilderInstance();
        // Only send presence online if not set by application
        configBuilder.setSendPresence(sendPresence); 
        String password = sessionDetails.getPassword();
        boolean noPassword = (password == null) || password.isEmpty();
        if (noPassword || sessionDetails.isGssapi())
            configBuilder.allowEmptyOrNullUsernames();
        else
            configBuilder.setUsernameAndPassword(sessionDetails.getUsername(), sessionDetails.getPassword());
        configBuilder.setResource(SmackChatService.DEFAULT_RESOURCE);
        configBuilder.setServiceName(parseDomain(sessionDetails.getJid()));
        configBuilder.setCustomSSLContext(sslContext);
        String host = sessionDetails.getHost();
        if ((host != null) && !host.isEmpty())
        {
            configBuilder.setHost(host);
            configBuilder.setPort(sessionDetails.getPort());
        }
        // Hook Host verifier to access session details and extract certificates
        // Requires preliminary build to get default hostname verifier
        chainHostnameVerifier.chainHostnameVerifier(configBuilder.build().getHostnameVerifier());
        configBuilder.setHostnameVerifier(chainHostnameVerifier);
        return configBuilder;
    }

}
