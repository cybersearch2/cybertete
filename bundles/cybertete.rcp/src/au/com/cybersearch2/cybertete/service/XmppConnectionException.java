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
package au.com.cybersearch2.cybertete.service;

import java.text.MessageFormat;

import javax.xml.ws.WebServiceException;

import au.com.cybersearch2.cybertete.model.ConnectionError;

/**
 * XmppConnectionException
 * Unchecked exception containing details of an XMPP connection error
 * @author Andrew Bowley
 * 23 Dec 2015
 */
public class XmppConnectionException extends WebServiceException
{
    private static final long serialVersionUID = 1L;
    private ConnectionError connectionError;
    private String host;
    private int port;
    
    /**
     * Create XmppConnectionException object
     * @param message
     * @param cause
     */
    public XmppConnectionException(String message, Throwable cause, ConnectionError connectionError, String host, int port)
    {
        super(message, cause);
        this.connectionError = connectionError;
        this.host = host;
        this.port = port;
    }

    /**
     * @return the connectionError
     */
    public ConnectionError getConnectionError()
    {
        return connectionError;
    }

    /**
     * @return the host
     */
    public String getHost()
    {
        return host;
    }

    /**
     * @return the port
     */
    public int getPort()
    {
        return port;
    }
 
    /**
     * Returns formatted message
     * @return String
     */
    public String getDetails()
    {
        return MessageFormat.format(ConnectionError.getMessageFormat(connectionError), 
                              getCause().getMessage(), 
                              host + ":" + port);

    }
}

