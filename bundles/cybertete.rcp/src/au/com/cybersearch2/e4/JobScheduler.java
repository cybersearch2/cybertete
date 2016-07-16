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
package au.com.cybersearch2.e4;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * JobScheduler
 * Creates and schedules a job given a job name and task to perform.
 * The job is expected to complete in a short time so a progress dialog is not employed.
 * @author Andrew Bowley
 * 10 Mar 2016
 */
public class JobScheduler
{
    /**
     * Create and schedule a job
     * @param name Job name
     * @param task Runnable to perform task
     */
    public void schedule(final String name, final Runnable task)
    {
        Job job = new Job(name)
        {

            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
                task.run();
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }
}
