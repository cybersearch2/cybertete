/**
    Copyright (C) 2015  www.cybersearch2.com.au

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
package au.com.cybersearch2.cybertete.status;

import java.security.cert.X509Certificate;
import java.util.List;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.cybertete.model.CyberteteEvents;

/**
 * X509Menu
 * Adds items to a context menu which selects from a list of X509 certificates
 * @author Andrew Bowley
 * 17 Dec 2015
 */
public class X509Menu
{
    /** The context menu */
    private Menu menu;
    /** Event broker service posts event to launch certificate information dialog */
    private IEventBroker eventBroker;

    /**
     * Create X509Menu object
     * @param menu The context menu
     * @param eventBroker Event broker service
     */
    public X509Menu(Menu menu, IEventBroker eventBroker)
    {
        this.menu = menu;
        this.eventBroker = eventBroker;
    }
 
    /**
     * Add items to the menu
     * @param controlFactory SWT widget factory
     * @param x509CertList List of X509 certificates
     */
    public void addX509Certs(ControlFactory controlFactory, List<X509Certificate> x509CertList)
    {
        controlFactory.menuItemInstance(menu, SWT.SEPARATOR);
        for (final X509Certificate cert: x509CertList)
        {
            MenuItem menuItem = controlFactory.menuItemInstance(menu, SWT.PUSH);
            String subject = cert.getSubjectDN().getName();
            int start = subject.indexOf("CN=");
            if (start != -1)
            {
                start += 3;
                int end = subject.indexOf(',', start);
                subject = end == -1 ? subject.substring(start) : subject.substring(start , end);
            }
            menuItem.setText(subject); 
            menuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) 
                { 
                    eventBroker.post(CyberteteEvents.CERT_INFO_POPUP, cert);
                }
            });
        }
    }


}
