 
package au.com.cybersearch2.cybertete.views;

import javax.inject.Inject;

import au.com.cybersearch2.cybertete.dialogs.SingleSignonControls;
import au.com.cybersearch2.cybertete.security.SingleSignonController;

public class SingleSignOnView {
    @Inject
    SingleSignonControls singleSignonControls;
    @Inject
    SingleSignonController controller;
}