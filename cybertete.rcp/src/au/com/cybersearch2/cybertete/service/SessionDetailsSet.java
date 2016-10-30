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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import au.com.cybersearch2.cybertete.model.service.ChatAccount;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;

/**
 * SessionDetailsSet
 * Collection of login session details including current user new user. 
 * Set ordered for user selection where current user JID is at the head and remaining JIDs are listed alphabetically.
 * @author Andrew Bowley
 * 15 Mar 2016
 */
public class SessionDetailsSet implements Iterable<SessionDetails>
{
    /** SessionDetails collection mapped by user JID */
    SessionDetailsMap sessionDetailsMap;
    /** Users ordered alphabetically */
    TreeSet<String> userSet;
    /** Newly created user */
    String newUser;
    /** Current user - may be new */
    String currentUser;
    /** Session details configuration of new user */
    SessionDetails newSessionDetails;

    /**
     * Construct SessionDetailsSet object
     * @param sessionDetailsMap Collection of session detail configurations
     * @param currentUser User JID from previous instantiation to be initial user if configuration exists 
     */
    public SessionDetailsSet(SessionDetailsMap sessionDetailsMap, String currentUser)
    {
        this.sessionDetailsMap = sessionDetailsMap;
        userSet = new TreeSet<String>();
        userSet.addAll(sessionDetailsMap.getUserCollection());
        createNewUser("");
        if (!currentUser.isEmpty() && getSessionDetailsByJid(currentUser) != null)
            this.currentUser = currentUser;
        else
            this.currentUser = newUser;
    }

    /**
     * Set current user and return the user's session details configuration
     * @param user User JID
     * @return SessionDetails object or null if user is unknown
     */
    public SessionDetails setCurrentUser(String user)
    {
        if (newUser.equals(user))
        {
            this.currentUser = user;
            return newSessionDetails;
        }
        if (userSet.contains(user))
        {
            this.currentUser = user;
            return sessionDetailsMap.getSessionDetails(user);
        }
        return null;
    }

    /**
     * Create a new chat account which provides all default session details
     * @param user User JID
     * @return ChatAccount object
     */
    public ChatAccount createNewUser(String user)
    {
        newUser = user;
        if (!user.isEmpty()) // Only set current user if new user is non-empty
            currentUser = user;
        newSessionDetails = new SessionDetails(user);
        return newSessionDetails;
    }

    /**
     * Returns the new user JID, which may be empty if no user has been created
     * @return user JID
     */
    public String getNewUser()
    {
        return newUser;
    }
    
    /**
     * Returns iterator to navigate session details configurations in user JID order
     * @return SessionDetails iterator 
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<SessionDetails> iterator()
    {
        return new Iterator<SessionDetails>(){

            Iterator<String> userIterator = userSet.iterator();
            @Override
            public boolean hasNext()
            {
                return userIterator.hasNext();
            }

            @Override
            public SessionDetails next()
            {
                return sessionDetailsMap.getSessionDetails(userIterator.next());
            }

            @Override
            public void remove()
            {
                
            }};
    }

    /**
     * Returns list of session details configurations in order suitable for user selection.
     * The current user JID is inserted at the head.
     * @return SessionDetails list
     */
    public synchronized List<SessionDetails> getCollection()
    {
        ArrayList<SessionDetails> sessionDetailsList = new ArrayList<SessionDetails>(userSet.size());
        Iterator<SessionDetails> iterator = iterator();
        while (iterator.hasNext())
        {
            SessionDetails sessionDetails = iterator.next();
            if (sessionDetails.getJid().equals(currentUser))
                // Put current user to start of list using insert
                sessionDetailsList.add(0, sessionDetails);
            else
                sessionDetailsList.add(sessionDetails);
        }
        return sessionDetailsList;
    }
 
    /**
     * Update this collection using specified session details. 
     * Updates configuration of existing user, otherwise creates new user.
     * @param sessionDetails SessionDetails object
     */
    public void setSessionDetails(SessionDetails sessionDetails)
    {
        synchronized(this)
        {
            String user = sessionDetails.getJid();
            SessionDetails existing = sessionDetailsMap.getSessionDetails(user);
            if (existing != null)
                existing.updateAccount(sessionDetails);
            else
            {
                newUser = user;
                this.newSessionDetails = sessionDetails;
            }
        }
    }

    /**
     * Returns session details for specified user JID. The new user is excluded. 
     * @param user User JID
     * @return SessionDetails or null if not found
     */
    public SessionDetails getSessionDetailsByJid(String user)
    {
        if (newUser.equals(user))
            return null;
        return sessionDetailsMap.getSessionDetails(user);
    }

    /**
     * Returns session details configuration of current user
     * @return SessionDetails object
     */
    public SessionDetails getSessionDetails()
    {
        return currentUser.equals(newUser) ? newSessionDetails : sessionDetailsMap.getSessionDetails(currentUser);
    }

    /**
     * Returns user configuration for given user, adding user to user set, if new user
     * @return SessionDetails object
     */
    public SessionDetails applySessionDetails(String user)
    {
        SessionDetails sessionDetails = setCurrentUser(user);
        if (!newUser.isEmpty() && (newUser.equals(user) || (sessionDetails == null)))
        {
            // Update current session details in memory
            sessionDetailsMap.putSessionDetails(newSessionDetails);
            userSet.add(newUser);
            // Normal case creates new empty user
            if (newUser.equals(user)) 
                createNewUser("");
        }
        // Unknown user is not expected
        if (sessionDetails == null)
        {   
            createNewUser(user);
            return newSessionDetails;
        }
        return sessionDetails;
    }

    /**
     * Remove specified user from set of users and return session details
     * @param user User JID
     * returns SessionDetails object
     */
    public SessionDetails removeUser(String user)
    {
        SessionDetails sessionDetails = sessionDetailsMap.getSessionDetails(user);
        if (sessionDetails != null)
            userSet.remove(user);
        return sessionDetails;
    }

    /**
     * Add specified user to set of users
     * @param user User JID
     */
    public void addUser(String user)
    {
        if (sessionDetailsMap.getSessionDetails(user) != null)
            userSet.add(user);
    }

    public void undoChanges(Set<SessionDetails> deletedSessions)
    {
        for (SessionDetails sessionDetails: deletedSessions)
            addUser(sessionDetails.getJid());
    }

}
