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
package au.com.cybersearch2.cybertete.smack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import au.com.cybersearch2.cybertete.model.dns.DnsResolver;
import au.com.cybersearch2.cybertete.model.dns.HostAddress;

import org.jivesoftware.smack.util.DNSUtil;

/**
 * SmackDnsResolver
 * @author Andrew Bowley
 * 25 Nov 2015
 */
public class SmackDnsResolver implements DnsResolver
{

    /**
     * @see au.com.cybersearch2.cybertete.model.dns.DnsResolver#resolveXMPPDomain(java.lang.String)
     */
    @Override
    public List<HostAddress> resolveXMPPDomain(String domain)
    {
        List<org.jivesoftware.smack.util.dns.HostAddress>xmppHostAddresses = DNSUtil.resolveXMPPDomain(domain, null);
        if (xmppHostAddresses.size() == 0)
            return Collections.emptyList();
        List<HostAddress> hostAddressList = new ArrayList<HostAddress>(xmppHostAddresses.size());
        for (org.jivesoftware.smack.util.dns.HostAddress xmppHostAddress: xmppHostAddresses)
        {
            HostAddress hostAddress = new HostAddress(xmppHostAddress.getFQDN(), xmppHostAddress.getPort());
            hostAddressList.add(hostAddress);
        }
        return hostAddressList;
    }

}
