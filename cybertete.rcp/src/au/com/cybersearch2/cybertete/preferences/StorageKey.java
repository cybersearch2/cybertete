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
package au.com.cybersearch2.cybertete.preferences;

import au.com.cybersearch2.e4.PrefKey;

/**
 * StorageKey
 * @author Andrew Bowley
 * 12 May 2016
 */
public enum StorageKey implements PrefKey
{
    host,
    port,
    auth_cid,
    plain_sasl,
    password,
    last_user,
    auto_login,
    client_cert_auth,
    keystore_type,
    keystore_file,
    keystore_password,
    sso_user,
    null_key;

    private String preference;
    
    static
    {
        host.preference = PreferenceConstants.HOST;
        port.preference = PreferenceConstants.PORT;
        auth_cid.preference = PreferenceConstants.AUTH_CID;
        plain_sasl.preference = PreferenceConstants.PLAIN_SASL;
        password.preference = PreferenceConstants.PASSWORD;
        last_user.preference = PreferenceConstants.LAST_USER;
        auto_login.preference = PreferenceConstants.AUTO_LOGIN;
        client_cert_auth.preference = PreferenceConstants.CLIENT_CERT_AUTH;
        keystore_type.preference = PreferenceConstants.KEYSTORE_TYPE;
        keystore_file.preference = PreferenceConstants.KEYSTORE_FILE;
        keystore_password.preference = PreferenceConstants.KEYSTORE_PASSWORD;
        sso_user.preference = PreferenceConstants.SSO_USER;
        null_key.preference = "";
    }

    @Override
    public String getPreference()
    {
        return preference;
    }
    
    public static StorageKey toStorageKey(String preference)
    {
    	switch(preference)
    	{
    	case PreferenceConstants.HOST: return host;
    	case PreferenceConstants.PORT: return port;
    	case PreferenceConstants.AUTH_CID: return auth_cid;
    	case PreferenceConstants.PLAIN_SASL: return plain_sasl;
    	case PreferenceConstants.PASSWORD: return password;
    	case PreferenceConstants.LAST_USER: return last_user;
    	case PreferenceConstants.AUTO_LOGIN: return auto_login;
    	case PreferenceConstants.CLIENT_CERT_AUTH: return client_cert_auth;
    	case PreferenceConstants.KEYSTORE_TYPE: return keystore_type;
    	case PreferenceConstants.KEYSTORE_FILE: return keystore_file;
    	case PreferenceConstants.KEYSTORE_PASSWORD: return keystore_password;
    	case PreferenceConstants.SSO_USER: return sso_user;
        default: return null_key;
    	}
    }
}
