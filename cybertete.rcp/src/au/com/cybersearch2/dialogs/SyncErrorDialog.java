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
package au.com.cybersearch2.dialogs;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * SnycErrorDialog
 * Launches standard error dialog in synchronous UI thread
 * @author Andrew Bowley
 * 20 Feb 2016
 */
public class SyncErrorDialog
{
    /** Synchronizes current thread while executing in the UI-Thread */
    @Inject
    UISynchronize sync;
    /** Active shell */
    @Inject @Named (IServiceConstants.ACTIVE_SHELL)
    Shell shell;
  
    /**
     * Show error message in system dialog
     * @param title The dialog's title
     * @param message The message
     */
    public void showError(final String title, final String message) 
    {
        sync.syncExec(new Runnable() {
            
            @Override
            public void run() 
            {
                MessageDialog.openError(shell, title, message);
            }
        });
    }
}
