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
package au.com.cybersearch2.cybertete;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.jivesoftware.smack.SmackInitialization;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;

import au.com.cybersearch2.cybertete.model.dns.DnsResolver;
import au.com.cybersearch2.cybertete.model.dns.HostAddress;
import au.com.cybersearch2.cybertete.smack.SmackDnsResolver;

/**
 * GlobalProperties
 * Properties such as System properties which are universally accessible
 * @author Andrew Bowley
 * 29 May 2016
 */
public class GlobalProperties
{
    /** System property name for JAAS login configuraiton file */
    public static final String AUTH_LOGIN = "java.security.auth.login.config";
    /** This additional configuration specifies SASLJavaX is required, while the normal configuration
     *  makes it optional. 
     */
    public static final String[] SMACK_SASL_CONFIG = 
    {
        "<?xml version=\"1.0\"?>",
        "<smack>",
            "<startupClasses>",
            "<className>org.jivesoftware.smack.sasl.javax.SASLJavaXSmackInitializer</className>",
            "</startupClasses>",
            "<optionalStartupClasses/>",  
        "</smack>"
     };

    private List<HostAddress> localXmppHosts;
    
    @PostConstruct
    void postConstruct()
    {
    	localXmppHosts = new ArrayList<>();
        setSystemProperties();
    }
    
    /**
     * Returns auth login config file path
     * @return path or empty String if not configured
     */
    public String getAuthLoginConfigPath()
    {
        return System.getProperty(AUTH_LOGIN, "");
    }
 
    public String getUserHome()
    {
        return System.getProperty("user.home");
    }
    
    public String getUserName()
    {
        return System.getProperty("user.name");
    }

    /**
     * Set auth login config file path
     * @param path The path to set
     */
    public void setAuthLoginConfigPath(String path)
    {
        System.setProperty(AUTH_LOGIN, path);
    }
 
    /**
     * Returns flag set true if  auth login config file exists
     * @return boolean
     */
    public boolean authLoginConfigExists()
    {
    	// Single signon not possible without a valid auth login configuration file
    	File authConfigFile = new File(getAuthLoginConfigPath());
    	return authConfigFile.exists();
    }
    
    public List<HostAddress> getLocalXmppHosts() {
    	List<HostAddress> localXmppHostsCopy = new ArrayList<>();
    	localXmppHostsCopy.addAll(localXmppHosts);
    	return localXmppHostsCopy;
	}

	/**
     * Overrides Smack java.security.auth.login.config system property setting 
     */
    void setSystemProperties()
    {
        // Smack SASLGSSAPIMechanism statically sets System property java.security.auth.login.config to "gss.conf".
        // This assumes the current user directory is predictable, but it is not.
        // Therefore, this setting has to be overriden. 
        // The following Smack Initialization will commence as normal, then read the additional configuration 
        // (SMACK_SASL_CONFIG) which ensures SASLGSSAPIMechanism is loaded as part of the SASLJavaX initialization. 
        String loginConfigPath = getAuthLoginConfigPath();
        StringBuilder builder = new StringBuilder();
        for (String line: SMACK_SASL_CONFIG)
            builder.append(line);
        InputStream stream = new ByteArrayInputStream(builder.toString().getBytes());
        try
        {
            SmackInitialization.processConfigFile(stream, null);
        }
        catch (Exception e)
        {
            throw new CyberteteException("Error initializing Chat Service", e);
        }
        // Use DNS to obtain local XMPP servers
   		DnsResolver dnsResolver = new SmackDnsResolver();
	   	Name[] names = Lookup.getDefaultSearchPath();
	   	for (Name name: names) 
	   	{
	   		localXmppHosts.addAll(dnsResolver.resolveXMPPDomain(name.toString(true)));
	   	}

        // Initialisation of the config builder is where SASLGSSAPIMechanism is first referenced.
        if (!loginConfigPath.isEmpty())
            setAuthLoginConfigPath(loginConfigPath);
    }
}
