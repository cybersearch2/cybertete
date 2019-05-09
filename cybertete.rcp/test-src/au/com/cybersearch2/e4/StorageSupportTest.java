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
package au.com.cybersearch2.e4;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.IOException;
import org.junit.Test;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import au.com.cybersearch2.cybertete.preferences.PreferenceConstants;
import au.com.cybersearch2.cybertete.preferences.StorageKey;

/**
 * StorageSupportTest
 * @author Andrew Bowley
 * 13 May 2016
 */
public class StorageSupportTest
{
    class TestStorageSupport extends StorageSupport
    {
        public TestStorageSupport()
        {
            this.preferences = mock(IEclipsePreferences.class);
            this.secureStorage = mock(SecureStorage.class);
        }
    }
    
    static final String PREFERENCES_ROOT = PreferenceConstants.NODEPATH + "/" + PreferenceConstants.SAVED;
    static final String GLOBAL_PREFS = "global_preferences";
    static final String PASSWORD = "secret";
    static final String TEST_JID = "mickymouse@disney.com";
    static final String TEST_HOST = "disney.com";

    @Test
    public void test_short_methods() throws BackingStoreException, IOException
    {
        TestStorageSupport underTest = new TestStorageSupport();
        when(underTest.secureStorage.secureGet(GLOBAL_PREFS, "password")).thenReturn(PASSWORD);
        assertThat(underTest.getSecureValue(GLOBAL_PREFS, StorageKey.password)).isEqualTo(PASSWORD);
        underTest.setSecureValue(TEST_JID, StorageKey.password, PASSWORD);
        verify(underTest.secureStorage).secureSave(TEST_JID, "password", PASSWORD);
        assertThat(underTest.secureDirty).isTrue();
        underTest.secureDirty = false;
        underTest.removeSecureValue(TEST_JID, StorageKey.password);
        verify(underTest.secureStorage).secureRemove(TEST_JID, "password");
        assertThat(underTest.secureDirty).isTrue();
        Preferences node = mock(Preferences.class);
        when(underTest.preferences.node(PREFERENCES_ROOT)).thenReturn(node);
        underTest.setValue(PREFERENCES_ROOT, StorageKey.host, TEST_HOST);
        verify(node).put("host", TEST_HOST);
        assertThat(underTest.prefsDirty).isTrue();
        node = mock(Preferences.class);
        when(underTest.preferences.node(PREFERENCES_ROOT)).thenReturn(node);
        when(node.get("host", "")).thenReturn(TEST_HOST);
        assertThat(underTest.getValue(PREFERENCES_ROOT, StorageKey.host, "")).isEqualTo(TEST_HOST);
    }

    @Test
    public void test_flush() throws BackingStoreException, IOException
    {
        TestStorageSupport underTest = new TestStorageSupport();
        underTest.prefsDirty = true;
        underTest.secureDirty = true;
        underTest.flush();
        verify(underTest.preferences).flush();
        verify(underTest.secureStorage).flush();
    }
    
    @Test
    public void test_flush_clean() throws BackingStoreException, IOException
    {
        TestStorageSupport underTest = new TestStorageSupport();
        underTest.flush();
        verify(underTest.preferences, times(0)).flush();
        verify(underTest.secureStorage, times(0)).flush();
    }

    @Test
    public void test_removeAttributes() throws BackingStoreException, IOException
    {
        TestStorageSupport underTest = new TestStorageSupport();
        Preferences node = mock(Preferences.class);
        when(underTest.preferences.node(PREFERENCES_ROOT)).thenReturn(node);
        when(node.keys()).thenReturn(new String[] {"host","port","authcid","plain_sasl","prefs_single_signon"});
        underTest.removeAttributes(PREFERENCES_ROOT, StorageKey.auth_cid);
        verify(node).remove("authcid");
     }
    
    @Test
    public void test_removeAttributes_ignore_already_removed() throws BackingStoreException, IOException
    {
        TestStorageSupport underTest = new TestStorageSupport();
        Preferences node = mock(Preferences.class);
        when(underTest.preferences.node(PREFERENCES_ROOT)).thenReturn(node);
        when(node.keys()).thenReturn(new String[] {"host","port","authcid","plain_sasl"});
        underTest.removeAttributes(PREFERENCES_ROOT, StorageKey.auth_cid);
        verify(node).remove("authcid");
     }
}

