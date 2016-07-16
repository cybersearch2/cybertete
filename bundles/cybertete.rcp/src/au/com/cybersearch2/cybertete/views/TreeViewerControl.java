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
package au.com.cybersearch2.cybertete.views;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import au.com.cybersearch2.controls.ControlFactory;

/**
 * TreeViewerControl
 * @author Andrew Bowley
 * 19 May 2016
 */
@Creatable
public class TreeViewerControl
{
    TreeViewer treeViewer;

    /**
     * postConstruct
     */
    @PostConstruct
    public void postConstruct(
        Composite parent, 
        ControlFactory controlFactory,
        final ESelectionService selectionService) 
    {
        treeViewer = controlFactory.treeViewerInstance(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        // Selection change listener fires selection service event
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
        
            @Override
            public void selectionChanged(SelectionChangedEvent event) 
            {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                selectionService.setSelection(selection.getFirstElement());
            }
        });
    }

    public void setProviders(ContactsContentProvider contentProvider, ContactsLabelProvider labelProvider)
    {
        treeViewer.setContentProvider(contentProvider);
        treeViewer.setLabelProvider(labelProvider);
    }
    
    public void setInput(Object input)
    {
        treeViewer.setInput(input);
    }
    
    public void setFocus()
    {
        treeViewer.getControl().setFocus();
    }

    /**
     * Set selection to path specified as object segements
     * @param segments The path to the selection
     * @param element The element in the tree to select
     */
    public void setSelection(Object[] segments, Object element)
    {
        TreePath treePath = new TreePath(segments);
        ISelection selection = new TreeSelection(treePath);
        treeViewer.setSelection(selection, true); 
        treeViewer.setExpandedState(treePath, true);
        treeViewer.refresh(element, false);    
    }

    public Control getControl()
    {
        return treeViewer.getControl();
    }

}
