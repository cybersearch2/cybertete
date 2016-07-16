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
package au.com.cybersearch2.cybertete.model;

/**
 * ChatPost
 * Contains details of a Chat message to be dispatched to Chat server
 * @author Andrew Bowley
 * 7 Apr 2016
 */
public class ChatPost
{
    /** Post contact entry */
    private ContactEntry from;
    /** Post message text */
    private String body;

    /**
     * Create ChatPost object
     * @param from Post contact entry
     * @param body Post message text
     */
    public ChatPost(ContactEntry from, String body)
    {
        this.from = from;
        this.body = body;
    }
    /**
     * @return the from
     */
    public ContactEntry getFrom()
    {
        return from;
    }
    /**
     * @return the body
     */
    public String getBody()
    {
        return body;
    }

    
}
