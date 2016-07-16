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

import java.security.KeyStore;
import java.security.cert.X509Certificate;

import au.com.cybersearch2.cybertete.model.service.KeystoreConfig;

/**
 * KeystoreData
 * Security information related to a loaded keystore
 * @author Andrew Bowley
 * 17 Mar 2016
 */
public class KeystoreData
{

    /** Represents a storage facility for cryptographic keys and certificates */
    KeyStore keyStore;
    /** Keystore password */
    char[] keypass;
    /** The certificate chain */
    X509Certificate[] certificateChain;
    /** Path to keystore file on disk */
    private String keystoreFile;

    /**
     * Create KeystoreData object by reading a keystore using given configuration data
     * It will not be initialized if the keystore file is not configured. 
     * This is allowed to indicated keystore data not available rather than setting a KeystoreData variable to null.
     * @param keystoreConfig Keystore configuration parameters
     * @param keystoreHelper Accesses keystore used to hold SSL certificates and encryption keys
     */
    public KeystoreData(KeystoreConfig keystoreConfig, KeystoreHelper keystoreHelper)
    {
        keystoreFile = keystoreConfig.getKeystoreFile();
        // If no keystore file specified, then leave this object uninitialized so the default keystore is selected
        if (keystoreFile != null)
            copy(keystoreHelper.getKeystoreData(keystoreConfig));
    }

    /**
     * Create KeystoreData object 
     * @param keystoreFile Keystore file path
     * @param keyStore The keystore object obtained from reading the keystore file
     * @param keypass Keystore password
     * @param certificateChain X509 Certificate chain extracted from keystore
     */
    public KeystoreData(String keystoreFile, KeyStore keyStore, char[] keypass, X509Certificate[] certificateChain)
    {
        this.keystoreFile = keystoreFile;
        this.keyStore = keyStore;
        this.keypass = keypass;
        this.certificateChain = certificateChain;
    }
    
    /**
     * @return the keyStore
     */
    public KeyStore getKeyStore()
    {
        return keyStore;
    }

    /**
     * @return the keypass
     */
    public char[] getKeypass()
    {
        return keypass;
    }

    /**
     * @return the keystoreFile
     */
    public String getKeystoreFile()
    {
        return keystoreFile;
    }

    /** 
     * Returns certificate chain from keystore
     * @return X509Certificate array, which will be empty if the certificate chain is not available 
     */
    public X509Certificate[] getCertificateChain()
    {
        return certificateChain == null ? new X509Certificate[]{} : certificateChain;
    }

    /**
     * Duplicate a KeystoreData object
     * @param keystoreData
     */
    void copy(KeystoreData keystoreData)
    {
        keystoreFile = keystoreData.getKeystoreFile();
        keyStore = keystoreData.getKeyStore();
        keypass = keystoreData.getKeypass();
        certificateChain = keystoreData.getCertificateChain();
    }


}
