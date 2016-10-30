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

import static org.mockito.Mockito.*;
import static org.fest.assertions.api.Assertions.assertThat;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration.Builder;
import org.junit.Test;

import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.service.ChainHostnameVerifier;

/**
 * XmppConnectionFactoryTest
 * @author Andrew Bowley
 * 4 Apr 2016
 */
public class XmppConnectionFactoryTest
{
    static final String TEST_PASSWORD = "secret";
    static final String TEST_USERNAME = "donald";
    static final String TEST_JID = "mickymouse@disney.com";
    static final String TEST_HOST = "google.talk";

    @Test
    public void test_configureConnection()
    {
        XMPPTCPConnectionConfiguration.Builder configBuilder = doConfiguration(TEST_PASSWORD, null);
        verify(configBuilder).setSendPresence(false);
        verify(configBuilder).setUsernameAndPassword(TEST_USERNAME, TEST_PASSWORD);
        verify(configBuilder, times(0)).setHost(any(String.class));
    }
    
    @Test
    public void test_configureConnection_host()
    {
        XMPPTCPConnectionConfiguration.Builder configBuilder = doConfiguration(TEST_PASSWORD, TEST_HOST);
        verify(configBuilder).setSendPresence(false);
        verify(configBuilder).setUsernameAndPassword(TEST_USERNAME, TEST_PASSWORD);
        verify(configBuilder).setHost(TEST_HOST);
        verify(configBuilder).setPort(5222);
    }

    @Test
    public void test_configureConnection_empty_host()
    {
        XMPPTCPConnectionConfiguration.Builder configBuilder = doConfiguration(TEST_PASSWORD, "");
        verify(configBuilder).setSendPresence(false);
        verify(configBuilder).setUsernameAndPassword(TEST_USERNAME, TEST_PASSWORD);
        verify(configBuilder, times(0)).setHost(any(String.class));
    }

    @Test
    public void test_configureConnection_no_password()
    {
        XMPPTCPConnectionConfiguration.Builder configBuilder = doConfiguration(null, null);
        verify(configBuilder).setSendPresence(false);
        verify(configBuilder).allowEmptyOrNullUsernames();
    }

    @Test
    public void test_configureConnectionn_empty_password()
    {
        XMPPTCPConnectionConfiguration.Builder configBuilder = doConfiguration("", null);
        verify(configBuilder).setSendPresence(false);
        verify(configBuilder).allowEmptyOrNullUsernames();
     }

    @Test
    public void test_configureConnectionn_single_signon()
    {
        XMPPTCPConnectionConfiguration.Builder configBuilder = doSsoConfiguration(TEST_PASSWORD);
        verify(configBuilder).allowEmptyOrNullUsernames();
     }
    @Test
    public void test_configureConnectionn_single_signon_no_password()
    {
        XMPPTCPConnectionConfiguration.Builder configBuilder = doSsoConfiguration(null);
        verify(configBuilder).allowEmptyOrNullUsernames();
     }

