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
package au.com.cybersearch2.cybertete.model.internal;

import au.com.cybersearch2.cybertete.model.LoginBean;

/**
 * LoginConfig
 * Login configuration captured from screen used for updating saved configuration.
 * @author Andrew Bowley
 * 1 Mar 2016
 */
public class LoginConfig implements LoginBean
{
    String jid;
    String host;
    String username;
    String password;
    String gssapiPrincipal;
    int port;
    boolean autoLogin;
    boolean plainSasl;

    /**
     * @see au.com.cybersearch2.cybertete.model.LoginBean#getJid()
     */
    @Override
    public String getJid()
    {
        return jid;
    }

    /**
     * @see au.com.cybersearch2.cybertete.model.LoginBean#getHost()
     */
    @Override
    public String getHost()
    {
        return host;
    }

    /**
     * @see au.com.cybersearch2.cybertete.model.LoginBean#getPort()
     */
    @Override
    public int getPort()
    {
        return port;
    }

    /**
     * @see au.com.cybersearch2.cybertete.model.LoginBean#getUsername()
     */
    @Override
    public String getUsername()
    {
        return username;
    }

    /**
     * @see au.com.cybersearch2.cybertete.model.LoginBean#getPassword()
     */
    @Override
    public String getPassword()
    {
        return password;
    }

    /**
     * @see au.com.cybersearch2.cybertete.model.LoginBean#isAutoLogin()
     */
    @Override
    public boolean isAutoLogin()
    {
        return autoLogin;
    }

    /**
     * @see au.com.cybersearch2.cybertete.model.LoginBean#isPlainSasl()
     */
    @Override
    public boolean isPlainSasl()
    {
        return plainSasl;
    }

    /**
     * @see au.com.cybersearch2.cybertete.model.LoginBean#getGssapiPrincipal()
     */
    @Override
    public String getGssapiPrincipal()
    {
        return gssapiPrincipal;
    }

    /**
     * @param jid the jid to set
     */
    public void setJid(String jid)
    {
        this.jid = jid;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host)
    {
        this.host = host;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @param gssapiPrincipal the gssapiPrincipal to set
     */
    public void setGssapiPrincipal(String gssapiPrincipal)
    {
        this.gssapiPrincipal = gssapiPrincipal;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    /**
     * @param autoLogin the autoLogin to set
     */
    public void setAutoLogin(boolean autoLogin)
    {
        this.autoLogin = autoLogin;
    }

    /**
     * @param plainSasl the plainSasl to set
     */
    public void setPlainSasl(boolean plainSasl)
    {
        this.plainSasl = plainSasl;
    }

}
