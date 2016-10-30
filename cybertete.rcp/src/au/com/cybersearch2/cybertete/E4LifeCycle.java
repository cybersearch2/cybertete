package au.com.cybersearch2.cybertete;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import com.opcoach.e4.preferences.ScopedPreferenceStore;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.InjectionException;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.PreSave;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessAdditions;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessRemovals;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.equinox.app.IApplicationContext;

import au.com.cybersearch2.cybertete.handlers.LifeCycleHandler;
import au.com.cybersearch2.cybertete.preferences.PreferenceConstants;
import au.com.cybersearch2.e4.InjectionFactory;

/**
 * Contains e4 LifeCycle annotated methods.<br />
 * There is a corresponding entry in <em>plugin.xml</em> (under the
 * <em>org.eclipse.core.runtime.products' extension point</em>) that references
 * this class.
 **/
public class E4LifeCycle
{
    /** Handler for post context create and process additions events */
    LifeCycleHandler lifeCycleHandler;
     /** Log Service logger */
    private Logger logger;

    /** Event broker service */
    @Inject IEventBroker eventBroker;
 
    /**
     * This method participates in the application lifecycle. 
     * Called after application context is created.
     * @param appContext IApplicationContext
     * @param workbenchContext IEclipseContext
     * @param eventBroker IEventBroker
     * @param loggerProvider ILoggerProvider
     * @param sync UISynchronize
     */
	@PostContextCreate
	void postContextCreate(IApplicationContext appContext, 
	                       final IEclipseContext workbenchContext,
	                       ILoggerProvider loggerProvider) 
	{
        // Close the static splash screen
        appContext.applicationRunning();
	    logger = loggerProvider.getClassLogger(E4LifeCycle.class);
	    logger.info("In postContextCreate()"); 
	    // Create preferences store which is shared by Preferences Page and
	    // application
	    ScopedPreferenceStore cybertetePreferenceStore = 
	            new ScopedPreferenceStore(InstanceScope.INSTANCE, PreferenceConstants.NODEPATH);
	    // Place in context using String key so third party plugin can retrieve it
	    workbenchContext.set(PreferenceConstants.NODEPATH + ".ScopedPreferenceStore", cybertetePreferenceStore);
	    // Place root preeference node in context for application to use
	    workbenchContext.set(IEclipsePreferences.class, cybertetePreferenceStore.getPreferenceNodes(false)[0]);
        InjectionFactory injectionFactory = new InjectionFactory(){

            @Override
            public <T> T make(Class<T> clazz)
                    throws InjectionException
            {
                return ContextInjectionFactory.make(clazz, workbenchContext);
            }};
        workbenchContext.set(InjectionFactory.class, injectionFactory);
        lifeCycleHandler = injectionFactory.make(LifeCycleHandler.class);
        // Command line arguments
        String[] argsArray = (String[])appContext.getArguments().get(IApplicationContext.APPLICATION_ARGS);
        List<String> args = new ArrayList<String>(argsArray.length);
        // Access the command line arguments
        for (String arg: argsArray)
            args.add(arg);
        lifeCycleHandler.onPostContextCreate(args);
	}

    @PreSave
	void preSave() 
	{
	}

    /**
     * This method participates in the application lifecycle. 
     * Called once the model is loaded.
     */
	@ProcessAdditions
	void processAdditions(MApplication application, EModelService modelService, EPartService partService) 
	{
        logger.info("In processAdditions()"); 
        lifeCycleHandler.completeApplicationStart(application, modelService, partService);
	}

	@ProcessRemovals
	void processRemovals(IEclipseContext workbenchContext) 
	{
	}
	
	/**
	 * The PreDestroy annotation is used on methods as a callback notification to
	 * signal that the instance is in the process of being removed by the
	 * container. The method annotated with PreDestroy is typically used to
	 * release resources that it has been holding. 
     */
	@PreDestroy
	public void preDestroy()
	{
        logger.info("In preDestroy() - Exiting Cybertete"); 
        lifeCycleHandler.onShutdown();
	}


}