    @Test
    public void test_getConnection()
    {
        final SSLContext sslContext = mock(SSLContext.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getPassword()).thenReturn(TEST_PASSWORD);
        when(sessionDetails.getUsername()).thenReturn(TEST_USERNAME);
        when(sessionDetails.getHost()).thenReturn(TEST_HOST);
        when(sessionDetails.getPort()).thenReturn(5222);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        XMPPTCPConnectionConfiguration.Builder configBuilder = mock(XMPPTCPConnectionConfiguration.Builder.class);
        XMPPTCPConnectionConfiguration xmppTcpConfig = mock(XMPPTCPConnectionConfiguration.class);
        HostnameVerifier hostnameVerifier = mock(HostnameVerifier.class);
        when(xmppTcpConfig.getHostnameVerifier()).thenReturn(hostnameVerifier );
        when(configBuilder.build()).thenReturn(xmppTcpConfig);
        final ChainHostnameVerifier chainHostnameVerifier = mock(ChainHostnameVerifier.class);
        XmppConnectionFactory underTest = new XmppConnectionFactory();
        underTest.chainHostnameVerifier = chainHostnameVerifier;
        final XmppConnection xmppConnection = mock(XmppConnection.class);
        underTest.artifactFactory = new XmppConnectionFactory.ArtifactFactory()
        {
            
            @Override
            public XmppConnection getXmppConnectionInstance(Builder connectionConfgBuilder)
            {
                XMPPTCPConnectionConfiguration connectionConfg = connectionConfgBuilder.build();
                assertThat(connectionConfg.getUsername().toString()).isEqualTo(TEST_USERNAME);
                assertThat(connectionConfg.getPassword()).isEqualTo(TEST_PASSWORD);
                assertThat(connectionConfg.getHostnameVerifier()).isEqualTo(chainHostnameVerifier);
                assertThat(connectionConfg.getResource()).isEqualTo("cybertete");
                assertThat(connectionConfg.getServiceName()).isEqualTo("disney.com");
                assertThat(connectionConfg.getCustomSSLContext()).isEqualTo(sslContext);
                assertThat(SASLAuthentication.getBlacklistedSASLMechanisms().contains("PLAIN")).isTrue();
                return xmppConnection;
            }

            @Override
            public Builder getConfigBuilderInstance()
            {
                return XMPPTCPConnectionConfiguration.builder();
            }
        };
        XmppConnection resultXmppConnection = underTest.getConnection(sessionDetails, sslContext);
        verify(resultXmppConnection).setPacketReplyTimeout(30000);
    }
    
    @Test
    public void test_getConnection_blacklist_plain_sasl()
    {
        final SSLContext sslContext = mock(SSLContext.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getPassword()).thenReturn(TEST_PASSWORD);
        when(sessionDetails.getUsername()).thenReturn(TEST_USERNAME);
        when(sessionDetails.getHost()).thenReturn(TEST_HOST);
        when(sessionDetails.getPort()).thenReturn(5222);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        when(sessionDetails.isPlainSasl()).thenReturn(true);
        XMPPTCPConnectionConfiguration.Builder configBuilder = mock(XMPPTCPConnectionConfiguration.Builder.class);
        XMPPTCPConnectionConfiguration xmppTcpConfig = mock(XMPPTCPConnectionConfiguration.class);
        HostnameVerifier hostnameVerifier = mock(HostnameVerifier.class);
        when(xmppTcpConfig.getHostnameVerifier()).thenReturn(hostnameVerifier );
        when(configBuilder.build()).thenReturn(xmppTcpConfig);
        final ChainHostnameVerifier chainHostnameVerifier = mock(ChainHostnameVerifier.class);
        XmppConnectionFactory underTest = new XmppConnectionFactory();
        underTest.chainHostnameVerifier = chainHostnameVerifier;
        final XmppConnection xmppConnection = mock(XmppConnection.class);
        underTest.artifactFactory = new XmppConnectionFactory.ArtifactFactory()
        {
            
            @Override
            public XmppConnection getXmppConnectionInstance(Builder connectionConfgBuilder)
            {
                XMPPTCPConnectionConfiguration connectionConfg = connectionConfgBuilder.build();
                assertThat(connectionConfg.getUsername().toString()).isEqualTo(TEST_USERNAME);
                assertThat(connectionConfg.getPassword()).isEqualTo(TEST_PASSWORD);
                assertThat(connectionConfg.getHostnameVerifier()).isEqualTo(chainHostnameVerifier);
                assertThat(connectionConfg.getResource()).isEqualTo("cybertete");
                assertThat(connectionConfg.getServiceName()).isEqualTo("disney.com");
                assertThat(connectionConfg.getCustomSSLContext()).isEqualTo(sslContext);
                assertThat(SASLAuthentication.getBlacklistedSASLMechanisms().contains("PLAIN")).isFalse();
                return xmppConnection;
            }

            @Override
            public Builder getConfigBuilderInstance()
            {
                return XMPPTCPConnectionConfiguration.builder();
            }

        };
        XmppConnection resultXmppConnection = underTest.getConnection(sessionDetails, sslContext);
        verify(resultXmppConnection).setPacketReplyTimeout(30000);
    }
    
