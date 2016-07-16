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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.ConnectException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.window.Window;
import org.junit.Test;

import au.com.cybersearch2.cybertete.CyberteteException;
import au.com.cybersearch2.cybertete.model.ConnectionError;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.cybertete.model.InteractiveLogin;
import au.com.cybersearch2.cybertete.model.service.ConnectLoginTask;
import au.com.cybersearch2.cybertete.model.service.KeystoreConfig;
import au.com.cybersearch2.cybertete.model.service.NetworkListener;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.security.KeystoreData;
import au.com.cybersearch2.cybertete.security.KeystoreHelper;
import au.com.cybersearch2.cybertete.security.PersistentSecurityData;
import au.com.cybersearch2.cybertete.security.SecurityConfig;
import au.com.cybersearch2.cybertete.security.SslSessionData;
import au.com.cybersearch2.cybertete.service.ChatLoginController;
import au.com.cybersearch2.cybertete.service.LoginData;
import au.com.cybersearch2.cybertete.service.ServiceThread;
import au.com.cybersearch2.cybertete.service.XmppConnectionException;
import au.com.cybersearch2.dialogs.SyncErrorDialog;

/**
 * ChatLoginControllerTest
 * @author Andrew Bowley
 * 18 Feb 2016
 */
public class ChatLoginControllerTest
{
    class LoginMocks
    {
        ChatLoginController chatLoginController;
        public ServiceThread serviceThread;
        public SyncErrorDialog errorDialog;
        public List<NetworkListener> networkListenerList;
        public InteractiveLogin loginDialog;
        public IEventBroker eventBroker;
        public LoginData loginData;
        public SessionDetails sessionDetails;
        public SSLContext sslContext;
        public X509Certificate[] certificateChain;
        public PersistentSecurityData persistentSecurityData;

        public Logger logger;
        public boolean loginResult;
 
        public LoginMocks(ChatLoginController chatLoginController, boolean isMockNetworkListener)
        {
            this(chatLoginController, isMockNetworkListener, false);
        }
        
        @SuppressWarnings("unchecked")
        public LoginMocks(ChatLoginController chatLoginController, boolean isMockNetworkListener, boolean isDefaultSslConfig)
        {
            this.chatLoginController = chatLoginController;  
            eventBroker = mock(IEventBroker.class);
            chatLoginController.eventBroker = eventBroker;
            errorDialog = mock(SyncErrorDialog.class);
            chatLoginController.errorDialog = errorDialog;
            sessionDetails = mock(SessionDetails.class);
            when(sessionDetails.getJid()).thenReturn(TEST_JID);
            persistentSecurityData = mock(PersistentSecurityData.class);
            chatLoginController.persistentSecurityData = persistentSecurityData; 
            if (!isDefaultSslConfig)
            {
                SecurityConfig securityConfig = mock(SecurityConfig.class);
                when(persistentSecurityData.keystoreConfigInstance()).thenReturn(securityConfig);
            }
            when(persistentSecurityData.isClientCertAuth()).thenReturn(!isDefaultSslConfig);
             loginData = mock(LoginData.class);
            when(loginData.getSessionDetails()).thenReturn(sessionDetails);
            chatLoginController.loginData = loginData;
            networkListenerList = mock(ArrayList.class);
            loginDialog = mock(InteractiveLogin.class);
            chatLoginController.dialog = loginDialog;
            logger = mock(Logger.class);
            chatLoginController.logger = logger;
            if (isMockNetworkListener)
            {
                chatLoginController.networkListenerList = networkListenerList;
                chatLoginController.logger = logger;
            }
            else
            {
                ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
                when(loggerProvider.getClassLogger(ChatLoginController.class)).thenReturn(logger);
                chatLoginController.loggerProvider = loggerProvider;
                chatLoginController.postConstruct();
            }
        }    
    }
    
    static final String TEST_HOST = "google.com";
    static final String TEST_JID = "mickymouse@disney.com";
    
    @Test
    public void test_postConstruct()
    {
        ChatLoginController chatLoginController = new ChatLoginController();
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        chatLoginController.loggerProvider = loggerProvider;
        chatLoginController.postConstruct();
        verify(loggerProvider).getClassLogger(ChatLoginController.class);
        assertThat(chatLoginController.networkListenerList).isEmpty();
    }
 
