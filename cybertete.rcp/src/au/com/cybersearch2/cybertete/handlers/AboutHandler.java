 
package au.com.cybersearch2.cybertete.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;

import au.com.cybersearch2.dialogs.SyncInfoDialog;

public class AboutHandler 
{
    /** Information dialog */
    @Inject
    SyncInfoDialog infoDialog;

    @Execute
	public void execute() 
	{
        infoDialog.showInfo("About Cybertete", "Version 1.3.2");
	}
		
}