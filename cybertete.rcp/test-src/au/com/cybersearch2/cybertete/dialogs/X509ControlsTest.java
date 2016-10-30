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
package au.com.cybersearch2.cybertete.dialogs;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.junit.Test;

import au.com.cybersearch2.controls.ControlFactory;
import au.com.cybersearch2.controls.CustomDialog.ButtonFactory;
import au.com.cybersearch2.dialogs.DialogHandler;

/**
 * X509ControlsTest
 * @author Andrew Bowley
 * 9 May 2016
 */
public class X509ControlsTest
{
    static final String TEST_JID = "mickymouse@disney.com";
    static final String SUBJECT_DN = "CN = " + TEST_JID;
    static final String ISSUER_DN = "CN=cybersearch2-HQ-CA";
    static final String START_DATE = "01/02/2010";
    static final String END_DATE = "30/04/2020";

    @Test
    public void test_createControls() throws ParseException
    {
        ControlFactory controlFactory = mock(ControlFactory.class);
        X509Certificate cert = mock(X509Certificate.class);
        Principal principal = mock(Principal.class);
        when(cert.getSubjectDN()).thenReturn(principal);
        when(principal.getName()).thenReturn(SUBJECT_DN);
        X500Principal issuerPrincipal = new X500Principal(ISSUER_DN);
        when(cert.getIssuerX500Principal()).thenReturn(issuerPrincipal);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = dateFormat.parse(START_DATE);
        when(cert.getNotBefore()).thenReturn(startDate);
        Date endDate = dateFormat.parse(END_DATE);
        when(cert.getNotAfter()).thenReturn(endDate);
        X509Controls underTest = new X509Controls(controlFactory, cert);
        Composite parent = mock(Composite.class);
        Composite container = mock(Composite.class);
        when(controlFactory.compositeInstance(parent)).thenReturn(container);
        Label label1 = mock(Label.class);
        Label label2 = mock(Label.class);
        Label label3 = mock(Label.class);
        Label label4 = mock(Label.class);
        when(controlFactory.labelInstance(container, SWT.NONE)).thenReturn(label1, label2, label3, label4);
        Text text1 = mock(Text.class);
        Text text2 = mock(Text.class);
        Text text3 = mock(Text.class);
        Text text4 = mock(Text.class);
        when(controlFactory.textInstance(container, SWT.MULTI | SWT.WRAP)).thenReturn(text1, text2, text3, text4);

        assertThat(underTest.createControls(parent, mock(DialogHandler.class))).isEqualTo(container);
        verify(container).setLayoutData(isA(GridData.class));
        verify(container).setLayout(isA(GridLayout.class));
        verifyCreateText(label1, text1, "Subject DN", SUBJECT_DN);
        verifyCreateText(label2, text2, "Issuer DN", ISSUER_DN);
        verifyCreateText(label3, text3, "Issued on", START_DATE);
        verifyCreateText(label4, text4, "Expires on", END_DATE);
    }

    @Test
    public void test_createButtonsForButtonBar()
    {
        ControlFactory controlFactory = mock(ControlFactory.class);
        X509Certificate cert = mock(X509Certificate.class);
        X509Controls underTest = new X509Controls(controlFactory, cert);
        Composite parent = mock(Composite.class);
        ButtonFactory buttonFactory = mock(ButtonFactory.class);
        assertThat(underTest.createBarButtons(parent, buttonFactory , mock(DialogHandler.class))).isTrue();
        verify(buttonFactory).buttonInstance(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }
    
    private void verifyCreateText(Label label, Text text,
            String labelText, String info)
    {
        verify(label).setText(labelText + "   ");
        verify(text).setText(info);
    }

}
