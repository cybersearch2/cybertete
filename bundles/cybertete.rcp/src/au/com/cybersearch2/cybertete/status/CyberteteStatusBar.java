package au.com.cybersearch2.cybertete.status;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import au.com.cybersearch2.statusbar.StatusBar;

/**
 * CyberteteStatusBar
 * Status line control built on Statusbar plugin
 * @author Andrew Bowley
 * 20 Apr 2016
 */
public class CyberteteStatusBar 
{
    static public final int PRESENCE_ID = 0;
    static public final int CONNECTION_ID = 1;
    static public final int SECURITY_ID = 2;
    
    /** Status line container arranges the items */
    @Inject
    StatusBar statusBar;
    /** Status line anchor contains presence indicator */
    @Inject
    PresenceStatus presenceStatus;
    /** Security indicator is aligned on the right of the main window client area */
    @Inject
    SecurityStatus securityStatus;
    /** Connection status indicator sits to the right of the presence indicator */
    @Inject
    ConnectionStatus connectionStatus;

    @PostConstruct
    void postConstruct()
    {
        statusBar.addStatusItem(presenceStatus.getStatusItem());
        statusBar.addStatusItem(connectionStatus.getStatusItem());
        statusBar.addStatusItem(securityStatus.getStatusItem());
    }
}