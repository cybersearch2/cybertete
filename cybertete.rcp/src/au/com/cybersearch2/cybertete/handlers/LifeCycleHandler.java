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

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.InjectionException;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.ImageFactory;
import au.com.cybersearch2.controls.PlatformTools;
import au.com.cybersearch2.controls.PresenceImageFactory;
import au.com.cybersearch2.controls.ResourceTools;
import au.com.cybersearch2.cybertete.Activator;
import au.com.cybersearch2.cybertete.CyberteteException;
import au.com.cybersearch2.cybertete.GlobalProperties;
import au.com.cybersearch2.cybertete.dialogs.LoginDialog;
import au.com.cybersearch2.cybertete.model.ChatAgent;
import au.com.cybersearch2.cybertete.model.ChatContacts;
import au.com.cybersearch2.cybertete.model.ContactsTree;
import au.com.cybersearch2.cybertete.model.InteractiveLogin;
import au.com.cybersearch2.cybertete.model.RosterAgent;
import au.com.cybersearch2.cybertete.model.internal.MultiGroupContactsTree;
import au.com.cybersearch2.cybertete.model.service.ChatListener;
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
 * LifeCycleHandler
 * Handles life cycle events "postContextCreate" and "processAdditions"
 * @author Andrew Bowley
 * 22 May 2016
 */
public class LifeCycleHandler
{
    /** Status bar contains the status line items */
    CyberteteStatusBar cyberteteStatusBar;
    /** Error dialog */
    SyncErrorDialog errorDialog;
    /** Logger */
    Logger logger;

    /** Support for LifeCycle handlers */
    @Inject
    LifeCycleHelper lifeCycleHelper;
    /** The context for dependency injection */
    @Inject
    IEclipseContext workbenchContext;
    /* Context injection factory */
    @Inject
    InjectionFactory injectionFactory;
    
    /**
     * postConstruct
     * @param loggerProvider Logger factory
     */
    @PostConstruct
    void postConstruct(ILoggerProvider loggerProvider)
    {
        logger = loggerProvider.getClassLogger(LifeCycleHandler.class);
    }
 
    /**
     * Handle postContextCreate
     * @param args Command line parameters
     */
    public void onPostContextCreate(final List<String> args)
    {
        // Install statically-derived objects
        installStatics();
        ResourceTools resourceTools = new ResourceTools();
        resourceTools.setResourceBundle(Activator.getBundle());
        put(ResourceTools.class, resourceTools);
        // Install image and SWT widget factories
        ImageFactory imageFactory = make(ImageFactory.class);
        put(ImageFactory.class, imageFactory);
        imageFactory.registerCustomFactory(new PresenceImageFactory());
        PlatformTools platformTools = put(PlatformTools.class);
        // Window static method used to get logo in top left Cybertete windows 
        platformTools.setDefaultImages(Collections.singletonList("icons/chat.gif"));
        put(ControlFactory.class);
        // Install other sundry application objects
        put(JobScheduler.class);
        errorDialog = put(SyncErrorDialog.class);
        put(SyncInfoDialog.class);
        put(SyncQuestionDialog.class);
        // Process command line
        processCommandLine(args);
        // Install application model support objects
        put(ApplicationModel.class);
        put(Contribution.class, getContribution());
        // Install handler to change perspective
        put(ChangePerspectiveHandler.class);
        // Install chat service
        installChatService();
    }