    @Test
    public void test_login() throws InterruptedException
    {
        ConnectLoginTask serviceLoginTask = mock(ConnectLoginTask.class);
        when(serviceLoginTask.getConnectionError()).thenReturn(ConnectionError.noError);
        LoginMocks loginContext = doLogin(serviceLoginTask, true);
        // ServiceThread calls connectLogin() when started
        loginContext.chatLoginController.connectLogin(serviceLoginTask);
        verify(serviceLoginTask).connectLogin(loginContext.sessionDetails, loginContext.sslContext);
        verify(loginContext.loginDialog).save(TEST_JID);
        assertThat(loginContext.loginResult).isTrue();
        assertThat(loginContext.chatLoginController.sslContext).isEqualTo(loginContext.sslContext);
        verify(loginContext.eventBroker).post(CyberteteEvents.CLIENT_CERT, loginContext.certificateChain);
    }
    
    @Test
    public void test_login_default_ssl() throws InterruptedException
    {
        ConnectLoginTask serviceLoginTask = mock(ConnectLoginTask.class);
        when(serviceLoginTask.getConnectionError()).thenReturn(ConnectionError.noError);
        LoginMocks loginContext = doLogin(serviceLoginTask, true, true);
        // ServiceThread calls connectLogin() when started
        loginContext.chatLoginController.connectLogin(serviceLoginTask);
        verify(serviceLoginTask).connectLogin(loginContext.sessionDetails, loginContext.sslContext);
        verify(loginContext.loginDialog).save(TEST_JID);
        assertThat(loginContext.loginResult).isTrue();
        assertThat(loginContext.chatLoginController.sslContext).isEqualTo(loginContext.sslContext);
        verify(loginContext.eventBroker).post(CyberteteEvents.CLIENT_CERT, loginContext.certificateChain);
    }
        
    @Test
    public void test_preDestroy() throws InterruptedException
    {
        ConnectLoginTask serviceLoginTask = mock(ConnectLoginTask.class);
        when(serviceLoginTask.getConnectionError()).thenReturn(ConnectionError.noError);
        LoginMocks loginContext = doLogin(serviceLoginTask, false);
        loginContext.chatLoginController.preDestroy();
        verify(loginContext.loginDialog).close();
    }
    
    @Test
    public void test_onConnected() throws InterruptedException
    {
        ConnectLoginTask serviceLoginTask = mock(ConnectLoginTask.class);
        when(serviceLoginTask.getConnectionError()).thenReturn(ConnectionError.noError);
        LoginMocks loginContext = doLogin(serviceLoginTask, false);
        List<NetworkListener> networkListenerList = loginContext.chatLoginController.networkListenerList;
        NetworkListener networkListener1 = mock(NetworkListener.class);
        networkListenerList.add(networkListener1);
        NetworkListener networkListener2 = mock(NetworkListener.class);
        networkListenerList.add(networkListener2);
        loginContext.chatLoginController.onConnected(TEST_HOST);
        verify(networkListener1).onConnected(TEST_HOST);
        verify(networkListener2).onConnected(TEST_HOST);
    }
    
    @Test
    public void test_onAuthenticated() throws InterruptedException
    {
        ConnectLoginTask serviceLoginTask = mock(ConnectLoginTask.class);
        when(serviceLoginTask.getConnectionError()).thenReturn(ConnectionError.noError);
        LoginMocks loginContext = doLogin(serviceLoginTask, false);
        List<NetworkListener> networkListenerList = loginContext.chatLoginController.networkListenerList;
        NetworkListener networkListener1 = mock(NetworkListener.class);
        networkListenerList.add(networkListener1);
        NetworkListener networkListener2 = mock(NetworkListener.class);
        networkListenerList.add(networkListener2);
        loginContext.chatLoginController.onAuthenticated();
        verify(networkListener1).onAuthenticated();
        verify(networkListener2).onAuthenticated();
    }
    
    @Test
    public void test_login_uncaught_service_exception() throws InterruptedException
    {
        ConnectLoginTask serviceLoginTask = mock(ConnectLoginTask.class);
        final LoginMocks loginContext = doLogin(serviceLoginTask, true);;
        final ArrayIndexOutOfBoundsException unexpectedException = 
                new ArrayIndexOutOfBoundsException("Out of bounds");
        loginContext.chatLoginController.uncaughtException(mock(Thread.class), unexpectedException);
        verify(loginContext.logger).error(unexpectedException, "Unexpected error during login");
        verify(loginContext.errorDialog).showError("Unexpected error during login", "Out of bounds");
        assertThat(loginContext.loginResult).isFalse();
    }
 