   XMPPTCPConnectionConfiguration.Builder doConfiguration(String password, String host)
    {
        SSLContext sslContext = mock(SSLContext.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getPassword()).thenReturn(password);
        when(sessionDetails.getUsername()).thenReturn(TEST_USERNAME);
        when(sessionDetails.getHost()).thenReturn(host);
        when(sessionDetails.getPort()).thenReturn(5222);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        final Builder configBuilder = mock(Builder.class);
        XMPPTCPConnectionConfiguration xmppTcpConfig = mock(XMPPTCPConnectionConfiguration.class);
        HostnameVerifier hostnameVerifier = mock(HostnameVerifier.class);
        when(xmppTcpConfig.getHostnameVerifier()).thenReturn(hostnameVerifier );
        when(configBuilder.build()).thenReturn(xmppTcpConfig);
        ChainHostnameVerifier chainHostnameVerifier = mock(ChainHostnameVerifier.class);
        XmppConnectionFactory underTest = new XmppConnectionFactory();
        underTest.chainHostnameVerifier = chainHostnameVerifier;
        underTest.artifactFactory = new XmppConnectionFactory.ArtifactFactory()
        {

            @Override
            public XmppConnection getXmppConnectionInstance(
                    Builder connectionConfgBuilder)
            {
                return null;
            }

            @Override
            public Builder getConfigBuilderInstance()
            {
                return configBuilder;
            }
        };
        underTest.configureConnection(sessionDetails, sslContext);
        verify(configBuilder).setResource("cybertete");
        verify(configBuilder).setServiceName("disney.com");
        verify(configBuilder).setCustomSSLContext(sslContext);
        verify(chainHostnameVerifier).chainHostnameVerifier(hostnameVerifier);
        verify(configBuilder).setHostnameVerifier(chainHostnameVerifier);
        return configBuilder;
    }
    
    XMPPTCPConnectionConfiguration.Builder doSsoConfiguration(String password)
    {
        SSLContext sslContext = mock(SSLContext.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getPassword()).thenReturn(password);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        when(sessionDetails.isGssapi()).thenReturn(true);
        final XMPPTCPConnectionConfiguration.Builder configBuilder = mock(XMPPTCPConnectionConfiguration.Builder.class);
        XMPPTCPConnectionConfiguration xmppTcpConfig = mock(XMPPTCPConnectionConfiguration.class);
        HostnameVerifier hostnameVerifier = mock(HostnameVerifier.class);
        when(xmppTcpConfig.getHostnameVerifier()).thenReturn(hostnameVerifier );
        when(configBuilder.build()).thenReturn(xmppTcpConfig);
        ChainHostnameVerifier chainHostnameVerifier = mock(ChainHostnameVerifier.class);
        XmppConnectionFactory underTest = new XmppConnectionFactory();
        underTest.chainHostnameVerifier = chainHostnameVerifier;
        underTest.artifactFactory = new XmppConnectionFactory.ArtifactFactory()
        {

            @Override
            public XmppConnection getXmppConnectionInstance(
                    Builder connectionConfgBuilder)
            {
                return null;
            }

            @Override
            public Builder getConfigBuilderInstance()
            {
                return configBuilder;
            }
        };
        underTest.configureConnection(sessionDetails, sslContext);
        verify(configBuilder).setSendPresence(false);
        verify(configBuilder, times(0)).setHost(any(String.class));
        verify(configBuilder).setResource("cybertete");
        verify(configBuilder).setServiceName("disney.com");
        verify(configBuilder).setCustomSSLContext(sslContext);
        verify(chainHostnameVerifier).chainHostnameVerifier(hostnameVerifier);
        verify(configBuilder).setHostnameVerifier(chainHostnameVerifier);
        return configBuilder;
    }

}
