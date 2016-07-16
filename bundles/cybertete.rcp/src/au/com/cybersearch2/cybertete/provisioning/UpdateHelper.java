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
package au.com.cybersearch2.cybertete.provisioning;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.equinox.p2.operations.ProvisioningJob;

/**
 * UpdateHelper
 * Interface hides SubMonitor class, which is final and therefore not mockable by Mockito
 * @author Andrew Bowley
 * 21 Apr 2016
 */
public interface UpdateHelper
{
    IStatus getAvailabilityStatus(IProgressMonitor monitor);
    ProvisioningJob getProvisioningJob();
    boolean hasResolved();
    String getResolutionResult();
    
    // ProvisioningJob.addJobChangeListener() is final, so cannot be mocked with Mockito
    void addListener(ProvisioningJob provisioningJob, JobChangeAdapter jobChangeListener);
}