    @Test
    public void test_login_XmppConnectionException() throws InterruptedException
    {
        ConnectLoginTask serviceLoginTask = mock(ConnectLoginTask.class);
        Throwable networkFailException = new ConnectException("Host unavailable");
        XmppConnectionException connectionException = 
                new XmppConnectionException("Network down", networkFailException, ConnectionError.connectionRefused, "google.com", 5222);
        doThrow(connectionException)
        .when(serviceLoginTask).connectLogin(isA(SessionDetails.class), isA(SSLContext.class));
        when(serviceLoginTask.getConnectionError()).thenReturn(ConnectionError.unclassified);
        LoginMocks loginContext = doLogin(serviceLoginTask, true);
        // ServiceThread calls connectLogin() when started
        loginContext.chatLoginController.connectLogin(serviceLoginTask);
        verify(loginContext.logger).error(connectionException);
        verify(loginContext.errorDialog).showError("Network down", connectionException.getDetails());
        verify(loginContext.loginDialog, times(0)).save(TEST_JID);
        assertThat(loginContext.loginResult).isFalse();
    }
    
    @Test
    public void test_login_CyberteteException() throws InterruptedException
    {
        ConnectLoginTask serviceLoginTask = mock(ConnectLoginTask.class);
        Throwable sslException = new GeneralSecurityException("Keystore passphrase incorrect");
        CyberteteException cyberteteException = 
                new CyberteteException("Failed to establish network security system.", sslException);
        doThrow(cyberteteException)
        .when(serviceLoginTask).connectLogin(isA(SessionDetails.class), isA(SSLContext.class));
        when(serviceLoginTask.getConnectionError()).thenReturn(ConnectionError.unclassified);
        LoginMocks loginContext = doLogin(serviceLoginTask, true);
        // ServiceThread calls connectLogin() when started
        loginContext.chatLoginController.connectLogin(serviceLoginTask);
        verify(loginContext.logger).error(cyberteteException);
        verify(loginContext.errorDialog).showError("Security Error", cyberteteException.getMessage());
        verify(loginContext.loginDialog, times(0)).save(TEST_JID);
        assertThat(loginContext.loginResult).isFalse();
    }

    @Test
    public void test_connectLogin_XmppConnectionException_user_cancel()
    {
        ChatLoginController chatLoginController = new ChatLoginController();
        chatLoginController.sslContext = mock(SSLContext.class);
        ConnectLoginTask serviceLoginTask = mock(ConnectLoginTask.class);
        Throwable networkFailException = new ConnectException("Host unavailable");
        XmppConnectionException connectionException = 
                new XmppConnectionException("Network down", networkFailException, ConnectionError.connectionRefused, "google.com", 5222);
        doThrow(connectionException)
        .when(serviceLoginTask).connectLogin(isA(SessionDetails.class), isA(SSLContext.class));
        SessionDetails sessionDetails = mock(SessionDetails.class);
         LoginData loginData = mock(LoginData.class);
        when(loginData.getSessionDetails()).thenReturn(sessionDetails);
        chatLoginController.loginData = loginData;
        chatLoginController.userCancel = true;
        chatLoginController.connectLogin(serviceLoginTask);
        assertThat(chatLoginController.userCancel).isFalse();
    }
    
    @Test
    public void test_connectLogin_CyberteteException_user_cancel()
    {
        ChatLoginController chatLoginController = new ChatLoginController();
        chatLoginController.sslContext = mock(SSLContext.class);
        ConnectLoginTask serviceLoginTask = mock(ConnectLoginTask.class);
        Throwable sslException = new GeneralSecurityException("Keystore passphrase incorrect");
        CyberteteException cyberteteException = 
                new CyberteteException("Failed to establish network security system.", sslException);
        doThrow(cyberteteException)
        .when(serviceLoginTask).connectLogin(isA(SessionDetails.class), isA(SSLContext.class));
        SessionDetails sessionDetails = mock(SessionDetails.class);
         LoginData loginData = mock(LoginData.class);
        when(loginData.getSessionDetails()).thenReturn(sessionDetails);
        chatLoginController.loginData = loginData;
        chatLoginController.userCancel = true;
        chatLoginController.connectLogin(serviceLoginTask);
        assertThat(chatLoginController.userCancel).isFalse();
    }

