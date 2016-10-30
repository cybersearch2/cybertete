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
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.junit.Test;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;

import au.com.cybersearch2.cybertete.CyberteteException;

/**
 * SecurityResourcesTest
 * @author Andrew Bowley
 * 31 May 2016
 */
public class SecurityResourcesTest
{
    private static final String KEYSTORE_PASS = "changeme";
    private static final String JKS = "JKS";
   
    private static final String[] CONFIG_CONTENTS =
    {
        "    com.sun.security.jgss.initiate {",
        "    com.sun.security.auth.module.Krb5LoginModule",
        "      required",
        "      useTicketCache=true",
        "      doNotPrompt=true;",
        "      };"
    };
 

    @Test
    public void test_postConstruct()
    {
        final boolean[] isGetKeyManagers = new boolean[]{false};
        SecurityResources underTest = new SecurityResources()
        {
            public KeyManager[] getKeyManagers(KeyStore keyStore, char[] keypass)
            {
                assertThat(keyStore).isNull();
                assertThat(keypass).isNull();
                isGetKeyManagers[0] = true;
                return new KeyManager[]{};
            }
        };
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(SecurityResources.class)).thenReturn(logger);
        underTest.postConstruct(loggerProvider);
        assertThat(underTest.logger).isEqualTo(logger);
        assertThat(isGetKeyManagers[0]).isTrue();
    }
    
    @Test
    public void test_getKeyManagers() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, IOException
    {
        SecurityResources underTest = new SecurityResources();
        KeyManager[] keyManagers = underTest.getKeyManagers(getTestKeyStore(), KEYSTORE_PASS.toCharArray());
        assertThat(keyManagers).isNotEmpty();
    }

    @Test
    public void test_getKeyManagers_UnrecoverableKeyException() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, IOException
    {
        SecurityResources underTest = new SecurityResources();
        try
        {
            underTest.getKeyManagers(getTestKeyStore(), "".toCharArray());
        }
        catch (CyberteteException e)
        {
            assertThat(e.getMessage()).isEqualTo("Private key cannot be recovered");
            assertThat(e.getCause()).isInstanceOf(UnrecoverableKeyException.class);
        }
    }

    @Test
    public void test_getKeyManagers_KeyStoreException() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, IOException
    {
        SecurityResources underTest = new SecurityResources();
        try
        {
            underTest.getKeyManagers(mock(KeyStore.class), KEYSTORE_PASS.toCharArray());
        }
        catch (CyberteteException e)
        {
            assertThat(e.getMessage()).isEqualTo("Key Management error");
            assertThat(e.getCause()).isInstanceOf(KeyStoreException.class);
        }
    }

    @Test
    public void test_getKeyManagers_NoSuchAlgorithmException() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, IOException
    {
        final NoSuchAlgorithmException exception = new NoSuchAlgorithmException("Mickymouse");
        SecurityResources underTest = new SecurityResources()
        {
            protected KeyManagerFactory getKeyManagerFactory() throws NoSuchAlgorithmException
            {
                throw exception;
            }
        };
        try
        {
            underTest.getKeyManagers(getTestKeyStore(), KEYSTORE_PASS.toCharArray());
        }
        catch (CyberteteException e)
        {
            assertThat(e.getMessage()).isEqualTo("SSL default algorithm error");
            assertThat(e.getCause()).isEqualTo(exception);
        }
    }

    @Test
    public void test_createDefaultLoginConfigFile() throws IOException
    {
        SecurityResources underTest = new SecurityResources();
        FileSystem fileSystem = MemoryFileSystemBuilder.newEmpty().build("test");
        try
        {   // Run test twice to exercise file rewrite
            Path configPath = fileSystem.getPath("home");
            underTest.createDefaultLoginConfigFile(configPath);
            Charset charset = Charset.forName("UTF-8");
            List<String> contents1 = Files.readAllLines(configPath, charset);
            assertThat(contents1.size()).isEqualTo(CONFIG_CONTENTS.length);
            int index = 0;
            for (String line: contents1)
                assertThat(line).isEqualTo(CONFIG_CONTENTS[index++]);
            underTest.createDefaultLoginConfigFile(configPath);
            List<String> contents2 = Files.readAllLines(configPath, charset);
            assertThat(contents2.size()).isEqualTo(CONFIG_CONTENTS.length);
            index = 0;
            for (String line: contents1)
                assertThat(line).isEqualTo(CONFIG_CONTENTS[index++]);
        }
        finally
        {
            fileSystem.close();
        }
    }
    
    @Test
    public void test_getSSLContext() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, IOException, KeyManagementException
    {
        SecurityResources underTest = new SecurityResources();
        KeyManager[] keyManagers = underTest.getKeyManagers(getTestKeyStore(), KEYSTORE_PASS.toCharArray());
        SSLContext sslContext = underTest.getSSLContext(keyManagers);
        assertThat(sslContext).isNotNull();
    }

    @Test
    public void test_getKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
    {
        SecurityResources underTest = new SecurityResources();
        KeyStore keystore = underTest.getKeyStore(new File("test-src/resources", "c2.keystore").getAbsolutePath(), JKS, "opensesame".toCharArray());
        assertThat(keystore).isNotNull();
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
        FileInputStream stream = new FileInputStream(new File("test-src/resources", "c2.keystore"));
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
