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
package au.com.cybersearch2.cybertete;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.Assert;

/**
 * GlobalPropertiesTest
 * @author Andrew Bowley
 * 30 May 2016
 */
public class GlobalPropertiesTest
{
    static final String AUTH_CONFIG_PATH = "/jaas/loginconfig";

    @Test
    public void test_setSystemProperties()
    {
        final boolean[] setterCalled = new boolean[]{false};
        GlobalProperties underTest = new GlobalProperties()
        {
            public String getAuthLoginConfigPath()
            {
                return AUTH_CONFIG_PATH;
            }
            
            public void setAuthLoginConfigPath(String path)
            {
                setterCalled[0] = true;
                assertThat(path).isEqualTo(AUTH_CONFIG_PATH);
            }
        };
        underTest.postConstruct();
        assertThat(setterCalled[0]).isTrue();
    }

    @Test
    public void test_setSystemProperties_no_auth_path_config()
    {
        GlobalProperties underTest = new GlobalProperties()
        {
            public String getAuthLoginConfigPath()
            {
                return "";
            }
            
            public void setAuthLoginConfigPath(String path)
            {
                Assert.fail("setAuthLoginConfigPath() called");
            }
        };
        underTest.postConstruct();
        assertThat(System.getProperty("java.security.auth.login.config")).isEqualTo("gss.conf");
    }
}
