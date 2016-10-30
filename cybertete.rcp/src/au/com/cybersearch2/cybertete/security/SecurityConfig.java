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
package au.com.cybersearch2.cybertete.security;

import au.com.cybersearch2.cybertete.model.service.KeystoreConfig;

/**
 * SecurityConfig
 * Keystore configuation implementation
 * @author Andrew Bowley
 * 16 Dec 2015
 */
public class SecurityConfig implements KeystoreConfig
{
    /** Keystore file path */
    private String keystoreFile;
    /** Keystore type */
    private String keystoreType;
    /** Keystore password */
    private String keystorePassword;

    /**
     * Create uninitialized SecurityConfig object to use as null token
     */
    public SecurityConfig()
    {
    }

    /**
     * Create SecurityConfig object
     * @param keystoreFile Keystore file path
     * @param keystoreType Keystore file path
     * @param keystorePassword Keystore password
     */
    public SecurityConfig(
        String keystoreFile,
        String keystoreType,
        String keystorePassword)
    {
        this.keystoreFile = keystoreFile;
        this.keystoreType= keystoreType;
        this.keystorePassword = keystorePassword;
    }

    /**
     * @see au.com.cybersearch2.cybertete.model.service.KeystoreConfig#getKeystoreFile()
     */
    @Override
    public String getKeystoreFile()
    {
        return keystoreFile;
    }

    /**
     * @see au.com.cybersearch2.cybertete.model.service.KeystoreConfig#getKeystoreType()
     */
    @Override
    public String getKeystoreType()
    {
        return keystoreType;
    }

    /**
     * @see au.com.cybersearch2.cybertete.model.service.KeystoreConfig#getKeystorePassword()
     */
    @Override
    public String getKeystorePassword()
    {
         return keystorePassword;
    }

    /**
     * Set configuration
     * @param keystoreConfig Keystore configuration
     */
    public void setConfig(KeystoreConfig keystoreConfig)
    {
        this.keystoreFile = keystoreConfig.getKeystoreFile();
        this.keystoreType = keystoreConfig.getKeystoreType();
        this.keystorePassword = keystoreConfig.getKeystorePassword();
    }

}
