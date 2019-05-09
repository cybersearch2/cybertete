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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import au.com.cybersearch2.cybertete.agents.LocalRosterTest;
import au.com.cybersearch2.cybertete.dialogs.AddContactControlsTest;
import au.com.cybersearch2.cybertete.dialogs.CyberteteLoginDialogTest;
import au.com.cybersearch2.cybertete.dialogs.DialogLoginControlsTest;
import au.com.cybersearch2.cybertete.dialogs.LoginControlsBaseTest;
import au.com.cybersearch2.cybertete.dialogs.LoginControlsTest;
import au.com.cybersearch2.cybertete.dialogs.LoginCustomControlsTest;
import au.com.cybersearch2.cybertete.dialogs.PasswordControlsTest;
import au.com.cybersearch2.cybertete.dialogs.PresenceControlsTest;
import au.com.cybersearch2.cybertete.dialogs.SecurityControlsTest;
import au.com.cybersearch2.cybertete.dialogs.UserSelectorTest;
import au.com.cybersearch2.cybertete.dialogs.X509ControlsTest;
import au.com.cybersearch2.cybertete.handlers.AddContactHandlerTest;
import au.com.cybersearch2.cybertete.handlers.AsyncChatWindowHandlerTest;
import au.com.cybersearch2.cybertete.handlers.ChangePerspectiveHandlerTest;
import au.com.cybersearch2.cybertete.handlers.ChangePresenceHandlerTest;
import au.com.cybersearch2.cybertete.handlers.ChatWindowHandlerTest;
import au.com.cybersearch2.cybertete.handlers.CloseAllHandlerTest;
import au.com.cybersearch2.cybertete.handlers.CloseAtStartupEventHandlerTest;
import au.com.cybersearch2.cybertete.handlers.ExitHandlerTest;
import au.com.cybersearch2.cybertete.handlers.LifeCycleHandlerTest;
import au.com.cybersearch2.cybertete.handlers.LoadKerberosConfigHandlerTest;
import au.com.cybersearch2.cybertete.handlers.LoginHandlerTest;
import au.com.cybersearch2.cybertete.handlers.SaveLoginSessionHandlerTest;
import au.com.cybersearch2.cybertete.handlers.SecurityHandlerTest;
import au.com.cybersearch2.cybertete.handlers.StartChatHandlerTest;
import au.com.cybersearch2.cybertete.handlers.UpdateHandlerTest;
import au.com.cybersearch2.cybertete.handlers.UpdateLoginConfigHandlerTest;
import au.com.cybersearch2.cybertete.model.internal.ContactEntryListTest;
import au.com.cybersearch2.cybertete.model.internal.MultiGroupContactsTreeTest;
import au.com.cybersearch2.cybertete.preferences.UserDataStoreTest;
import au.com.cybersearch2.cybertete.security.KerberosCallbackHandlerTest;
import au.com.cybersearch2.cybertete.security.KerberosDataTest;
import au.com.cybersearch2.cybertete.security.KeystoreDataTest;
import au.com.cybersearch2.cybertete.security.KeystoreHelperTest;
import au.com.cybersearch2.cybertete.security.PersistentSecurityDataTest;
import au.com.cybersearch2.cybertete.security.SecurityConfigControllerTest;
import au.com.cybersearch2.cybertete.security.SecurityResourcesTest;
import au.com.cybersearch2.cybertete.service.ChatLoginControllerTest;
import au.com.cybersearch2.cybertete.service.ChatLoginProgressTaskTest;
import au.com.cybersearch2.cybertete.service.CommunicationsStateTest;
import au.com.cybersearch2.cybertete.service.ConnectionNotifierTest;
import au.com.cybersearch2.cybertete.service.LoginDataTest;
import au.com.cybersearch2.cybertete.service.ServiceThreadTest;
import au.com.cybersearch2.cybertete.service.SessionDetailsMapTest;
import au.com.cybersearch2.cybertete.service.SessionDetailsSetTest;
import au.com.cybersearch2.cybertete.smack.SmackChatResponderTest;
import au.com.cybersearch2.cybertete.smack.SmackChatServiceTest;
import au.com.cybersearch2.cybertete.smack.SmackLoginTaskTest;
import au.com.cybersearch2.cybertete.smack.SmackRosterTest;
import au.com.cybersearch2.cybertete.smack.XmppConnectionBaseTest;
import au.com.cybersearch2.cybertete.smack.XmppConnectionFactoryTest;
import au.com.cybersearch2.cybertete.views.ChatSessionViewTest;
import au.com.cybersearch2.cybertete.views.ContactsLabelProviderTest;
import au.com.cybersearch2.cybertete.views.ContactsViewTest;
import au.com.cybersearch2.cybertete.views.TreeViewerControlTest;
import au.com.cybersearch2.cybertete.views.ViewLoginControlsTest;
import au.com.cybersearch2.e4.LifeCycleHelperTest;
import au.com.cybersearch2.e4.SecureStorageTest;
import au.com.cybersearch2.e4.StorageSupportTest;
import au.com.cybersearch2.cybertete.status.ConnectionStatusTest;
import au.com.cybersearch2.cybertete.status.PresenceStatusTest;
import au.com.cybersearch2.cybertete.status.SecurityStatusTest;

