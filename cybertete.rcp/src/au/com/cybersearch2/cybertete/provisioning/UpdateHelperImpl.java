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
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;

/**
 * UpdateHelperImpl
 * @author Andrew Bowley
 * 21 Apr 2016
 */
public class UpdateHelperImpl implements UpdateHelper
{
    UpdateOperation updateOperation;
    SubMonitor subMonitor;
    
    public UpdateHelperImpl(ProvisioningSession session)
    {
        updateOperation = new UpdateOperation(session);
    }

    /**
     * @see au.com.cybersearch2.cybertete.provisioning.UpdateHelper#getAvailabilityStatus(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public IStatus getAvailabilityStatus(IProgressMonitor monitor)
    {
        subMonitor = SubMonitor.convert(monitor, "Checking for application updates...", 200);
        return updateOperation.resolveModal(subMonitor.newChild(100));
    }

    /**
     * @see au.com.cybersearch2.cybertete.provisioning.UpdateHelper#getProvisioningJob()
     */
    @Override
    public ProvisioningJob getProvisioningJob()
    {
        if (subMonitor == null)
            return null; // Null allowed as next call can return null too
        return updateOperation.getProvisioningJob(subMonitor.newChild(100));
    }

    /**
     * @see au.com.cybersearch2.cybertete.provisioning.UpdateHelper#hasResolved()
     */
    @Override
    public boolean hasResolved()
    {
        return updateOperation.hasResolved();
    }

    /**
     * @see au.com.cybersearch2.cybertete.provisioning.UpdateHelper#getResolutionResult()
     */
    @Override
    public String getResolutionResult()
    {
        return updateOperation.getResolutionResult().toString();
    }

    @Override
    public void addListener(ProvisioningJob provisioningJob, JobChangeAdapter jobChangeListener)
    {
        provisioningJob.addJobChangeListener(jobChangeListener);
    }

}
