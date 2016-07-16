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

/**
 * HostAddress
 * Container for Fully qualified domain name and port
 * @author Andrew Bowley
 * 25 Nov 2015
 */
public class HostAddress
{
    private final String fqdn;
    private final int port;

    /**
     * Creates a new HostAddress with the given FQDN and port
     * 
     * @param fqdn Fully qualified domain name.
     * @param port The port to connect on.
     */
    public HostAddress(String fqdn, int port)
    {
        this.fqdn = fqdn;
        this.port = port;
    }

    public String getFqdn()
    {
        return fqdn;
    }

    public int getPort()
    {
        return port;
    }

}
