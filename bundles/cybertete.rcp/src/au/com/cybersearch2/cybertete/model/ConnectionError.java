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
package au.com.cybersearch2.cybertete.model;

import javax.security.sasl.SaslException;

/**
 * ConnectionError
 * Errors anticipated to occur on a connection in establishment or running state,
 * except the "unclssified" type is any other error.
 * Includes message formats for reporting errors.
 * @author Andrew Bowley
 * 25 Nov 2015
 */
public enum ConnectionError
{
    noError,
    notAuthorized,
    notAcceptable,
    unknownHost,
    connectionRefused,
    connectionTimeout,
    packetTimeout,
    permissionDenied,
    noCertificatePath,
    unclassified;
  
    /**
     * Returns message format specified by type of connection error
     * Parameter {0} is the exceptiom message and {1} is the host address and port
     * @param type ConnectionError enum
     * @return Message format
     */
    public static String getMessageFormat(ConnectionError type)
    {
        switch (type)
        {
        case notAuthorized:
            return "Password or account invalid";
        case notAcceptable:
            return "Subscription request rejected";
        case unknownHost:
            return "Chat Server at {1} is in unknown domain";
        case connectionRefused:
            return "Connection request to Chat Server at {1} refused";
        case connectionTimeout:
            return "Connection timed out - Maybe Chat server busy or firewall issue";
        case packetTimeout:
            return "Timed out waiting for response from {1}";
        case permissionDenied:
            return "Connection to Chat server blocked by firewall";
        case noCertificatePath:
        default:
        }
        return "{0}";
    }

    /**
     * Returns ConnectionError mapped to exception parameter
     * @param throwable The exception
     * @return ConnectionError
     */
    public static ConnectionError classifyException(Throwable throwable)
    {
        ConnectionError connectionError = ConnectionError.unclassified;
        Throwable cause = throwable.getCause();
        boolean isSaslException = (cause != null) && (cause instanceof SaslException);
        String message = throwable.getMessage();
        if (isSaslException || message.contains("not-authorized"))
            connectionError = notAuthorized;
        else if (message.contains("not-acceptable"))
            connectionError = notAcceptable;
        else if (message.contains("UnknownHost"))
            connectionError = unknownHost;
        else if (message.contains("Connection refused"))
            connectionError = connectionRefused;
        else if (message.contains("Connection timed out"))
            connectionError = connectionTimeout;
        else if (message.contains("No response"))
            connectionError = packetTimeout;
        else if (message.contains("Permission denied"))
            connectionError = permissionDenied;
        else if (message.contains("certification path"))
            connectionError = noCertificatePath;
        return connectionError;
    }
}
