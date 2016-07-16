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

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;

import au.com.cybersearch2.cybertete.CyberteteException;

/**
 * SecurityResources
 * The security resource environment
 * @author Andrew Bowley
 * 30 May 2016
 */
public class SecurityResources
{
    /** Name for Java GSS-API authenication configuration */
    public static final String KRB5_INITIATOR = "com.sun.security.jgss.initiate";

    /** JAAS configuration by default */
    private static final String[] CONFIG_CONTENTS =
    {
        "    com.sun.security.jgss.initiate {",
        "    com.sun.security.auth.module.Krb5LoginModule",
        "      required",
        "      useTicketCache=true",
        "      doNotPrompt=true;",
        "      };"
    };

    /** Logger */
    Logger logger;


    /**
     * postConstruct
     * @param loggerProvider Logger factory
     */
    @PostConstruct
    void postConstruct(ILoggerProvider loggerProvider)
    {
        logger = loggerProvider.getClassLogger(SecurityResources.class);
        // Test default key managers are available for SSL. A CyberteteException is thrown if this fails.
        getKeyManagers(null, null);
    }

    /**
     * Returns the AppConfigurationEntries for the Java GSS-API authenication configuration
     * @return AppConfigurationEntry array
     */
    public AppConfigurationEntry[] getAppConfigurationEntry()
    {
        try
        {
            return getConfiguration().getAppConfigurationEntry(KRB5_INITIATOR);
        }
        catch(SecurityException e)
        {
            logger.error(e, "Error accessing configuration named \"" + KRB5_INITIATOR + "\"");
        }
        return new AppConfigurationEntry[]{};
    }

    /**
     * Returns Kerberos login context
     * @return LoginContext object
     * @throws LoginException
     */
    public LoginContext loginContextInstance() throws LoginException
    {
        return new LoginContext(KRB5_INITIATOR);
    }

    /**
     * Create Kerberos configuration file required for single signon.
     * @throws IOException
     */
    public void createDefaultLoginConfigFile(Path configPath) throws IOException
    {
        ArrayList<String> lines = new ArrayList<>();
        for (String line: CONFIG_CONTENTS)
            lines.add(line);
        if (!writeFile(configPath, lines))
            throw new IOException("Cannot update Login Configuration file: " + configPath.toString());
    }

    /**
     * Returns key managers from given keystore
     * @param keyStore Keystore containing SSL data
     * @param keypass Password to keystore
     * @return KeyManager array
     */
    public KeyManager[] getKeyManagers(KeyStore keyStore, char[] keypass)
    {
        try
        {
            KeyManagerFactory kmf = getKeyManagerFactory();
            kmf.init(keyStore, keypass);
            return kmf.getKeyManagers();
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new CyberteteException("SSL default algorithm error", e);
        }
        catch (UnrecoverableKeyException e)
        {
            throw new CyberteteException("Private key cannot be recovered", e);
        }
        catch (KeyStoreException e)
        {
            throw new CyberteteException("Key Management error", e);
        }
    }

    /**
     * Returns SSL context instance
     * @param kms Key managers
     * @return SSLContext object
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException 
     */
    public SSLContext getSSLContext(KeyManager[] kms) throws NoSuchAlgorithmException, KeyManagementException
    {
        SSLContext sslContext = SSLContext.getInstance(SecurityConfig.SSL_PROTOCOL);
        sslContext.init(kms, null, new java.security.SecureRandom());
        return sslContext;
    }

    /**
     * Returns loaded keystore
     * @param keyStoreFile The file path 
     * @param keyStoreType The keystore type
     * @param keypass The keystore password 
     * @return KeyStore object
     * @throws KeyStoreException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     */
    public KeyStore getKeyStore(String keyStoreFile, String keyStoreType, char[] keypass) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException
    {
        KeyStore keyStore =  KeyStore.getInstance(keyStoreType);
        FileInputStream fileStream = null;
        try
        {
             fileStream = new FileInputStream(keyStoreFile);
             keyStore.load(fileStream, keypass);
        }
        finally
        {
            if (fileStream != null)
                try
                {   // Close quietly 
                    fileStream.close();
                }
                catch (IOException e)
                {
                }
        }
        return keyStore;
    }

    /**
     * Returns default key manager factory
     * @return KeyManagerFactory object
     * @throws NoSuchAlgorithmException
     */
    protected KeyManagerFactory getKeyManagerFactory() throws NoSuchAlgorithmException
    {
        return KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    }

    /**
     * Get the installed login Configuration.
     * @return the login Configuration.
     */
    protected Configuration getConfiguration()
    {
        return Configuration.getConfiguration();
    }
    
    /**
     * Writes given lines to specified path, creating path if necessary
     * @param path File system path
     * @param lines File content as a collection of lines
     * @return flag set true if operation was permitted
     * @throws IOException
     */
    protected boolean writeFile(Path path, ArrayList<String> lines) throws IOException
    {
        Charset charset = Charset.forName("UTF-8");
        if (!Files.exists(path, new LinkOption[]{LinkOption.NOFOLLOW_LINKS}))
            Files.write(path, lines, charset, StandardOpenOption.CREATE_NEW);
        else if (Files.isWritable(path))
            Files.write(path, lines, charset, StandardOpenOption.WRITE);
        else 
            return false;
        return true;
    }
 
}
