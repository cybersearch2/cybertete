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
package au.com.cybersearch2.cybertete.model.dns;

import java.util.List;

/**
 * DnsResolver
 * @author Andrew Bowley
 * 25 Nov 2015
 */
public interface DnsResolver
{
    /**
     * Interface based on org.jivesoftware.smack.util.DNSUil resolveXMPPDomain()
     * Returns a list of HostAddresses under which the specified XMPP server can be reached at for client-to-server
     * communication. A DNS lookup for a SRV record in the form "_xmpp-client._tcp.example.com" is attempted, according
     * to section 3.2.1 of RFC 6120. If that lookup fails, it's assumed that the XMPP server lives at the host resolved
     * by a DNS lookup at the specified domain on the default port of 5222.
     * <p>
     * As an example, a lookup for "example.com" may return "im.example.com:5269".
     * </p>
     *
     * @param domain Fully qualified domain address
     * @return List of HostAddress, which encompasses the hostname and port that the
     *      XMPP server can be reached at for the specified domain.
     */
    List<HostAddress> resolveXMPPDomain(String domain);
}
