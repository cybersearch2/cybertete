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
package au.com.cybersearch2.cybertete.service;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

import au.com.cybersearch2.cybertete.security.SslSessionData;

/**
 * ChainHostnameVerifier
 * Hooks into SSL host verifier chain and extract SSL data such as server certificates
 * @author Andrew Bowley
 * 31 Mar 2016
 */
public class ChainHostnameVerifier extends SslSessionData implements HostnameVerifier
{
    /** The next verifier in the chain */
    private HostnameVerifier hostnameVerifier;
    /** Host name passed in verify call */
    private String hostName;

    /**
     * Create ChainHostnameVerifier object
     */
    public ChainHostnameVerifier()
    {
        super();
        hostName = "";
    }

    /**
     * Set next verifier to chain to
     * @param hostnameVerifier HostnameVerifier object
     */
    public void chainHostnameVerifier(HostnameVerifier hostnameVerifier)
    {
        this.hostnameVerifier = hostnameVerifier;
    }

    /**
     * Returns host name passed in verify call   
     * @return Host name
     */
    public String getHostName()
    {
        return hostName;
    }

    /**
     * verify
     * @see javax.net.ssl.HostnameVerifier#verify(java.lang.String, javax.net.ssl.SSLSession)
     */
    @Override
    public boolean verify(String hostName, SSLSession session)
    {
        this.hostName = hostName;
        try
        {
            certificates = getX509Certs(session.getPeerCertificates());
        }
        catch (SSLPeerUnverifiedException e)
        {   // This exception is not expected
            // The getPeerCertificates() method can be used only when using certificate-based cipher suites; 
            // using it with non-certificate-based cipher suites, such as Kerberos, 
            // will throw an SSLPeerUnverifiedException
            clearCertificates();
        }
        protocol = session.getProtocol();
        cipherSuite = session.getCipherSuite();
        // Chain handler is set when XmppConnectionFactory creates a connection
        return hostnameVerifier != null ? hostnameVerifier.verify(hostName, session) : true;
    }

    /**
     * Returns list of sever certificates
     * certChain X509 certificate array
     * @return List of X509Certificate objects
     */
    List<X509Certificate> getX509Certs(Certificate[] certChain)
    {
        List<X509Certificate> x509Certs = new ArrayList<X509Certificate>(certChain.length);
        for (Certificate cert: certChain)
            x509Certs.add((X509Certificate)cert);
        return x509Certs;

    }
}
