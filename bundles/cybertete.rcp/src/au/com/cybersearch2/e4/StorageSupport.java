/**
    Copyright (C) 2016  www.cybersearch2.com.au

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General protected License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General protected License for more details.

    You should have received a copy of the GNU General protected License
    along with this program.  If not, see <http://www.gnu.org/licenses/> */
package au.com.cybersearch2.e4;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * StorageSupport
 * Provides utility methods for Eclipse Preferences and Secure Storage
 * @author Andrew Bowley
 * 12 May 2016
 */
public abstract class StorageSupport
{
    /** Root node of Eclipse configuration persistence mechanism */
    @Inject
    @Preference(nodePath = "/") 
    protected IEclipsePreferences preferences;
    /** Store passwords in an encrypted way */ 
    @Inject
    protected SecureStorage secureStorage;

    /** Flag set true if preference changes need to be flushed to file system */
    volatile boolean prefsDirty;
    /** Flag set true if secure data changes need to be flushed to file system */
    volatile boolean secureDirty;

    /**
     * preDestroy
     */
    @PreDestroy
    protected void preDestroy()
    {   // Flush any outstanding changes. Errors not expected and are ignored.
        try
        {
            flush();
        }
        catch (BackingStoreException | IOException e)
        {
        }
    }
    

    /**
     * Returns in clear value stored in encrypted way
     * @param path Path to the node
     * @param prefKey Key with which the value is associated
     * @return value
     */
    protected String getSecureValue(String path, PrefKey prefKey)
    {
        return secureStorage.secureGet(path, prefKey.getPreference());
    }

    /**
     * Store value in encryted way
     * @param path Path to the node
     * @param prefKey Key with which the value is going to be associated
     * @param value Value to store
     */
    protected void setSecureValue(String path, PrefKey prefKey, String value)
    {
        secureStorage.secureSave(path, prefKey.getPreference(), value);
        secureDirty = true;
    }
    
    /**
     * Removes value associated with the key
     * @param path Path to the node
     * @param prefKey Key with which the value is associated
     */
    protected void removeSecureValue(String path, PrefKey prefKey)
    {
        secureStorage.secureRemove(path, prefKey.getPreference());
        secureDirty = true;
    }
    
    /**
     * Store boolean value on root preference node
     * @param path Absolute path to the node
     * @param prefKey Key with which the value is going to be associated
     * @param value Value to store
     * @throws BackingStoreException
     */
    protected void setBoolean(String path, PrefKey prefKey, boolean value)
    {
        Preferences node = preferences.node(path);
        node.putBoolean(prefKey.getPreference(), value);
        prefsDirty = true;
    }
    
    /**
     * Returns boolean value from root preference node
     * @param path Absolute path to the node
     * @param prefKey Key with which the value is associated
     * @return value
     */
    protected boolean getBoolean(String path, PrefKey prefKey, boolean defaultValue)
    {
        Preferences node = preferences.node(path);
        return node.getBoolean(prefKey.getPreference(), defaultValue);
    }

    /**
     * Store text value on root preference node
     * @param path Absolute path to the node
     * @param prefKey Key with which the value is going to be associated
     * @param value Value to store
     * @throws BackingStoreException
     */
    protected void setValue(String path, PrefKey prefKey, String value)
    {
        Preferences node = preferences.node(path);
        node.put(prefKey.getPreference(), value);
        prefsDirty = true;
    }
    
    /**
     * Returns text value from root preference node
     * @param path Absolute path to the node
     * @param prefKey Key with which the value is associated
     * @param defaultValue Default value
     * @return value
     */
    protected String getValue(String path, PrefKey prefKey, String defaultValue)
    {
        Preferences node = preferences.node(path);
        return node.get(prefKey.getPreference(), defaultValue);
    }

    /**
     * Returns all of the keys that have an associated value in a node
     * specified by it's absolute path. 
     * @param path Absolute path to the node
     * @return an array of the keys that have an associated value in this node.
     * @throws BackingStoreException if this operation cannot be completed due
     *         to a failure in the backing store, or inability to communicate
     *         with it.
     */
    protected String[] getKeys(String path) throws BackingStoreException
    {
        return preferences.node(path).keys();
    }
 
    /**
     * Returns the names of the children of specified node
     * @param path Absolute path to the node
     * @return the names of the children of this node.
     * @throws BackingStoreException if this operation cannot be completed due
     *         to a failure in the backing store, or inability to communicate
     *         with it.
     */
    protected String[] getChildrenNames(String path) throws BackingStoreException
    {
        return preferences.node(path).childrenNames();
    }
    
    /**
     * Commit all changes to file system
     * @throws BackingStoreException
     * @throws IOException
     */
    protected void flush() throws BackingStoreException, IOException
    {
        if (prefsDirty)
        {
            preferences.flush();
            prefsDirty = false;
        }
        if (secureDirty)
        {
            secureStorage.flush();
            secureDirty = false;
        }
    }

    /**
     * Remove node and all of its descendants, invalidating any properties
     * contained in the removed nodes.
     * @param path Absolute path to the node
     * @throws BackingStoreException
     */
    protected void removeNode(String path) throws BackingStoreException
    {
        preferences.node(path).removeNode();
        prefsDirty = true;
    }
    
    /**
     * Remove attributes from given node
     * @param path Absolute path to the node
     * @param prefKeys Attribute keys
     * @throws BackingStoreException
     */
    protected void removeAttributes(String path, PrefKey... prefKeys) throws BackingStoreException
    {
        Preferences node = preferences.node(path);
        Set<String> attributeSet = new HashSet<String>();
        for (String attribute: node.keys())
            attributeSet.add(attribute);
        for (PrefKey key: prefKeys)
            if (attributeSet.contains(key.getPreference()))
                node.remove(key.getPreference());
        prefsDirty = true;
     }


    

}
