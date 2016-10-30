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

import java.security.cert.X509Certificate;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

import au.com.cybersearch2.controls.CustomDialog;
import au.com.cybersearch2.cybertete.dialogs.DialogFactory;
import au.com.cybersearch2.cybertete.dialogs.X509Controls;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;
import au.com.cybersearch2.e4.JobScheduler;

/**
 * InfoPopupHandler
 * Handles selection of menu options which pop up dialogs
 * @author Andrew Bowley
 * 24 Mar 2016
 */
public class InfoPopupHandler
{
    @Inject
    DialogFactory dialogFactory;
    /** Job scheduler */
    @Inject 
    JobScheduler jobScheduler;
    /** Syncs with task run in UI thread */
    @Inject
    UISynchronize sync;
 
    /**
     * Handle display certificate information event
     * @param cert X509 Certificate
     * @param activeShell Active shell
     */
    @Inject @Optional
    void onCertInfoHandler(@UIEventTopic(CyberteteEvents.CERT_INFO_POPUP) X509Certificate cert, 
                           @Optional  @Named(IServiceConstants.ACTIVE_SHELL)Shell activeShell)
    {
        String subject = cert.getSubjectDN().getName();
        int start = subject.indexOf("CN=");
        if (start != -1)
        {
            start += 3;
            int end = subject.indexOf(',', start);
            subject = end == -1 ? subject.substring(start) : subject.substring(start , end);
        }
        final CustomDialog<X509Controls> dialog = dialogFactory.x509DialogInstance(activeShell, subject, cert);
        jobScheduler.schedule(subject, new Runnable(){

            @Override
            public void run()
            {
                dialog.syncOpen(sync);
            }});
    }
}
