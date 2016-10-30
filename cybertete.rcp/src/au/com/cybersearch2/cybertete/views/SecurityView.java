 
package au.com.cybersearch2.cybertete.views;

import javax.inject.Inject;

import au.com.cybersearch2.cybertete.dialogs.SecurityControls;
import au.com.cybersearch2.cybertete.security.SecurityConfigController;

/**
 * SecurityView
 * Configures security including client cert authentication and single signon
 * @author Andrew Bowley
 * 9 Mar 2016
 */
public class SecurityView
{
    @Inject
    SecurityControls securityControls;
    @Inject
    SecurityConfigController controller;
 }