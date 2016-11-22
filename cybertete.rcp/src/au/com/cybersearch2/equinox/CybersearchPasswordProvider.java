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
package au.com.cybersearch2.equinox;

import javax.crypto.spec.PBEKeySpec;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.equinox.security.storage.provider.IPreferencesContainer;
import org.eclipse.equinox.security.storage.provider.PasswordProvider;

import au.com.cybersearch2.cybertete.Activator;


/**
 * PasswordProvider
 * @author Andrew Bowley
 * 18Nov.,2016
 */
public class CybersearchPasswordProvider extends PasswordProvider
{
    Logger logger;
    
    
    public CybersearchPasswordProvider()
    {
        logger = Activator.getLoggerProvider().getClassLogger(getClass());
    }
    
    @Override
    public PBEKeySpec getPassword(IPreferencesContainer container,
            int passwordType)
    {
        return new PBEKeySpec(System.getProperty("user.name",  "c2Au61^^^^").toCharArray()); 
    }

    
}