/**
 * CyberteteTestSuite
 * @author Andrew Bowley
 * 9 Mar 2016
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    GlobalPropertiesTest.class,
    LoginControlsBaseTest.class,
    LoginControlsTest.class,
    DialogLoginControlsTest.class,
    ViewLoginControlsTest.class,
    ChatWindowHandlerTest.class,
    ChatLoginControllerTest.class,
    CommunicationsStateTest.class,
    LocalRosterTest.class,
    XmppConnectionFactoryTest.class,
    SmackChatServiceTest.class,
    ConnectionNotifierTest.class,
    XmppConnectionBaseTest.class,
    SmackLoginTaskTest.class,
    SmackChatResponderTest.class,
    SmackRosterTest.class,
    ChatSessionViewTest.class,
    LoginHandlerTest.class,
    UpdateLoginConfigHandlerTest.class,
    AsyncChatWindowHandlerTest.class,
    LoginDataTest.class,
    CyberteteLoginDialogTest.class,
    ServiceThreadTest.class,
    ChatLoginProgressTaskTest.class,
    SessionDetailsSetTest.class,
    AddContactHandlerTest.class,
    SecurityHandlerTest.class,
    UpdateHandlerTest.class,
    ChangePresenceHandlerTest.class,
    LoadKerberosConfigHandlerTest.class,
    ExitHandlerTest.class,
    CloseAllHandlerTest.class,
    CloseAtStartupEventHandlerTest.class,
    ChangePerspectiveHandlerTest.class,
    StartChatHandlerTest.class,
    SaveLoginSessionHandlerTest.class,
    KerberosDataTest.class,
    KeystoreDataTest.class,
    KeystoreHelperTest.class,
    PersistentSecurityDataTest.class,
    KerberosCallbackHandlerTest.class,
    SecurityConfigControllerTest.class,
    SecurityResourcesTest.class,
    ContactsLabelProviderTest.class,
    TreeViewerControlTest.class,
    ContactsViewTest.class,
    ContactEntryListTest.class,
    MultiGroupContactsTreeTest.class,
    SecurityControlsTest.class,
    AddContactControlsTest.class,
    X509ControlsTest.class,
    LoginCustomControlsTest.class,
    PasswordControlsTest.class,
    PresenceControlsTest.class,
    StorageSupportTest.class,
    SecureStorageTest.class,
    UserSelectorTest.class,
    SessionDetailsMapTest.class,
    UserDataStoreTest.class,
    LifeCycleHelperTest.class,
    LifeCycleHandlerTest.class /*,
    ConnectionStatusTest.class,
    PresenceStatusTest.class,
    SecurityStatusTest.class*/
})
public class CyberteteTestSuite
{

    public CyberteteTestSuite()
    {
    }

}
