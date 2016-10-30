package au.com.cybersearch2.cybertete.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class CybertetePreferencePage extends FieldEditorPreferencePage 
	{

	public CybertetePreferencePage() 
	{
		super(GRID);
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() 
	{
        BooleanFieldEditor boolEditor = 
            new BooleanFieldEditor(PreferenceConstants.AUTO_LOGIN,
                "Login automatically at startup", getFieldEditorParent());
        addField(boolEditor);
	}

}