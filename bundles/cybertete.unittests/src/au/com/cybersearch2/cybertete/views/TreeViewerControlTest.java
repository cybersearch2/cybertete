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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

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
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.cybertete.model.ContactEntry;
import au.com.cybersearch2.cybertete.model.ContactGroup;

/**
 * TreeViewerControlTest
 * @author Andrew Bowley
 * 20 May 2016
 */
public class TreeViewerControlTest
{
  
    @Test
    public void test_postConstruct()
    {
        TreeViewerControl underTest = new TreeViewerControl();
        Composite parent = mock(Composite.class);
        ControlFactory controlFactory = mock(ControlFactory.class);
        TreeViewer treeViewer = mock(TreeViewer.class);
        when(controlFactory.treeViewerInstance(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL)).thenReturn(treeViewer);
        ESelectionService selectionService = mock(ESelectionService.class);
        underTest.postConstruct(parent, controlFactory, selectionService);
        ArgumentCaptor<ISelectionChangedListener> selectionListener = ArgumentCaptor.forClass(ISelectionChangedListener.class);
        verify(treeViewer).addSelectionChangedListener(selectionListener.capture());
        ContactEntry contactEntry = mock(ContactEntry.class);
        SelectionChangedEvent event = mock(SelectionChangedEvent.class);
        IStructuredSelection selection = mock(IStructuredSelection.class);
        when(selection.getFirstElement()).thenReturn(contactEntry);
        when(event.getSelection()).thenReturn(selection);
        selectionListener.getValue().selectionChanged(event);
        verify(selectionService).setSelection(contactEntry);
    }
    
    @Test
    public void test_setSelection()
    {
        TreeViewerControl underTest = new TreeViewerControl();
        ContactEntry contactEntry = mock(ContactEntry.class);
        ContactGroup parent = mock(ContactGroup.class);
        when(contactEntry.getParent()).thenReturn(parent);
        TreeViewer treeViewer = mock(TreeViewer.class);
        underTest.treeViewer = treeViewer;
        Object[] segments = new Object[2];
        segments[0] = parent;
        segments[1] = contactEntry;
        underTest.setSelection(segments, contactEntry);
        ArgumentCaptor<ISelection> selectionCaptor = ArgumentCaptor.forClass(ISelection.class);
        verify(treeViewer).setSelection(selectionCaptor.capture(), eq(true)); 
        TreeSelection selection = (TreeSelection)selectionCaptor.getValue();
        ArgumentCaptor<TreePath> treePathCaptor = ArgumentCaptor.forClass(TreePath.class);
        verify(treeViewer).setExpandedState(treePathCaptor.capture(), eq(true));
        TreePath treePath = treePathCaptor.getValue();
        assertThat(treePath.getSegment(0)).isEqualTo(parent);
        assertThat(treePath.getSegment(1)).isEqualTo(contactEntry);
        assertThat(selection.getPaths()[0]).isEqualTo(treePath);
        verify(treeViewer).refresh(contactEntry, false);    
    }
    
    @Test
    public void test_short_methods()
    {
        // TreeView.setInput() is final, so cannot be mocked
        TreeViewerControl underTest = new TreeViewerControl();
        TreeViewer treeViewer = mock(TreeViewer.class);
        Control control = mock(Control.class);
        when(treeViewer.getControl()).thenReturn(control);
        underTest.treeViewer = treeViewer;
        underTest.setFocus();
        verify(control).setFocus();
        assertThat(underTest.getControl()).isEqualTo(control);
    }
}
