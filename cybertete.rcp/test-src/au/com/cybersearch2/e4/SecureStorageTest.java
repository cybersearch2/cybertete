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

import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;
import org.junit.Test;

import au.com.cybersearch2.dialogs.SyncErrorDialog;

/**
 * SecureStorageTest
 * @author Andrew Bowley
 * 12 May 2016
 */
public class SecureStorageTest
{
    static final String GLOBAL_PREFS = "global_preferences";
    static final String PASSWORD = "changeit";
    static final String KEYSTORE_PASSWORD = "prefs_keystore_password";

    @Test
    public void test_secureSave() throws StorageException
    {
        SecureStorage underTest = new SecureStorage();
        ISecurePreferences rootNode = mock(ISecurePreferences.class);
        ISecurePreferences node = mock(ISecurePreferences.class);
        when(rootNode.node(GLOBAL_PREFS)).thenReturn(node);
        underTest.rootNode = rootNode;
        underTest.secureSave(GLOBAL_PREFS, KEYSTORE_PASSWORD, PASSWORD);
        verify(node).put(KEYSTORE_PASSWORD, PASSWORD, true);
    }
    
    @Test
    public void test_secureSave_exeption() throws StorageException
    {
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        StorageException exception = new StorageException(StorageException.ENCRYPTION_ERROR, "Encyption error");
        SecureStorage underTest = new SecureStorage();
        underTest.errorDialog = errorDialog;
        ISecurePreferences rootNode = mock(ISecurePreferences.class);
        ISecurePreferences node = mock(ISecurePreferences.class);
        doThrow(exception)
        .when(node).put(KEYSTORE_PASSWORD, PASSWORD, true);
        when(rootNode.node(GLOBAL_PREFS)).thenReturn(node);
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(SecureStorage.class)).thenReturn(logger );
        underTest.postConstruct(loggerProvider, mock(ISecurePreferences.class));
        underTest.rootNode = rootNode;
        underTest.secureSave(GLOBAL_PREFS, KEYSTORE_PASSWORD, PASSWORD);
        verify(logger).error(exception, "Error setting \"" + KEYSTORE_PASSWORD  + "\"");
        verify(errorDialog).showError("Secure storage error", "Error setting \"" + KEYSTORE_PASSWORD  + "\"");
    }

    @Test
    public void test_secureGet() throws StorageException
    {
        SecureStorage underTest = new SecureStorage();
        ISecurePreferences rootNode = mock(ISecurePreferences.class);
        ISecurePreferences node = mock(ISecurePreferences.class);
        when(node.get(KEYSTORE_PASSWORD, "")).thenReturn(PASSWORD);
        when(rootNode.node(GLOBAL_PREFS)).thenReturn(node);
        underTest.rootNode = rootNode;
        assertThat(underTest.secureGet(GLOBAL_PREFS, KEYSTORE_PASSWORD)).isEqualTo(PASSWORD);
    }

    @Test
    public void test_secureGet_exeption() throws StorageException
    {
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        StorageException exception = new StorageException(StorageException.ENCRYPTION_ERROR, "Encyption error");
        SecureStorage underTest = new SecureStorage();
        underTest.errorDialog = errorDialog;
        ISecurePreferences rootNode = mock(ISecurePreferences.class);
        ISecurePreferences node = mock(ISecurePreferences.class);
        
        when(node.get(KEYSTORE_PASSWORD, "")).thenThrow(exception);
        when(rootNode.node(GLOBAL_PREFS)).thenReturn(node);
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(SecureStorage.class)).thenReturn(logger );
        underTest.postConstruct(loggerProvider, mock(ISecurePreferences.class));
        underTest.rootNode = rootNode;
        assertThat(underTest.secureGet(GLOBAL_PREFS, KEYSTORE_PASSWORD)).isEmpty();
        String message = "Error getting \"" + KEYSTORE_PASSWORD + "\" from \"" + GLOBAL_PREFS + "\"";
        verify(logger).error(exception, message);
        verify(errorDialog).showError("Secure storage error", message);
    }

    @Test
    public void test_secureRemove() throws StorageException
    {
        SecureStorage underTest = new SecureStorage();
        ISecurePreferences rootNode = mock(ISecurePreferences.class);
        ISecurePreferences node = mock(ISecurePreferences.class);
        when(rootNode.node(GLOBAL_PREFS)).thenReturn(node);
        underTest.rootNode = rootNode;
        underTest.secureRemove(GLOBAL_PREFS, KEYSTORE_PASSWORD);
        verify(node).remove(KEYSTORE_PASSWORD);
    }
    
    @Test
    public void test_flush() throws StorageException, IOException
    {
        SecureStorage underTest = new SecureStorage();
        ISecurePreferences rootNode = mock(ISecurePreferences.class);
        underTest.rootNode = rootNode;
        underTest.flush();
        verify(rootNode).flush();
    }

}
