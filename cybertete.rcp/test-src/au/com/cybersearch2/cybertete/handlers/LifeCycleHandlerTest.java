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
package au.com.cybersearch2.cybertete.handlers;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.*;

import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.ImageFactory;
import au.com.cybersearch2.controls.PlatformTools;
import au.com.cybersearch2.controls.ResourceTools;
import au.com.cybersearch2.cybertete.Activator;
import au.com.cybersearch2.cybertete.GlobalProperties;
import au.com.cybersearch2.cybertete.dialogs.LoginDialog;
import au.com.cybersearch2.cybertete.model.ChatAgent;
import au.com.cybersearch2.cybertete.model.InteractiveLogin;
import au.com.cybersearch2.cybertete.model.RosterAgent;
import au.com.cybersearch2.cybertete.model.internal.MultiGroupContactsTree;
import au.com.cybersearch2.cybertete.model.service.ChatService;
import au.com.cybersearch2.cybertete.preferences.UserDataStore;
import au.com.cybersearch2.cybertete.security.KerberosCallbackHandler;
import au.com.cybersearch2.cybertete.security.KerberosData;
import au.com.cybersearch2.cybertete.security.KeystoreHelper;
import au.com.cybersearch2.cybertete.security.PersistentSecurityData;
import au.com.cybersearch2.cybertete.security.SecurityResources;
import au.com.cybersearch2.cybertete.service.ChainHostnameVerifier;
import au.com.cybersearch2.cybertete.service.ChatLoginController;
import au.com.cybersearch2.cybertete.service.CommunicationsState;
import au.com.cybersearch2.cybertete.service.LoginData;
import au.com.cybersearch2.cybertete.service.SessionOwner;
import au.com.cybersearch2.cybertete.smack.SmackChatService;
import au.com.cybersearch2.cybertete.status.ConnectionStatus;
import au.com.cybersearch2.cybertete.status.CyberteteStatusBar;
import au.com.cybersearch2.cybertete.status.SecurityStatus;
import au.com.cybersearch2.dialogs.SyncErrorDialog;
import au.com.cybersearch2.dialogs.SyncInfoDialog;
import au.com.cybersearch2.dialogs.SyncQuestionDialog;
import au.com.cybersearch2.e4.ApplicationModel;
import au.com.cybersearch2.e4.Contribution;
import au.com.cybersearch2.e4.InjectionFactory;
import au.com.cybersearch2.e4.JobScheduler;
import au.com.cybersearch2.e4.LifeCycleHelper;
import au.com.cybersearch2.e4.SecureStorage;
import au.com.cybersearch2.statusbar.StatusBar;

/**
 * LifeCycleHandlerTest
 * @author Andrew Bowley
 * 24 May 2016
 */
public class LifeCycleHandlerTest
{
    class Target
    {
    }
    
    class SubTarget extends Target
    {
    }
    
    static final String[] COMMAND_LINE_ARGS = 
    {
        "-os","win32","-ws","win32","-arch","x86","-consoleLog","-clearPersistedState","-data","@/users/mickymouse","-debug"   
    };

    @Test
    public void test_context_methods()
    {
        Target target = new Target();
        LifeCycleHandler underTest = new LifeCycleHandler();
        IEclipseContext workbenchContext = mock(IEclipseContext.class);
        underTest.workbenchContext = workbenchContext;
        InjectionFactory injectionFactory = mock(InjectionFactory.class);
        underTest.injectionFactory = injectionFactory;
        when(injectionFactory.make(Target.class)).thenReturn(target);
        assertThat(underTest.make(Target.class)).isEqualTo(target);
        injectionFactory = mock(InjectionFactory.class);
        underTest.injectionFactory = injectionFactory;
        SubTarget subTarget = new SubTarget();
        when(injectionFactory.make(Target.class)).thenReturn(target);
        assertThat(underTest.put(Target.class)).isEqualTo(target);
        verify(workbenchContext).set(Target.class, target);
        injectionFactory = mock(InjectionFactory.class);
        underTest.injectionFactory = injectionFactory;
        when(injectionFactory.make(SubTarget.class)).thenReturn(subTarget);
        assertThat(underTest.put(SubTarget.class, Target.class)).isEqualTo(subTarget);
        verify(workbenchContext).set(Target.class, target);
        when(workbenchContext.get(Target.class)).thenReturn(target);
        assertThat(underTest.get(Target.class)).isEqualTo(target);
    }

