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

import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

/**
 * SslSessionData
 * Contains server certificates and SSL cipher suite and protocol
 * @author Andrew Bowley
 * 17 Dec 2015
 */
public class SslSessionData
{
    /** Empty certificate list */
    public final static List<X509Certificate> EMPTY_CERT_LIST;
    /** Empty session data object */
    public final static SslSessionData EMPTY_SSL_SESSION_DATA;

    /** Server certificate list */
    protected List<X509Certificate> certificates;
    /** SSL protocol */
    protected String protocol;
    /** SSL cipher suite */
    protected String cipherSuite;
    
    static
    {
        EMPTY_CERT_LIST = Collections.emptyList(); 
        EMPTY_SSL_SESSION_DATA = new SslSessionData(EMPTY_CERT_LIST, "", "");
    }
    
    /**
     * Create SslSessionData object
     * @param certificates Certificate chain of the ssession peer
     * @param protocol  Name of the SSL cipher suite which is used for all connections in the session
     * @param cipherSuite The SSL cipher suite
     */
    public SslSessionData(List<X509Certificate> certificates, String protocol, String cipherSuite)
    {
        this.certificates = certificates;
        this.protocol = protocol;
        this.cipherSuite = cipherSuite;
    }

    /**
     * Create empty SslSessionData object
     */
    protected SslSessionData()
    {
        this(EMPTY_CERT_LIST, "", "");
    }

    /**
     * Returns list of sever certificates
     * @return List of X509Certificate objects
     */
    public List<X509Certificate> getCertificates()
    {
        return certificates;
    }

    /**
     * Returns SSL Session protocol
     * @return the protocol
     */
    public String getProtocol()
    {
        return protocol;
    }

    /**
     * Returns SSL Session cipher suite
     * @return the cipher suite
     */
    public String getCipherSuite()
    {
        return cipherSuite;
    }

    /**
     * Clear SSL server certificates
     */
    public void clearCertificates()
    {
        certificates = EMPTY_CERT_LIST;

    }
  
}