    /**
     * Handle processAdditions.
     * The application model is loaded and application startup will complete on exit.
     */
    public void completeApplicationStart(MApplication application, EModelService modelService, EPartService partService) 
    {
        /** Application Model is not available until loaded */
        ApplicationModel applicationModel = get(ApplicationModel.class);
        applicationModel.setApplicationModel(application, modelService, partService, getBasicFactory());
        //Implements XMPP Instant Messaging
        ChatService chatService = get(ChatService.class);
        // ChatLoginController manages Login dialog and communications establishment
        ChatLoginController chatLoginController = get(ChatLoginController.class);
        // Login dialog launches Chat service
        InteractiveLogin loginDialog = get(InteractiveLogin.class);
        if ((chatService == null) || (chatLoginController == null) || (loginDialog == null))
            throw new IllegalStateException("Chat Service installation not completed successfully");
        // Connect, login and load roster
        boolean isSessionStarted = startChatService(chatService, chatLoginController);
        if (!isSessionStarted)
            chatService.close();
        put(ChatAgent.class, chatService);
        put(RosterAgent.class, chatService);
        // Login Handler launches Login dialog
        put(LoginHandler.class);
        put(InfoPopupHandler.class);
        cyberteteStatusBar = make(CyberteteStatusBar.class);
        if (!isSessionStarted && !loginDialog.showAdvanceOptions())
            // Close when workbench can be closed
            lifeCycleHelper
                .subscribeStartupComplete(make(CloseAtStartupEventHandler.class));
        else
            // Run when startup complete
            lifeCycleHelper
                .subscribeStartupComplete(make(RunAtStartupEventHandler.class));
    }

    /**
     * Handle shutdown
     */
    public void onShutdown()
    {
        ChatService chatService = get(ChatService.class);
        if (chatService != null)
            chatService.close();
        ImageFactory imageFactory = get(ImageFactory.class);
        if (imageFactory != null)
            imageFactory.dispose();
    }
    
    /**
     * Install statically-derived objects
     */
    void installStatics()
    {
        // Insert default file system object which creates objects that provide access to the file systems
        put(FileSystem.class, getFileSystem());
        // Insert secure preferences root node
        ISecurePreferences securePreferences = getSecurePreferences();
        if (securePreferences == null)
            throw new CyberteteException("Unable to create secure preferences using default location");
        put(ISecurePreferences.class, securePreferences);
    }
    

    /**
     * Install Chat service and dependent objects in Eclipse context
     */
    void installChatService() 
    {
        put(GlobalProperties.class);
        put(SecurityResources.class);
        put(SecureStorage.class);
        put(UserDataStore.class);
        put(PersistentSecurityData.class);
        put(KeystoreHelper.class);
        put(KerberosData.class);
        put(LoginData.class);
        put(LoginDialog.class, InteractiveLogin.class);
        // Instantiate Kerberos callback handler to be accessed by JAAS as a singleton
        put(KerberosCallbackHandler.class);
        put(SaveLoginSessionHandler.class);
        put(UpdateLoginConfigHandler.class);
        put(LoadKerberosConfigHandler.class);
        ChatLoginController chatLoginController = put(ChatLoginController.class);
        put(ChainHostnameVerifier.class);
        put(StatusBar.class);
        // ConnectionStatus sits in the middle of the status bar
        put(ConnectionStatus.class);
        // SecurityStatus sits on the status bar on the right and indicates if connection is secure
        put(SecurityStatus.class);
        // CommunicationsState tracks connection state changes      
        CommunicationsState communicationsState = put(CommunicationsState.class);
        put(MultiGroupContactsTree.class, ContactsTree.class);
        put(SessionOwner.class);
        // ChatWindowHandler intercepts Chat-related messages and updates ChatSessionView accordingly.
        // Waits for UIEvents.UILifeCycle.APP_STARTUP_COMPLETE post before performing rendering activity    
        put(ChatWindowHandler.class, ChatListener.class);
        // Add global objects to Chat service
        ChatService chatService = put(SmackChatService.class, ChatService.class);
        put(ChatContacts.class, chatService);
        chatService.addChatConnectionListener(communicationsState);
        chatService.addNetworkListener(communicationsState);
        chatService.addNetworkListener(chatLoginController);
     }

