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

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;

import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;

import au.com.cybersearch2.cybertete.CyberteteException;
import au.com.cybersearch2.cybertete.model.service.KeystoreConfig;

/**
 * KeystoreHelper
 * Accesses keystore used to hold SSL certificates and encryption keys
 * @author Andrew Bowley
 * 3 May 2016
 */
public class KeystoreHelper
{
    private static final String KEYSTORE_ERROR = "Keystore error";
    private static final String ENCRYPTION_ERROR = "Encryption error";
    private static final String ALGORITM_ERROR = "Error creating SSL Context for protocol " + SecurityConfig.SSL_PROTOCOL;

    /** Logger */
    Logger logger;

    /** KeyManager factory */
    @Inject 
    SecurityResources securityResources;
    
    /**
     * postConstruct
     * @param loggerProvider Logger factory
     */
    @PostConstruct
    void postConstruct(ILoggerProvider loggerProvider)
    {
        logger = loggerProvider.getClassLogger(KeystoreHelper.class);
    }

    /**
     * Load keystore data from disk.
     * @param keystoreConfig Keystore configuration parameters
     * @throws CyberteteException if error occurs opening the keystore file or extracting the data
     */
    public KeystoreData getKeystoreData(KeystoreConfig keystoreConfig)
    {
        String keyStoreFile = keystoreConfig.getKeystoreFile();
        char[] keypass = keystoreConfig.getKeystorePassword().toCharArray();
        X509Certificate[] certificateChain = new X509Certificate[]{};
        try
        {
            KeyStore keyStore = securityResources.getKeyStore(keyStoreFile, keystoreConfig.getKeystoreType(), keypass);
            Enumeration<String> aliases = keyStore.aliases(); 
            while (aliases.hasMoreElements())
            {
                String alias = aliases.nextElement();
                if (keyStore.isKeyEntry(alias))
                {
                    Certificate[] certs = keyStore.getCertificateChain(alias);
                    certificateChain = new X509Certificate[certs.length];
                    int index = 0;
                    for (Certificate cert: certs)
                        certificateChain[index++] = (X509Certificate)cert;
                    break;
                }
            }
            return new KeystoreData(keyStoreFile, keyStore, keypass, certificateChain);
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error(ENCRYPTION_ERROR, e);
            throw new CyberteteException(ENCRYPTION_ERROR, e);
        }
        catch (KeyStoreException e)
        {
            logger.error(keystoreConfig.getKeystoreFile(), e);
            throw new CyberteteException(KEYSTORE_ERROR, e);
        }
        catch (CertificateException e)
        {
            logger.error(keystoreConfig.getKeystoreFile(), e);
            throw new CyberteteException("Certificate error", e);
        }
        catch (IOException e)
        {
            logger.error(keystoreConfig.getKeystoreFile(), e);
            throw new CyberteteException("Keystore file error", e);
        }
     }

    /**
     * Returns SSL context
     * @param keystoreData Keystore data
     * @return SSLContext object
     * @throws CyberteteException if error occurs opening the keystore file or extracting the data
     */
    public SSLContext getSslContext(KeystoreData keystoreData)  
    {
        SSLContext newSslContext = null;
        String keystoreFile = keystoreData.getKeystoreFile();
        try
        {
            KeyManager[] kms = securityResources.getKeyManagers(keystoreData.getKeyStore(), keystoreData.getKeypass());
            newSslContext = securityResources.getSSLContext(kms);
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error(ALGORITM_ERROR, e);
            throw new CyberteteException(ALGORITM_ERROR, e);
        }
        catch (KeyManagementException e)
        {
            String reportFile = keystoreFile == null ? "<default keystore>" : keystoreFile;
            logger.error(reportFile, e);
            throw new CyberteteException("Key Management error", e);
        }
        return newSslContext;
    }

}
