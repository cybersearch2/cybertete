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

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Presence.Type;

/**
 * SmackPresence
 * Translates Presence model to XMPP presence.
 * Smack Presence class is final, so cannot be subclassed to achieve conversion.
 * @author Andrew Bowley
 * 23 Dec 2015
 */
public class SmackPresence
{
    private Presence xmppPresence;
    
    /**
     * Create SmackPresence object
     */
    public SmackPresence(au.com.cybersearch2.cybertete.model.Presence modelPresence)
    {
        Mode mode = null;
        switch (modelPresence)
        {
        case online: mode = Mode.available; break;
        case away: mode = Mode.away; break;
        case dnd: mode = Mode.dnd; break;
        default: break; 
        }
        Type type = mode == null ? 
           Type.unavailable :
           Type.available;
            
        xmppPresence = new Presence(type, "", 1, mode);
    }

    /**
     * @return the xmppPresence
     */
    public Presence getXmppPresence()
    {
        return xmppPresence;
    }

}