    @Test
    public void test_startChatService()
    {
        ChatService chatService = mock(ChatService.class);
        ChatLoginController chatLoginController = mock(ChatLoginController.class);
        LifeCycleHandler underTest = new LifeCycleHandler();
        when(chatService.startSession(chatLoginController)).thenReturn(true);
        assertThat(underTest.startChatService(chatService, chatLoginController)).isTrue();
    }
    
    @Test
    public void test_startChatService_exception()
    {
        SyncErrorDialog errorDialog = mock(SyncErrorDialog.class);
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(LifeCycleHandler.class)).thenReturn(logger );
        IllegalStateException exception = new IllegalStateException("Software bug");
        ChatService chatService = mock(ChatService.class);
        ChatLoginController chatLoginController = mock(ChatLoginController.class);
        LifeCycleHandler underTest = new LifeCycleHandler();
        underTest.postConstruct(loggerProvider);
        underTest.errorDialog = errorDialog;
        when(chatService.startSession(chatLoginController)).thenThrow(exception);
        try
        {
            underTest.startChatService(chatService, chatLoginController);
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        }
        catch(RuntimeException e)
        {
            verify(logger).error(exception, "Fatal error while starting Cybertete");
            verify(errorDialog).showError("Fatal error while starting Cybertete", "Software bug");
        }
    }

    @Test
    public void test_installChatService()
    {
        SmackChatService chatService = mock(SmackChatService.class);
        ChatLoginController chatLoginController = mock(ChatLoginController.class);
        CommunicationsState communicationsState = mock(CommunicationsState.class);
        GlobalProperties globalProperties = mock(GlobalProperties.class);
        SecurityResources securityResources = mock(SecurityResources.class);
        SecureStorage secureStorage = mock(SecureStorage.class);
        UserDataStore userDataStore = mock(UserDataStore.class);
        PersistentSecurityData persistentSecurityData = mock(PersistentSecurityData.class);
        KeystoreHelper keystoreHelper = mock(KeystoreHelper.class);
        KerberosData kerberosData = mock(KerberosData.class);
        LoginData loginData = mock(LoginData.class);
        LoginDialog loginDialog= mock(LoginDialog.class); // , InteractiveLogin.class);
        KerberosCallbackHandler kerberosCallbackHandler = mock(KerberosCallbackHandler.class);
        SaveLoginSessionHandler saveLoginSessionHandler = mock(SaveLoginSessionHandler.class);
        UpdateLoginConfigHandler updateLoginConfigHandler = mock(UpdateLoginConfigHandler.class);
        LoadKerberosConfigHandler loadKerberosConfigHandler = mock(LoadKerberosConfigHandler.class);
        ChainHostnameVerifier chainHostnameVerifier = mock(ChainHostnameVerifier.class);
        StatusBar statusBar = mock(StatusBar.class);
        ConnectionStatus connectionStatus = mock(ConnectionStatus.class);
        SecurityStatus securityStatus = mock(SecurityStatus.class);
        MultiGroupContactsTree multiGroupContactsTree = mock(MultiGroupContactsTree.class); //, ContactsTree.class);
        SessionOwner sessionOwner = mock(SessionOwner.class);
        ChatWindowHandler chatWindowHandler = mock(ChatWindowHandler.class); //, ChatListener.class);
        LifeCycleHandler underTest = new LifeCycleHandler();
        LifeCycleHelper lifeCycleHelper = mock (LifeCycleHelper.class);
        underTest.lifeCycleHelper = lifeCycleHelper;
        InjectionFactory injectionFactory = mock(InjectionFactory.class);
        underTest.injectionFactory = injectionFactory;
        IEclipseContext workbenchContext = mock(IEclipseContext.class);
        underTest.workbenchContext = workbenchContext;
        when(injectionFactory.make(SmackChatService.class)).thenReturn(chatService);
        when(injectionFactory.make(ChatLoginController.class)).thenReturn(chatLoginController);
        when(injectionFactory.make(CommunicationsState.class)).thenReturn(communicationsState);
        when(injectionFactory.make(KerberosCallbackHandler.class)).thenReturn(kerberosCallbackHandler);
        when(injectionFactory.make(SaveLoginSessionHandler.class)).thenReturn(saveLoginSessionHandler);
        when(injectionFactory.make(UpdateLoginConfigHandler.class)).thenReturn(updateLoginConfigHandler);
        when(injectionFactory.make(LoadKerberosConfigHandler.class)).thenReturn(loadKerberosConfigHandler);
        when(injectionFactory.make(ChainHostnameVerifier.class)).thenReturn(chainHostnameVerifier);
        when(injectionFactory.make(StatusBar.class)).thenReturn(statusBar);
        when(injectionFactory.make(ConnectionStatus.class)).thenReturn(connectionStatus);
        when(injectionFactory.make(SecurityStatus.class)).thenReturn(securityStatus);
        when(injectionFactory.make(MultiGroupContactsTree.class)).thenReturn(multiGroupContactsTree);
        when(injectionFactory.make(SessionOwner.class)).thenReturn(sessionOwner);
        when(injectionFactory.make(ChatWindowHandler.class)).thenReturn(chatWindowHandler);
        when(injectionFactory.make(GlobalProperties.class)).thenReturn(globalProperties);
        when(injectionFactory.make(SecurityResources.class)).thenReturn(securityResources);
        when(injectionFactory.make(SecureStorage.class)).thenReturn(secureStorage);
        when(injectionFactory.make(UserDataStore.class)).thenReturn(userDataStore);
        when(injectionFactory.make(PersistentSecurityData.class)).thenReturn(persistentSecurityData);
        when(injectionFactory.make(KeystoreHelper.class)).thenReturn(keystoreHelper);
        when(injectionFactory.make(KerberosData.class)).thenReturn(kerberosData);
        when(injectionFactory.make(LoginData.class)).thenReturn(loginData);
        when(injectionFactory.make(LoginDialog.class)).thenReturn(loginDialog);
        underTest.installChatService();
        verify(chatService).addChatConnectionListener(communicationsState);
        verify(chatService).addNetworkListener(communicationsState);
        verify(chatService).addNetworkListener(chatLoginController);
    }
 
    @Test
    public void test_onPostContextCreate() throws Exception
    {
        BundleContext bundleContext = mock(BundleContext.class);
        Bundle bundle = mock(Bundle.class);
        when(bundleContext.getBundle()).thenReturn(bundle);
        Activator activator = new Activator();
        activator.start(bundleContext);
        List<String> args = new ArrayList<String>();
        for (String arg: COMMAND_LINE_ARGS)
            args.add(arg);
        ResourceTools resourceTools = mock(ResourceTools.class);
        ImageFactory imageFactory = mock(ImageFactory.class);
        PlatformTools platformTools = mock(PlatformTools.class);
        ChangePerspectiveHandler changePerspectiveHandler = mock(ChangePerspectiveHandler.class);
        JobScheduler jobScheduler = mock(JobScheduler.class);
        SyncErrorDialog syncErrorDialog = mock(SyncErrorDialog.class);
        ControlFactory controlFactory = mock(ControlFactory.class);
        SyncInfoDialog syncInfoDialog = mock(SyncInfoDialog.class);
        SyncQuestionDialog syncQuestionDialog = mock(SyncQuestionDialog.class);
        
        SmackChatService chatService = mock(SmackChatService.class);
        ChatLoginController chatLoginController = mock(ChatLoginController.class);
        CommunicationsState communicationsState = mock(CommunicationsState.class);
        SecureStorage secureStorage = mock(SecureStorage.class);
        UserDataStore userDataStore = mock(UserDataStore.class);
        PersistentSecurityData persistentSecurityData = mock(PersistentSecurityData.class);
        KeystoreHelper keystoreHelper = mock(KeystoreHelper.class);
        KerberosData kerberosData = mock(KerberosData.class);
        LoginData loginData = mock(LoginData.class);
        LoginDialog loginDialog= mock(LoginDialog.class); // , InteractiveLogin.class);
        KerberosCallbackHandler kerberosCallbackHandler = mock(KerberosCallbackHandler.class);
        SaveLoginSessionHandler saveLoginSessionHandler = mock(SaveLoginSessionHandler.class);
        UpdateLoginConfigHandler updateLoginConfigHandler = mock(UpdateLoginConfigHandler.class);
        LoadKerberosConfigHandler loadKerberosConfigHandler = mock(LoadKerberosConfigHandler.class);
        ChainHostnameVerifier chainHostnameVerifier = mock(ChainHostnameVerifier.class);
        MultiGroupContactsTree multiGroupContactsTree = mock(MultiGroupContactsTree.class); //, ContactsTree.class);
        SessionOwner sessionOwner = mock(SessionOwner.class);
        ChatWindowHandler chatWindowHandler = mock(ChatWindowHandler.class); //, ChatListener.class);
        ApplicationModel applicationModel = mock(ApplicationModel.class);
        final FileSystem fileSystem = mock(FileSystem.class);
        final ISecurePreferences securePreferences = mock(ISecurePreferences.class);
        LifeCycleHandler underTest = new LifeCycleHandler()
        {
            @Override
            protected FileSystem getFileSystem()
            {
                return fileSystem;
            }

            @Override
            protected ISecurePreferences getSecurePreferences()
            {
                return securePreferences;
            }
        };
        LifeCycleHelper lifeCycleHelper = mock (LifeCycleHelper.class);
        underTest.lifeCycleHelper = lifeCycleHelper;
        when(lifeCycleHelper.getArgValue("clearPersistedState", args, true)).thenReturn("true");
        InjectionFactory injectionFactory = mock(InjectionFactory.class);
        underTest.injectionFactory = injectionFactory;
        IEclipseContext workbenchContext = mock(IEclipseContext.class);
        underTest.workbenchContext = workbenchContext;
        ILoggerProvider loggerProvider = mock(ILoggerProvider.class);
        Logger logger = mock(Logger.class);
        when(loggerProvider.getClassLogger(LifeCycleHandler.class)).thenReturn(logger );
        underTest.postConstruct(loggerProvider);
        when(injectionFactory.make(ResourceTools.class)).thenReturn(resourceTools);
        when(injectionFactory.make(ImageFactory.class)).thenReturn(imageFactory);
        when(injectionFactory.make(PlatformTools.class)).thenReturn(platformTools);
        when(injectionFactory.make(ApplicationModel.class)).thenReturn(applicationModel);
        when(injectionFactory.make(ChangePerspectiveHandler.class)).thenReturn(changePerspectiveHandler);
        when(injectionFactory.make(JobScheduler.class)).thenReturn(jobScheduler);
        when(injectionFactory.make(SyncErrorDialog.class)).thenReturn(syncErrorDialog);
        when(injectionFactory.make(ControlFactory.class)).thenReturn(controlFactory);
        when(injectionFactory.make(SyncInfoDialog.class)).thenReturn(syncInfoDialog);
        when(injectionFactory.make(SyncQuestionDialog.class)).thenReturn(syncQuestionDialog);

        when(injectionFactory.make(SmackChatService.class)).thenReturn(chatService);
        when(injectionFactory.make(ChatLoginController.class)).thenReturn(chatLoginController);
        when(injectionFactory.make(CommunicationsState.class)).thenReturn(communicationsState);
        when(injectionFactory.make(KerberosCallbackHandler.class)).thenReturn(kerberosCallbackHandler);
        when(injectionFactory.make(SaveLoginSessionHandler.class)).thenReturn(saveLoginSessionHandler);
        when(injectionFactory.make(UpdateLoginConfigHandler.class)).thenReturn(updateLoginConfigHandler);
        when(injectionFactory.make(LoadKerberosConfigHandler.class)).thenReturn(loadKerberosConfigHandler);
        when(injectionFactory.make(ChainHostnameVerifier.class)).thenReturn(chainHostnameVerifier);
        when(injectionFactory.make(MultiGroupContactsTree.class)).thenReturn(multiGroupContactsTree);
        when(injectionFactory.make(SessionOwner.class)).thenReturn(sessionOwner);
        when(injectionFactory.make(ChatWindowHandler.class)).thenReturn(chatWindowHandler);
        when(injectionFactory.make(SecureStorage.class)).thenReturn(secureStorage);
        when(injectionFactory.make(UserDataStore.class)).thenReturn(userDataStore);
        when(injectionFactory.make(PersistentSecurityData.class)).thenReturn(persistentSecurityData);
        when(injectionFactory.make(KeystoreHelper.class)).thenReturn(keystoreHelper);
        when(injectionFactory.make(KerberosData.class)).thenReturn(kerberosData);
        when(injectionFactory.make(LoginData.class)).thenReturn(loginData);
        when(injectionFactory.make(LoginDialog.class)).thenReturn(loginDialog);
        underTest.onPostContextCreate(args);
        verify(logger).info("New installation of Cybertete");
        verify(workbenchContext).set(Bundle.class, bundle);
        verify(platformTools).setDefaultImages(Collections.singletonList("icons/chat.gif"));
        verify(chatService).addChatConnectionListener(communicationsState);
        verify(chatService).addNetworkListener(communicationsState);
        verify(chatService).addNetworkListener(chatLoginController);
        put(workbenchContext, FileSystem.class, fileSystem);
        put(workbenchContext, ISecurePreferences.class, securePreferences);
        ArgumentCaptor<Contribution> contribCaptor = ArgumentCaptor.forClass(Contribution.class);
        verify(workbenchContext).set(eq(Contribution.class), contribCaptor.capture());
        assertThat(contribCaptor.getValue().uriForClass(Object.class)).isEqualTo("bundleclass://au.com.cybersearch2.cybertete/" + Object.class.getName());
    }
    

    @Test
    public void test_completeApplicationStart()
    {
        ChatService chatService = mock(ChatService.class);
        ChatLoginController chatLoginController = mock(ChatLoginController.class);
        when(chatService.startSession(chatLoginController)).thenReturn(true);
        LoginDialog loginDialog = mock(LoginDialog.class);
        when(loginDialog.showAdvanceOptions()).thenReturn(false);
        final MBasicFactory modelFactory = mock(MBasicFactory.class);

        LifeCycleHandler underTest = new LifeCycleHandler()
        {
            protected MBasicFactory getBasicFactory()
            {
                return modelFactory;
            }
        };
        LifeCycleHelper lifeCycleHelper = mock (LifeCycleHelper.class);
        underTest.lifeCycleHelper = lifeCycleHelper;
        InjectionFactory injectionFactory = mock(InjectionFactory.class);
        LoginHandler loginHandler = mock(LoginHandler.class);
        InfoPopupHandler infoPopupHandler = mock(InfoPopupHandler.class);
        CyberteteStatusBar cyberteteStatusBar = mock(CyberteteStatusBar.class);
        when(injectionFactory.make(LoginHandler.class)).thenReturn(loginHandler);
        when(injectionFactory.make(InfoPopupHandler.class)).thenReturn(infoPopupHandler);
        when(injectionFactory.make(CyberteteStatusBar.class)).thenReturn(cyberteteStatusBar);
        underTest.injectionFactory = injectionFactory;
        IEclipseContext workbenchContext = mock(IEclipseContext.class);
        underTest.workbenchContext = workbenchContext;
        ApplicationModel applicationModel = mock(ApplicationModel.class);
        when(workbenchContext.get(ApplicationModel.class)).thenReturn(applicationModel);
        when(workbenchContext.get(ChatService.class)).thenReturn(chatService);
        when(workbenchContext.get(ChatLoginController.class)).thenReturn(chatLoginController);
        when(workbenchContext.get(InteractiveLogin.class)).thenReturn(loginDialog);
        RunAtStartupEventHandler runAtStartupEventHandler = mock(RunAtStartupEventHandler.class);
        when(injectionFactory.make(RunAtStartupEventHandler.class)).thenReturn(runAtStartupEventHandler);
        MApplication application = mock(MApplication.class);
        EModelService modelService = mock(EModelService.class);
        EPartService partService = mock(EPartService.class);
        underTest.completeApplicationStart(application, modelService, partService);
        verify(applicationModel).setApplicationModel(application, modelService, partService, modelFactory);
        verify(lifeCycleHelper).subscribeStartupComplete(runAtStartupEventHandler);
        put(workbenchContext, ChatAgent.class, chatService);
        put(workbenchContext, RosterAgent.class, chatService);
        put(workbenchContext, LoginHandler.class, loginHandler);
        put(workbenchContext, InfoPopupHandler.class, infoPopupHandler);
    }
    
    @Test
    public void test_completeApplicationStart_chat_service_fail()
    {
        ChatService chatService = mock(ChatService.class);
        ChatLoginController chatLoginController = mock(ChatLoginController.class);
        when(chatService.startSession(chatLoginController)).thenReturn(false);
        LoginDialog loginDialog = mock(LoginDialog.class);
        when(loginDialog.showAdvanceOptions()).thenReturn(false);
        LifeCycleHandler underTest = new LifeCycleHandler();
        LifeCycleHelper lifeCycleHelper = mock (LifeCycleHelper.class);
        underTest.lifeCycleHelper = lifeCycleHelper;
        InjectionFactory injectionFactory = mock(InjectionFactory.class);
         LoginHandler loginHandler = mock(LoginHandler.class);
        InfoPopupHandler infoPopupHandler = mock(InfoPopupHandler.class);
        when(injectionFactory.make(LoginHandler.class)).thenReturn(loginHandler);
        when(injectionFactory.make(InfoPopupHandler.class)).thenReturn(infoPopupHandler);
        underTest.injectionFactory = injectionFactory;
        IEclipseContext workbenchContext = mock(IEclipseContext.class);
        underTest.workbenchContext = workbenchContext;
        ApplicationModel applicationModel = mock(ApplicationModel.class);
        when(workbenchContext.get(ApplicationModel.class)).thenReturn(applicationModel);
        when(workbenchContext.get(ChatService.class)).thenReturn(chatService);
        when(workbenchContext.get(ChatLoginController.class)).thenReturn(chatLoginController);
        when(workbenchContext.get(InteractiveLogin.class)).thenReturn(loginDialog);
        CloseAtStartupEventHandler closeAtStartupEventHandler = mock(CloseAtStartupEventHandler.class);
        when(injectionFactory.make(CloseAtStartupEventHandler.class)).thenReturn(closeAtStartupEventHandler);
        MApplication application = mock(MApplication.class);
        EModelService modelService = mock(EModelService.class);
        EPartService partService = mock(EPartService.class);
        underTest.completeApplicationStart(application, modelService, partService);
        verify(lifeCycleHelper).subscribeStartupComplete(closeAtStartupEventHandler);
        put(workbenchContext, ChatAgent.class, chatService);
        put(workbenchContext, RosterAgent.class, chatService);
        put(workbenchContext, LoginHandler.class, loginHandler);
        put(workbenchContext, InfoPopupHandler.class, infoPopupHandler);
        verify(chatService).close();
    }


    @Test
    public void test_completeApplicationStart_advanced_login()
    {
        ChatService chatService = mock(ChatService.class);
        ChatLoginController chatLoginController = mock(ChatLoginController.class);
        when(chatService.startSession(chatLoginController)).thenReturn(true);
        LoginDialog loginDialog = mock(LoginDialog.class);
        when(loginDialog.showAdvanceOptions()).thenReturn(true);
        LifeCycleHandler underTest = new LifeCycleHandler();
        LifeCycleHelper lifeCycleHelper = mock (LifeCycleHelper.class);
        underTest.lifeCycleHelper = lifeCycleHelper;
        InjectionFactory injectionFactory = mock(InjectionFactory.class);
        LoginHandler loginHandler = mock(LoginHandler.class);
        InfoPopupHandler infoPopupHandler = mock(InfoPopupHandler.class);
        when(injectionFactory.make(LoginHandler.class)).thenReturn(loginHandler);
        when(injectionFactory.make(InfoPopupHandler.class)).thenReturn(infoPopupHandler);
        underTest.injectionFactory = injectionFactory;
        IEclipseContext workbenchContext = mock(IEclipseContext.class);
        underTest.workbenchContext = workbenchContext;
        ApplicationModel applicationModel = mock(ApplicationModel.class);
        when(workbenchContext.get(ApplicationModel.class)).thenReturn(applicationModel);
        when(workbenchContext.get(ChatService.class)).thenReturn(chatService);
        when(workbenchContext.get(ChatLoginController.class)).thenReturn(chatLoginController);
        when(workbenchContext.get(InteractiveLogin.class)).thenReturn(loginDialog);
        RunAtStartupEventHandler runAtStartupEventHandler = mock(RunAtStartupEventHandler.class);
        when(injectionFactory.make(RunAtStartupEventHandler.class)).thenReturn(runAtStartupEventHandler);
        MApplication application = mock(MApplication.class);
        EModelService modelService = mock(EModelService.class);
        EPartService partService = mock(EPartService.class);
        underTest.completeApplicationStart(application, modelService, partService);
        verify(lifeCycleHelper).subscribeStartupComplete(runAtStartupEventHandler);
        put(workbenchContext, ChatAgent.class, chatService);
        put(workbenchContext, RosterAgent.class, chatService);
        put(workbenchContext, LoginHandler.class, loginHandler);
        put(workbenchContext, InfoPopupHandler.class, infoPopupHandler);
        verify(chatService, times(0)).close();
    }

    private <T> void put(IEclipseContext workbenchContext, Class<T> clazz, T object)
    {
        verify(workbenchContext).set(clazz, object);
    }
    
}
