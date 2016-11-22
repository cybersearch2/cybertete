package au.com.cybersearch2.cybertete;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import au.com.cybersearch2.classylog.E4JavaLogger;

import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;

/**
 * 
 * Activator
 * Customizes the starting and stopping of a bundle. Here it performs Log Service registration.
 * Also provides static plugin ID and application title. 
 * <p>
 * {@code BundleActivator} is an interface that may be implemented when a bundle
 * is started or stopped. The Framework can create instances of a bundle's
 * {@code BundleActivator} as required. If an instance's
 * {@code BundleActivator.start} method executes successfully, it is guaranteed
 * that the same instance's {@code BundleActivator.stop} method will be called
 * when the bundle is to be stopped.
 * This class is specified in the manifest file:
 * <p>
 * Bundle-Activator: au.com.cybersearch2.cybertete.Activator
 * </p>
 * @author Andrew Bowley
 * 23 Dec 2015
 */
public class Activator implements BundleActivator 
{
    /** Plugin id (manifest Bundle-SymbolicName) */
    public static final String PLUGIN_ID = "au.com.cybersearch2.cybertete";
    /** Application title to head main window and be used in dialogs */
    public static final String APPLICATION_TITLE = "Cybertete";

    /** Bundle in BundleContext of most recent start() call */
    private static Bundle bundle;
 
    /**
     * A bundle's execution context within the Framework. The context is used to
     * grant access to other methods so that this bundle can interact with the
     * Framework.
     * 
     * <p>
     * {@code BundleContext} methods allow a bundle to:
     * <ul>
     * <li>Subscribe to events published by the Framework.</li>
     * <li>Register service objects with the Framework service registry.</li>
     * <li>Retrieve {@code ServiceReferences} from the Framework service registry.</li>
     * <li>Get and release service objects for a referenced service.</li>
     * <li>Install new bundles in the Framework.</li>
     * <li>Get the list of bundles installed in the Framework.</li>
     * <li>Get the {@link Bundle} object for a bundle.</li>
     * <li>Create {@code File} objects for files in a persistent storage area
     * provided for the bundle by the Framework.</li>
     * </ul>
     */ 
	private static BundleContext context;
	/** Log Service registration */
    private ServiceRegistration<?> registration;

    /**
     * Returns bundle context singleton
     * @return BundleContext object
     */
	static BundleContext getContext() 
	{
		return context;
	}

    /**
     * Set bundle context singleton. Keeps Findbugs happy when set by method call from non-static class member.
     * @return BundleContext
     */
	static void setContext(BundleContext bundleContext)
	{
	    context = bundleContext;
	}

    /**
     * Set bundle singleton. Keeps Findbugs happy when set by method call from non-static class member.
     * @return Bundle
     */
    static void setBundle(Bundle value)
    {
        bundle = value;
    }
    
	static public Bundle getBundle()
	{
	    return bundle;
	}
	
	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception 
	{
	    setBundle(bundleContext.getBundle());
        // Use JavaLogger as LoggerProvider for e4 so using same logger as Smack Library.
        ILoggerProvider logService = new ILoggerProvider() {
            @Override
            public Logger getClassLogger(Class<?> clazz) 
            {
                // E4JavaLogger wraps JavaLogger for use in this context
                return new E4JavaLogger(clazz);
            }
        };
        registration = bundleContext.registerService(ILoggerProvider.class, logService, null);
	    setContext(bundleContext);
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception 
	{
        registration.unregister();
        registration = null;
	    setContext(null);
	}

    public static ILoggerProvider getLoggerProvider()
    {
        BundleContext context = getContext();
        if (context != null)
        {
            ServiceReference<ILoggerProvider> service = context.getServiceReference(ILoggerProvider.class);
            if (service != null)
                return context.getService(service);
        }
        return getNullLoggerProvider();
    }

    static ILoggerProvider getNullLoggerProvider()
    {
        return new ILoggerProvider() {
            @Override
            public Logger getClassLogger(Class<?> clazz) 
            {
                // E4JavaLogger wraps JavaLogger for use in this context
                return new Logger(){

                    @Override
                    public boolean isErrorEnabled()
                    {
                        return false;
                    }

                    @Override
                    public void error(Throwable t, String message)
                    {
                    }

                    @Override
                    public boolean isWarnEnabled()
                    {
                        return false;
                    }

                    @Override
                    public void warn(Throwable t, String message)
                    {
                    }

                    @Override
                    public boolean isInfoEnabled()
                    {
                        return false;
                    }

                    @Override
                    public void info(Throwable t, String message)
                    {
                    }

                    @Override
                    public boolean isTraceEnabled()
                    {
                        return false;
                    }

                    @Override
                    public void trace(Throwable t, String message)
                    {
                    }

                    @Override
                    public boolean isDebugEnabled()
                    {
                        return false;
                    }

                    @Override
                    public void debug(Throwable t)
                    {
                    }

                    @Override
                    public void debug(Throwable t, String message)
                    {
                    }};
            }
        };

    }
}
