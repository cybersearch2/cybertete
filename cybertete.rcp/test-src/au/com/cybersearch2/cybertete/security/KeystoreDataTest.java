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

import java.security.KeyStore;
import java.security.cert.X509Certificate;

import org.junit.Test;

import au.com.cybersearch2.cybertete.model.service.KeystoreConfig;

/**
 * KeystoreDataTest
 * @author Andrew Bowley
 * 3 May 2016
 */
public class KeystoreDataTest
{
    private static final String KEYSTORE_FILE = "keystore.jks";
    private static final String KEYSTORE_PASS = "changeme";

    @Test
    public void test_constructor()
    {
        KeyStore keyStore = mock(KeyStore.class);
        char[] keypass = KEYSTORE_PASS.toCharArray();
        X509Certificate[] certificateChain = new X509Certificate[]{mock(X509Certificate.class)};
        KeystoreConfig keystoreConfig = mock(KeystoreConfig.class);
        KeystoreHelper keystoreHelper = mock(KeystoreHelper.class);
        KeystoreData keystoreData = new KeystoreData(KEYSTORE_FILE, keyStore, keypass, certificateChain);
        when(keystoreHelper.getKeystoreData(keystoreConfig)).thenReturn(keystoreData);
        when(keystoreConfig.getKeystoreFile()).thenReturn(KEYSTORE_FILE);
        KeystoreData underTest = new KeystoreData(keystoreConfig, keystoreHelper);
        assertThat(underTest.getKeystoreFile()).isEqualTo(KEYSTORE_FILE);
        assertThat(underTest.getKeyStore()).isEqualTo(keyStore);
        assertThat(underTest.getKeypass()).isEqualTo(keypass);
        assertThat(underTest.getCertificateChain()).isEqualTo(certificateChain);
    }
    
}
