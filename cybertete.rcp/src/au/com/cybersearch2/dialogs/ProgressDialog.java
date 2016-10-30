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

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * ProgressDialog
 * Display modal dialog to display progress and can be cancelled by the user
 * @author Andrew Bowley
 * 11 May 2016
 */
public class ProgressDialog
{
    ProgressMonitorDialog progressMonitorDialog;
    UISynchronize sync;
    
    /**
     * 
     */
    public ProgressDialog(ProgressMonitorDialog progressMonitorDialog, UISynchronize sync)
    {
        this.sync = sync;
        this.progressMonitorDialog = progressMonitorDialog;
     }

    public boolean open(final IRunnableWithProgress runnableWithProgress, final UncaughtExceptionHandler uncaughtExceptionHandler)
    {
        final boolean success[] = new boolean[]{false};
        // Modal dialog must be launched on UI thread
        sync.syncExec(new Runnable() {
            
            @Override
            public void run() 
            {
                try
                {
                    progressMonitorDialog.run(true, true, runnableWithProgress);
                    success[0] = true;
                }
                catch (InvocationTargetException e)
                {
                    uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e.getCause());
                }
                catch (InterruptedException e)
                {
                }
           }});
        return success[0];
    }
}
