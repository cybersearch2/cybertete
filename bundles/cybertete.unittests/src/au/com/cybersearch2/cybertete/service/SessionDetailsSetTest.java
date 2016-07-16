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

import static org.mockito.Mockito.*;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import au.com.cybersearch2.cybertete.model.service.ChatAccount;
import au.com.cybersearch2.cybertete.model.service.SessionDetails;
import au.com.cybersearch2.cybertete.service.SessionDetailsMap;

/**
 * SessionDetailsSetTest
 * @author Andrew Bowley
 * 21 Mar 2016
 */
public class SessionDetailsSetTest
{
    static final String TEST_JID = "mickymouse@disney.com";
    static final String TEST_JID2 = "adeline@google.com";
    static final String TEST_JID3 = "scooby@microsoft.com";
    static final String TEST_PASSWORD = "secret";
    
    @Test
    public void test_constructor()
    {
        SessionDetailsMap sessionDetailsMap = mock(SessionDetailsMap.class);
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetailsMap.getSessionDetails(TEST_JID2)).thenReturn(sessionDetails);
        Collection<String> userCollection = new ArrayList<String>(2);
        userCollection.add(TEST_JID);
        userCollection.add(TEST_JID2);
        when(sessionDetailsMap.getUserCollection()).thenReturn(userCollection );
        SessionDetailsSet underTest = new SessionDetailsSet(sessionDetailsMap, TEST_JID2);
        assertThat(underTest.newUser).isEmpty();
        assertThat(underTest.newSessionDetails).isNotNull();
        assertThat(underTest.userSet.toArray(new String[2])).isEqualTo( new String[]{TEST_JID2, TEST_JID});
        assertThat(underTest.currentUser).isEqualTo(TEST_JID2);
    }
    
    @Test
    public void test_constructor_no_last_user()
    {
        SessionDetailsMap sessionDetailsMap = mock(SessionDetailsMap.class);
        Collection<String> userCollection = new ArrayList<String>(2);
        userCollection.add(TEST_JID);
        userCollection.add(TEST_JID2);
        when(sessionDetailsMap.getUserCollection()).thenReturn(userCollection );
        SessionDetailsSet underTest = new SessionDetailsSet(sessionDetailsMap, "");
        assertThat(underTest.newUser).isEmpty();
        assertThat(underTest.newSessionDetails).isNotNull();
        assertThat(underTest.userSet.toArray(new String[2])).isEqualTo( new String[]{TEST_JID2, TEST_JID});
        assertThat(underTest.currentUser).isEmpty();
    }

    @Test
    public void test_constructor_last_user_not_found()
    {
        SessionDetailsMap sessionDetailsMap = mock(SessionDetailsMap.class);
        when(sessionDetailsMap.getSessionDetails(TEST_JID3)).thenReturn(null);
        Collection<String> userCollection = new ArrayList<String>(2);
        userCollection.add(TEST_JID);
        userCollection.add(TEST_JID2);
        when(sessionDetailsMap.getUserCollection()).thenReturn(userCollection );
        SessionDetailsSet underTest = new SessionDetailsSet(sessionDetailsMap, TEST_JID3);
        assertThat(underTest.newUser).isEmpty();
        assertThat(underTest.newSessionDetails).isNotNull();
        assertThat(underTest.userSet.toArray(new String[2])).isEqualTo( new String[]{TEST_JID2, TEST_JID});
        assertThat(underTest.currentUser).isEmpty();
    }
 
    @Test
    public void test_setCurrentUser()
    {
        SessionDetailsMap sessionDetailsMap = mock(SessionDetailsMap.class);
        SessionDetails sessionDetails1 = mock(SessionDetails.class);
        SessionDetails sessionDetails2 = mock(SessionDetails.class);
        when(sessionDetailsMap.getSessionDetails(TEST_JID)).thenReturn(sessionDetails1);
        when(sessionDetailsMap.getSessionDetails(TEST_JID2)).thenReturn(sessionDetails2);
        Collection<String> userCollection = new ArrayList<String>(2);
        userCollection.add(TEST_JID);
        userCollection.add(TEST_JID2);
        when(sessionDetailsMap.getUserCollection()).thenReturn(userCollection );
        SessionDetailsSet underTest = new SessionDetailsSet(sessionDetailsMap, TEST_JID);
        assertThat(underTest.setCurrentUser(TEST_JID2)).isEqualTo(sessionDetails2);
        assertThat(underTest.getSessionDetails()).isEqualTo(sessionDetails2);
    }

    @Test
    public void test_setCurrentUser_to_new_user()
    {
        SessionDetailsMap sessionDetailsMap = mock(SessionDetailsMap.class);
        SessionDetails sessionDetails1 = mock(SessionDetails.class);
        SessionDetails sessionDetails2 = mock(SessionDetails.class);
        when(sessionDetailsMap.getSessionDetails(TEST_JID)).thenReturn(sessionDetails1);
        when(sessionDetailsMap.getSessionDetails(TEST_JID2)).thenReturn(sessionDetails2);
        Collection<String> userCollection = new ArrayList<String>(2);
        userCollection.add(TEST_JID);
        userCollection.add(TEST_JID2);
        when(sessionDetailsMap.getUserCollection()).thenReturn(userCollection );
        SessionDetailsSet underTest = new SessionDetailsSet(sessionDetailsMap, TEST_JID);
        underTest.newUser = TEST_JID3;
        SessionDetails newSessionDetails = mock(SessionDetails.class);
        underTest.newSessionDetails = newSessionDetails;
        assertThat(underTest.setCurrentUser(TEST_JID3)).isEqualTo(newSessionDetails);
    }
    
    @Test
    public void test_setCurrentUser_to_unknown_user()
    {
        SessionDetailsMap sessionDetailsMap = mock(SessionDetailsMap.class);
        SessionDetails sessionDetails1 = mock(SessionDetails.class);
        SessionDetails sessionDetails2 = mock(SessionDetails.class);
        when(sessionDetailsMap.getSessionDetails(TEST_JID)).thenReturn(sessionDetails1);
        when(sessionDetailsMap.getSessionDetails(TEST_JID2)).thenReturn(sessionDetails2);
        Collection<String> userCollection = new ArrayList<String>(2);
        userCollection.add(TEST_JID);
        userCollection.add(TEST_JID2);
        when(sessionDetailsMap.getUserCollection()).thenReturn(userCollection );
        SessionDetailsSet underTest = new SessionDetailsSet(sessionDetailsMap, TEST_JID);
        underTest.newUser = TEST_JID3;
        SessionDetails newSessionDetails = mock(SessionDetails.class);
        underTest.newSessionDetails = newSessionDetails;
        assertThat(underTest.setCurrentUser("unknown")).isNull();
    }

    @Test
    public void test_createNewUser()
    {
        SessionDetailsMap sessionDetailsMap = mock(SessionDetailsMap.class);
        Collection<String> userCollection = new ArrayList<String>(2);
        userCollection.add(TEST_JID);
        userCollection.add(TEST_JID2);
        when(sessionDetailsMap.getUserCollection()).thenReturn(userCollection );
        SessionDetailsSet underTest = new SessionDetailsSet(sessionDetailsMap, TEST_JID);
        ChatAccount chatAccount = underTest.createNewUser(TEST_JID3);
        assertThat(chatAccount.getJid()).isEqualTo(TEST_JID3);
        assertThat(underTest.newUser).isEqualTo(TEST_JID3);
        assertThat(underTest.currentUser).isEqualTo(TEST_JID3);
        assertThat(underTest.newSessionDetails.getJid()).isEqualTo(TEST_JID3);
        assertThat(underTest.getNewUser()).isEqualTo(TEST_JID3);
        assertThat(underTest.getSessionDetails()).isEqualTo(underTest.newSessionDetails);
    }

    @Test
    public void test_iterator()
    {
        SessionDetailsMap sessionDetailsMap = mock(SessionDetailsMap.class);
        SessionDetails sessionDetails1 = mock(SessionDetails.class);
        SessionDetails sessionDetails2 = mock(SessionDetails.class);
        SessionDetails sessionDetails3 = mock(SessionDetails.class);
        when(sessionDetailsMap.getSessionDetails(TEST_JID)).thenReturn(sessionDetails1);
        when(sessionDetailsMap.getSessionDetails(TEST_JID2)).thenReturn(sessionDetails2);
        when(sessionDetailsMap.getSessionDetails(TEST_JID3)).thenReturn(sessionDetails3);
        Collection<String> userCollection = new ArrayList<String>(2);
        userCollection.add(TEST_JID);
        userCollection.add(TEST_JID2);
        userCollection.add(TEST_JID3);
        when(sessionDetailsMap.getUserCollection()).thenReturn(userCollection);
        SessionDetailsSet underTest = new SessionDetailsSet(sessionDetailsMap, TEST_JID);
        Iterator<SessionDetails> iterator = underTest.iterator();
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isEqualTo(sessionDetails2); // adeline
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isEqualTo(sessionDetails1); // mickymouse
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isEqualTo(sessionDetails3); // scooby
        assertThat(iterator.hasNext()).isFalse();
    }
   
    @Test
    public void test_getCollection()
    {
        SessionDetailsMap sessionDetailsMap = mock(SessionDetailsMap.class);
        SessionDetails sessionDetails1 = mock(SessionDetails.class);
        when(sessionDetails1.getJid()).thenReturn(TEST_JID);
        SessionDetails sessionDetails2 = mock(SessionDetails.class);
        when(sessionDetails2.getJid()).thenReturn(TEST_JID2);
        SessionDetails sessionDetails3 = mock(SessionDetails.class);
        when(sessionDetails3.getJid()).thenReturn(TEST_JID3);
        when(sessionDetailsMap.getSessionDetails(TEST_JID)).thenReturn(sessionDetails1);
        when(sessionDetailsMap.getSessionDetails(TEST_JID2)).thenReturn(sessionDetails2);
        when(sessionDetailsMap.getSessionDetails(TEST_JID3)).thenReturn(sessionDetails3);
        Collection<String> userCollection = new ArrayList<String>(2);
        userCollection.add(TEST_JID);
        userCollection.add(TEST_JID2);
        userCollection.add(TEST_JID3);
        when(sessionDetailsMap.getUserCollection()).thenReturn(userCollection);
        SessionDetailsSet underTest = new SessionDetailsSet(sessionDetailsMap, TEST_JID);
        underTest.setCurrentUser(TEST_JID3);
        List<SessionDetails> resultList = underTest.getCollection();
        Iterator<SessionDetails> iterator = resultList.iterator();
        assertThat(iterator.next().getJid()).isEqualTo(TEST_JID3); // scooby
        assertThat(iterator.next().getJid()).isEqualTo(TEST_JID2); // adeline
        assertThat(iterator.next().getJid()).isEqualTo(TEST_JID);  // mickymouse
    }
 
    @Test
    public void test_setSessionDetails()
    {
        SessionDetailsMap sessionDetailsMap = mock(SessionDetailsMap.class);
        SessionDetails sessionDetails1 = mock(SessionDetails.class);
        when(sessionDetails1.getJid()).thenReturn(TEST_JID);
        SessionDetails sessionDetails2 = mock(SessionDetails.class);
        when(sessionDetails2.getJid()).thenReturn(TEST_JID2);
        SessionDetails sessionDetails21 = mock(SessionDetails.class);
        when(sessionDetails21.getJid()).thenReturn(TEST_JID2);
        when(sessionDetailsMap.getSessionDetails(TEST_JID)).thenReturn(sessionDetails1);
        when(sessionDetailsMap.getSessionDetails(TEST_JID2)).thenReturn(sessionDetails2);
        Collection<String> userCollection = new ArrayList<String>(2);
        userCollection.add(TEST_JID);
        userCollection.add(TEST_JID2);
        when(sessionDetailsMap.getUserCollection()).thenReturn(userCollection);
        SessionDetailsSet underTest = new SessionDetailsSet(sessionDetailsMap, TEST_JID);
        underTest.setSessionDetails(sessionDetails21);
        verify(sessionDetails2).updateAccount(sessionDetails21);
        assertThat(underTest.getNewUser()).isEmpty();
    }
    
    @Test
    public void test_setSessionDetails_new_user()
    {
        SessionDetailsMap sessionDetailsMap = mock(SessionDetailsMap.class);
        SessionDetails sessionDetails1 = mock(SessionDetails.class);
        when(sessionDetails1.getJid()).thenReturn(TEST_JID);
        SessionDetails sessionDetails2 = mock(SessionDetails.class);
        when(sessionDetails2.getJid()).thenReturn(TEST_JID2);
        SessionDetails sessionDetails3 = mock(SessionDetails.class);
        when(sessionDetails3.getJid()).thenReturn(TEST_JID3);
        when(sessionDetailsMap.getSessionDetails(TEST_JID)).thenReturn(sessionDetails1);
        when(sessionDetailsMap.getSessionDetails(TEST_JID2)).thenReturn(sessionDetails2);
        Collection<String> userCollection = new ArrayList<String>(2);
        userCollection.add(TEST_JID);
        userCollection.add(TEST_JID2);
        when(sessionDetailsMap.getUserCollection()).thenReturn(userCollection);
        SessionDetailsSet underTest = new SessionDetailsSet(sessionDetailsMap, TEST_JID);
        underTest.setSessionDetails(sessionDetails3);
        assertThat(underTest.getNewUser()).isEqualTo(TEST_JID3);
        assertThat(underTest.newSessionDetails).isEqualTo(sessionDetails3);
    }

    @Test
    public void test_getSessionDetailsByJid()
    {
        SessionDetailsMap sessionDetailsMap = mock(SessionDetailsMap.class);
        SessionDetails sessionDetails1 = mock(SessionDetails.class);
        when(sessionDetails1.getJid()).thenReturn(TEST_JID);
        SessionDetails sessionDetails2 = mock(SessionDetails.class);
        when(sessionDetails2.getJid()).thenReturn(TEST_JID2);
        SessionDetails sessionDetails3 = mock(SessionDetails.class);
        when(sessionDetails3.getJid()).thenReturn(TEST_JID3);
        when(sessionDetailsMap.getSessionDetails(TEST_JID)).thenReturn(sessionDetails1);
        when(sessionDetailsMap.getSessionDetails(TEST_JID2)).thenReturn(sessionDetails2);
        when(sessionDetailsMap.getSessionDetails(TEST_JID3)).thenReturn(sessionDetails3);
        Collection<String> userCollection = new ArrayList<String>(2);
        userCollection.add(TEST_JID);
        userCollection.add(TEST_JID2);
        userCollection.add(TEST_JID3);
        when(sessionDetailsMap.getUserCollection()).thenReturn(userCollection);
        SessionDetailsSet underTest = new SessionDetailsSet(sessionDetailsMap, TEST_JID);
        assertThat(underTest.getSessionDetailsByJid(TEST_JID)).isEqualTo(sessionDetails1);
        assertThat(underTest.getSessionDetailsByJid(TEST_JID2)).isEqualTo(sessionDetails2);
        assertThat(underTest.getSessionDetailsByJid(TEST_JID3)).isEqualTo(sessionDetails3);
        underTest.createNewUser("new.user@nu.com");
        assertThat(underTest.getSessionDetailsByJid("new.user@nu.com")).isNull();
        assertThat(underTest.getSessionDetailsByJid("")).isNull();
       
    }
    
    @Test
    public void test_applySessionDetails_new_user()
    {
        SessionDetailsMap sessionDetailsMap = mock(SessionDetailsMap.class);
        Collection<String> userCollection = new ArrayList<String>(2);
        userCollection.add(TEST_JID);
        userCollection.add(TEST_JID2);
        when(sessionDetailsMap.getUserCollection()).thenReturn(userCollection );
        SessionDetailsSet underTest = new SessionDetailsSet(sessionDetailsMap, TEST_JID);
        underTest.createNewUser(TEST_JID3);
        SessionDetails newSessionDetails = underTest.newSessionDetails;
        assertThat(underTest.applySessionDetails(TEST_JID3)).isEqualTo(newSessionDetails);
        assertThat(underTest.userSet).contains(TEST_JID3);
        assertThat(underTest.newUser).isEmpty();
        assertThat(underTest.currentUser).isEqualTo(TEST_JID3);
        assertThat(underTest.newSessionDetails.getJid()).isEqualTo("");
        assertThat(underTest.getNewUser()).isEmpty();
    }

    @Test
    public void test_applySessionDetails_current_user()
    {
        SessionDetailsMap sessionDetailsMap = mock(SessionDetailsMap.class);
        Collection<String> userCollection = new ArrayList<String>(2);
        userCollection.add(TEST_JID);
        userCollection.add(TEST_JID2);
        when(sessionDetailsMap.getUserCollection()).thenReturn(userCollection );
        SessionDetails sessionDetails = mock(SessionDetails.class);
        when(sessionDetailsMap.getSessionDetails(TEST_JID)).thenReturn(sessionDetails);
        SessionDetailsSet underTest = new SessionDetailsSet(sessionDetailsMap, TEST_JID);
        underTest.createNewUser(TEST_JID3);
        assertThat(underTest.applySessionDetails(TEST_JID)).isEqualTo(sessionDetails);
        assertThat(underTest.userSet.contains(TEST_JID3)).isFalse();
        assertThat(underTest.newUser).isEqualTo(TEST_JID3);
        assertThat(underTest.currentUser).isEqualTo(TEST_JID);
        assertThat(underTest.newSessionDetails.getJid()).isEqualTo(TEST_JID3);
     }

    @Test
    public void test_removeUser()
    {
        SessionDetailsMap sessionDetailsMap = mock(SessionDetailsMap.class);
        SessionDetails sessionDetails1 = mock(SessionDetails.class);
        when(sessionDetails1.getJid()).thenReturn(TEST_JID);
        SessionDetails sessionDetails2 = mock(SessionDetails.class);
        when(sessionDetails2.getJid()).thenReturn(TEST_JID2);
        SessionDetails sessionDetails3 = mock(SessionDetails.class);
        when(sessionDetails3.getJid()).thenReturn(TEST_JID3);
        when(sessionDetailsMap.getSessionDetails(TEST_JID)).thenReturn(sessionDetails1);
        when(sessionDetailsMap.getSessionDetails(TEST_JID2)).thenReturn(sessionDetails2);
        when(sessionDetailsMap.getSessionDetails(TEST_JID3)).thenReturn(sessionDetails3);
        Collection<String> userCollection = new ArrayList<String>(2);
        userCollection.add(TEST_JID);
        userCollection.add(TEST_JID2);
        userCollection.add(TEST_JID3);
        when(sessionDetailsMap.getUserCollection()).thenReturn(userCollection);
        SessionDetailsSet underTest = new SessionDetailsSet(sessionDetailsMap, TEST_JID);
        assertThat(underTest.removeUser(TEST_JID)).isEqualTo(sessionDetails1);
        assertThat(underTest.userSet.size()).isEqualTo(2);
        assertThat(underTest.userSet).contains(TEST_JID2, TEST_JID3);
        assertThat(underTest.removeUser(TEST_JID3)).isEqualTo(sessionDetails3);
        assertThat(underTest.userSet.size()).isEqualTo(1);
        assertThat(underTest.userSet).contains(TEST_JID2);
        assertThat(underTest.removeUser("new.user@nu.com")).isNull();
    }
 
    @Test
    public void test_addUser()
    {
        SessionDetailsMap sessionDetailsMap = mock(SessionDetailsMap.class);
        SessionDetails sessionDetails1 = mock(SessionDetails.class);
        when(sessionDetails1.getJid()).thenReturn(TEST_JID);
        SessionDetails sessionDetails2 = mock(SessionDetails.class);
        when(sessionDetails2.getJid()).thenReturn(TEST_JID2);
        SessionDetails sessionDetails3 = mock(SessionDetails.class);
        when(sessionDetails3.getJid()).thenReturn(TEST_JID3);
        when(sessionDetailsMap.getSessionDetails(TEST_JID)).thenReturn(sessionDetails1);
        when(sessionDetailsMap.getSessionDetails(TEST_JID2)).thenReturn(sessionDetails2);
        when(sessionDetailsMap.getSessionDetails(TEST_JID3)).thenReturn(sessionDetails3);
        Collection<String> userCollection = new ArrayList<String>(2);
        userCollection.add(TEST_JID);
        userCollection.add(TEST_JID2);
        userCollection.add(TEST_JID3);
        when(sessionDetailsMap.getUserCollection()).thenReturn(userCollection);
        SessionDetailsSet underTest = new SessionDetailsSet(sessionDetailsMap, TEST_JID);
        assertThat(underTest.removeUser(TEST_JID)).isEqualTo(sessionDetails1);
        assertThat(underTest.removeUser(TEST_JID3)).isEqualTo(sessionDetails3);
        underTest.addUser(TEST_JID);
        assertThat(underTest.userSet).contains(TEST_JID);
        underTest.addUser(TEST_JID3);
        assertThat(underTest.userSet).contains(TEST_JID3);
        assertThat(underTest.userSet).hasSize(3);
        underTest.addUser("new.user@nu.com");
        assertThat(underTest.userSet).hasSize(3);
    }
    
    @Test
    public void test_undoChanges()
    {
        SessionDetailsMap sessionDetailsMap = mock(SessionDetailsMap.class);
        SessionDetails sessionDetails1 = mock(SessionDetails.class);
        when(sessionDetails1.getJid()).thenReturn(TEST_JID);
        SessionDetails sessionDetails2 = mock(SessionDetails.class);
        when(sessionDetails2.getJid()).thenReturn(TEST_JID2);
        SessionDetails sessionDetails3 = mock(SessionDetails.class);
        when(sessionDetails3.getJid()).thenReturn(TEST_JID3);
        when(sessionDetailsMap.getSessionDetails(TEST_JID)).thenReturn(sessionDetails1);
        when(sessionDetailsMap.getSessionDetails(TEST_JID2)).thenReturn(sessionDetails2);
        when(sessionDetailsMap.getSessionDetails(TEST_JID3)).thenReturn(sessionDetails3);
        Collection<String> userCollection = new ArrayList<String>(2);
        userCollection.add(TEST_JID);
        userCollection.add(TEST_JID2);
        userCollection.add(TEST_JID3);
        when(sessionDetailsMap.getUserCollection()).thenReturn(userCollection);
        SessionDetailsSet underTest = new SessionDetailsSet(sessionDetailsMap, TEST_JID);
        assertThat(underTest.removeUser(TEST_JID)).isEqualTo(sessionDetails1);
        assertThat(underTest.removeUser(TEST_JID3)).isEqualTo(sessionDetails3);
        Set<SessionDetails> deletedSessions = new HashSet<SessionDetails>();
        deletedSessions.add(sessionDetails1);
        deletedSessions.add(sessionDetails3);
        underTest.undoChanges(deletedSessions);
        assertThat(underTest.userSet).hasSize(3);
    }
}
