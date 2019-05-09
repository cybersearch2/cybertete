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
package au.com.cybersearch2.cybertete.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import au.com.cybersearch2.controls.ButtonControl;
import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.CustomControls;
import au.com.cybersearch2.controls.ImageFactory;
import au.com.cybersearch2.cybertete.model.Presence;
import au.com.cybersearch2.dialogs.DialogHandler;

/**
 * PresenceControls
 * User selects one of 3 Presence Status options
 * @author Andrew Bowley
 * 4 Nov 2015
 */
public class PresenceControls extends CustomControls
{
    public static final String TITLE = "Change Contact Status";
    // Options for the radio buttons
    private static Presence[] presences;

    static
    {
        presences = new Presence[] { Presence.online, Presence.away, Presence.dnd };
    }
    
    /** Presence selection, updated by onFocus listener */
    volatile Presence presence;
    /** Image factory */
    ImageFactory imageFactory;

    /** Listener sets presence when selection made by user */
    FocusListener listener = new FocusListener(){

        @Override
        public void focusGained(FocusEvent event)
        {
            Button button = (Button)event.getSource();
            presence = (Presence) button.getData();
        }

        @Override
        public void focusLost(FocusEvent e)
        {
        }};

    /**
     * Create PresenceControls object
     * @param controlFactory SWT widget factory
     * @param imageFactory Image factory
     */
    public PresenceControls(ControlFactory controlFactory, ImageFactory imageFactory)
    {
        super(controlFactory);
        this.imageFactory = imageFactory;
        // Default to online
        presence = presences[0];
    }

    /**
     * createDialogArea
     * @see au.com.cybersearch2.controls.CustomControls#createControls(org.eclipse.swt.widgets.Composite, au.com.cybersearch2.dialogs.DialogHandler)
     */
    @Override
    public Control createControls(Composite parent, DialogHandler dialogHandler) 
    {
        Composite composite = controlFactory.compositeInstance(parent);
        GridLayout layout = new GridLayout(1, false);
        composite.setLayout(layout);
        Group group = controlFactory.groupInstance(composite, SWT.NONE);
        // ControlFactory hides final factory classes
        controlFactory.getDefaultLayout().applyTo(group);
        controlFactory.getDefaultGridData().grab(true, true).applyTo(group);
        for (int i = 0; i < presences.length; i++) 
        {
            ButtonControl button = new ButtonControl(controlFactory, group, SWT.RADIO);
            button.setText(presences[i].getDisplayText());
            button.setImage(imageFactory.getMappedImage(presences[i]));
            button.setData(presences[i]);
            button.addFocusListener(listener);
        }
        return composite;
    }

    /**
     * Returns presence selection
     * @return Presence
     */
    public Presence getPresence()
    {
        return presence;
    }
    
}
