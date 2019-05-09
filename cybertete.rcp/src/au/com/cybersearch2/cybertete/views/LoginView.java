 
package au.com.cybersearch2.cybertete.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;

/**
 * LoginView
 * Advanced login window, contains fields for options such as host, port and 
 * authenication username, when different from JID.
 * This is only a shell. The implementation is delegated to ViewLoginControls and AdvancedLoginController objects.
 * @author Andrew Bowley
 * 14 Mar 2016
 */
public class LoginView
{
    /** Application model ID for this view */
    public static final String LOGIN_VIEW_ID = "au.com.cybersearch2.cybertete.part.login";

    @Inject ViewLoginControls loginControls;
    
	/**
	 * Post construct
	 */
	@PostConstruct
	public void postConstruct()
	{
	    // The screen content is bound to this view by the parent composite
        loginControls.createDialogArea();
        loginControls.createButtonsForButtonBar();
    }

    /**
     * Set focus on current selection
     */
    @Focus
    void setFocus() 
    {
    	loginControls.setFocus();
    }
}
