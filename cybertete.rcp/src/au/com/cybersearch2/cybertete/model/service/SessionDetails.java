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
package au.com.cybersearch2.cybertete.model.service;

import static org.jxmpp.util.XmppStringUtils.parseLocalpart;
import static org.jxmpp.util.XmppStringUtils.parseDomain;

/**
 * SessionDetails
 * Information required for one user, identified by JID, to log in.
 * Collates on user JID.
 * @author Andrew Bowley
 * 3 Apr 2016
 */
public class SessionDetails implements ChatAccount, AuthenticationData, Comparable<SessionDetails>
{
    /** User JID */
    private String jid;
    /** Authentication user ID (optional) */
	private String authcid;
	/** Host name (optional) */
	private String host;
	/** Password (not required for single signon) */
	private String password;
	/** Port - required if host is set */
	private int port;
	/** Flag set true if plain SASL authentication allowed */
	private boolean plainSasl;
	/** Dirty flag */
	boolean isDirty;

	/** 
	 * Create SessionDetails object
	 * @param jid User JID. May be empty String if new configuration being created.
	 * @param password Authentication password
	 */
    public SessionDetails(String jid, String password) 
    {
        this.jid = jid;
        this.password = password;
        // Set dirty flag true so this object is persisted
        isDirty = true;
    }

	/**
	 * Create SessionDetails when using authentication mechanism which does not require a password
	 * @param jid User JID
	 */
	public SessionDetails(String jid) 
	{
	    this(jid, "");
        setAuthcid(""); // No authentication with password
        setHost("");    // Host taken from domain part of JID
	}

	/**
	 * Create a SessionDetails object for a new chat account
	 * @param chatAccount ChatAccount object
	 */
    public SessionDetails(ChatAccount chatAccount)
    {
        this.jid = chatAccount.getJid();
        updateAccount(chatAccount);
   }

    /**
     * Update details from a chat account
     * @param account ChatAccount object
     */
    public void updateAccount(ChatAccount account)
    {
        password = account.getPassword();
        host = account.getHost();
        port = account.getPort();
        authcid = account.getAuthcid();
        plainSasl = account.isPlainSasl();
        isDirty = true;
    }

    /**
     * @see au.com.cybersearch2.cybertete.model.service.AuthenticationData#getUsername()
     */
    @Override
    public CharSequence getUsername()
    {
        return authcid == null ? parseLocalpart(jid) : authcid;
    }

    /**
     * @see au.com.cybersearch2.cybertete.model.service.AuthenticationData#getPassword()
     */
    @Override
	public String getPassword() 
	{
		return password;
	}

    /**
     * Clear password
     */
    public void clearPassword()
    {
        this.password = "";
        isDirty = true;
    }

    /**
     * @return user JID
     */
    @Override
    public String getJid()
    {
        return jid;
    }

    /**
     * Returns authentication user ID 
     * @return authcid or null if not used
     */
    @Override
    public String getAuthcid()
    {
        return authcid;
    }

    /**
     * Set authentication user ID
     * @param authcid String
     */
    public void setAuthcid(String authcid)
    {
        this.authcid = authcid;
        isDirty = true;
    }

    /**
     * Returns host
     * @return host or null if not used
     */
    @Override
    public String getHost() 
    {
        return host;
    }

    /**
     * Set host
     * @param host String
     */
    public void setHost(String host)
    {
        this.host = host;
        isDirty = true;
    }

    /**
     * @return the port
     */
    @Override
    public int getPort()
    {
        return port;
    }

    /**
     * Set the port
     * @param port int
     */
    public void setPort(int port)
    {
        this.port = port;
        isDirty = true;
    }

    /**
     * @return Flag set true if plain SASL authentication allowed
     */
    @Override
    public boolean isPlainSasl()
    {
        return plainSasl;
    }

    /**
     * Set flag for plain SASL authentication allowed
     * @param plainSasl boolean
     */
    public void setPlainSasl(boolean plainSasl)
    {
        this.plainSasl = plainSasl;
        isDirty = true;
    }

    /**
     * Returns domain part of user JID
     * @return domain or empty String if JID empty
     */
    public String getDomain()
    {
        return jid.isEmpty()  ? "" : parseDomain(jid);
    }

    /**
     * Returns flag set true if any changes have been made since last clearDirtyFlag() call
     * @return boolean
     */
    public boolean isDirty()
    {
        return isDirty;
    }
 
    /**
     * Clears dirty flag
     */
    public void clearDirtyFlag()
    {
        isDirty = false;
    }
    
    /**
     * Compares this object with the specified object for order
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(SessionDetails another)
    {
        return jid.compareTo(another.getJid());
    }

    /**
     * Indicates whether some other object is "equal to" this one
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object another)
    {
        if (another instanceof SessionDetails)
            return ((SessionDetails)another).getJid().equals(jid);
        return false;
    }

    /**
     * Returns a hash code value for the object
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return jid.hashCode();
    }

}