    @Test
    public void test_short_methods()
    {
        ChatLoginController chatLoginController = new ChatLoginController();
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetails.getJid()).thenReturn(TEST_JID);
        LoginData loginData = mock(LoginData.class);
        when(loginData.getSessionDetails()).thenReturn(sessionDetails);
        chatLoginController.loginData = loginData;
        chatLoginController.sslContext = mock(SSLContext.class);
        chatLoginController.onSaveKeystoreConfigHandler(mock(KeystoreConfig.class));
        assertThat(chatLoginController.sslContext).isNull();
        chatLoginController.sslContext = mock(SSLContext.class);
        chatLoginController.onSaveClientCertConfigHandler(Boolean.TRUE);
        assertThat(chatLoginController.sslContext).isNull();
        Logger logger = mock(Logger.class);
        chatLoginController.logger = logger;
        chatLoginController.onUserCancelHandler("prompt");
        verify(logger).info("Cancelling: prompt");
        assertThat(chatLoginController.userCancel).isTrue();
        chatLoginController.userCancel = false;
        chatLoginController.onInterrupt();
        assertThat(chatLoginController.userCancel).isTrue();
        assertThat(chatLoginController.getUser()).isEqualTo(TEST_JID);
        ArrayList<NetworkListener> networkListenerList = new ArrayList<NetworkListener>();
        chatLoginController.networkListenerList = networkListenerList;
        final boolean[] networkHit = new boolean[]{false,false,false,false};
        NetworkListener networkListener = new NetworkListener(){

            @Override
            public void onUnavailable(String message)
            {
                networkHit[0] = true;
            }

            @Override
            public void onConnected(String hostName)
            {
                assertThat(hostName).isEqualTo(TEST_HOST);
                networkHit[1] = true;
            }

            @Override
            public void onSecured(SslSessionData sslSessionData)
            {
                networkHit[2] = true;
            }

            @Override
            public void onAuthenticated()
            {
                networkHit[3] = true;
            }};
         chatLoginController.addNetworkListener(networkListener);
        assertThat(networkListenerList.size()).isEqualTo(1);
        chatLoginController.onSecured(mock(SslSessionData.class));
        chatLoginController.onAuthenticated();
        chatLoginController.onConnected(TEST_HOST);
        chatLoginController.onUnavailable("message");
        assertThat(networkHit[0]).isFalse();
        assertThat(networkHit[1]).isTrue();
        assertThat(networkHit[2]).isFalse();
        assertThat(networkHit[3]).isTrue();
        chatLoginController.removeNetworkListener(networkListener);
        assertThat(networkListenerList.size()).isEqualTo(0);
    }
    
    public LoginMocks doLogin(ConnectLoginTask serviceLoginTask, boolean isMockNetworkListener) throws InterruptedException
    {
        return doLogin(serviceLoginTask, isMockNetworkListener, false);
    }
    
    public LoginMocks doLogin(ConnectLoginTask serviceLoginTask, boolean isMockNetworkListener, final boolean isDefaultSslConfig) throws InterruptedException
    {
        final KeystoreData keystoreData= mock(KeystoreData.class);
        X509Certificate[] certificateChain = new X509Certificate[]{};
        when(keystoreData.getCertificateChain()).thenReturn(certificateChain );
        SSLContext sslContext = mock(SSLContext.class);
        KeystoreHelper keystoreHelper = mock(KeystoreHelper.class);
        when(keystoreHelper.getSslContext(keystoreData)).thenReturn(sslContext);
        ChatLoginController chatLoginController = new ChatLoginController() {
            @Override
            protected KeystoreData getKeystoreData(KeystoreConfig keystoreConfig)
            {
                return keystoreData;
            }
        };
        chatLoginController.keystoreHelper = keystoreHelper;
        LoginMocks loginContext = new LoginMocks(chatLoginController, isMockNetworkListener, isDefaultSslConfig);
        loginContext.sslContext = sslContext;
        loginContext.certificateChain = certificateChain;
        when(loginContext.loginDialog.open(loginContext.loginData)).thenReturn(Window.OK, Window.CANCEL);
         loginContext.loginResult = chatLoginController.login(serviceLoginTask);
        verify(loginContext.loginDialog).displayProgressDialog(chatLoginController, serviceLoginTask);
        return loginContext;
    } 

}
