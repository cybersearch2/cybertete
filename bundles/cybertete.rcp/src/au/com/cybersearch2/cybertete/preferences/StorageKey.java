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
    single_signon,
    plain_sasl,
    password,
    last_user,
    auto_login,
    client_cert_auth,
    keystore_type,
    keystore_file,
    keystore_password;

    private String preference;
    
    static
    {
        host.preference = PreferenceConstants.HOST;
        port.preference = PreferenceConstants.PORT;
        auth_cid.preference = PreferenceConstants.AUTH_CID;
        single_signon.preference = PreferenceConstants.SINGLE_SIGNON;
        plain_sasl.preference = PreferenceConstants.PLAIN_SASL;
        password.preference = PreferenceConstants.PASSWORD;
        last_user.preference = PreferenceConstants.LAST_USER;
        auto_login.preference = PreferenceConstants.AUTO_LOGIN;
        client_cert_auth.preference = PreferenceConstants.CLIENT_CERT_AUTH;
        keystore_type.preference = PreferenceConstants.KEYSTORE_TYPE;
        keystore_file.preference = PreferenceConstants.KEYSTORE_FILE;
        keystore_password.preference = PreferenceConstants.KEYSTORE_PASSWORD;
    }

    @Override
    public String getPreference()
    {
        return preference;
    }
    
    public static StorageKey toStorageKey(String preference)
    {
        if (preference.equals(PreferenceConstants.HOST))
            return host;
        else if (preference.equals(PreferenceConstants.PORT))
            return port;
        else if (preference.equals(PreferenceConstants.AUTH_CID))
            return auth_cid;
        else if (preference.equals(PreferenceConstants.SINGLE_SIGNON))
            return single_signon;
        else if (preference.equals(PreferenceConstants.PLAIN_SASL))
            return plain_sasl;
        else if (preference.equals(PreferenceConstants.PASSWORD))
            return password;
        return null;
    }
}