    /**
     * Start Chat service
     * @param chatService Implements XMPP Instant Messaging
     * @param chatLoginController Manages Login dialog and communications establishment
     * @return flag set true it login succeeds
     */
    boolean startChatService(ChatService chatService, ChatLoginController chatLoginController)
    {
        try
        {
            return chatService.startSession(chatLoginController);   
        }
        catch(final Throwable e)
        {
            final String message = "Fatal error while starting " + Activator.APPLICATION_TITLE;
            logger.error(e, message);
            errorDialog.showError(message, e.getMessage());
            /*  Findbugs info on original System.exit():
             *  Invoking System.exit shuts down the entire Java virtual machine. This should only been done when it is appropriate. 
             *  Such calls make it hard or impossible for your code to be invoked by other code. 
             *  Consider throwing a RuntimeException instead.
             */
            throw new RuntimeException("Exiting due fatal error while starting application");
        }

    }

    /**
     * Obtain an instance of the specified class and inject it with the context.
     * @param clazz The class to be instantiated
     * @return an instance of the specified class
     * @throws InjectionException if an exception occurred while performing this operation
     */
    public <T> T make(Class<? extends T> clazz)
    {
        return injectionFactory.make(clazz);
    }

    /**
     * Sets an instance of an object to be associated with a given class in the context.
     * @param clazz The class to store a value for
     * @return the object instance
      */
    public <T> T put(Class<T> clazz)
    {
        return put(clazz, make(clazz));
    }

    /**
     * Sets given instance of an object to be associated with a given class in the context.
     * @param clazz The class to store a value for
     * @return the object instance
     */
    public <T> T put(Class<T> clazz, T instance)
    {
        workbenchContext.set(clazz, instance);
        return instance;
    }

    /**
     * Sets an instance of an object to be associated with a given class in the context.
     * @param clazz The class to store a value for
     * @param superClazz Object association class, which is a super class for generalization
     * @return the object instance
     */
    public <T> T put(Class<? extends T> clazz, Class<T> superClazz)
    {
        T instance = make(clazz);
        workbenchContext.set(superClazz, instance);
        return instance;
    }

    /**
     * Returns object from context with given class
     * @param clazz The class with which to retrieve the value
     * @return the object instance
     */
    public <T> T get(Class<T> clazz)
    {
        return workbenchContext.get(clazz);
    }

    /**
     * Log if "clearPersistedState" argument used on command line
     * @param appContext IApplicationContext
     */
    private void processCommandLine(List<String> args)
    {
        if (lifeCycleHelper.getArgValue("clearPersistedState", args, true).equals(Boolean.TRUE.toString()))
            logger.info("New installation of " + Activator.APPLICATION_TITLE);
    }

    Contribution getContribution()
    {
        return  new Contribution(){

            @Override
            public String uriForClass(Class<?> contributionClass)
            {
                return "bundleclass://" + Activator.PLUGIN_ID + "/" + contributionClass.getName();
            }};
    }


    /**
     * Returns the default {@code FileSystem}. The default file system creates
     * objects that provide access to the file systems accessible to the Java
     * virtual machine. The <em>working directory</em> of the file system is
     * the current user directory, named by the system property {@code user.dir}.
     * This allows for interoperability with the {@link java.io.File java.io.File}
     * class.
     * @return  the default file system
     */
    protected FileSystem getFileSystem()
    {
        return FileSystems.getDefault();
    }

    /**
     * Returns default secure preferences.
     * The framework will attempt to open secure preferences in a user-specific location. 
     * As a result, the information stored can be shared among all programs run by the user. 
     * @return default instance of secure preferences, <code>null</code> if application
     * was unable to create secure preferences using default location
     */
    protected ISecurePreferences getSecurePreferences()
    {
        return SecurePreferencesFactory.getDefault();
    }
 
    /**
     * Returns the <b>Factory</b> for the model.
     * It provides a create method for each non-abstract class of the model.
     * @return MBasicFactory object
     */
    protected MBasicFactory getBasicFactory()
    {
        return MBasicFactory.INSTANCE;
    }


}
