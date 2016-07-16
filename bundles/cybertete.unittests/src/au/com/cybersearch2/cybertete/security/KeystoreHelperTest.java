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
package au.com.cybersearch2.cybertete.security;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;

import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.junit.Test;

import au.com.cybersearch2.cybertete.CyberteteException;
import au.com.cybersearch2.cybertete.model.service.KeystoreConfig;

/**
 * KeystoreHelperTest
 * @author Andrew Bowley
 * 3 May 2016
 */
public class KeystoreHelperTest
{
    private static final String KEYSTORE_FILE = "keystore.jks";
    private static final String KEYSTORE_PASS = "changeme";
    private static final String JKS = "JKS";

    @Test
    public void test_postConstruct()
    {
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(KeystoreHelper.class)).thenReturn(logger);
        KeystoreHelper keystoreHelper = new KeystoreHelper();
        keystoreHelper.postConstruct(loggerProvider);
        assertThat(keystoreHelper.logger).isEqualTo(logger);
    }

    @Test
    public void test_getKeystoreData() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, IOException
    {
        KeystoreConfig keystoreConfig = mock(KeystoreConfig.class);
        when(keystoreConfig.getKeystoreFile()).thenReturn(KEYSTORE_FILE);
        when(keystoreConfig.getKeystorePassword()).thenReturn(KEYSTORE_PASS);
        when(keystoreConfig.getKeystoreType()).thenReturn(JKS);
        // Cannot mock Keystore - everything is final
        final KeyStore keyStore = getTestKeyStore();
        SecurityResources securityResources = mock(SecurityResources.class);
        KeystoreHelper underTest = new KeystoreHelper();
        underTest.securityResources = securityResources;
        when(securityResources.getKeyStore(KEYSTORE_FILE, JKS, KEYSTORE_PASS.toCharArray())).thenReturn(keyStore);
        // Comments reflect how test keyStore is set up
        //Vector<String> aliases = new Vector<String>();
        //aliases.addElement("alias1");
        //aliases.addElement("alias2");
        //when(keyStore.aliases()).thenReturn(aliases.elements()); // KeyStoreException
        //when(keyStore.isKeyEntry("alias1")).thenReturn(false);
        //when(keyStore.isKeyEntry("alias2")).thenReturn(true);
        //X509Certificate x509Certificate1 = mock(X509Certificate.class);
        //X509Certificate x509Certificate2 = mock(X509Certificate.class);
        //Certificate[] certs = new Certificate[]{x509Certificate1, x509Certificate2};
        //when(keyStore.getCertificateChain("alias2")).thenReturn(certs);
        KeystoreData keystoreData = underTest.getKeystoreData(keystoreConfig);
        assertThat(keystoreData.getKeystoreFile()).isEqualTo(KEYSTORE_FILE);
        assertThat(keystoreData.getKeyStore()).isEqualTo(keyStore);
        assertThat(keystoreData.getKeypass()).isEqualTo(KEYSTORE_PASS.toCharArray());
        X509Certificate[] certificateChain = keystoreData.getCertificateChain();
        assertThat(certificateChain.length).isEqualTo(2);
   }
    
    @Test
    public void test_getKeystoreData_NoSuchAlgorithmException() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, IOException
    {
        KeystoreConfig keystoreConfig = mock(KeystoreConfig.class);
        when(keystoreConfig.getKeystoreFile()).thenReturn(KEYSTORE_FILE);
        when(keystoreConfig.getKeystorePassword()).thenReturn(KEYSTORE_PASS);
        when(keystoreConfig.getKeystoreType()).thenReturn(JKS);
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(KeystoreHelper.class)).thenReturn(logger);
        final String message = "No mickymouse algorithm";
        final NoSuchAlgorithmException exception = new NoSuchAlgorithmException(message);
        SecurityResources securityResources = mock(SecurityResources.class);
        KeystoreHelper underTest = new KeystoreHelper();
        underTest.securityResources = securityResources;
        when(securityResources.getKeyStore(KEYSTORE_FILE, JKS, KEYSTORE_PASS.toCharArray())).thenThrow(exception);
        underTest.postConstruct(loggerProvider);
        try
        {
            underTest.getKeystoreData(keystoreConfig);
            failBecauseExceptionWasNotThrown(CyberteteException.class);
        }
        catch (CyberteteException e)
        {
            String text = "Encryption error";
            verify(logger).error(text, exception);
            assertThat(e.getMessage()).isEqualTo(text);
            assertThat(e.getCause()).isEqualTo(exception);
        }
    }
    
    @Test
    public void test_getKeystoreData_KeyStoreException() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, IOException
    {
        KeystoreConfig keystoreConfig = mock(KeystoreConfig.class);
        when(keystoreConfig.getKeystoreFile()).thenReturn(KEYSTORE_FILE);
        when(keystoreConfig.getKeystorePassword()).thenReturn(KEYSTORE_PASS);
        when(keystoreConfig.getKeystoreType()).thenReturn(JKS);
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(KeystoreHelper.class)).thenReturn(logger);
        final String message = "Keystore corrupt";
        final KeyStoreException exception = new KeyStoreException(message);
        SecurityResources securityResources = mock(SecurityResources.class);
        KeystoreHelper underTest = new KeystoreHelper();
        underTest.securityResources = securityResources;
        when(securityResources.getKeyStore(KEYSTORE_FILE, JKS, KEYSTORE_PASS.toCharArray())).thenThrow(exception);
        underTest.postConstruct(loggerProvider);
        try
        {
            underTest.getKeystoreData(keystoreConfig);
            failBecauseExceptionWasNotThrown(CyberteteException.class);
        }
        catch (CyberteteException e)
        {
            verify(logger).error(KEYSTORE_FILE, exception);
            assertThat(e.getMessage()).isEqualTo("Keystore error");
            assertThat(e.getCause()).isEqualTo(exception);
        }
    }
    
    @Test
    public void test_getKeystoreData_CertificateException() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, IOException
    {
        KeystoreConfig keystoreConfig = mock(KeystoreConfig.class);
        when(keystoreConfig.getKeystoreFile()).thenReturn(KEYSTORE_FILE);
        when(keystoreConfig.getKeystorePassword()).thenReturn(KEYSTORE_PASS);
        when(keystoreConfig.getKeystoreType()).thenReturn(JKS);
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(KeystoreHelper.class)).thenReturn(logger);
        final String message = "Certificate chain broken";
        final CertificateException exception = new CertificateException(message);
        SecurityResources securityResources = mock(SecurityResources.class);
        KeystoreHelper underTest = new KeystoreHelper();
        underTest.securityResources = securityResources;
        when(securityResources.getKeyStore(KEYSTORE_FILE, JKS, KEYSTORE_PASS.toCharArray())).thenThrow(exception);
        underTest.postConstruct(loggerProvider);
        try
        {
            underTest.getKeystoreData(keystoreConfig);
            failBecauseExceptionWasNotThrown(CyberteteException.class);
        }
        catch (CyberteteException e)
        {
            verify(logger).error(KEYSTORE_FILE, exception);
            assertThat(e.getMessage()).isEqualTo("Certificate error");
            assertThat(e.getCause()).isEqualTo(exception);
        }
    }
    
    @Test
    public void test_getKeystoreData_IOException() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, IOException
    {
        KeystoreConfig keystoreConfig = mock(KeystoreConfig.class);
        when(keystoreConfig.getKeystoreFile()).thenReturn(KEYSTORE_FILE);
        when(keystoreConfig.getKeystorePassword()).thenReturn(KEYSTORE_PASS);
        when(keystoreConfig.getKeystoreType()).thenReturn(JKS);
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(KeystoreHelper.class)).thenReturn(logger);
        final String message = "File not found";
        final IOException exception = new IOException(message);
        SecurityResources securityResources = mock(SecurityResources.class);
        KeystoreHelper underTest = new KeystoreHelper();
        underTest.securityResources = securityResources;
        when(securityResources.getKeyStore(KEYSTORE_FILE, JKS, KEYSTORE_PASS.toCharArray())).thenThrow(exception);
        underTest.postConstruct(loggerProvider);
        try
        {
            underTest.getKeystoreData(keystoreConfig);
            failBecauseExceptionWasNotThrown(CyberteteException.class);
        }
        catch (CyberteteException e)
        {
            verify(logger).error(KEYSTORE_FILE, exception);
            assertThat(e.getMessage()).isEqualTo("Keystore file error");
            assertThat(e.getCause()).isEqualTo(exception);
        }
    }
    
    @Test
    public void test_getSslContext() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException
    {
        KeyStore keyStore = mock(KeyStore.class);
        char[] keypass = KEYSTORE_PASS.toCharArray();
        X509Certificate[] certificateChain = new X509Certificate[]{mock(X509Certificate.class)};
        KeystoreData keystoreData = new KeystoreData(KEYSTORE_FILE, keyStore, keypass, certificateChain);
        final SSLContext sslContext = mock(SSLContext.class);
        final KeyManager[] keyManagers = new KeyManager[]{mock(KeyManager.class)};
        SecurityResources securityResources = mock(SecurityResources.class);
        KeystoreHelper underTest = new KeystoreHelper();
        underTest.securityResources = securityResources;
        when(securityResources.getKeyManagers(keyStore, keypass)).thenReturn(keyManagers);
        when(securityResources.getSSLContext(keyManagers)).thenReturn(sslContext);
        underTest.securityResources = securityResources;
        assertThat(underTest.getSslContext(keystoreData)).isEqualTo(sslContext);
    }
    
    @Test
    public void test_getSslContext_NoSuchAlgorithmException() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException
    {
        final String message = "No mickymouse algorithm";
        final NoSuchAlgorithmException exception = new NoSuchAlgorithmException(message);
        KeyStore keyStore = mock(KeyStore.class);
        char[] keypass = KEYSTORE_PASS.toCharArray();
        X509Certificate[] certificateChain = new X509Certificate[]{mock(X509Certificate.class)};
        KeystoreData keystoreData = new KeystoreData(KEYSTORE_FILE, keyStore, keypass, certificateChain);
        final KeyManager[] keyManagers = new KeyManager[]{mock(KeyManager.class)};
        SecurityResources securityResources = mock(SecurityResources.class);
        KeystoreHelper underTest = new KeystoreHelper();
        underTest.securityResources = securityResources;
        when(securityResources.getKeyManagers(keyStore, keypass)).thenReturn(keyManagers);
        when(securityResources.getSSLContext(keyManagers)).thenThrow(exception);
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(KeystoreHelper.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        underTest.securityResources = securityResources;
        try
        {
            underTest.getSslContext(keystoreData);
            failBecauseExceptionWasNotThrown(CyberteteException.class);
        }
        catch (CyberteteException e)
        {
            String text = "Error creating SSL Context for protocol " + SecurityConfig.SSL_PROTOCOL;
            verify(logger).error(text, exception);
            assertThat(e.getMessage()).isEqualTo(text);
            assertThat(e.getCause()).isEqualTo(exception);
        }
    }
       
    @Test
    public void test_getSslContext_KeyManagementException() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException
    {
        final String message = "Cannot manage keys";
        final KeyManagementException exception = new KeyManagementException(message);
        KeyStore keyStore = mock(KeyStore.class);
        char[] keypass = KEYSTORE_PASS.toCharArray();
        X509Certificate[] certificateChain = new X509Certificate[]{mock(X509Certificate.class)};
        KeystoreData keystoreData = new KeystoreData(KEYSTORE_FILE, keyStore, keypass, certificateChain);
        final KeyManager[] keyManagers = new KeyManager[]{mock(KeyManager.class)};
        SecurityResources securityResources = mock(SecurityResources.class);
        KeystoreHelper underTest = new KeystoreHelper();
        underTest.securityResources = securityResources;
        when(securityResources.getKeyManagers(keyStore, keypass)).thenReturn(keyManagers);
        when(securityResources.getSSLContext(keyManagers)).thenThrow(exception);
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(KeystoreHelper.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        underTest.securityResources = securityResources;
        try
        {
            underTest.getSslContext(keystoreData);
            failBecauseExceptionWasNotThrown(CyberteteException.class);
        }
        catch (CyberteteException e)
        {
            verify(logger).error(KEYSTORE_FILE, exception);
            assertThat(e.getMessage()).isEqualTo("Key Management error");
            assertThat(e.getCause()).isEqualTo(exception);
        }
    }
       
    @Test
    public void test_getSslContext_KeyManagementException_default_SSL() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException
    {
        final String message = "Cannot manage keys";
        final KeyManagementException exception = new KeyManagementException(message);
        KeystoreData keystoreData = new KeystoreData(null, null, null, null);
        final KeyManager[] keyManagers = new KeyManager[]{mock(KeyManager.class)};
        SecurityResources securityResources = mock(SecurityResources.class);
        KeystoreHelper underTest = new KeystoreHelper();
        underTest.securityResources = securityResources;
        when(securityResources.getKeyManagers(null, null)).thenReturn(keyManagers);
        when(securityResources.getSSLContext(keyManagers)).thenThrow(exception);
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(KeystoreHelper.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        underTest.securityResources = securityResources;
        try
        {
            underTest.getSslContext(keystoreData);
            failBecauseExceptionWasNotThrown(CyberteteException.class);
        }
        catch (CyberteteException e)
        {
            verify(logger).error("<default keystore>", exception);
            assertThat(e.getMessage()).isEqualTo("Key Management error");
            assertThat(e.getCause()).isEqualTo(exception);
        }
    }
   
   /**
     * Returns test keystore with one private key entry with alias "alias2".
     * This key has 2 chained certificates.
     * There is also a trusted certificate entry with alias "alias1".
     * The private key is obtained from a real keystore - c2.keystore in resources folder.
     * @return
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     * @throws UnrecoverableEntryException
     */
    KeyStore getTestKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableEntryException
    {
        KeyStore ks = KeyStore.getInstance("JKS");
        FileInputStream stream = new FileInputStream(new File("resources", "c2.keystore"));
        ks.load(stream, "opensesame".toCharArray());
        stream.close();
        assertThat(ks.isKeyEntry("cybersearch2.local")).isTrue();
        KeyStore.ProtectionParameter protParam =
                new KeyStore.PasswordProtection("opensesame".toCharArray());
        // get my private key
        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
            ks.getEntry("cybersearch2.local", protParam);
        assertThat(pkEntry).isNotNull();
        KeyStore testKeyStore = keyStoreInstance(pkEntry);
        assertThat(testKeyStore.isKeyEntry("alias2")).isTrue();
        KeyStore.ProtectionParameter testProtParam =
                new KeyStore.PasswordProtection(KEYSTORE_PASS.toCharArray());
        // get trusted cert
        KeyStore.TrustedCertificateEntry testCertEntry = (KeyStore.TrustedCertificateEntry)
                testKeyStore.getEntry("alias1", null);
        assertThat(testCertEntry).isNotNull();
        // get my private key
        KeyStore.PrivateKeyEntry testPkEntry = (KeyStore.PrivateKeyEntry)
                testKeyStore.getEntry("alias2", testProtParam);
        assertThat(testPkEntry).isNotNull();
        assertThat(testKeyStore.getCertificateChain("alias2").length).isEqualTo(2);
        return testKeyStore;
   }
    
    KeyStore keyStoreInstance(KeyStore.PrivateKeyEntry pkEntry) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException, CertificateException, IOException
    {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(null, KEYSTORE_PASS.toCharArray());
        KeyStore.ProtectionParameter protParam =
            new KeyStore.PasswordProtection(KEYSTORE_PASS.toCharArray());
        ks.setEntry("alias2", pkEntry, protParam);
        KeyStore.TrustedCertificateEntry trusted = new KeyStore.TrustedCertificateEntry(pkEntry.getCertificateChain()[1]);
        ks.setEntry("alias1", trusted, null);
        return ks;
    }

}
